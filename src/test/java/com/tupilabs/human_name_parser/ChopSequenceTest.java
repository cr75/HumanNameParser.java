package com.tupilabs.human_name_parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Steve Ash
 */
public class ChopSequenceTest {

    private static final String SEQ = "0123456789";

    @Test
    public void shouldReturnSameWithNoRemoves() throws Exception {
        ChopSequence cs1 = new ChopSequence(SEQ);
        assertEquals(10, cs1.length());
        assertEquals(SEQ, cs1.toString());
        for (int i = 0; i <= 9; i++) {
            assertEquals(Integer.toString(i).charAt(0), cs1.charAt(i));
            assertEquals(i, cs1.originalIndex(i));
        }
        assertEquals(SEQ, cs1.subSequence(0, cs1.length()).toString());
        assertEquals("012345678", cs1.subSequence(0, cs1.length() - 1).toString());
        assertEquals("01234567", cs1.subSequence(0, cs1.length() - 2).toString());
        assertEquals("0123456", cs1.subSequence(0, cs1.length() - 3).toString());
        assertEquals("012345", cs1.subSequence(0, cs1.length() - 4).toString());
        assertEquals("123456789", cs1.subSequence(1, cs1.length()).toString());
        assertEquals("23456789", cs1.subSequence(2, cs1.length()).toString());
        assertEquals("3456789", cs1.subSequence(3, cs1.length()).toString());
        assertEquals("456789", cs1.subSequence(4, cs1.length()).toString());
        assertEquals("567", cs1.subSequence(5, 8).toString());
    }

    @Test
    public void shouldRemoveFirstRepeatedly() throws Exception {
        ChopSequence cs1 = new ChopSequence(SEQ);
        String exp = cs1.toString();
        while (cs1.length() > 0) {
            cs1.remove(0, 1);
            exp = exp.substring(1);
            assertEquals(exp, cs1.toString());
        }
        assertEquals("", cs1.toString());
        assertEquals(0, cs1.length());
    }

    @Test
    public void shouldRemoveLastRepeatedly() throws Exception {
        ChopSequence cs1 = new ChopSequence(SEQ);
        String exp = cs1.toString();
        while (cs1.length() > 0) {
            cs1.remove(cs1.length() - 1, cs1.length());
            exp = exp.substring(0, exp.length() - 1);
            assertEquals(exp, cs1.toString());
        }
        assertEquals("", cs1.toString());
        assertEquals(0, cs1.length());
    }

    @Test
    public void shouldRemoveNothing() throws Exception {
        ChopSequence cs1 = new ChopSequence(SEQ);
        cs1.remove(1, 1);
        stringAssertEquals(SEQ, cs1);
        cs1.remove(cs1.length() - 1, cs1.length() - 1);
        stringAssertEquals(SEQ, cs1);
    }

    @Test
    public void shouldRemoveOverlapping() throws Exception {
        ChopSequence cs1 = new ChopSequence(SEQ);
        cs1.remove(1, 3);
        stringAssertEquals("03456789", cs1);
        cs1.remove(3, 6);
        stringAssertEquals("03489", cs1);
        cs1.remove(0, 3);
        stringAssertEquals("89", cs1);
        cs1.remove(1, 2);
        stringAssertEquals("8", cs1);
        cs1.remove(0, 1);
        stringAssertEquals("", cs1);
    }

    @Test
    public void shouldRemoveSubseq() throws Exception {
        ChopSequence cs1 = new ChopSequence(SEQ);
        cs1.remove(1, 3);
        stringAssertEquals("03456789", cs1);
        BackedSequence sub1 = cs1.subSequence(0, 4);
        stringAssertEquals("0345", sub1);
        sub1.remove(1, 2);
        stringAssertEquals("045", sub1);
        stringAssertEquals("0456789", cs1);
        BackedSequence sub2 = sub1.subSequence(1, 3);
        stringAssertEquals("45", sub2);
        sub2.remove(1, 2);
        stringAssertEquals("4", sub2);
        stringAssertEquals("046789", cs1);
    }

    @Test
    public void shouldMoveRight() throws Exception {
        assertMoveRight("a,bcdef", 1, 1, "ab,cdef");
        assertMoveRight("a,bcdef", 1, 2, "abc,def");
        assertMoveRight("a,bcdef", 1, 3, "abcd,ef");
        assertMoveRight("a,bcdef", 1, 4, "abcde,f");
        assertMoveRight("a,bcdef", 1, 5, "abcdef,");
    }

    private void assertMoveRight(String input, int start, int moveCount, String expected) {
        ChopSequence cs1 = new ChopSequence(input);
        cs1.moveRight(start, moveCount);
        stringAssertEquals(expected, cs1);
    }

    @Test
    public void shouldMoveLeft() throws Exception {
        assertMoveLeft("abcde,f", 5, 1, "abcd,ef");
        assertMoveLeft("abcde,f", 5, 2, "abc,def");
        assertMoveLeft("abcde,f", 5, 3, "ab,cdef");
        assertMoveLeft("abcde,f", 5, 4, "a,bcdef");
        assertMoveLeft("abcde,f", 5, 5, ",abcdef");
    }

    private void assertMoveLeft(String input, int start, int moveCount, String expected) {
        ChopSequence cs1 = new ChopSequence(input);
        cs1.moveLeft(start, moveCount);
        stringAssertEquals(expected, cs1);
    }

    @Test
    public void shouldFlipA() throws Exception {
        assertFlip("a,b", 1, "b,a");
        assertFlip("a,bc", 1, "bc,a");
        assertFlip("a,bcd", 1, "bcd,a");
        assertFlip("a,bcde", 1, "bcde,a");
        assertFlip("a,bcdef", 1, "bcdef,a");
        assertFlip("ab,c", 2, "c,ab");
        assertFlip("ab,cd", 2, "cd,ab");
        assertFlip("ab,cde", 2, "cde,ab");
        assertFlip("ab,cdef", 2, "cdef,ab");
        assertFlip("ab,cdefg", 2, "cdefg,ab");
        assertFlip("ab,cdefgh", 2, "cdefgh,ab");
        assertFlip("ab,cdefghi", 2, "cdefghi,ab");
        assertFlip("abc,d", 3, "d,abc");
        assertFlip("abc,de", 3, "de,abc");
        assertFlip("abc,def", 3, "def,abc");
        assertFlip("abc,defg", 3, "defg,abc");
        assertFlip("abc,defgh", 3, "defgh,abc");
        assertFlip("abc,defghi", 3, "defghi,abc");
        assertFlip("abc,defghij", 3, "defghij,abc");
    }

    private void assertFlip(String inp, int pivot, String exp) {
        ChopSequence cs1 = new ChopSequence(inp);
        cs1.flip(pivot);
        stringAssertEquals(exp, cs1);
        // make sure that the redirects all still work
        for (int i = 0; i < inp.length(); i++) {
            int orig = cs1.originalIndex(i);
            assertEquals(cs1.charAt(i), inp.charAt(orig));
        }
    }

    private void stringAssertEquals(String expected, CharSequence cs) {
        assertEquals(expected.length(), cs.length());
        assertEquals(expected, cs.toString());
        for (int i = 0; i < expected.length(); i++) {
            assertEquals("bad char at " + i + " expected " + expected + " got " + cs.toString(), expected.charAt(i), cs.charAt(i));
        }
    }
}