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

import io.vavr.collection.HashSet;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import lombok.NonNull;
import opennlp.tools.util.Span;
import opennlpmodgen.tools.conllu.ConlluPOSTag;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.parser.ConlluWordLine;
import org.springframework.stereotype.Component;

@Component
public class ConlluValidatorHelper {
    private static final Set<String> POS_TAGS = HashSet.of(ConlluPOSTag.values()).map(ConlluPOSTag::name);

    public boolean notEmpty(@NonNull ConlluSentence sentence) {
        return !sentence.getText().isBlank() && !sentence.getWords().isEmpty();
    }

    public boolean formsValid(@NonNull ConlluSentence sentence) {
        return sentence.getWords().flatMap(this::getForms).forAll(this::formValid);
    }

    private Seq<String> getForms(ConlluWordLine word) {
        return word.isCompound()
                ? word.getSubWords().map(ConlluWordLine::getForm).prepend(word.getForm())
                : Vector.of(word.getForm());
    }

    private boolean formValid(String form) {
        return !form.isBlank() && !form.contains(" ") && !"_".equals(form);
    }

    public boolean lemmasValid(@NonNull ConlluSentence sentence) {
        return sentence.getWords().flatMap(this::getLemmas).forAll(this::lemmaValid);
    }

    private Seq<String> getLemmas(ConlluWordLine word) {
        return word.isCompound()
                ? word.getSubWords().map(ConlluWordLine::getLemma)
                : Vector.of(word.getLemma());
    }

    private boolean lemmaValid(String lemma) {
        return !lemma.isBlank() && !lemma.contains(" ") && !"_".equals(lemma);
    }

    public boolean posTagsValid(@NonNull ConlluSentence sentence) {
        return sentence.getWords().flatMap(this::getPosTags).forAll(this::posTagValid);
    }

    private Seq<String> getPosTags(ConlluWordLine word) {
        return word.isCompound()
                ? word.getSubWords().map(ConlluWordLine::getPosTag)
                : Vector.of(word.getPosTag());
    }

    private boolean posTagValid(String posTag) {
        return POS_TAGS.contains(posTag);
    }

    public boolean textEqualsConcatenatedForms(@NonNull ConlluSentence sentence) {
        var text = sentence.getText();
        var coveredTextBoundsOpt = sentence.getWords()
                .map(ConlluWordLine::getForm)
                .foldLeft(Option.some(new Span(0, 0)), (prevSpan, form) -> getCoveredTextBounds(text, prevSpan, form));
        return coveredTextBoundsOpt.isDefined()
                && coveredTextBoundsOpt.get().getStart() == 0
                && coveredTextBoundsOpt.get().getEnd() == text.length();
    }

    private Option<Span> getCoveredTextBounds(String text, Option<Span> prevSpanOpt, String form) {
        return prevSpanOpt.flatMap(prevSpan -> getCoveredTextBounds(text, prevSpan, form));
    }

    private Option<Span> getCoveredTextBounds(String text, Span prevSpan, String form) {
        var currentStartIdx = text.indexOf(form, prevSpan.getEnd());
        if (currentStartIdx < 0) {
            // word not found
            return Option.none();
        }
        if (!isAdjacentOrSeparatedBySingleSpace(text, prevSpan.getEnd(), currentStartIdx)) {
            // not adjacent
            return Option.none();
        }
        var spanStart = prevSpan.getEnd() == 0 ? currentStartIdx : prevSpan.getStart();
        var spanEnd = currentStartIdx + form.length();
        return Option.some(new Span(spanStart, spanEnd));
    }

    private boolean isAdjacentOrSeparatedBySingleSpace(String text, int previousEndIdx, int currentStartIdx) {
        return previousEndIdx == currentStartIdx || previousEndIdx + 1 == currentStartIdx && text.charAt(previousEndIdx) == ' ';
    }
}
