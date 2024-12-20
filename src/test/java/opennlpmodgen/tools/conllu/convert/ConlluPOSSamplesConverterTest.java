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

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import opennlp.tools.postag.POSSample;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.parser.ConlluWordLine;
import opennlpmodgen.tools.conllu.util.ConlluValidator;
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
public class ConlluPOSSamplesConverterTest {
    @Mock
    private ConlluValidator validator;
    @InjectMocks
    private ConlluPOSSamplesConverter converter;

    @Test
    public void convert() {
        var sentence = new ConlluSentence("Some example sentence ten example.", Vector.of(
                new ConlluWordLine(1, 2, "Some", Vector.of(
                        new ConlluWordLine(1, "Somt", "l1", "ADJ"),
                        new ConlluWordLine(2, "mex", "l2", "ADV"))),
                new ConlluWordLine(3, "example", "l3", "VERB"),
                new ConlluWordLine(4, 6, "sentence", Vector.of(
                        new ConlluWordLine(4, "sen", "l4", "NOUN"),
                        new ConlluWordLine(5, "ten", "l5", "PROPN"),
                        new ConlluWordLine(6, "ce", "l6", "AUX"))),
                new ConlluWordLine(7, "ten", "l7", "NOUN"),
                new ConlluWordLine(8, "example", "l8", "VERB"),
                new ConlluWordLine(9, ".", "l9", "PUNCT")));
        given(validator.isValidForLemmatization(any())).willReturn(true);

        var samples = converter.convert(Vector.of(sentence), "lx");

        verify(validator).isValidForLemmatization(sentence);
        verifyNoMoreInteractions(validator);
        assertThat(samples).isEqualTo(Vector.of(createSample(Vector.of(
                Tuple.of("Somt mex", "ADJ ADV"),
                Tuple.of("example", "VERB"),
                Tuple.of("sen", "NOUN"),
                Tuple.of("ten", "PROPN"),
                Tuple.of("ce", "AUX"),
                Tuple.of("ten", "NOUN"),
                Tuple.of("example", "VERB"),
                Tuple.of(".", "PUNCT")))));
    }

    @Test
    public void convertIncorrect() {
        var sentence1 = new ConlluSentence("1", Vector.empty());
        var sentence2 = new ConlluSentence("2", Vector.empty());
        given(validator.isValidForLemmatization(any())).willReturn(false);

        var samples = converter.convert(Vector.of(sentence1, sentence2), "lx");

        verify(validator).isValidForLemmatization(sentence1);
        verify(validator).isValidForLemmatization(sentence2);
        verifyNoMoreInteractions(validator);
        assertThat(samples).isEqualTo(Vector.empty());
    }

    private POSSample createSample(Seq<Tuple2<String, String>> posTags) {
        return new POSSample(
                posTags.map(Tuple2::_1).toJavaList(),
                posTags.map(Tuple2::_2).toJavaList());
    }
}
