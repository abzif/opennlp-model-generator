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

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class SampleSplitter {
    public <S extends Serializable> Tuple2<Seq<S>, Seq<S>> splitSamples(@NonNull Seq<S> samples) {
        // split samples
        // train samples contains 90% of original samples
        // evaluation samples contains 10% of original samples (every 10th element)
        return samples.zipWithIndex()
                .partition(t -> (t._2 + 1) % 10 != 0)
                .map((zs1, zs2) -> Tuple.of(zs1.map(Tuple2::_1), zs2.map(Tuple2::_1)));
    }
}
