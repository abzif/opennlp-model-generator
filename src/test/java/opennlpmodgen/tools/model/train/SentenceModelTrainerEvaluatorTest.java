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
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.util.Span;
import opennlpmodgen.tools.model.ModelAlgorithm;
import opennlpmodgen.tools.model.util.EOSCharsSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SentenceModelTrainerEvaluatorTest {
    @Mock
    private EOSCharsSupplier eosCharsSupplier;
    @InjectMocks
    private SentenceModelTrainer trainer;
    @InjectMocks
    private SentenceModelEvaluator evaluator;

    @BeforeEach
    private void setUp() {
        given(eosCharsSupplier.getEOSChars(any())).willReturn(Vector.ofAll('#', '@', '$'));
    }

    @Test
    public void trainEvaluate() {
        Seq<SentenceSample> trainSamples = Vector.of(
                createSample("a1# a2@"),
                createSample("b1$ b2#"),
                createSample("c1$ c2$"),
                createSample("d1@ d2@"),
                createSample("e1@ e2#"),
                createSample("f1# f2$"));
        Seq<SentenceSample> evalSamples = Vector.of(
                createSample("g1 g2@"),
                createSample("h1 h2#"),
                createSample("i1 i2$"));

        var modelOpt = trainer.trainModel(ModelAlgorithm.MAXENT_QN, "lx", trainSamples);
        assertThat(modelOpt).isNotEmpty();

        var evalInfo = evaluator.evaluateModel(modelOpt.get(), evalSamples);
        assertThat(evalInfo._1).isGreaterThanOrEqualTo(0.0).isLessThanOrEqualTo(1.0);
    }

    @Test
    public void trainEvaluateNoData() {
        var modelOpt = trainer.trainModel(ModelAlgorithm.NAIVE_BAYES, "lx", Vector.of());

        assertThat(modelOpt).isEmpty();
    }

    @Test
    public void trainIncorrectAlgorithm() {
        assertThatThrownBy(() -> trainer.trainModel("dummy", "lx", Vector.of()));
    }

    private SentenceSample createSample(String s) {
        return new SentenceSample(s, new Span(0, s.length()));
    }
}
