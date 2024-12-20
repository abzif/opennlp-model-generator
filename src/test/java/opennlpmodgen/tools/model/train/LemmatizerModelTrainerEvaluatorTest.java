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
import io.vavr.collection.Vector;
import opennlp.tools.lemmatizer.LemmaSample;
import opennlpmodgen.tools.model.ModelAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LemmatizerModelTrainerEvaluatorTest {
    private LemmatizerModelTrainer trainer = new LemmatizerModelTrainer();
    private LemmatizerModelEvaluator evaluator = new LemmatizerModelEvaluator();

    @Test
    public void trainEvaluate() {
        Seq<LemmaSample> trainSamples = Vector.of(
                new LemmaSample(List.of("as", "ax"), List.of("N", "V"), List.of("a", "a")),
                new LemmaSample(List.of("bs", "bx"), List.of("N", "V"), List.of("b", "b")),
                new LemmaSample(List.of("cs", "cx"), List.of("N", "V"), List.of("c", "c")));
        Seq<LemmaSample> evalSamples = Vector.of(
                new LemmaSample(List.of("ds", "dx"), List.of("N", "V"), List.of("d", "d")),
                new LemmaSample(List.of("es", "ex"), List.of("N", "V"), List.of("e", "e")),
                new LemmaSample(List.of("fs", "fx"), List.of("N", "V"), List.of("f", "f")));

        var modelOpt = trainer.trainModel(ModelAlgorithm.PERCEPTRON, "lx", trainSamples);
        assertThat(modelOpt).isNotEmpty();

        var evalInfo = evaluator.evaluateModel(modelOpt.get(), evalSamples);
        assertThat(evalInfo._1).isGreaterThanOrEqualTo(0.0).isLessThanOrEqualTo(1.0);
    }

    @Test
    public void trainNoData() {
        var modelOpt = trainer.trainModel(ModelAlgorithm.NAIVE_BAYES, "lx", Vector.of());

        assertThat(modelOpt).isEmpty();
    }

    @Test
    public void trainIncorrectAlgorithm() {
        assertThatThrownBy(() -> trainer.trainModel("dummy", "lx", Vector.of()));
    }
}
