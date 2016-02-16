package org.elasticsearch.plugin.kuromoji;

import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.KuromojiConcatFilterFactory;

public class KuromojiConcatPlugin extends AbstractPlugin {
    @Override public String name() {
        return "kuromoji_concat-plugin";
    }

    @Override public String description() {
        return "Concatinate kuromoji tokens to single token";
    }

    public void onModule(AnalysisModule module){
        module.addTokenFilter("kuromoji_concat", KuromojiConcatFilterFactory.class);
    }
}
