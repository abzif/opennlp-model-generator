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

import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import opennlpmodgen.tools.util.WebClient;
import opennlpmodgen.tools.util.WebResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class UniDepURLSupplierTest {
    @Mock
    private WebClient webClient;
    @InjectMocks
    private UniDepURLSupplier supplier;

    @Test
    public void getTreebankURLIncorrectMainPage() throws Exception {
        given(webClient.makeGetRequest(any())).willReturn(
                new WebResponse(new URL("http://host/path"), HashMap.empty(), Option.none()));

        assertThatThrownBy(() -> supplier.getTreebankURL());
    }

    @Test
    public void getTreebankURLIncorrectDownloadPage() throws Exception {
        given(webClient.makeGetRequest(any())).willReturn(
                new WebResponse(new URL("http://host1/path1"), HashMap.empty(), Option.some(copyClassPathResourceAsBytes("uni-dep-main.html"))),
                new WebResponse(new URL("http://host2/path2"), HashMap.empty(), Option.none()));

        assertThatThrownBy(() -> supplier.getTreebankURL());
    }

    @Test
    public void getTreebankURL() throws Exception {
        given(webClient.makeGetRequest(any())).willReturn(
                new WebResponse(new URL("http://host1/path1"), HashMap.empty(), Option.some(copyClassPathResourceAsBytes("uni-dep-main.html"))),
                new WebResponse(new URL("http://host2/path2"), HashMap.empty(), Option.some(copyClassPathResourceAsBytes("uni-dep-download.html"))));

        var url = supplier.getTreebankURL();

        verify(webClient).makeGetRequest(new URL("http://universaldependencies.org"));
        verify(webClient).makeGetRequest(new URL("http://hdl.handle.net/11234/1-4611"));
        verifyNoMoreInteractions(webClient);
        assertThat(url).isEqualTo(new URL("http://host2/repository/xmlui/bitstream/handle/11234/1-4611/ud-treebanks-v2.9.tgz?sequence=1&isAllowed=y"));
    }

    private byte[] copyClassPathResourceAsBytes(String path) throws Exception {
        return FileCopyUtils.copyToByteArray(new ClassPathResource(path, getClass()).getInputStream());
    }
}
