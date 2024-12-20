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
package opennlpmodgen.tools.conllu.util;

import io.vavr.collection.Vector;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.parser.ConlluWordLine;
import opennlpmodgen.tools.util.TextNormalizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConlluNormalizerTest {
    @Mock
    private TextNormalizer textNormalizer;
    @InjectMocks
    private ConlluNormalizer normalizer;

    @Test
    public void normalizeSentence() {
        given(textNormalizer.normalizeText(any(), any())).willAnswer(answer -> answer.getArgument(0).toString().replace("x", "X"));
        var sentence = new ConlluSentence("x sentence x", Vector.of(
                new ConlluWordLine(1, 2, "fx12", Vector.of(
                        new ConlluWordLine(1, "fx1", "lx1", "ADJ"),
                        new ConlluWordLine(2, "fx2", "lx2", "ADV"))),
                new ConlluWordLine(3, "fx3", "lx3", "VERB")));

        var actual = normalizer.normalizeSentence(sentence, "lx");

        verify(textNormalizer, times(8)).normalizeText(any(), eq("lx"));
        verifyNoMoreInteractions(textNormalizer);
        assertThat(actual).isEqualTo(new ConlluSentence("X sentence X", Vector.of(
                new ConlluWordLine(1, 2, "fX12", Vector.of(
                        new ConlluWordLine(1, "fX1", "lX1", "ADJ"),
                        new ConlluWordLine(2, "fX2", "lX2", "ADV"))),
                new ConlluWordLine(3, "fX3", "lX3", "VERB"))));
    }
}
