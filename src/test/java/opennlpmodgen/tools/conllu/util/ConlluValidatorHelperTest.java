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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConlluValidatorHelperTest {
    private ConlluValidatorHelper validator = new ConlluValidatorHelper();

    @Test
    public void validateEmpty() {
        var sentence = new ConlluSentence("", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN")));
        assertThat(validator.notEmpty(sentence)).isFalse();

        sentence = new ConlluSentence("xyz", Vector.of());
        assertThat(validator.notEmpty(sentence)).isFalse();

        sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN")));
        assertThat(validator.notEmpty(sentence)).isTrue();
    }

    @Test
    public void validateForm() {
        var sentence = new ConlluSentence("xyz", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "", "l2", "VERB")));
        assertThat(validator.formsValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz a b", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "a b", "l2", "VERB")));
        assertThat(validator.formsValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz _", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "_", "l2", "VERB")));
        assertThat(validator.formsValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz _", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, 3, "_", Vector.of(
                        new ConlluWordLine(2, "ab", "l2", "ADJ"),
                        new ConlluWordLine(3, "c", "l3", "ADV")))));
        assertThat(validator.formsValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, 3, "abc", Vector.of(
                        new ConlluWordLine(2, "_", "l2", "ADJ"),
                        new ConlluWordLine(3, "c", "l3", "ADV")))));
        assertThat(validator.formsValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, 3, "abc", Vector.of(
                        new ConlluWordLine(2, "ab", "l2", "ADJ"),
                        new ConlluWordLine(3, "c", "l3", "ADV")))));
        assertThat(validator.formsValid(sentence)).isTrue();
    }

    @Test
    public void validateLemma() {
        var sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "", "VERB")));
        assertThat(validator.lemmasValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "l 2", "VERB")));
        assertThat(validator.lemmasValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "_", "VERB")));
        assertThat(validator.lemmasValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, 3, "abc", Vector.of(
                        new ConlluWordLine(2, "ab", "_", "ADJ"),
                        new ConlluWordLine(3, "c", "l3", "ADV")))));
        assertThat(validator.lemmasValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, 3, "abc", Vector.of(
                        new ConlluWordLine(2, "ab", "l2", "ADJ"),
                        new ConlluWordLine(3, "c", "l3", "ADV")))));
        assertThat(validator.lemmasValid(sentence)).isTrue();
    }

    @Test
    public void validatePosTag() {
        var sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "l2", "")));
        assertThat(validator.posTagsValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "l2", "AAAAA")));
        assertThat(validator.posTagsValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, 3, "abc", Vector.of(
                        new ConlluWordLine(2, "ab", "l2", "ADJ"),
                        new ConlluWordLine(3, "c", "l3", "BBBBBB")))));
        assertThat(validator.posTagsValid(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, 3, "abc", Vector.of(
                        new ConlluWordLine(2, "ab", "l2", "ADJ"),
                        new ConlluWordLine(3, "c", "l3", "VERB")))));
        assertThat(validator.posTagsValid(sentence)).isTrue();
    }

    @Test
    public void validateTextEqualsConcatenatedForms() {
        var sentence = new ConlluSentence(" xyz abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "l2", "VERB")));
        assertThat(validator.textEqualsConcatenatedForms(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc ", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "l2", "VERB")));
        assertThat(validator.textEqualsConcatenatedForms(sentence)).isFalse();

        sentence = new ConlluSentence("xy abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "l2", "VERB")));
        assertThat(validator.textEqualsConcatenatedForms(sentence)).isFalse();

        sentence = new ConlluSentence("xyz ac", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "l2", "VERB")));
        assertThat(validator.textEqualsConcatenatedForms(sentence)).isFalse();

        sentence = new ConlluSentence("xyz  abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "l2", "VERB")));
        assertThat(validator.textEqualsConcatenatedForms(sentence)).isFalse();

        sentence = new ConlluSentence("xyz sss abc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "l2", "VERB")));
        assertThat(validator.textEqualsConcatenatedForms(sentence)).isFalse();

        sentence = new ConlluSentence("xyz abc xyzabc", Vector.of(
                new ConlluWordLine(1, "xyz", "l1", "NOUN"),
                new ConlluWordLine(2, "abc", "l2", "VERB"),
                new ConlluWordLine(3, "xyz", "l3", "NOUN"),
                new ConlluWordLine(4, "abc", "l4", "VERB")));
        assertThat(validator.textEqualsConcatenatedForms(sentence)).isTrue();
    }
}
