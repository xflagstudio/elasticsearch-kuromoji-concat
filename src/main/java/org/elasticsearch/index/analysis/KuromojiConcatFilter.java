package org.elasticsearch.index.analysis;

// modified from https://issues.apache.org/jira/browse/SOLR-7193

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class KuromojiConcatFilter extends TokenFilter {
    private CharTermAttribute charTermAtt = addAttribute(CharTermAttribute.class);
    private OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    private TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    private StringBuilder stringBuilder = new StringBuilder();
    boolean exhausted = false;

    public KuromojiConcatFilter(TokenStream input) {
        super(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (exhausted) {
            return false;
        }
        while (input.incrementToken()) {
            if (typeAtt.type().equals("word")) {
                stringBuilder.append(charTermAtt.buffer(), 0, charTermAtt.length());
            }
        }
        exhausted = true;
        int length = stringBuilder.length();
        if (length > 0) {
            char[] termBuffer = charTermAtt.resizeBuffer(length);
            stringBuilder.getChars(0, length, termBuffer, 0);
            stringBuilder.setLength(0);
            charTermAtt.setLength(length);
            offsetAtt.setOffset(0, offsetAtt.endOffset());
            typeAtt.setType("word");
            posIncrAtt.setPositionIncrement(1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        exhausted = false;
    }
}
