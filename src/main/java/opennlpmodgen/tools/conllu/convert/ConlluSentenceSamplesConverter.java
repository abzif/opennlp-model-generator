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

import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.util.Span;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.util.ConlluValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConlluSentenceSamplesConverter implements ConlluSamplesConverter<SentenceSample> {
    @NonNull
    private final ConlluValidator validator;

    @Override
    public Seq<SentenceSample> convert(@NonNull Seq<ConlluSentence> sentences, @NonNull String language) {
        var validatedSentences = sentences.filter(validator::isValidForTokenization);
        var groupedSentences = Vector.ofAll(validatedSentences.sliding(10, 10));
        return groupedSentences.map(this::convert);
    }

    private SentenceSample convert(Seq<ConlluSentence> sentences) {
        return sentences.foldLeft(new SentenceSample(""), this::convert);
    }

    private SentenceSample convert(SentenceSample prevSample, ConlluSentence sentence) {
        var prevText = prevSample.getDocument();
        var prevSpans = Vector.of(prevSample.getSentences());
        var currText = sentence.getText();
        if (prevText.isEmpty()) {
            return new SentenceSample(currText, new Span(0, currText.length()));
        } else {
            var text = prevText + " " + currText;
            var lastSpan = prevSpans.last();
            var start = lastSpan.getEnd() + 1;
            var end = start + currText.length();
            var spans = prevSpans.append(new Span(start, end)).toJavaArray(Span[]::new);
            return new SentenceSample(text, spans);
        }
    }
}
