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
import opennlp.tools.cmdline.postag.POSEvaluationErrorListener;
import opennlp.tools.postag.POSEvaluator;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlpmodgen.tools.model.util.SeqObjectStream;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class POSModelEvaluator implements ModelEvaluator<POSModel, POSSample> {
    @Override
    @SneakyThrows
    public Tuple2<Double, String> evaluateModel(@NonNull POSModel model, @NonNull Seq<POSSample> samples) {
        var misclassified = new ByteArrayOutputStream();
        var evaluator = new POSEvaluator(
                new POSTaggerME(model),
                new POSEvaluationErrorListener(misclassified));
        evaluator.evaluate(new SeqObjectStream<>(samples));
        return Tuple.of(evaluator.getWordAccuracy(), new String(misclassified.toByteArray(), StandardCharsets.UTF_8));
    }
}
