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
import lombok.NonNull;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import org.springframework.stereotype.Component;

@Component
public class ConlluTransformers {
    private final Seq<ConlluTransformer> transformers;

    public ConlluTransformers(@NonNull ConlluTransformer... transformers) {
        this.transformers = Vector.of(transformers);
    }

    public ConlluSentence transformSentence(@NonNull ConlluSentence sentence, @NonNull String language) {
        return transformers.foldLeft(
                sentence,
                (prevSentence, transformer) -> applyTransformer(prevSentence, transformer, language));
    }

    private ConlluSentence applyTransformer(ConlluSentence sentence, ConlluTransformer transformer, String language) {
        return transformer.supportsLanguage(language)
                ? transformer.transform(sentence, language)
                : sentence;
    }
}
