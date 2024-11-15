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

import com.google.common.jimfs.Jimfs;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenizerModel;
import opennlpmodgen.tools.model.ModelAlgorithm;
import opennlpmodgen.tools.model.train.TokenizerModelTrainer;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelPersisterTest {
    private ModelPersister persister = new ModelPersister();

    @Test
    public void writeReadModel() {
        Seq<TokenSample> samples = Vector.of(
                TokenSample.parse("a1 a2|.", "|"),
                TokenSample.parse("b1 b2|.", "|"),
                TokenSample.parse("c1 c2|.", "|"));
        TokenizerModelTrainer trainer = new TokenizerModelTrainer();
        TokenizerModel trainedModel = trainer.trainModel(ModelAlgorithm.MAXENT, "lx", samples).get();
        Path modelPath = Jimfs.newFileSystem().getPath("model.bin");

        persister.writeModel(trainedModel, modelPath);
        TokenizerModel persistedModel = persister.readModel(modelPath, TokenizerModel.class);

        assertThat(modelPath).exists();
        assertThat(persistedModel.getClass()).isEqualTo(trainedModel.getClass());
    }
}
