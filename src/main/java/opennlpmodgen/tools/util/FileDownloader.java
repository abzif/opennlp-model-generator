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
package opennlpmodgen.tools.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class FileDownloader {
    @NonNull
    private final WebClient webClient;

    @SneakyThrows
    public void downloadFile(@NonNull URL inputURL, @NonNull Path outputPath) {
        var bytes = webClient.makeGetRequest(inputURL).getBody().getOrElse(new byte[0]);
        PathUtils.createParentDirectories(outputPath);
        Files.write(outputPath, bytes);
    }
}
