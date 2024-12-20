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
package opennlpmodgen.tools.model.util;

import io.vavr.collection.TreeMap;
import io.vavr.collection.Vector;
import lombok.NonNull;
import lombok.SneakyThrows;
import opennlp.tools.util.model.BaseModel;
import opennlpmodgen.tools.util.PathUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class EvalReportPersister {
    @SneakyThrows
    public void writeEvaluationReport(
            double evaluationScore,
            int trainingSampleSize,
            int evaluationSampleSize,
            @NonNull String algorithm,
            @NonNull BaseModel model,
            @NonNull String misclassifiedDetails,
            @NonNull Path evalReportPath) {
        var evaluationProperties = Vector.of(String.format("Evaluation-Score=%s", evaluationScore),
                String.format("Training-Sample-Size=%s", trainingSampleSize),
                String.format("Evaluation-Sample-Size=%s", evaluationSampleSize),
                String.format("Training-Algorithm=%s", algorithm));
        var modelProperties = TreeMap.ofAll(model.getArtifact("manifest.properties")).map(e -> String.format("%s=%s", e._1, e._2));
        var misclassifiedTxt = Vector.of(misclassifiedDetails.split("\\R"));
        var reportTxt = Vector.of("=== EVALUATION INFO ===")
                .appendAll(evaluationProperties)
                .append("")
                .append("=== MODEL PROPERTIES ===")
                .appendAll(modelProperties)
                .append("")
                .append("=== MISCLASSIFIED DETAILS ===")
                .appendAll(misclassifiedTxt)
                .mkString("\n");
        PathUtils.createParentDirectories(evalReportPath);
        Files.writeString(evalReportPath, reportTxt);
    }
}
