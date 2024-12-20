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
package opennlpmodgen.tools.unidep;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import opennlpmodgen.tools.unidep.util.UniDepConlluUncompressor;
import opennlpmodgen.tools.unidep.util.UniDepURLSupplier;
import opennlpmodgen.tools.util.FileDownloader;
import opennlpmodgen.tools.util.FileUpToDateChecker;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
@CommonsLog
public class UniDepConlluDownloader {
    @NonNull
    private final FileUpToDateChecker fileUpToDateChecker;
    @NonNull
    private final UniDepURLSupplier uniDepURLSupplier;
    @NonNull
    private final FileDownloader fileDownloader;
    @NonNull
    private final UniDepConlluUncompressor uniDepConlluUncompressor;

    public void downloadUniDepConlluFile(@NonNull Path uniDepTreebankPath, @NonNull String language, @NonNull Path conlluPath) {
        log.info("Get Universal Dependencies treebank URL");
        URL treebankURL = uniDepURLSupplier.getTreebankURL();
        if (!fileUpToDateChecker.isUpToDate(uniDepTreebankPath, treebankURL)) {
            log.info(String.format("Downloading treebank from: '%s' to file: '%s'", treebankURL, uniDepTreebankPath));
            fileDownloader.downloadFile(treebankURL, uniDepTreebankPath);
        } else {
            log.info(String.format("Skip downloading treebank from: '%s', treebank file: '%s' is up to date", treebankURL, uniDepTreebankPath));
        }
        if (!fileUpToDateChecker.isUpToDate(conlluPath, uniDepTreebankPath)) {
            log.info(String.format("Uncompressing treebank for language: '%s' to conllu file: '%s'", language, conlluPath));
            uniDepConlluUncompressor.uncompressConlluFile(uniDepTreebankPath, language, conlluPath);
        } else {
            log.info(String.format("Skip uncompressing treebank for language: '%s', conllu file: '%s' is up to date", language, conlluPath));
        }
    }
}
