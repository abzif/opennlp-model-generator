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
package opennlpmodgen.tools.unidep;

import com.google.common.jimfs.Jimfs;
import opennlpmodgen.tools.unidep.util.UniDepConlluUncompressor;
import opennlpmodgen.tools.unidep.util.UniDepURLSupplier;
import opennlpmodgen.tools.util.FileDownloader;
import opennlpmodgen.tools.util.FileUpToDateChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class UniDepConlluDownloaderTest {
    @Mock
    private FileUpToDateChecker checker;
    @Mock
    private UniDepURLSupplier uniDepURLSupplier;
    @Mock
    private FileDownloader fileDownloader;
    @Mock
    private UniDepConlluUncompressor uniDepConlluUncompressor;
    @InjectMocks
    private UniDepConlluDownloader downloader;

    @Test
    public void downloadUniDepConlluFile_TreebankOutOfDate_ConlluOutOfDate() throws Exception {
        Path rootPath = Jimfs.newFileSystem().getPath("root");
        Path treebankPath = rootPath.resolve("t.tgz");
        Path conlluPath = rootPath.resolve("f.conllu");
        URL treebankURL = new URL("http://ud.org/treebank.tgz");
        given(uniDepURLSupplier.getTreebankURL()).willReturn(treebankURL);
        given(checker.isUpToDate(any(), any(URL.class))).willReturn(false);
        given(checker.isUpToDate(any(), any(Path.class))).willReturn(false);

        downloader.downloadUniDepConlluFile(treebankPath, "lx", conlluPath);

        verify(uniDepURLSupplier).getTreebankURL();
        verify(checker).isUpToDate(treebankPath, treebankURL);
        verify(fileDownloader).downloadFile(treebankURL, treebankPath);
        verify(checker).isUpToDate(conlluPath, treebankPath);
        verify(uniDepConlluUncompressor).uncompressConlluFile(treebankPath, "lx", conlluPath);
        verifyNoMoreInteractions(checker, uniDepURLSupplier, fileDownloader, uniDepConlluUncompressor);
    }

    @Test
    public void downloadUniDepConlluFile_TreebankUpToDate_ConlluOutOfDate() throws Exception {
        Path rootPath = Jimfs.newFileSystem().getPath("root");
        Path treebankPath = rootPath.resolve("t.tgz");
        Path conlluPath = rootPath.resolve("f.conllu");
        URL treebankURL = new URL("http://ud.org/treebank.tgz");
        given(uniDepURLSupplier.getTreebankURL()).willReturn(treebankURL);
        given(checker.isUpToDate(any(), any(URL.class))).willReturn(true);
        given(checker.isUpToDate(any(), any(Path.class))).willReturn(false);

        downloader.downloadUniDepConlluFile(treebankPath, "lx", conlluPath);

        verify(uniDepURLSupplier).getTreebankURL();
        verify(checker).isUpToDate(treebankPath, treebankURL);
        verify(checker).isUpToDate(conlluPath, treebankPath);
        verify(uniDepConlluUncompressor).uncompressConlluFile(treebankPath, "lx", conlluPath);
        verifyNoMoreInteractions(checker, uniDepURLSupplier, fileDownloader, uniDepConlluUncompressor);
    }

    @Test
    public void downloadUniDepConlluFile_TreebankUpToDate_ConlluUpToDate() throws Exception {
        Path rootPath = Jimfs.newFileSystem().getPath("root");
        Path treebankPath = rootPath.resolve("t.tgz");
        Path conlluPath = rootPath.resolve("f.conllu");
        URL treebankURL = new URL("http://ud.org/treebank.tgz");
        given(uniDepURLSupplier.getTreebankURL()).willReturn(treebankURL);
        given(checker.isUpToDate(any(), any(URL.class))).willReturn(true);
        given(checker.isUpToDate(any(), any(Path.class))).willReturn(true);

        downloader.downloadUniDepConlluFile(treebankPath, "lx", conlluPath);

        verify(uniDepURLSupplier).getTreebankURL();
        verify(checker).isUpToDate(treebankPath, treebankURL);
        verify(checker).isUpToDate(conlluPath, treebankPath);
        verifyNoMoreInteractions(checker, uniDepURLSupplier, fileDownloader, uniDepConlluUncompressor);
    }
}
