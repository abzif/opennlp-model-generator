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

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import lombok.NonNull;
import lombok.SneakyThrows;
import opennlp.tools.cmdline.tokenizer.TokenEvaluationErrorListener;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenizerEvaluator;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlpmodgen.tools.model.util.SeqObjectStream;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class TokenizerModelEvaluator implements ModelEvaluator<TokenizerModel, TokenSample> {
    @Override
    @SneakyThrows
    public Tuple2<Double, String> evaluateModel(@NonNull TokenizerModel model, @NonNull Seq<TokenSample> samples) {
        var misclassified = new ByteArrayOutputStream();
        var evaluator = new TokenizerEvaluator(
                new TokenizerME(model),
                new TokenEvaluationErrorListener(misclassified));
        evaluator.evaluate(new SeqObjectStream<>(samples));
        return Tuple.of(evaluator.getFMeasure().getFMeasure(), new String(misclassified.toByteArray(), StandardCharsets.UTF_8));
    }
}
