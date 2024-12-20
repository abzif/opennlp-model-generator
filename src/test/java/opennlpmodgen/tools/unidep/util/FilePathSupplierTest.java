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
package opennlpmodgen.tools.unidep.util;

import com.google.common.jimfs.Jimfs;
import opennlpmodgen.tools.util.ResourceDirectorySupplier;
import opennlpmodgen.tools.util.RootDirectorySupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FilePathSupplierTest {
    @Mock
    private RootDirectorySupplier rootDirectorySupplier;
    @Mock
    private ResourceDirectorySupplier resourceDirectorySupplier;
    @InjectMocks
    private FilePathSupplier supplier;

    @Test
    public void getFile() {
        var rootPath = Jimfs.newFileSystem().getPath("");
        given(rootDirectorySupplier.getRootDirectory()).willReturn(rootPath);
        var resPath = rootPath.resolve("res");
        given(resourceDirectorySupplier.getResourceDirectory(any())).willReturn(resPath);

        assertThat(supplier.getTreebankFile()).isEqualTo(rootPath.resolve("uni-dep-treebank.tgz"));
        assertThat(supplier.getConlluFile("lx")).isEqualTo(rootPath.resolve("lx.conllu"));
        assertThat(supplier.getSentenceModelFile("lx")).isEqualTo(resPath.resolve("lx-sentence-detector.onlpm"));
        assertThat(supplier.getSentenceReportFile("lx")).isEqualTo(resPath.resolve("lx-sentence-detector.txt"));
        assertThat(supplier.getTokenizerModelFile("lx")).isEqualTo(resPath.resolve("lx-tokenizer.onlpm"));
        assertThat(supplier.getTokenizerReportFile("lx")).isEqualTo(resPath.resolve("lx-tokenizer.txt"));
        assertThat(supplier.getPOSModelFile("lx")).isEqualTo(resPath.resolve("lx-pos-tagger.onlpm"));
        assertThat(supplier.getPOSReportFile("lx")).isEqualTo(resPath.resolve("lx-pos-tagger.txt"));
        assertThat(supplier.getLemmatizerModelFile("lx")).isEqualTo(resPath.resolve("lx-lemmatizer.onlpm"));
        assertThat(supplier.getLemmatizerReportFile("lx")).isEqualTo(resPath.resolve("lx-lemmatizer.txt"));
    }
}
