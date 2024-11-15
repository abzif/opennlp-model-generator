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
import lombok.NonNull;
import lombok.Value;

@Value
public class ConlluWordLine {
    private final int startId;
    private final int endId;
    private final String form;
    private final String lemma;
    private final String posTag;
    private final Seq<ConlluWordLine> subWords;

    public ConlluWordLine(int id, @NonNull String form, @NonNull String lemma, @NonNull String posTag) {
        this.startId = id;
        this.endId = id;
        this.form = form;
        this.lemma = lemma;
        this.posTag = posTag;
        this.subWords = Vector.empty();
    }

    public ConlluWordLine(int startId, int endId, @NonNull String form, @NonNull Seq<ConlluWordLine> subWords) {
        this.startId = startId;
        this.endId = endId;
        this.form = form;
        this.lemma = "";
        this.posTag = "";
        this.subWords = subWords;
    }

    public boolean isCompound() {
        return startId != endId;
    }

    public String joinSubForms() {
        return subWords.map(ConlluWordLine::getForm).mkString(" ");
    }

    public String joinLemmas() {
        return subWords.map(ConlluWordLine::getLemma).mkString(" ");
    }

    public String joinPosTags() {
        return subWords.map(ConlluWordLine::getPosTag).mkString(" ");
    }
}
