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
package opennlpmodgen.tools.model;

import lombok.NonNull;
import opennlp.tools.lemmatizer.LemmaSample;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlpmodgen.tools.model.train.LemmatizerModelEvaluator;
import opennlpmodgen.tools.model.train.LemmatizerModelTrainer;
import opennlpmodgen.tools.model.util.EvalReportPersister;
import opennlpmodgen.tools.model.util.ModelPersister;
import opennlpmodgen.tools.model.util.SampleSplitter;
import org.springframework.stereotype.Component;

@Component
public class LemmatizerModelProcessor extends ModelProcessor<LemmatizerModel, LemmaSample> {
    public LemmatizerModelProcessor(
            @NonNull SampleSplitter splitter,
            @NonNull LemmatizerModelTrainer trainer,
            @NonNull LemmatizerModelEvaluator evaluator,
            @NonNull ModelPersister modelPersister,
            @NonNull EvalReportPersister evalReportPersister) {
        super(splitter, trainer, evaluator, modelPersister, evalReportPersister);
    }
}
