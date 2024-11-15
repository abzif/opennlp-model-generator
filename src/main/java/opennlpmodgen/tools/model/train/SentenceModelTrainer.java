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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import opennlp.tools.sentdetect.SentenceDetectorFactory;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.util.InsufficientTrainingDataException;
import opennlp.tools.util.TrainingParameters;
import opennlpmodgen.tools.model.util.EOSCharsSupplier;
import opennlpmodgen.tools.model.util.SeqObjectStream;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SentenceModelTrainer implements ModelTrainer<SentenceModel, SentenceSample> {
    @NonNull
    private final EOSCharsSupplier eosCharsSupplier;

    @Override
    @SneakyThrows
    public Option<SentenceModel> trainModel(@NonNull String algorithm, @NonNull String language, @NonNull Seq<SentenceSample> samples) {
        try {
            var params = TrainingParameters.defaultParams();
            params.put(TrainingParameters.ALGORITHM_PARAM, algorithm);
            var model = SentenceDetectorME.train(
                    language,
                    new SeqObjectStream<>(samples),
                    new SentenceDetectorFactory(language, true, null, getEosChars(language)),
                    params);
            return Option.some(model);
        } catch (InsufficientTrainingDataException e) {
            return Option.none();
        }
    }

    private char[] getEosChars(String language) {
        var eosChars = eosCharsSupplier.getEOSChars(language);
        var eosCharsPrimitive = new char[eosChars.size()];
        eosChars.zipWithIndex().forEach(t -> eosCharsPrimitive[t._2] = t._1);
        return eosCharsPrimitive;
    }
}
