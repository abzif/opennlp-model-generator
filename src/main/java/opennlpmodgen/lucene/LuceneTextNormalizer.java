package opennlpmodgen.lucene;

import opennlp.tools.util.StringUtil;
import opennlpmodgen.tools.util.TextNormalizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;

public class LuceneTextNormalizer implements TextNormalizer {
    @Override
    public String normalizeText(String text, String language) {
        // lowercase
        text = StringUtil.toLowerCase(text);
        // fold to ascii
        var inputChars = text.toCharArray();
        var inputLength = inputChars.length;
        var outputChars = new char[inputLength * 4];
        var outputLength = ASCIIFoldingFilter.foldToASCII(inputChars, 0, outputChars, 0, inputLength);
        return new String(outputChars, 0, outputLength);
    }
}
