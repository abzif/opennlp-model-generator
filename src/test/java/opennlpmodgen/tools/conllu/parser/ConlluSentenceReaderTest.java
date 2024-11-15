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

import com.google.common.jimfs.Jimfs;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import opennlpmodgen.tools.conllu.transformer.ConlluTransformers;
import opennlpmodgen.tools.conllu.util.ConlluNormalizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class ConlluSentenceReaderTest {
    @Mock
    private ConlluParser parser;
    @Mock
    private ConlluTransformers transformers;
    @Mock
    private ConlluNormalizer normalizer;
    @InjectMocks
    private ConlluSentenceReader reader;

    @Test
    public void readSentences() throws Exception {
        Path conlluPath = Jimfs.newFileSystem().getPath("file.txt");
        Files.writeString(conlluPath, Vector.of(
                "# t1",
                "a",
                "",
                "",
                "# t2",
                "b",
                "",
                "# t3").mkString("\n"));
        given(parser.parse(any())).willReturn(
                new ConlluSentence("s1", Vector.empty()),
                new ConlluSentence("s2", Vector.empty()),
                new ConlluSentence("s3", Vector.empty()));
        given(transformers.transformSentence(any(), any())).willReturn(
                new ConlluSentence("s1-t", Vector.empty()),
                new ConlluSentence("s2-t", Vector.empty()),
                new ConlluSentence("s3-t", Vector.empty()));
        given(normalizer.normalizeSentence(any(), any())).willReturn(
                new ConlluSentence("s1-n", Vector.empty()),
                new ConlluSentence("s2-t", Vector.empty()),
                new ConlluSentence("s3-t", Vector.empty()));

        Seq<ConlluSentence> sentences = reader.readSentences(conlluPath, "x");

        verify(parser).parse(Vector.of("# t1", "a"));
        verify(transformers).transformSentence(new ConlluSentence("s1", Vector.empty()), "x");
        verify(normalizer).normalizeSentence(new ConlluSentence("s1-t", Vector.empty()), "x");
        verify(parser).parse(Vector.of("# t2", "b"));
        verify(transformers).transformSentence(new ConlluSentence("s2", Vector.empty()), "x");
        verify(normalizer).normalizeSentence(new ConlluSentence("s2-t", Vector.empty()), "x");
        verify(parser).parse(Vector.of("# t3"));
        verify(transformers).transformSentence(new ConlluSentence("s3", Vector.empty()), "x");
        verify(normalizer).normalizeSentence(new ConlluSentence("s3-t", Vector.empty()), "x");
        verifyNoMoreInteractions(parser, transformers, normalizer);
        assertThat(sentences).isEqualTo(Vector.of(
                new ConlluSentence("s1-t", Vector.empty()),
                new ConlluSentence("s2-t", Vector.empty()),
                new ConlluSentence("s3-t", Vector.empty()),
                new ConlluSentence("s1-n", Vector.empty())));
    }
}
