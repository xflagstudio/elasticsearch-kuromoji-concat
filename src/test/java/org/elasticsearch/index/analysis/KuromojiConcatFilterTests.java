package org.elasticsearch.index.analysis;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsModule;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.EnvironmentModule;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexNameModule;
import org.elasticsearch.index.settings.IndexSettingsModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.elasticsearch.plugin.analysis.kuromoji.AnalysisKuromojiPlugin;
import org.elasticsearch.test.ElasticsearchTestCase;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


public class KuromojiConcatFilterTests extends ElasticsearchTestCase {

    @Test
    public void testBaseFormFilterFactory() throws IOException {
        // AnalysisService analysisService = createAnalysisService();
        // TokenFilterFactory concatFilter = analysisService.tokenFilter("kuromoji_concat");
        // assertThat(concatFilter, instanceOf( KuromojiConcatFilterFactory.class ));

        // String source = "私は制限スピードを超える。";
        // String[] expected = new String[]{"私", "は", "制限", "スピード", "を"};
        // Tokenizer tokenizer = new JapaneseTokenizer(new StringReader(source), null, true, JapaneseTokenizer.Mode.SEARCH);
        // assertSimpleTSOutput(concatFilter.create(tokenizer), expected);
    }

        public AnalysisService createAnalysisService() {
            Settings settings = ImmutableSettings.settingsBuilder()
                .loadFromClasspath("org/elasticsearch/index/analysis/kuromoji_analysis.json")
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();

        Index index = new Index("test");

        Injector parentInjector = new ModulesBuilder().add(new SettingsModule(settings),
                new EnvironmentModule(new Environment(settings)),
                new IndicesAnalysisModule())
                .createInjector();

        AnalysisModule analysisModule = new AnalysisModule(settings, parentInjector.getInstance(IndicesAnalysisService.class));
        new AnalysisKuromojiPlugin().onModule(analysisModule);

        Injector injector = new ModulesBuilder().add(
                new IndexSettingsModule(index, settings),
                new IndexNameModule(index),
                analysisModule)
                .createChildInjector(parentInjector);

        return injector.getInstance(AnalysisService.class);
    }
        public static void assertSimpleTSOutput(TokenStream stream,
                                            String[] expected) throws IOException {
        stream.reset();
        CharTermAttribute termAttr = stream.getAttribute(CharTermAttribute.class);
        assertThat(termAttr, notNullValue());
        int i = 0;
        while (stream.incrementToken()) {
            assertThat(expected.length, greaterThan(i));
            assertThat( "expected different term at index " + i, expected[i++], equalTo(termAttr.toString()));
        }
        assertThat("not all tokens produced", i, equalTo(expected.length));
    }


}
