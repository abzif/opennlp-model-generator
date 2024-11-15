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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class ConlluDeTransformerTest {
    private ConlluDeTransformer transformer = new ConlluDeTransformer();

    @Test
    public void supportsLanguage() {
        assertThat(transformer.supportsLanguage("de")).isEqualTo(true);
        assertThat(transformer.supportsLanguage("xy")).isEqualTo(false);
    }

    @Test
    public void transformNoun() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "ausbildung", "bildung", "NOUN"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "NOUN"),
                        new ConlluWordLine(3, "b2", "b2", "NOUN"))),
                new ConlluWordLine(4, "c", "c", "V")));

        var actual = transformer.transform(sentence, "de");

        assertThat(actual).isEqualTo(new ConlluSentence("", Vector.of()));
    }

    @Test
    public void transformSubNoun() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "a", "a", "NOUN"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "NOUN"),
                        new ConlluWordLine(3, "herrschaft", "schaft", "NOUN"))),
                new ConlluWordLine(4, "c", "c", "V")));

        var actual = transformer.transform(sentence, "de");

        assertThat(actual).isEqualTo(new ConlluSentence("", Vector.of()));
    }

    @ParameterizedTest
    @CsvSource({
            "'abc .',             'abc.'",
            "'xyz !',             'xyz!'",
            "'abc : def ; ghi ?', 'abc: def; ghi?'",
            "'abc , def',         'abc, def'",
            "'abc ( def ) ghi',   'abc (def) ghi'",
            "'abc \" def \" ghi', 'abc \"def\" ghi'",
            "'\" abc def . \"',   '\"abc def.\"'",
            "abc 's,              abc's"})
    public void transformText(String originalText, String transformedText) {
        var sentence = new ConlluSentence(originalText, Vector.of());

        var actual = transformer.transform(sentence, "de");

        assertThat(actual).isEqualTo(new ConlluSentence(transformedText, Vector.of()));
    }

    @Test
    public void transformDer() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "die", "die", "D"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "N"),
                        new ConlluWordLine(3, "des", "des", "N"))),
                new ConlluWordLine(4, "dem", "dem", "V")));

        var actual = transformer.transform(sentence, "de");

        assertThat(actual).isEqualTo(new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "die", "der", "D"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "N"),
                        new ConlluWordLine(3, "des", "der", "N"))),
                new ConlluWordLine(4, "dem", "der", "V"))));
    }

    @Test
    public void transformSich() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "sich", "a", "D"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "N"),
                        new ConlluWordLine(3, "sich", "b2", "N"))),
                new ConlluWordLine(4, "sich", "c", "V")));

        var actual = transformer.transform(sentence, "de");

        assertThat(actual).isEqualTo(new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "sich", "sich", "D"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "N"),
                        new ConlluWordLine(3, "sich", "sich", "N"))),
                new ConlluWordLine(4, "sich", "sich", "V"))));
    }

    @Test
    public void transformZu() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "zum", "a", "D"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "N"),
                        new ConlluWordLine(3, "zur", "b2", "N"))),
                new ConlluWordLine(4, "c", "c", "V"),
                new ConlluWordLine(5, "zur", "d", "V")));

        var actual = transformer.transform(sentence, "de");

        assertThat(actual).isEqualTo(new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, 2, "zum", Vector.of(
                        new ConlluWordLine(1, "zu", "zu", "ADP"),
                        new ConlluWordLine(2, "dem", "der", "DET"))),
                new ConlluWordLine(3, 4, "b", Vector.of(
                        new ConlluWordLine(3, "b1", "b1", "N"),
                        new ConlluWordLine(4, "zur", "b2", "N"))),
                new ConlluWordLine(5, "c", "c", "V"),
                new ConlluWordLine(6, 7, "zur", Vector.of(
                        new ConlluWordLine(6, "zu", "zu", "ADP"),
                        new ConlluWordLine(7, "der", "der", "DET"))))));
    }

    @Test
    public void transformIn() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "im", "a", "D"),
                new ConlluWordLine(2, 3, "b", Vector.of(
                        new ConlluWordLine(2, "b1", "b1", "N"),
                        new ConlluWordLine(3, "ins", "b2", "N"))),
                new ConlluWordLine(4, "c", "c", "V"),
                new ConlluWordLine(5, "ins", "d", "V")));

        var actual = transformer.transform(sentence, "de");

        assertThat(actual).isEqualTo(new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, 2, "im", Vector.of(
                        new ConlluWordLine(1, "in", "in", "ADP"),
                        new ConlluWordLine(2, "dem", "der", "DET"))),
                new ConlluWordLine(3, 4, "b", Vector.of(
                        new ConlluWordLine(3, "b1", "b1", "N"),
                        new ConlluWordLine(4, "ins", "b2", "N"))),
                new ConlluWordLine(5, "c", "c", "V"),
                new ConlluWordLine(6, 7, "ins", Vector.of(
                        new ConlluWordLine(6, "in", "in", "ADP"),
                        new ConlluWordLine(7, "das", "der", "DET"))))));
    }
}
