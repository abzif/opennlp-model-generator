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

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.SortedMap;
import io.vavr.collection.TreeMap;
import io.vavr.collection.Vector;
import lombok.NonNull;
import lombok.SneakyThrows;
import opennlpmodgen.tools.util.PathUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

@Component
public class UniDepConlluUncompressor {
    private static final Pattern CONLLU_FILE_NAME_PATTERN = Pattern.compile(".*/([A-Za-z]+)_.*\\.conllu");

    @SneakyThrows
    public void uncompressConlluFile(@NonNull Path uniDepTreebankPath, @NonNull String language, @NonNull Path conlluPath) {
        SortedMap<String, Seq<String>> conlluTxtByFileName = TreeMap.empty();
        try (var in = new BufferedInputStream(Files.newInputStream(uniDepTreebankPath));
             var gzipIn = new GzipCompressorInputStream(in);
             var tarIn = new TarArchiveInputStream(gzipIn)) {
            for (var entry = tarIn.getNextEntry(); entry != null; entry = tarIn.getNextEntry()) {
                if (isConlluFile(entry, language)) {
                    conlluTxtByFileName = conlluTxtByFileName.put(entry.getName(), readConnluFile(tarIn));
                }
            }
        }
        var conlluTxt = conlluTxtByFileName
                .map(this::adjustConlluTxt)
                .values()
                .foldLeft(Vector.<String>empty(), Vector::appendAll)
                .mkString("\n");
        PathUtils.createParentDirectories(conlluPath);
        Files.writeString(conlluPath, conlluTxt);
    }

    private boolean isConlluFile(TarArchiveEntry entry, String language) {
        if (entry.isFile()) {
            var matcher = CONLLU_FILE_NAME_PATTERN.matcher(entry.getName());
            if (matcher.matches()) {
                return language.equalsIgnoreCase(matcher.group(1));
            }
        }
        return false;
    }

    @SneakyThrows
    private Seq<String> readConnluFile(TarArchiveInputStream tarIn) {
        var bytes = IOUtils.toByteArray(tarIn);
        var str = new String(bytes, StandardCharsets.UTF_8);
        // keep only useful lines, remove comments
        return Vector.of(str.split("\\R"))
                .filter(line -> line.startsWith("#") && line.contains("text") || !line.startsWith("#"));
    }

    private Tuple2<String, Seq<String>> adjustConlluTxt(String fileName, Seq<String> lines) {
        lines = lines.prepend("####### " + fileName);
        lines = !lines.last().isEmpty() ? lines.append("") : lines;
        return Tuple.of(fileName, lines);
    }
}
