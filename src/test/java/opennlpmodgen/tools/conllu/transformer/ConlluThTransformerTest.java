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

public class ConlluThTransformerTest {
    private ConlluThTransformer transformer = new ConlluThTransformer();

    @Test
    public void supportsLanguage() {
        assertThat(transformer.supportsLanguage("th")).isEqualTo(true);
        assertThat(transformer.supportsLanguage("xy")).isEqualTo(false);
    }

    @Test
    public void transform() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "a", "_", "N"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "_", "N"),
                        new ConlluWordLine(3, "b2", "_", "N"))),
                new ConlluWordLine(4, "c", "lc", "V")));

        var actual = transformer.transform(sentence, "th");

        assertThat(actual).isEqualTo(new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "a", "a", "N"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "N"),
                        new ConlluWordLine(3, "b2", "b2", "N"))),
                new ConlluWordLine(4, "c", "lc", "V"))));
    }
}
