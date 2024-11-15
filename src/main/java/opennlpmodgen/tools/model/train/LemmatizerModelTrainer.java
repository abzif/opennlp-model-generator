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
import opennlp.tools.lemmatizer.LemmaSample;
import opennlp.tools.lemmatizer.LemmatizerFactory;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.util.InsufficientTrainingDataException;
import opennlp.tools.util.TrainingParameters;
import opennlpmodgen.tools.model.util.SeqObjectStream;
import org.springframework.stereotype.Component;

@Component
public class LemmatizerModelTrainer implements ModelTrainer<LemmatizerModel, LemmaSample> {
    @Override
    @SneakyThrows
    public Option<LemmatizerModel> trainModel(@NonNull String algorithm, @NonNull String language, @NonNull Seq<LemmaSample> samples) {
        try {
            var params = TrainingParameters.defaultParams();
            params.put(TrainingParameters.ALGORITHM_PARAM, algorithm);
            var model = LemmatizerME.train(language,
                    new SeqObjectStream<>(samples),
                    params,
                    new LemmatizerFactory());
            return Option.some(model);
        } catch (InsufficientTrainingDataException e) {
            return Option.none();
        }
    }
}
