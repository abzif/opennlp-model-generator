/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package opennlpmodgen.tools.conllu.transformer;

import io.vavr.collection.*;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.parser.ConlluWordLine;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ConlluDeTransformer implements ConlluTransformer {
    private static final Set<String> DER_FORMS = HashSet.of("der", "die", "das", "des", "dem", "den");
    private static final Map<String, String> ZU_FORMS = HashMap.of("zum", "dem", "zur", "der");
    private static final Map<String, String> IN_FORMS = HashMap.of("im", "dem", "ins", "das");

    @Override
    public ConlluSentence transform(ConlluSentence sentence, String language) {
        return lemmatizationCorrect(sentence.getWords())
                ? new ConlluSentence(transformText(sentence.getText()), transformWords(true, 0, sentence.getWords()))
                : new ConlluSentence("", Vector.of());
    }

    private boolean lemmatizationCorrect(Seq<ConlluWordLine> words) {
        return words.forAll(this::lemmatizationCorrect);
    }

    private boolean lemmatizationCorrect(ConlluWordLine word) {
        if (word.isCompound()) {
            return lemmatizationCorrect(word.getSubWords());
        }
        if ("NOUN".equals(word.getPosTag())) {
            // compound words sometimes are lemmatized to their last word
            // in example: kundendienstzentrums => zentrum, geheimdienste => dienst
            // it causes an enormous number of outcomes in the lemmatizer and lemmatizer trainer fails with 'out of memory' error
            // we can not fix it so we ignore the whole sentence
            var form = word.getForm().toLowerCase(Locale.GERMAN);
            var lemma = word.getLemma().toLowerCase(Locale.GERMAN);
            // we consider noun as correcly lemmatized
            // if form/lemma length difference is not bigger than 3 letters
            // and both form and lemma starts with the same letter
            if (Math.abs(form.length() - lemma.length()) > 3) {
                return false;
            }
            if (!form.startsWith(lemma.substring(0, Math.min(1, lemma.length())))) {
                return false;
            }
        }
        return true;
    }

    private String transformText(String text) {
        // remove unecessary space characters
        // if punctuation characters are separated by whitespace then tokenizer has no training data - tokenizer training fails
        return text.replace(" .", ".")
                .replace(" ?", "?")
                .replace(" !", "!")
                .replace(" ,", ",")
                .replace(" :", ":")
                .replace(" ;", ";")
                .replace(" '", "'")
                .replace("( ", "(")
                .replace(" )", ")")
                .replaceAll("\" ([^\"]+) \"", "\"$1\"");
    }

    private Seq<ConlluWordLine> transformWords(boolean topLevel, int prevEndId, Seq<ConlluWordLine> words) {
        return words.scanLeft(new ConlluWordLine(prevEndId, "", "", ""),
                (prevWord, word) -> transformWord(topLevel, prevWord, word)).tail();
    }

    private ConlluWordLine transformWord(boolean topLevel, ConlluWordLine prevWord, ConlluWordLine word) {
        // make lemmatization consistent:
        //   der, die, das, des, dem, den => der
        //   zum, zur => zu+der (sometimes it is like that, sometimes not lemmatized, sometimes lemmatized to 'zu')
        //   im, ins => in+der (like above)
        //   sich => sich (sometimes lemmatized as er|sie|es)
        int offset = prevWord.getEndId() + 1 - word.getStartId();
        if (word.isCompound()) {
            return new ConlluWordLine(word.getStartId() + offset, word.getEndId() + offset, word.getForm(),
                    transformWords(false, word.getStartId() + offset - 1, word.getSubWords()));
        } else {
            var form = word.getForm().toLowerCase(Locale.GERMAN);
            if (DER_FORMS.contains(form)) {
                return new ConlluWordLine(word.getStartId() + offset, word.getForm(), "der", word.getPosTag());
            } else if ("sich".equals(form)) {
                return new ConlluWordLine(word.getStartId() + offset, word.getForm(), "sich", word.getPosTag());
            } else if (topLevel && ZU_FORMS.containsKey(form)) {
                return new ConlluWordLine(word.getStartId() + offset, word.getStartId() + 1 + offset, word.getForm(), Vector.of(
                        new ConlluWordLine(word.getStartId() + offset, "zu", "zu", "ADP"),
                        new ConlluWordLine(word.getStartId() + 1 + offset, ZU_FORMS.get(form).get(), "der", "DET")));
            } else if (topLevel && IN_FORMS.containsKey(form)) {
                return new ConlluWordLine(word.getStartId() + offset, word.getStartId() + 1 + offset, word.getForm(), Vector.of(
                        new ConlluWordLine(word.getStartId() + offset, "in", "in", "ADP"),
                        new ConlluWordLine(word.getStartId() + 1 + offset, IN_FORMS.get(form).get(), "der", "DET")));
            } else {
                return new ConlluWordLine(word.getStartId() + offset, word.getForm(), word.getLemma(), word.getPosTag());
            }
        }
    }

    @Override
    public boolean supportsLanguage(String language) {
        return "de".equals(language);
    }
}
