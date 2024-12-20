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
package opennlpmodgen.tools.unidep.util;

import com.google.common.jimfs.Jimfs;
import io.vavr.collection.Vector;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class UniDepConlluUncompressorTest {
    private UniDepConlluUncompressor uncompressor = new UniDepConlluUncompressor();

    @Test
    public void uncompressConlluFile() throws Exception {
        Path rootPath = Jimfs.newFileSystem().getPath("");
        Path treebankPath = rootPath.resolve("treebank.tgz");
        Path conlluPath = rootPath.resolve("lx.conllu");
        Files.write(treebankPath, FileCopyUtils.copyToByteArray(new ClassPathResource("treebank.tgz", getClass()).getInputStream()));

        uncompressor.uncompressConlluFile(treebankPath, "lx", conlluPath);

        Assertions.assertThat(conlluPath).content().isEqualTo(Vector.of(
                "####### lx-abc/lx_abc-test.conllu",
                "# text = lx xyz",
                "1",
                "",
                "####### lx-abc/lx_abc-train.conllu",
                "# text = lx xyz",
                "2",
                "",
                "# text = lx xyz",
                "3",
                "",
                "####### lx-xyz/lx_xyz-train.conllu",
                "# text = lx xyz",
                "4",
                "",
                "# text = lx xyz",
                "5",
                "").mkString("\n"));
    }
}
