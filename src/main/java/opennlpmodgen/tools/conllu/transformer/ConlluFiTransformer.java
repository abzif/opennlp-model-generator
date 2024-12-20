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

import io.vavr.collection.Seq;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.parser.ConlluWordLine;
import org.springframework.stereotype.Component;

@Component
public class ConlluFiTransformer implements ConlluTransformer {
    @Override
    public ConlluSentence transform(ConlluSentence sentence, String language) {
        // lemma sometimes contains '#' character, remove it
        return new ConlluSentence(sentence.getText(), transform(sentence.getWords()));
    }

    private Seq<ConlluWordLine> transform(Seq<ConlluWordLine> words) {
        return words.map(this::transform);
    }

    private ConlluWordLine transform(ConlluWordLine word) {
        return word.isCompound()
                ? new ConlluWordLine(word.getStartId(), word.getEndId(), word.getForm(), transform(word.getSubWords()))
                : new ConlluWordLine(word.getStartId(), word.getForm(), word.getLemma().replace("#", ""), word.getPosTag());
    }

    @Override
    public boolean supportsLanguage(String language) {
        return "fi".equals(language);
    }
}
