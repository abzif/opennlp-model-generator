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
import io.vavr.control.Option;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ConlluParser {
    public ConlluSentence parse(@NonNull Seq<String> lines) {
        String sentenceText = lines
                .map(this::parseSentenceText)
                .filter(Option::isDefined)
                .map(Option::get)
                .getOrElse("");
        Seq<ConlluWordLine> words = lines
                .map(this::parseWordLine)
                .filter(Option::isDefined)
                .map(Option::get);

        return new ConlluSentence(sentenceText, postProcessWords(words));
    }

    private Option<String> parseSentenceText(String line) {
        // # text = <sentenceText>
        if (line.startsWith("#")) {
            String[] fragments = line.substring(1).split("=", 2);
            if (fragments.length == 2 && fragments[0].trim().equals("text")) {
                return Option.of(fragments[1].trim());
            }
        }
        return Option.none();
    }

    private Option<ConlluWordLine> parseWordLine(String line) {
        String[] fragments = line.split("\\t");
        if (fragments.length >= 4 && fragments[0].matches("[0-9]+")) {
            // single word line: <id> <form> <lemma> <posTag>
            return Option.of(new ConlluWordLine(
                    Integer.parseInt(fragments[0]),
                    fragments[1],
                    fragments[2],
                    fragments[3]));
        } else if (fragments.length >= 2 && fragments[0].matches("[0-9]+\\-[0-9]+")) {
            // compound word line: <startId>-<endId> <form>
            String[] ids = fragments[0].split("\\-");
            return Option.of(new ConlluWordLine(
                    Integer.parseInt(ids[0]),
                    Integer.parseInt(ids[1]),
                    fragments[1],
                    Vector.empty()));
        }
        return Option.none();
    }

    private Seq<ConlluWordLine> postProcessWords(Seq<ConlluWordLine> words) {
        return words.foldLeft(Vector.empty(), this::combineWord);
    }

    private Seq<ConlluWordLine> combineWord(Seq<ConlluWordLine> words, ConlluWordLine word) {
        if (words.isEmpty()) {
            return Vector.of(word);
        }
        ConlluWordLine lastWord = words.last();
        if (lastWord.isCompound() && !word.isCompound() && lastWord.getStartId() <= word.getStartId() && word.getStartId() <= lastWord.getEndId()) {
            // last word is compound and current word should be added as sub-word
            return words.update(words.size() - 1, new ConlluWordLine(lastWord.getStartId(), lastWord.getEndId(), lastWord.getForm(), lastWord.getSubWords().append(word)));
        } else {
            return words.append(word);
        }
    }
}
