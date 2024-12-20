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
package opennlpmodgen.tools.conllu;

import lombok.NonNull;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenizerModel;
import opennlpmodgen.tools.conllu.convert.ConlluTokenSamplesConverter;
import opennlpmodgen.tools.conllu.parser.ConlluSentenceReader;
import opennlpmodgen.tools.model.TokenizerModelProcessor;
import opennlpmodgen.tools.util.FileUpToDateChecker;
import org.springframework.stereotype.Component;

@Component
public class ConlluTokenizerModelProcessor extends ConlluModelProcessor<TokenizerModel, TokenSample> {
    public ConlluTokenizerModelProcessor(
            @NonNull FileUpToDateChecker checker,
            @NonNull ConlluSentenceReader sentenceReader,
            @NonNull ConlluTokenSamplesConverter converter,
            @NonNull TokenizerModelProcessor modelProcessor) {
        super(checker, sentenceReader, converter, modelProcessor);
    }
}
