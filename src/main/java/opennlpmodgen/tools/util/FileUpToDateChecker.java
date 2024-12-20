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

import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class FileUpToDateChecker {
    @NonNull
    private final WebClient webClient;

    @SneakyThrows
    public boolean isUpToDate(@NonNull Path outputPath, @NonNull URL inputURL) {
        if (Files.exists(outputPath)) {
            FileTime outputLastModified = Files.getLastModifiedTime(outputPath);
            long outputFileLength = Files.size(outputPath);
            Map<String, String> headers = webClient.makeHeadRequest(inputURL).getHeaders();
            Option<String> lastModifiedHeader = headers.get("last-modified");
            Option<String> contentLengthHeader = headers.get("content-length");
            if (lastModifiedHeader.isDefined() || contentLengthHeader.isDefined()) {
                boolean lastModifiedUpToDate = lastModifiedHeader.isDefined()
                        ? getLastModifiedMillis(lastModifiedHeader.get()) <= outputLastModified.toMillis()
                        : true;
                boolean contentLengthUpToDate = contentLengthHeader.isDefined()
                        ? getContentLength(contentLengthHeader.get()) <= outputFileLength
                        : true;
                return lastModifiedUpToDate && contentLengthUpToDate;
            }
        }
        return false;
    }

    private long getLastModifiedMillis(String lastModifiedHeader) {
        return ZonedDateTime.parse(lastModifiedHeader, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli();
    }

    private long getContentLength(String contentLengthHeader) {
        return Long.parseLong(contentLengthHeader);
    }

    @SneakyThrows
    public boolean isUpToDate(@NonNull Path outputPath, @NonNull Path inputPath) {
        if (Files.exists(outputPath) && Files.exists(inputPath)) {
            FileTime outputLastModified = Files.getLastModifiedTime(outputPath);
            FileTime inputLastModified = Files.getLastModifiedTime(inputPath);
            return inputLastModified.compareTo(outputLastModified) <= 0;
        }
        return false;
    }
}
