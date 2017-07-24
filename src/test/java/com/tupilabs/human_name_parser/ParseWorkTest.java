package com.tupilabs.human_name_parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tupilabs.human_name_parser.HumanNameParser.ParseWork;

/**
 * @author Steve Ash
 */
public class ParseWorkTest {

    @Test
    public void shouldNormalize() throws Exception {
        assertNorm(" A B C  D E F  ", "A B C D E F");
        assertNorm("A B C  D E F  ", "A B C D E F");
        assertNorm("  A B C  D E F", "A B C D E F");
        assertNorm("A B C D E F", "A B C D E F");
        assertNorm("A B C D   E    F     ", "A B C D E F");
        assertNorm(" A  B  C  D  E F", "A B C D E F");
    }

    private void assertNorm(String input, String output) {
        ParseWork work = new ParseWork(input);
        work.normalizeCurrent();
        assertEquals(output, work.current.toString());
    }
}