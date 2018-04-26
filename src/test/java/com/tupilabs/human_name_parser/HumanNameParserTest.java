package com.tupilabs.human_name_parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.tupilabs.human_name_parser.HumanNameParser.ParseWork;

/**
 * @author Steve Ash
 */
public class HumanNameParserTest {

    private final Splitter ws = Splitter.on(CharMatcher.whitespace()).omitEmptyStrings();

    @Test
    public void shouldParseSimple1() throws Exception {
        HumanNameParser p1 = new HumanNameParser(true);
        ParsedName name1 = p1.parse("STEVE M ASH");
        assertEquals(Arrays.asList("STEVE", "M", "ASH"), name1.getTokens());
        assertEquals(Arrays.asList(Label.First, Label.MiddleInitial, Label.Last), name1.getLabels());

        SegmentedName seg1 = name1.toSegmented();
        assertEquals("STEVE", seg1.getFirst());
        assertEquals("M", seg1.getMiddle());
        assertEquals("ASH", seg1.getLast());
    }

    @Test
    public void shouldParseSimple2() throws Exception {
        HumanNameParser p1 = new HumanNameParser(true);
        ParsedName name1 = p1.parse("STEVE M R ASH");
        assertEquals(Arrays.asList("STEVE", "M", "R", "ASH"), name1.getTokens());
        assertEquals(Arrays.asList(Label.First, Label.MiddleInitial, Label.MiddleInitial, Label.Last), name1.getLabels());

        SegmentedName seg1 = name1.toSegmented();
        assertEquals("STEVE", seg1.getFirst());
        assertEquals("M R", seg1.getMiddle());
        assertEquals("ASH", seg1.getLast());
    }
    
    @Test
    public void shouldParseSimple3() throws Exception {
        // 
        HumanNameParser p1 = new HumanNameParser(true);
        ParsedName name1 = p1.parse("SHARON A. WALGREEN, RDH");
        assertEquals(Arrays.asList("SHARON", "A.", "WALGREEN", "RDH"), name1.getTokens());
        assertEquals(Arrays.asList(Label.First, Label.MiddleInitial, Label.Last, Label.Postnominal), name1.getLabels());

        SegmentedName seg1 = name1.toSegmented();
        assertEquals("SHARON", seg1.getFirst());
        assertEquals("A.", seg1.getMiddle());
        assertEquals("WALGREEN", seg1.getLast());
        assertEquals("RDH", seg1.getPostnominal());
    }

    @Test
    public void shouldMakeParsedNameFrom() throws Exception {
        assertName("", "", new ArrayList<Label>());
        assertName("Steve", "FFFFF", Arrays.asList(Label.First));
        assertName("Steve ", "FFFFF ", Arrays.asList(Label.First));
        assertName(" Steve ", " FFFFF ", Arrays.asList(Label.First));
        assertName("Steve Ash", "FFFFF LLL", Arrays.asList(Label.First, Label.Last));
        assertName("Steve Ash Ash", "FFFFF LLL LLL", Arrays.asList(Label.First, Label.Last, Label.Last));
    }

    private void assertName(String name, String codes, List<Label> expected) {
        Preconditions.checkArgument(name.length() == codes.length(), name, codes);
        List<String> toks = ws.splitToList(name);
        assertEquals(expected.size(), toks.size());
        ParseWork work = new ParseWork(name);
        for (int i = 0; i < codes.length(); i++) {
            work.posToLabel[i] = decode(codes.charAt(i));
        }
        ParsedName parsedName = HumanNameParser.makeParsedNameFrom(work, false);
        assertEquals(expected.size(), parsedName.size());
        for (int i = 0; i < parsedName.size(); i++) {
            System.out.println(name + " " + i + " got " + parsedName.getLabels().get(i) + " expected: " +
                    expected.get(i) + " for name tok " + parsedName.getTokens().get(i));
            assertEquals(expected.get(i), parsedName.getLabels().get(i));
        }
    }

    private Label decode(char code) {
        switch (code) {
            case 'F': return Label.First;
            case 'L': return Label.Last;
            case ' ': return Label.Whitespace;
            default:
                throw new IllegalArgumentException("Dont know " + Character.toString(code));
        }
    }
}