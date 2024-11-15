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

import io.vavr.collection.Vector;
import opennlpmodgen.tools.util.TextNormalizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class EOSCharsSupplierTest {
    @Mock
    private TextNormalizer textNormalizer;
    @InjectMocks
    private EOSCharsSupplier supplier;

    @Test
    public void getEOSCharsJa() {
        given(textNormalizer.normalizeText(any(), any())).willReturn("1", "2", "！");

        var chars = supplier.getEOSChars("ja");

        verify(textNormalizer).normalizeText("。", "ja");
        verify(textNormalizer).normalizeText("？", "ja");
        verify(textNormalizer).normalizeText("！", "ja");
        verifyNoMoreInteractions(textNormalizer);
        assertThat(chars).isEqualTo(Vector.of('。', '？', '！', '1', '2'));
    }

    @Test
    public void getEOSCharsZh() {
        given(textNormalizer.normalizeText(any(), any())).willReturn("1", "2", "！");

        var chars = supplier.getEOSChars("zh");

        verify(textNormalizer).normalizeText("。", "zh");
        verify(textNormalizer).normalizeText("？", "zh");
        verify(textNormalizer).normalizeText("！", "zh");
        verifyNoMoreInteractions(textNormalizer);
        assertThat(chars).isEqualTo(Vector.of('。', '？', '！', '1', '2'));
    }

    @Test
    public void getEOSCharsTh() {
        given(textNormalizer.normalizeText(any(), any())).willReturn("1", "\n");

        var chars = supplier.getEOSChars("th");

        verify(textNormalizer).normalizeText(" ", "th");
        verify(textNormalizer).normalizeText("\n", "th");
        verifyNoMoreInteractions(textNormalizer);
        assertThat(chars).isEqualTo(Vector.of(' ', '\n', '1'));
    }

    @Test
    public void getEOSCharsDefault() {
        given(textNormalizer.normalizeText(any(), any())).willReturn("1", "2", "!");

        var chars = supplier.getEOSChars("en");

        verify(textNormalizer).normalizeText(".", "en");
        verify(textNormalizer).normalizeText("?", "en");
        verify(textNormalizer).normalizeText("!", "en");
        verifyNoMoreInteractions(textNormalizer);
        assertThat(chars).isEqualTo(Vector.of('.', '?', '!', '1', '2'));
    }
}
