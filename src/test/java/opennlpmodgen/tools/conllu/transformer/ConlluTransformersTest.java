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
package opennlpmodgen.tools.conllu.transformer;

import io.vavr.collection.Vector;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class ConlluTransformersTest {
    @Mock
    private ConlluTransformer transformer1;
    @Mock
    private ConlluTransformer transformer2;
    @Mock
    private ConlluTransformer transformer3;
    private ConlluTransformers transformers;

    @BeforeEach
    public void setUp() {
        transformers = new ConlluTransformers(transformer1, transformer2, transformer3);
    }

    @Test
    public void transformSentence() {
        given(transformer1.supportsLanguage(any())).willReturn(true);
        given(transformer1.transform(any(), any())).willReturn(new ConlluSentence("s-t1", Vector.of()));
        given(transformer2.supportsLanguage(any())).willReturn(false);
        given(transformer3.supportsLanguage(any())).willReturn(true);
        given(transformer3.transform(any(), any())).willReturn(new ConlluSentence("s-t3", Vector.of()));

        var actual = transformers.transformSentence(new ConlluSentence("s", Vector.of()), "lx");

        verify(transformer1).supportsLanguage("lx");
        verify(transformer1).transform(new ConlluSentence("s", Vector.of()), "lx");
        verify(transformer2).supportsLanguage("lx");
        verify(transformer3).supportsLanguage("lx");
        verify(transformer3).transform(new ConlluSentence("s-t1", Vector.of()), "lx");
        verifyNoMoreInteractions(transformer1, transformer2, transformer3);
        assertThat(actual).isEqualTo(new ConlluSentence("s-t3", Vector.of()));
    }
}
