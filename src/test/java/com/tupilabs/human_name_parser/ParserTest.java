/*
 * The MIT License
 *
 * Copyright (c) 2010-2015 Jason Priem, Bruno P. Kinoshita
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tupilabs.human_name_parser;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

public class ParserTest {
    
    private static final Logger LOGGER = Logger.getLogger(ParserTest.class.getName());

    private static File testNames = null;
    private HumanNameParser parser;
    
    @BeforeClass
    public static void setUp() {
        testNames = new File(ParserTest.class.getResource("/testNames.txt").getFile());
    }

    @Before
    public void setUpParser() throws Exception {
        parser = new HumanNameParser(true);
    }

    @Test
    public void shouldTestone() throws Exception {
        String input = "Sérgio Vieira de Mello";
        ParsedName parsed = parser.parse(input);
        LOGGER.info("Got " + parsed);
    }

    @Test
    public void testAll() throws IOException {
        BufferedReader buffer = null;
        FileReader reader = null;
        Splitter splitter = Splitter.on('|');

        try {
            reader = new FileReader(testNames);
            buffer = new BufferedReader(reader);
            
            String line = null;
            while ((line = buffer.readLine()) != null) {
                if (StringUtils.isBlank(line)) {
                    LOGGER.warning("Empty line in testNames.txt");
                    continue;
                }

                List<String> toks = splitter.splitToList(line);
                if (toks.size() != 9) {
                    LOGGER.warning(String.format("Invalid line in testNames.txt: %s", line));
                    continue;
                }
                
                validateLine(toks);
            }
        } finally {
            if (reader != null)
                reader.close();
            if (buffer != null)
                buffer.close();
        }
    }

    /**
     * Validates a line in the testNames.txt file.
     *
     * @param tokens the tokens with leading spaces
     */
    private void validateLine(List<String> tokens) {
        String name = tokens.get(0).trim();
        
        String leadingInit = tokens.get(1).trim();
        String first = tokens.get(2).trim();
        String nickname = tokens.get(3).trim();
        String middle = tokens.get(4).trim();
        String last = tokens.get(5).trim();
        String suffix = tokens.get(6).trim();
        String salutation = tokens.get(7).trim();
        String postnominal = tokens.get(8).trim();

        try {
            ParsedName parsed = parser.parse(name);
            SegmentedName pn = parsed.toSegmented();

            assertToken(leadingInit, pn.getLeadingInit());
            assertToken(first, pn.getFirst());
            assertToken(nickname, pn.getNicknames());
            assertToken(middle, pn.getMiddle());
            assertToken(last, pn.getLast());
            assertToken(suffix, pn.getSuffix());
            assertToken(salutation, pn.getSalutation());
            assertToken(postnominal, pn.getPostnominal());
        } catch (Throwable e) {
            throw new IllegalArgumentException("Problem parsing name " + name, e);
        }
    }

    private static void assertToken(String expected, String leadingInit) {
        String toCheck = Strings.nullToEmpty(leadingInit);
        assertEquals(expected, toCheck);
    }
}
