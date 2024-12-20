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
package opennlpmodgen.tools.util;

import com.google.common.jimfs.Jimfs;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class FileDownloaderTest {
    @Mock
    private WebClient webClient;
    @InjectMocks
    private FileDownloader downloader;

    @Test
    public void downloadFile() throws Exception {
        given(webClient.makeGetRequest(any())).willReturn(new WebResponse(
                new URL("http://host/path"),
                HashMap.empty(),
                Option.some("###content###".getBytes())));
        var outputPath = Jimfs.newFileSystem().getPath("some", "dir", "file.txt");

        downloader.downloadFile(new URL("http://host/p/file.txt"), outputPath);

        verify(webClient).makeGetRequest(new URL("http://host/p/file.txt"));
        verifyNoMoreInteractions(webClient);
        assertThat(outputPath).content().isEqualTo("###content###");
    }
}
