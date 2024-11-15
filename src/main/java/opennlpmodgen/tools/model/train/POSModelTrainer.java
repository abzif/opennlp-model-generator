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
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InsufficientTrainingDataException;
import opennlp.tools.util.TrainingParameters;
import opennlpmodgen.tools.model.util.SeqObjectStream;
import org.springframework.stereotype.Component;

@Component
public class POSModelTrainer implements ModelTrainer<POSModel, POSSample> {
    @Override
    @SneakyThrows
    public Option<POSModel> trainModel(@NonNull String algorithm, @NonNull String language, @NonNull Seq<POSSample> samples) {
        try {
            var params = TrainingParameters.defaultParams();
            params.put(TrainingParameters.ALGORITHM_PARAM, algorithm);
            var model = POSTaggerME.train(
                    language,
                    new SeqObjectStream<>(samples),
                    params,
                    new POSTaggerFactory());
            return Option.some(model);
        } catch (InsufficientTrainingDataException e) {
            return Option.none();
        }
    }
}
