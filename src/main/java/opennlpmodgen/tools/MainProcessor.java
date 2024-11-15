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
package opennlpmodgen.tools;

import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import opennlpmodgen.tools.conllu.ConlluLemmatizerModelProcessor;
import opennlpmodgen.tools.conllu.ConlluPOSModelProcessor;
import opennlpmodgen.tools.conllu.ConlluSentenceModelProcessor;
import opennlpmodgen.tools.conllu.ConlluTokenizerModelProcessor;
import opennlpmodgen.tools.model.ModelAlgorithm;
import opennlpmodgen.tools.unidep.UniDepConlluDownloader;
import opennlpmodgen.tools.unidep.util.FilePathSupplier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainProcessor {
    @NonNull
    private final FilePathSupplier filePathSupplier;
    @NonNull
    private final UniDepConlluDownloader conlluFileDownloader;
    @NonNull
    private final ConlluSentenceModelProcessor sentenceModelProcessor;
    @NonNull
    private final ConlluTokenizerModelProcessor tokenizerModelProcessor;
    @NonNull
    private final ConlluPOSModelProcessor posModelProcessor;
    @NonNull
    private final ConlluLemmatizerModelProcessor lemmatizerModelProcessor;

    // MAXENT - try it
    // MAXENT_QN - try it
    // PERCEPTRON - try it
    // PERCEPTRON_SEQUENCE - not supported in sentence/tokenizer trainer, skip it
    // NAIVE_BAYES - try it
    public static final Seq<String> TOKENIZER_ALGORITHMS = Vector.of(ModelAlgorithm.MAXENT, ModelAlgorithm.MAXENT_QN, ModelAlgorithm.PERCEPTRON, ModelAlgorithm.NAIVE_BAYES);

    // MAXENT - try it
    // MAXENT_QN - takes a lot of time to train in pos phase, do not use
    // PERCEPTRON - try it
    // PERCEPTRON_SEQUENCE - takes a lot of time to train in pos phase, do not use
    // NAIVE_BAYES - try it
    public static final Seq<String> POS_ALGORITHMS = Vector.of(ModelAlgorithm.MAXENT, ModelAlgorithm.PERCEPTRON, ModelAlgorithm.NAIVE_BAYES);

    // MAXENT - try it
    // MAXENT_QN - often fails with out of memory error during lemmatizer training, do not use
    // PERCEPTRON - takes a lot of time to train in lemmatization phase, do not use
    // PERCEPTRON_SEQUENCE - takes a lot of time to train in lemmatization phase, do not use
    // NAIVE_BAYES - sometimes fails with out of memory error, it is also not better than MAXENT, do not use
    public static final Seq<String> LEMMATIZER_ALGORITHMS = Vector.of(ModelAlgorithm.MAXENT);

    public void processUniDepConlluModel(@NonNull String language) {
        var treebankFile = filePathSupplier.getTreebankFile();
        var conlluFile = filePathSupplier.getConlluFile(language);
        var sentenceModelFile = filePathSupplier.getSentenceModelFile(language);
        var sentenceReportFile = filePathSupplier.getSentenceReportFile(language);
        var tokenizerModelFile = filePathSupplier.getTokenizerModelFile(language);
        var tokenizerReportFile = filePathSupplier.getTokenizerReportFile(language);
        var posModelFile = filePathSupplier.getPOSModelFile(language);
        var posReportFile = filePathSupplier.getPOSReportFile(language);
        var lemmatizerModelFile = filePathSupplier.getLemmatizerModelFile(language);
        var lemmatizerReportFile = filePathSupplier.getLemmatizerReportFile(language);
        // download treebank, unpack conllu file
        conlluFileDownloader.downloadUniDepConlluFile(treebankFile, language, conlluFile);
        // try several algorithms and choose the best model
        sentenceModelProcessor.processConlluModel(conlluFile, TOKENIZER_ALGORITHMS, language, sentenceModelFile, sentenceReportFile);
        tokenizerModelProcessor.processConlluModel(conlluFile, TOKENIZER_ALGORITHMS, language, tokenizerModelFile, tokenizerReportFile);
        posModelProcessor.processConlluModel(conlluFile, POS_ALGORITHMS, language, posModelFile, posReportFile);
        lemmatizerModelProcessor.processConlluModel(conlluFile, LEMMATIZER_ALGORITHMS, language, lemmatizerModelFile, lemmatizerReportFile);
    }
}
