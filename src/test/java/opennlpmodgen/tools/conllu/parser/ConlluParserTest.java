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
package opennlpmodgen.tools.conllu.parser;

import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConlluParserTest {
    private ConlluParser parser = new ConlluParser();

    @Test
    public void parseSentenceIncorrectFormat() {
        Seq<String> lines = Vector.of("# comment",
                "# id = 1",
                "# text = This sentence has an incorrect format.",
                "Line too short",
                "A\tLine\tdoes\tnot start with a number",
                "1-2\tCompound word without sub-words",
                "3-4\tCompound word with sub-word out of range",
                "2\tw2\tl2\tADV",
                "5-6\tCompound word with sub-word out of range",
                "7\tw7\tl7\tNOUN",
                "8\t.\tl8\tPUNCT");

        ConlluSentence sentence = parser.parse(lines);

        Assertions.assertThat(sentence).isEqualTo(
                new ConlluSentence("This sentence has an incorrect format.", Vector.of(
                        new ConlluWordLine(1, 2, "Compound word without sub-words", Vector.of()),
                        new ConlluWordLine(3, 4, "Compound word with sub-word out of range", Vector.of()),
                        new ConlluWordLine(2, "w2", "l2", "ADV"),
                        new ConlluWordLine(5, 6, "Compound word with sub-word out of range", Vector.of()),
                        new ConlluWordLine(7, "w7", "l7", "NOUN"),
                        new ConlluWordLine(8, ".", "l8", "PUNCT"))));
    }

    @Test
    public void parseSentenceWithoutCompoundWords() {
        Seq<String> lines = Vector.of("# text = Some example sentence = XYZ.",
                "1\tSome\tl1\tADP",
                "2\texample\tl2\tADV",
                "3\tsentence\tl3\tNOUN",
                "4\t.\tl4\tPUNCT");

        ConlluSentence sentence = parser.parse(lines);

        Assertions.assertThat(sentence).isEqualTo(
                new ConlluSentence("Some example sentence = XYZ.", Vector.of(
                        new ConlluWordLine(1, "Some", "l1", "ADP"),
                        new ConlluWordLine(2, "example", "l2", "ADV"),
                        new ConlluWordLine(3, "sentence", "l3", "NOUN"),
                        new ConlluWordLine(4, ".", "l4", "PUNCT"))));
    }

    @Test
    public void parseSentenceWithCompoundWords() {
        Seq<String> lines = Vector.of("# text = Some example sentence = XYZ.",
                "1-2\tSome",
                "1\tSo\tl1\tADJ",
                "2\tme\tl2\tADV",
                "3\texample\tl3\tVERB",
                "4-6\tsentence",
                "4\tsen\tl4\tNOUN",
                "5\tten\tl5\tPROPN",
                "6\tce\tl6\tAUX",
                "7\t.\tl7\tPUNCT");

        ConlluSentence sentence = parser.parse(lines);

        Assertions.assertThat(sentence).isEqualTo(
                new ConlluSentence("Some example sentence = XYZ.", Vector.of(
                        new ConlluWordLine(1, 2, "Some", Vector.of(
                                new ConlluWordLine(1, "So", "l1", "ADJ"),
                                new ConlluWordLine(2, "me", "l2", "ADV"))),
                        new ConlluWordLine(3, "example", "l3", "VERB"),
                        new ConlluWordLine(4, 6, "sentence", Vector.of(
                                new ConlluWordLine(4, "sen", "l4", "NOUN"),
                                new ConlluWordLine(5, "ten", "l5", "PROPN"),
                                new ConlluWordLine(6, "ce", "l6", "AUX"))),
                        new ConlluWordLine(7, ".", "l7", "PUNCT"))));
    }
}
