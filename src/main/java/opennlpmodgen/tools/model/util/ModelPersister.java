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

import lombok.NonNull;
import lombok.SneakyThrows;
import opennlp.tools.util.model.BaseModel;
import opennlpmodgen.tools.util.PathUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ModelPersister {
    @SneakyThrows
    public <M extends BaseModel> M readModel(@NonNull Path modelPath, @NonNull Class<M> modelClass) {
        try (var in = new BufferedInputStream(Files.newInputStream(modelPath))) {
            return modelClass.getConstructor(InputStream.class).newInstance(in);
        }
    }

    @SneakyThrows
    public void writeModel(@NonNull BaseModel model, @NonNull Path modelPath) {
        PathUtils.createParentDirectories(modelPath);
        try (var out = new BufferedOutputStream(Files.newOutputStream(modelPath))) {
            model.serialize(out);
        }
    }
}
