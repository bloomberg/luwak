package uk.co.flax.luwak;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.IntPoint;
import org.junit.Before;
import org.junit.Test;

import uk.co.flax.luwak.matchers.SimpleMatcher;
import uk.co.flax.luwak.presearcher.MatchAllPresearcher;
import uk.co.flax.luwak.queryparsers.FlexibleLuceneQueryParser;

import org.apache.lucene.queryparser.flexible.standard.config.PointsConfig;

import static uk.co.flax.luwak.assertions.MatchesAssert.assertThat;

/**
 * Copyright (c) 2013 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TestMonitorFlexibleLuceneQueryParser{

    static final String TEXTFIELD = "TEXTFIELD";

    static final Analyzer ANALYZER = new WhitespaceAnalyzer();

    private Monitor monitor;
    private FlexibleLuceneQueryParser parser;
    @Before
    public void setUp() throws IOException {
        parser = new FlexibleLuceneQueryParser(TEXTFIELD, ANALYZER);
        monitor = new Monitor(parser, new MatchAllPresearcher());
    }

    @Test
    public void matchIntPointfields() throws IOException,UpdateException,Exception {

        parser.getPointsConfig().put("age",new PointsConfig(NumberFormat.getIntegerInstance(Locale.ROOT), Integer.class));


        InputDocument doc = InputDocument.builder("doc1")
                .addField(new IntPoint("age",1))
                .addField(new IntPoint("somethingelse",2))
                .build();

        MonitorQuery mq  = new MonitorQuery("query1","(age:1 somethingelse:2)");
        monitor.update(mq);
        Matches<?> matches = monitor.match(doc, SimpleMatcher.FACTORY);
        assertThat(matches)
        .hasMatchCount("doc1", 1);

        mq  = new MonitorQuery("query1","age:1");
        monitor.update(mq);
        matches = monitor.match(doc, SimpleMatcher.FACTORY);
        assertThat(matches)
        .hasMatchCount("doc1", 1);
    }

    @Test
    public void matchDoublePointfields() throws IOException,UpdateException,Exception {

        parser.getPointsConfig().put("money",new PointsConfig(NumberFormat.getNumberInstance(Locale.ROOT), Double.class));
        parser.getPointsConfig().put("somethingelse",new PointsConfig(NumberFormat.getIntegerInstance(Locale.ROOT), Integer.class));

        InputDocument doc = InputDocument.builder("doc1")
                .addField(new DoublePoint("money",1.0))
                .addField(new IntPoint("somethingelse",2))
                .build();

        MonitorQuery mq  = new MonitorQuery("query1","(money:1.0 somethingelse:2)");
        monitor.update(mq);
        Matches<?> matches = monitor.match(doc, SimpleMatcher.FACTORY);
        assertThat(matches)
        .hasMatchCount("doc1", 1);

        mq  = new MonitorQuery("query1","money:1");
        monitor.update(mq);
        matches = monitor.match(doc, SimpleMatcher.FACTORY);
        assertThat(matches)
        .hasMatchCount("doc1", 1);
    }

}
