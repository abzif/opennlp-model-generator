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
package opennlpmodgen.tools;

import com.google.common.jimfs.Jimfs;
import io.vavr.collection.Vector;
import opennlpmodgen.tools.conllu.ConlluLemmatizerModelProcessor;
import opennlpmodgen.tools.conllu.ConlluPOSModelProcessor;
import opennlpmodgen.tools.conllu.ConlluSentenceModelProcessor;
import opennlpmodgen.tools.conllu.ConlluTokenizerModelProcessor;
import opennlpmodgen.tools.model.ModelAlgorithm;
import opennlpmodgen.tools.unidep.UniDepConlluDownloader;
import opennlpmodgen.tools.unidep.util.FilePathSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class MainProcessorTest {
    @Mock
    private FilePathSupplier fileSupplier;
    @Mock
    private UniDepConlluDownloader conlluFileDownloader;
    @Mock
    private ConlluSentenceModelProcessor sentenceModelProcessor;
    @Mock
    private ConlluTokenizerModelProcessor tokenizerModelProcessor;
    @Mock
    private ConlluPOSModelProcessor posModelProcessor;
    @Mock
    private ConlluLemmatizerModelProcessor lemmatizerModelProcessor;
    @InjectMocks
    private MainProcessor processor;

    @Test
    public void processUniDepConlluModel() {
        var rootPath = Jimfs.newFileSystem().getPath("");
        var treebankPath = rootPath.resolve("t.tgz");
        given(fileSupplier.getTreebankFile()).willReturn(treebankPath);
        var conlluPath = rootPath.resolve("lx.conllu");
        given(fileSupplier.getConlluFile(any())).willReturn(conlluPath);
        var sentenceModelPath = rootPath.resolve("lx-s.onlpm");
        given(fileSupplier.getSentenceModelFile(any())).willReturn(sentenceModelPath);
        var sentenceReportPath = rootPath.resolve("lx-s.txt");
        given(fileSupplier.getSentenceReportFile(any())).willReturn(sentenceReportPath);
        var tokenizerModelPath = rootPath.resolve("lx-t.onlpm");
        given(fileSupplier.getTokenizerModelFile(any())).willReturn(tokenizerModelPath);
        var tokenizerReportPath = rootPath.resolve("lx-t.txt");
        given(fileSupplier.getTokenizerReportFile(any())).willReturn(tokenizerReportPath);
        var posModelPath = rootPath.resolve("lx-p.onlpm");
        given(fileSupplier.getPOSModelFile(any())).willReturn(posModelPath);
        var posReportPath = rootPath.resolve("lx-p.txt");
        given(fileSupplier.getPOSReportFile(any())).willReturn(posReportPath);
        var lemmatizerModelPath = rootPath.resolve("lx-l.onlpm");
        given(fileSupplier.getLemmatizerModelFile(any())).willReturn(lemmatizerModelPath);
        var lemmatizerReportPath = rootPath.resolve("lx-l.txt");
        given(fileSupplier.getLemmatizerReportFile(any())).willReturn(lemmatizerReportPath);
        var tokenizerAlgorithms = Vector.of(ModelAlgorithm.MAXENT, ModelAlgorithm.MAXENT_QN, ModelAlgorithm.PERCEPTRON, ModelAlgorithm.NAIVE_BAYES);
        var posAlgorithms = Vector.of(ModelAlgorithm.MAXENT, ModelAlgorithm.PERCEPTRON, ModelAlgorithm.NAIVE_BAYES);
        var lemmatizerAlgorithms = Vector.of(ModelAlgorithm.MAXENT);

        processor.processUniDepConlluModel("lx");

        verify(fileSupplier).getTreebankFile();
        verify(fileSupplier).getConlluFile("lx");
        verify(fileSupplier).getSentenceModelFile("lx");
        verify(fileSupplier).getSentenceReportFile("lx");
        verify(fileSupplier).getTokenizerModelFile("lx");
        verify(fileSupplier).getTokenizerReportFile("lx");
        verify(fileSupplier).getPOSModelFile("lx");
        verify(fileSupplier).getPOSReportFile("lx");
        verify(fileSupplier).getLemmatizerModelFile("lx");
        verify(fileSupplier).getLemmatizerReportFile("lx");
        verify(conlluFileDownloader).downloadUniDepConlluFile(treebankPath, "lx", conlluPath);
        verify(sentenceModelProcessor).processConlluModel(conlluPath, tokenizerAlgorithms, "lx", sentenceModelPath, sentenceReportPath);
        verify(tokenizerModelProcessor).processConlluModel(conlluPath, tokenizerAlgorithms, "lx", tokenizerModelPath, tokenizerReportPath);
        verify(posModelProcessor).processConlluModel(conlluPath, posAlgorithms, "lx", posModelPath, posReportPath);
        verify(lemmatizerModelProcessor).processConlluModel(conlluPath, lemmatizerAlgorithms, "lx", lemmatizerModelPath, lemmatizerReportPath);
        verifyNoMoreInteractions(fileSupplier, conlluFileDownloader, sentenceModelProcessor, tokenizerModelProcessor, posModelProcessor, lemmatizerModelProcessor);
    }
}
