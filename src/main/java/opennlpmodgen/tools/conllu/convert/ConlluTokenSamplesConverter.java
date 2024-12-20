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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.util.Span;
import opennlpmodgen.tools.conllu.parser.ConlluSentence;
import opennlpmodgen.tools.conllu.parser.ConlluWordLine;
import opennlpmodgen.tools.conllu.util.ConlluValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConlluTokenSamplesConverter implements ConlluSamplesConverter<TokenSample> {
    @NonNull
    private final ConlluValidator validator;

    @Override
    public Seq<TokenSample> convert(@NonNull Seq<ConlluSentence> sentences, @NonNull String language) {
        return sentences
                .filter(validator::isValidForTokenization)
                .map(ConlluSentence::flattenWords)
                .map(this::convert);
    }

    private TokenSample convert(ConlluSentence sentence) {
        var text = sentence.getText();
        var spans = createSpans(text, getForms(sentence.getWords()));
        // TokenSample.parse adds trailing space
        // don't know if it is really needed but simulate this behavior
        return new TokenSample(text + " ", spans.toJavaArray(size -> new Span[size]));
    }

    private Seq<String> getForms(Seq<ConlluWordLine> words) {
        return words.map(ConlluWordLine::getForm);
    }

    private Seq<Span> createSpans(String text, Seq<String> forms) {
        return forms.scanLeft(new Span(0, 0), (prevSpan, form) -> createSpan(text, prevSpan, form)).tail();
    }

    private Span createSpan(String text, Span prevSpan, String form) {
        int start = text.indexOf(form, prevSpan.getEnd());
        return new Span(start, start + form.length());
    }
}
