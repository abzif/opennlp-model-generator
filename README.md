# OpenNLP model generator

OpenNLP model generator computes models for [Apache OpenNLP](https://opennlp.apache.org) from [Universal Dependencies](https://universaldependencies.org) annotated language files.
OpenNLP supports natural language processing with tools like: sentence detector, tokenizer, part of speech tagger, lemmatizer etc.
However, models for various languages are not easily available.
This project allows to train and evaluate models for any language supported by Universal Dependencies treebank.

## Pre-trained models

Pre-trained models for various languages are available on [model page](https://abzif.github.io/opennlp-model-generator/models.html)

For now models for the following languages are automatically computed:

- [cs - czech](https://abzif.github.io/opennlp-model-generator/models.html#cs)
- [da - danish](https://abzif.github.io/opennlp-model-generator/models.html#da)
- [de - german](https://abzif.github.io/opennlp-model-generator/models.html#de)
- [el - greek](https://abzif.github.io/opennlp-model-generator/models.html#el)
- [en - english](https://abzif.github.io/opennlp-model-generator/models.html#en)
- [es - spanish](https://abzif.github.io/opennlp-model-generator/models.html#es)
- [fi - finnish](https://abzif.github.io/opennlp-model-generator/models.html#fi)
- [fr - french](https://abzif.github.io/opennlp-model-generator/models.html#fr)
- [he - hebrew](https://abzif.github.io/opennlp-model-generator/models.html#he)
- [it - italian](https://abzif.github.io/opennlp-model-generator/models.html#it)
- [ja - japanese](https://abzif.github.io/opennlp-model-generator/models.html#ja)
- [ko - korean](https://abzif.github.io/opennlp-model-generator/models.html#ko)
- [no - norwegian](https://abzif.github.io/opennlp-model-generator/models.html#no)
- [pl - polish](https://abzif.github.io/opennlp-model-generator/models.html#pl)
- [pt - portugal](https://abzif.github.io/opennlp-model-generator/models.html#pt)
- [ru - russian](https://abzif.github.io/opennlp-model-generator/models.html#ru)
- [sv - swedish](https://abzif.github.io/opennlp-model-generator/models.html#sv)
- [uk - ukrainian](https://abzif.github.io/opennlp-model-generator/models.html#uk)
- [zh - chinese](https://abzif.github.io/opennlp-model-generator/models.html#zh)

## Lucene/SOLR analysis chain

Pre-trained models can be used in SOLR analyzers. Example analyzer chain is presented below:

```
<analyzer>
  <!-- tokenizer -->
  <tokenizer class="solr.OpenNLPTokenizerFactory" sentenceModel="xy-sentence-detector.onlpm" tokenizerModel="xy-tokenizer.onlpm"/>
  <!-- helper filters, optional -->
  <filter class="solr.LowerCaseFilterFactory"/>
  <filter class="solr.ASCIIFoldingFilterFactory"/>
  <!-- part of speech tagging -->
  <filter class="solr.OpenNLPPOSFilterFactory" posTaggerModel="xy-pos-tagger.onlpm"/>
  <!-- lemmatizer -->
  <filter class="solr.OpenNLPLemmatizerFilterFactory" lemmatizerModel="xy-lemmatizer.onlpm"/>
  <!-- other necessary filters TypeTokenFilterFactory, TypeAsPayloadFilterFactory, SynonymGraphFilterFactory etc -->
</analyzer>
```

## Interactive model verifier

Here is a simple program to interactively verify openNLP models.
The program waits for input text, then after analysis, it prints tokens, part-of-speech and lemma to console

```
public class InteractiveModelVerifier {
    public static void main(String[] args) throws Exception {
        var modelDirectory = Path.of("<directory where *.onlpm models are stored>");
        var language = "<language>";
        verifyModels(modelDirectory, language);
    }

    private static void verifyModels(Path modelDirectory, String language) throws Exception {
        var sentenceDetector = new SentenceDetectorME(new SentenceModel(modelDirectory.resolve(String.format("%s-sentence-detector.onlpm", language))));
        var tokenizer = new TokenizerME(new TokenizerModel(modelDirectory.resolve(String.format("%s-tokenizer.onlpm", language))));
        var posTagger = new POSTaggerME(new POSModel(modelDirectory.resolve(String.format("%s-pos-tagger.onlpm", language))));
        var lemmatizer = new LemmatizerME(new LemmatizerModel(modelDirectory.resolve(String.format("%s-lemmatizer.onlpm", language))));
        while (true) {
            System.out.println("Enter text or 'q' to quit");
            var reader = new BufferedReader(new InputStreamReader(System.in));
            var line = reader.readLine();
            if ("q".equalsIgnoreCase(line)) {
                break;
            } else {
                verifyModels(line, sentenceDetector, tokenizer, posTagger, lemmatizer);
            }
        }
    }

    private static void verifyModels(String text, SentenceDetector sentenceDetector, Tokenizer tokenizer, POSTagger posTagger, Lemmatizer lemmatizer) {
        var sentences = sentenceDetector.sentDetect(text);
        for (var sentence : sentences) {
            System.out.println(sentence);
            var tokens = tokenizer.tokenize(sentence);
            var posTags = posTagger.tag(tokens);
            var lemmas = lemmatizer.lemmatize(tokens, posTags);
            for (int i = 0; i < tokens.length; i++) {
                System.out.println(String.format("%s\t%s\t%s", tokens[i], posTags[i], lemmas[i]));
            }
            System.out.println();
        }
    }
}
```

## Training and evaluation process

Universal Dependencies treebank consists of **conllu** files for many languages. **Conllu** file contains annotated sentences in a particular language.
Annotations describe tokens and part of speech and lemma for every token. Possible POS tags are listed [here](https://universaldependencies.org/u/pos)

Models are trained on original text and normalized text (lowercased and folded to ASCII), so it should work for both variants.
Such models may be used in [Apache SOLR](https://solr.apache.org) or [Elasticsearch](https://www.elastic.co) which supports OpenNLP analyzers.

The process of training and evaluation of models roughly consists of the following steps:

- Download universal dependencies treebank (only if does not exist locally or newer version is available).
- Unpack conllu files for a particular language
- For every supported trainer (sentence-detector, tokenizer, pos-tagger, lemmatizer) perform further steps. Training is performed only if a model does not exist or newer conllu file is available.
    - Read the sentences from conllu file, concatenate the original sentences with normalized sentences
    - Optional: Try to fix the data (in example for 'de' language)
    - Convert sentences to sample stream for a particular trainer (token sample stream, lemma sample stream etc)
    - Train and evaluate model. Several available algorithms are tried and evaluated. Only the best one is choosen.
    - Save model and evaluation report.

All files (input or generated models) are processed in directory **$HOME/.cache/opennlp-model-generator** (or its subdirectories)

## Evaluation results (openNLP version 1.9.3)

Several models were trained for different language types. The results of their evaluation are presented below.
Available sentences are divided into training/evaluation sets. Every 10th sentence goes to evaluation set. 90% of sentences is used for training.

- **language**: language code + language name
- **training sentences**: approximate number of training sentences
- **models**: training algorithm (algorithm with the best evaluation score) + score (ranging from 0.0 to 1.0)

### Alphabetic latin languages

These languages use alphabetic latin script with native diacritic characters. Words are separated by whitespace.

|   language    | training sentences | sentence-detector |     tokenizer     |   pos-tagger   |   lemmatizer   |
|:-------------:|:------------------:|:-----------------:|:-----------------:|:--------------:|:--------------:|
| de<br>german  |        65k         | MAXENT_QN<br>0.72 | MAXENT_QN<br>0.99 | MAXENT<br>0.94 | MAXENT<br>0.96 |
| en<br>english |        35k         | MAXENT_QN<br>0.74 | MAXENT_QN<br>0.99 | MAXENT<br>0.94 | MAXENT<br>0.98 |
| es<br>spanish |        30k         | MAXENT_QN<br>0.96 | MAXENT_QN<br>0.99 | MAXENT<br>0.94 | MAXENT<br>0.98 |
| fr<br>french  |        25k         | MAXENT_QN<br>0.92 | MAXENT_QN<br>0.99 | MAXENT<br>0.95 | MAXENT<br>0.98 |
| pl<br>polish  |        36k         |  MAXENT<br>0.95   | MAXENT_QN<br>0.99 | MAXENT<br>0.96 | MAXENT<br>0.96 |

Models generated for such types of languages have good quality.
These types of languages are supported very well.
Sentence detection score is relatively low, because many sentences in the sample were not properly ended.

### Alphabetic non-latin languages

These languages use alphabetic non-latin scripts (greek, cyrillic). Words are separated by whitespace.

|    language     | training sentences | sentence-detector |     tokenizer      |     pos-tagger     |   lemmatizer   |
|:---------------:|:------------------:|:-----------------:|:------------------:|:------------------:|:--------------:|
|   el<br>greek   |         2k         | MAXENT_QN<br>0.90 | MAXENT_QN<br>0.99  | PERCEPTRON<br>0.95 | MAXENT<br>0.95 |
|  ru<br>russian  |        99k         | MAXENT_QN<br>0.93 | MAXENT_QN<br>0.99  |   MAXENT<br>0.96   | MAXENT<br>0.97 |
| uk<br>ukrainian |         6k         |  MAXENT<br>0.91   | PERCEPTRON<br>0.99 |   MAXENT<br>0.94   | MAXENT<br>0.94 |

These types of languages are also well supported.

### Abjads languages

These languages are commonly written from right to left. Vovels are often omitted. Words are separated by whitespace.

|   language   | training sentences | sentence-detector  |     tokenizer     |   pos-tagger   |         lemmatizer         |
|:------------:|:------------------:|:------------------:|:-----------------:|:--------------:|:--------------------------:|
| ar<br>arabic |         7k         | MAXENT_QN<br>0.71  | MAXENT_QN<br>0.97 | MAXENT<br>0.93 | Serialization<br>exception |
| he<br>hebrew |         8k         | PERCEPTRON<br>0.94 | MAXENT_QN<br>0.92 | MAXENT<br>0.94 |       MAXENT<br>0.96       |

Evaluation score is a bit lower for these languages.
Lemmatizer model training for arabian language fails. Computed model cannot be serialized. Don't know the reason.

### South-east asian languages

These languages use logographs/syllabic scripts. Words usually are not separated which causes problems with tokenization.

|    language    | training sentences | sentence-detector |     tokenizer      |     pos-tagger     |   lemmatizer   |
|:--------------:|:------------------:|:-----------------:|:------------------:|:------------------:|:--------------:|
| ja<br>japanese |        16k         | MAXENT_QN<br>0.96 | NAIVEBAYES<br>0.79 | PERCEPTRON<br>0.96 | MAXENT<br>0.97 |
|  ko<br>korean  |        30k         | MAXENT_QN<br>0.94 | MAXENT_QN<br>0.99  |   MAXENT<br>0.89   | MAXENT<br>0.90 |
| zh<br>chinese  |         9k         |  MAXENT<br>0.98   | MAXENT_QN<br>0.91  | PERCEPTRON<br>0.94 | MAXENT<br>0.99 |

The results are not that impressive.
Tokenization quality for japanese is quite low. Tokenizer seems not to support well such types of languages.
Maybe if tokenizer have a dictionary of "known words" then trained model would be better.
POS tagging/lemmatization for korean language is also not good.
Chinese tokenization quality is higher than for japanese.
Chinese words are shorter than japanese words, it means that surrounding context is shorter.
This may explain why tokenizer better segments chinese words than japanese words.
