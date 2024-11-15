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
import io.vavr.collection.Vector;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.parser.ConlluWordLine;
import org.springframework.stereotype.Component;

@Component
public class ConlluDefaultTransformer implements ConlluTransformer {
    @Override
    public ConlluSentence transform(ConlluSentence sentence, String language) {
        return tokenizationCorrect(sentence.getWords())
                ? sentence
                : new ConlluSentence("", Vector.of());
    }

    private boolean tokenizationCorrect(Seq<ConlluWordLine> words) {
        return words.forAll(this::tokenizationCorrect);
    }

    private boolean tokenizationCorrect(ConlluWordLine word) {
        if (word.isCompound()) {
            return tokenizationCorrect(word.getSubWords());
        }
        // words with hyphen (example: so-called, run-on etc) sometimes are not split at hyphen
        // this causes inconsistent tokenization of such words
        // reject such sentences, so tokenizer will be trained to always split at hyphen
        var form = word.getForm();
        return "-".equals(form) || !form.contains("-");
    }

    @Override
    public boolean supportsLanguage(String language) {
        return true;
    }
}
