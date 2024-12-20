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
package opennlpmodgen.tools.conllu.convert;

import io.vavr.collection.Vector;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.util.Span;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.util.ConlluValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConlluSentenceSamplesConverterTest {
    @Mock
    private ConlluValidator validator;
    @InjectMocks
    private ConlluSentenceSamplesConverter converter;

    @Test
    public void convert() {
        var sentences = Vector.of(
                new ConlluSentence("n1", Vector.empty()),
                new ConlluSentence("n2", Vector.empty()),
                new ConlluSentence("n3", Vector.empty()),
                new ConlluSentence("n4", Vector.empty()),
                new ConlluSentence("n5", Vector.empty()),
                new ConlluSentence("n6", Vector.empty()),
                new ConlluSentence("n7", Vector.empty()),
                new ConlluSentence("n8", Vector.empty()),
                new ConlluSentence("n9", Vector.empty()),
                new ConlluSentence("n10", Vector.empty()),
                new ConlluSentence("n11", Vector.empty()),
                new ConlluSentence("n12", Vector.empty()),
                new ConlluSentence("n13", Vector.empty()));
        given(validator.isValidForTokenization(any())).willReturn(true);

        var sample = converter.convert(sentences, "lx");

        verify(validator, times(13)).isValidForTokenization(any());
        verifyNoMoreInteractions(validator);
        assertThat(sample).isEqualTo(Vector.of(
                new SentenceSample("n1 n2 n3 n4 n5 n6 n7 n8 n9 n10",
                        new Span(0, 2), new Span(3, 5), new Span(6, 8), new Span(9, 11), new Span(12, 14),
                        new Span(15, 17), new Span(18, 20), new Span(21, 23), new Span(24, 26), new Span(27, 30)),
                new SentenceSample("n11 n12 n13",
                        new Span(0, 3), new Span(4, 7), new Span(8, 11))));
    }

    @Test
    public void convertIncorrect() {
        var sentence1 = new ConlluSentence("1", Vector.empty());
        var sentence2 = new ConlluSentence("2", Vector.empty());
        given(validator.isValidForTokenization(any())).willReturn(false);

        var samples = converter.convert(Vector.of(sentence1, sentence2), "lx");

        verify(validator).isValidForTokenization(sentence1);
        verify(validator).isValidForTokenization(sentence2);
        verifyNoMoreInteractions(validator);
        assertThat(samples).isEqualTo(Vector.empty());
    }
}
