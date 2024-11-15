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
import opennlpmodgen.tools.conllu.parser.ConlluWordLine;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConlluDefaultTransformerTest {
    private ConlluDefaultTransformer transformer = new ConlluDefaultTransformer();

    @Test
    public void supportsLanguage() {
        assertThat(transformer.supportsLanguage("en")).isEqualTo(true);
        assertThat(transformer.supportsLanguage("xy")).isEqualTo(true);
    }

    @Test
    public void transformNotSplittedWord() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "abc-xyz", "a", "NOUN"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "NOUN"),
                        new ConlluWordLine(3, "b2", "b2", "NOUN"))),
                new ConlluWordLine(4, "c", "c", "V")));

        var actual = transformer.transform(sentence, "xy");

        assertThat(actual).isEqualTo(new ConlluSentence("", Vector.of()));
    }

    @Test
    public void transformNotSplittedSubWord() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "a", "a", "NOUN"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "NOUN"),
                        new ConlluWordLine(3, "abc-xyz", "b2", "NOUN"))),
                new ConlluWordLine(4, "c", "c", "V")));

        var actual = transformer.transform(sentence, "xy");

        assertThat(actual).isEqualTo(new ConlluSentence("", Vector.of()));
    }

    @Test
    public void transformSingleHyphen() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "-", "a", "NOUN"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "NOUN"),
                        new ConlluWordLine(3, "-", "b2", "NOUN"))),
                new ConlluWordLine(4, "c", "c", "V")));

        var actual = transformer.transform(sentence, "xy");

        assertThat(actual).isEqualTo(sentence);
    }
}
