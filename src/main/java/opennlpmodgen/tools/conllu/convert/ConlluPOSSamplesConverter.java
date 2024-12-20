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
package opennlpmodgen.tools.conllu.convert;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import opennlp.tools.postag.POSSample;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.parser.ConlluWordLine;
import opennlpmodgen.tools.conllu.util.ConlluValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConlluPOSSamplesConverter implements ConlluSamplesConverter<POSSample> {
    @NonNull
    private final ConlluValidator validator;

    @Override
    public Seq<POSSample> convert(@NonNull Seq<ConlluSentence> sentences, @NonNull String language) {
        return sentences
                .filter(validator::isValidForLemmatization)
                .map(ConlluSentence::flattenWords)
                .map(this::convert);
    }

    private POSSample convert(ConlluSentence sentence) {
        var posTags = getPosTags(sentence.getWords());
        return new POSSample(
                posTags.map(Tuple2::_1).toJavaList(),
                posTags.map(Tuple2::_2).toJavaList());
    }

    private Seq<Tuple2<String, String>> getPosTags(Seq<ConlluWordLine> words) {
        return words.map(this::getPosTag);
    }

    private Tuple2<String, String> getPosTag(ConlluWordLine word) {
        return word.isCompound()
                ? Tuple.of(word.joinSubForms(), word.joinPosTags())
                : Tuple.of(word.getForm(), word.getPosTag());
    }
}
