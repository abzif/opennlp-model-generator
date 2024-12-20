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

import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SampleSplitterTest {
    private SampleSplitter splitter = new SampleSplitter();

    @Test
    public void splitSamples() {
        Seq<Integer> samples = Vector.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21);

        Tuple2<Seq<Integer>, Seq<Integer>> splittedSamples = splitter.splitSamples(samples);

        Assertions.assertThat(splittedSamples._1).isEqualTo(Vector.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21));
        Assertions.assertThat(splittedSamples._2).isEqualTo(Vector.of(10, 20));
    }
}
