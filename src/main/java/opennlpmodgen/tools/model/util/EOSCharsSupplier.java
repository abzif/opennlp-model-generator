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
package opennlpmodgen.tools.model.util;

import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import opennlpmodgen.tools.util.TextNormalizer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EOSCharsSupplier {
    @NonNull
    private final TextNormalizer textNormalizer;

    private static final Seq<Character> JA_ZH_EOS_CHARS = Vector.of('。', '？', '！');
    private static final Seq<Character> TH_EOS_CHARS = Vector.of(' ', '\n');
    private static final Seq<Character> DEFAULT_EOS_CHARS = Vector.of('.', '?', '!');

    public Seq<Character> getEOSChars(@NonNull String language) {
        if ("ja".equals(language) || "zh".equals(language)) {
            return getOriginalAndNormalizedEOSChars(JA_ZH_EOS_CHARS, language);
        } else if ("th".equals(language)) {
            return getOriginalAndNormalizedEOSChars(TH_EOS_CHARS, language);
        } else {
            return getOriginalAndNormalizedEOSChars(DEFAULT_EOS_CHARS, language);
        }
    }

    private Seq<Character> getOriginalAndNormalizedEOSChars(Seq<Character> eosChars, String language) {
        return eosChars.appendAll(eosChars.map(ch -> normalizeChar(ch, language))).distinct();
    }

    private char normalizeChar(char ch, String language) {
        return textNormalizer.normalizeText(String.valueOf(ch), language).charAt(0);
    }
}
