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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import opennlpmodgen.tools.util.ResourceDirectorySupplier;
import opennlpmodgen.tools.util.RootDirectorySupplier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class FilePathSupplier {
    @NonNull
    private final RootDirectorySupplier rootDirectorySupplier;
    @NonNull
    private final ResourceDirectorySupplier resourceDirectorySupplier;

    public Path getTreebankFile() {
        return getRootDir().resolve("uni-dep-treebank.tgz");
    }

    public Path getConlluFile(@NonNull String language) {
        return getRootDir().resolve(String.format("%s.conllu", language));
    }

    public Path getSentenceModelFile(@NonNull String language) {
        return getResourceDir(language).resolve(String.format("%s-sentence-detector.onlpm", language));
    }

    public Path getSentenceReportFile(@NonNull String language) {
        return getResourceDir(language).resolve(String.format("%s-sentence-detector.txt", language));
    }

    public Path getTokenizerModelFile(@NonNull String language) {
        return getResourceDir(language).resolve(String.format("%s-tokenizer.onlpm", language));
    }

    public Path getTokenizerReportFile(@NonNull String language) {
        return getResourceDir(language).resolve(String.format("%s-tokenizer.txt", language));
    }

    public Path getPOSModelFile(@NonNull String language) {
        return getResourceDir(language).resolve(String.format("%s-pos-tagger.onlpm", language));
    }

    public Path getPOSReportFile(@NonNull String language) {
        return getResourceDir(language).resolve(String.format("%s-pos-tagger.txt", language));
    }

    public Path getLemmatizerModelFile(@NonNull String language) {
        return getResourceDir(language).resolve(String.format("%s-lemmatizer.onlpm", language));
    }

    public Path getLemmatizerReportFile(@NonNull String language) {
        return getResourceDir(language).resolve(String.format("%s-lemmatizer.txt", language));
    }

    private Path getRootDir() {
        return rootDirectorySupplier.getRootDirectory();
    }

    private Path getResourceDir(String language) {
        return resourceDirectorySupplier.getResourceDirectory(language);
    }
}
