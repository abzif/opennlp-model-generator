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
package opennlpmodgen.tools.conllu;

public enum ConlluPOSTag {
    // open class words
    ADJ, // adjective,
    ADV, // adverb
    INTJ, // interjection
    NOUN, // noun
    PROPN, // proper noun
    VERB, // verb
    // closed class words
    ADP, // adposition
    AUX, // auxilary
    CCONJ, // coordinating conjunction
    DET, // determiner
    NUM, // numeral
    PART, // particle
    PRON, // pronoun
    SCONJ, // subordinating conjunction
    // other
    PUNCT, // punctuation
    SYM, // symbol
    X // other
}
