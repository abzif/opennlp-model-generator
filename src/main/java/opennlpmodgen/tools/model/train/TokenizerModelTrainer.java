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
package opennlpmodgen.tools.model.train;

import io.vavr.collection.Seq;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.SneakyThrows;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InsufficientTrainingDataException;
import opennlp.tools.util.TrainingParameters;
import opennlpmodgen.tools.model.util.SeqObjectStream;
import org.springframework.stereotype.Component;

@Component
public class TokenizerModelTrainer implements ModelTrainer<TokenizerModel, TokenSample> {
    @Override
    @SneakyThrows
    public Option<TokenizerModel> trainModel(@NonNull String algorithm, @NonNull String language, @NonNull Seq<TokenSample> samples) {
        try {
            var params = TrainingParameters.defaultParams();
            params.put(TrainingParameters.ALGORITHM_PARAM, algorithm);
            var model = TokenizerME.train(
                    new SeqObjectStream<>(samples),
                    new TokenizerFactory(language, null, false, null),
                    params);
            return Option.some(model);
        } catch (InsufficientTrainingDataException e) {
            return Option.none();
        }
    }
}
