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
import opennlp.tools.tokenize.TokenSample;
import opennlpmodgen.tools.model.ModelAlgorithm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TokenizerModelTrainerEvaluatorTest {
    private TokenizerModelTrainer trainer = new TokenizerModelTrainer();
    private TokenizerModelEvaluator evaluator = new TokenizerModelEvaluator();

    @Test
    public void trainEvaluate() throws Exception {
        Seq<TokenSample> trainSamples = Vector.of(
                TokenSample.parse("a1 a2|.", "|"),
                TokenSample.parse("b1 b2|.", "|"),
                TokenSample.parse("c1 c2|.", "|"));
        Seq<TokenSample> evalSamples = Vector.of(
                TokenSample.parse("d1 d2|.", "|"),
                TokenSample.parse("e1 e2|.", "|"),
                TokenSample.parse("f1 f2|.", "|"));

        var modelOpt = trainer.trainModel(ModelAlgorithm.MAXENT, "lx", trainSamples);
        assertThat(modelOpt).isNotEmpty();

        var evalInfo = evaluator.evaluateModel(modelOpt.get(), evalSamples);
        assertThat(evalInfo._1).isGreaterThanOrEqualTo(0.0).isLessThanOrEqualTo(1.0);
    }

    @Test
    public void trainEvaluateNoData() {
        var modelOpt = trainer.trainModel(ModelAlgorithm.PERCEPTRON, "lx", Vector.of());

        assertThat(modelOpt).isEmpty();
    }

    @Test
    public void trainIncorrectAlgorithm() {
        assertThatThrownBy(() -> trainer.trainModel("dummy", "lx", Vector.of()));
    }
}
