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
package opennlpmodgen.tools.model;

import opennlp.tools.ml.maxent.GISTrainer;
import opennlp.tools.ml.maxent.quasinewton.QNTrainer;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.ml.perceptron.PerceptronTrainer;
import opennlp.tools.ml.perceptron.SimplePerceptronSequenceTrainer;

public interface ModelAlgorithm {
    public static final String MAXENT = GISTrainer.MAXENT_VALUE;
    public static final String MAXENT_QN = QNTrainer.MAXENT_QN_VALUE;
    public static final String PERCEPTRON = PerceptronTrainer.PERCEPTRON_VALUE;
    public static final String PERCEPTRON_SEQUENCE = SimplePerceptronSequenceTrainer.PERCEPTRON_SEQUENCE_VALUE;
    public static final String NAIVE_BAYES = NaiveBayesTrainer.NAIVE_BAYES_VALUE;
}
