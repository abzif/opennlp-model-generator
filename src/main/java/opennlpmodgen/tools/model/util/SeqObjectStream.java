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

import io.vavr.collection.Iterator;
import io.vavr.collection.Seq;
import lombok.NonNull;
import opennlp.tools.util.ObjectStream;

public class SeqObjectStream<T> implements ObjectStream<T> {
    private final Seq<T> seq;
    private Iterator<T> it;

    public SeqObjectStream(@NonNull Seq<T> seq) {
        this.seq = seq;
        reset();
    }

    @Override
    public T read() {
        return it.hasNext() ? it.next() : null;
    }

    @Override
    public void reset() {
        it = seq.iterator();
    }
}
