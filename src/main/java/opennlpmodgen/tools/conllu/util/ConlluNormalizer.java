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
package opennlpmodgen.tools.conllu.util;

import io.vavr.collection.Seq;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.parser.ConlluWordLine;
import opennlpmodgen.tools.util.TextNormalizer;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ConlluNormalizer {
    @NonNull
    private final TextNormalizer textNormalizer;

    public ConlluSentence normalizeSentence(@NonNull ConlluSentence sentence, @NonNull String language) {
        return normalize(sentence, text -> textNormalizer.normalizeText(text, language));
    }

    private ConlluSentence normalize(ConlluSentence sentence, Function<String, String> textConverter) {
        return new ConlluSentence(textConverter.apply(sentence.getText()), normalize(sentence.getWords(), textConverter));
    }

    private Seq<ConlluWordLine> normalize(Seq<ConlluWordLine> words, Function<String, String> textConverter) {
        return words.map(word -> normalize(word, textConverter));
    }

    private ConlluWordLine normalize(ConlluWordLine word, Function<String, String> textConverter) {
        return word.isCompound()
                ? new ConlluWordLine(word.getStartId(), word.getEndId(), textConverter.apply(word.getForm()), normalize(word.getSubWords(), textConverter))
                : new ConlluWordLine(word.getStartId(), textConverter.apply(word.getForm()), textConverter.apply(word.getLemma()), word.getPosTag());
    }
}
