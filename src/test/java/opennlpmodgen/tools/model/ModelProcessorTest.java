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

import com.google.common.jimfs.Jimfs;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import opennlp.tools.util.model.BaseModel;
import opennlpmodgen.tools.model.train.ModelEvaluator;
import opennlpmodgen.tools.model.train.ModelTrainer;
import opennlpmodgen.tools.model.util.EvalReportPersister;
import opennlpmodgen.tools.model.util.ModelPersister;
import opennlpmodgen.tools.model.util.SampleSplitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModelProcessorTest {
    @Mock
    private SampleSplitter splitter;
    @Mock
    private ModelTrainer<BaseModel, String> trainer;
    @Mock
    private ModelEvaluator<BaseModel, String> evaluator;
    @Mock
    private ModelPersister modelPersister;
    @Mock
    private EvalReportPersister evalReportPersister;
    @InjectMocks
    private ModelProcessor processor;

    @Test
    public void processModel() throws Exception {
        Seq<String> algorithms = Vector.of("a", "b", "c");
        String language = "lx";
        Path rootPath = Jimfs.newFileSystem().getPath("root");
        Path modelPath = rootPath.resolve("model.bin");
        Path reportPath = rootPath.resolve("report.txt");
        Seq<String> samples = Vector.of("a", "b", "c");
        Seq<String> trainSamples = Vector.of("a", "b");
        Seq<String> evalSamples = Vector.of("c");
        given(splitter.<String>splitSamples(any())).willReturn(Tuple.of(trainSamples, evalSamples));
        BaseModel modelA = mock(BaseModel.class);
        BaseModel modelB = mock(BaseModel.class);
        BaseModel modelC = mock(BaseModel.class);
        given(trainer.trainModel(any(), any(), any())).willReturn(Option.some(modelA), Option.some(modelB), Option.some(modelC));
        Tuple2<Double, String> evalInfoA = Tuple.of(0.1, "ma");
        Tuple2<Double, String> evalInfoB = Tuple.of(0.9, "mb");
        Tuple2<Double, String> evalInfoC = Tuple.of(0.9, "mc");
        given(evaluator.evaluateModel(any(), any())).willReturn(evalInfoA, evalInfoB, evalInfoC);

        processor.processModel(samples, algorithms, language, modelPath, reportPath);

        verify(splitter).splitSamples(samples);
        verify(trainer).trainModel("a", language, trainSamples);
        verify(evaluator).evaluateModel(modelA, evalSamples);
        verify(trainer).trainModel("b", language, trainSamples);
        verify(evaluator).evaluateModel(modelB, evalSamples);
        verify(trainer).trainModel("c", language, trainSamples);
        verify(evaluator).evaluateModel(modelC, evalSamples);
        verify(modelPersister).writeModel(modelB, modelPath);
        verify(evalReportPersister).writeEvaluationReport(0.9, 2, 1, "b", modelB, "mb", reportPath);
        verifyNoMoreInteractions(splitter, trainer, evaluator, modelPersister, evalReportPersister);
    }

    @Test
    public void processModelNoData() throws Exception {
        Seq<String> algorithms = Vector.of("a", "b", "c");
        String language = "lx";
        Path rootPath = Jimfs.newFileSystem().getPath("root");
        Path modelPath = rootPath.resolve("model.bin");
        Path reportPath = rootPath.resolve("report.txt");
        Seq<String> samples = Vector.of("a", "b", "c");
        Seq<String> trainSamples = Vector.of("a", "b");
        Seq<String> evalSamples = Vector.of("c");
        given(splitter.<String>splitSamples(any())).willReturn(Tuple.of(trainSamples, evalSamples));
        given(trainer.trainModel(any(), any(), any())).willReturn(Option.none());

        processor.processModel(samples, algorithms, language, modelPath, reportPath);

        verify(splitter).splitSamples(samples);
        verify(trainer).trainModel("a", language, trainSamples);
        verify(trainer).trainModel("b", language, trainSamples);
        verify(trainer).trainModel("c", language, trainSamples);
        verifyNoMoreInteractions(splitter, trainer, evaluator, modelPersister, evalReportPersister);
    }
}
