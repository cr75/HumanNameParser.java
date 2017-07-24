package com.tupilabs.human_name_parser;

import com.google.common.base.Preconditions;

/**
 * A charsequence that lets you "remove" subsequences allowing the CharSequence to appear as a smaller
 * sequence -- but all of the original mappings to original characters are maintained
 * @author Steve Ash
 */
public class ChopSequence implements BackedSequence {

    private final StringBuilder original;
    private final int[] redirect;
    private int length;

    public ChopSequence(CharSequence original) {

        this.original = new StringBuilder(original);
        this.redirect = new int[original.length()];
        this.length = original.length();
        // init for no redirect
        for (int i = 0; i < original.length(); i++) {
            redirect[i] = i;
        }
    }

    /**
     * Removes a chunk of the chars from this sequence logically, meaning that subsequent
     * calls to this sequence will act as if all of the chars were removed entirely
     * @param start
     * @param end
     */
    @Override
    public void remove(final int start, final int end) {
        if (start < 0 || end > length) {
            throw new StringIndexOutOfBoundsException(end);
        }
        int j = start;
        for (int i = end; i < length; i++) {
            redirect[j++] = redirect[i];
        }
        length -= (end - start);
    }

    @Override
    public int originalIndex(int index) {
        return redirect[index];
    }

    @Override
    public void flip(int pivot) {
        flipRange(0, pivot, length - 1);
    }

    public void setCharAt(int index, char newChar) {
        int target = originalIndex(index);
        this.original.setCharAt(target, newChar);
    }

    void flipRange(final int start, final int pivot, final int endIncl) {
        Preconditions.checkState(start <= pivot);
        Preconditions.checkState(pivot <= endIncl);
        int before = pivot - start;
        int after = endIncl - pivot;
        int length = endIncl - start + 1;
        int min = Math.min(before, after);

        if (after == before) {
            swap(0, length - min, min);
        } else if (after > before) {
            // small is on the left
            int sm = before;
            int bg = after;
            rotateLeft(start, endIncl, sm);
            moveRight(start, bg);
        } else {
            // small is on the right
            int sm = after;
            int bg = before;
            rotateRight(start, endIncl, sm);
            moveLeft(endIncl, bg);
        }
    }

    private void flopRight(int start, int endIncl, int count) {
        if (start >= endIncl) {
            return;
        }
        int target = endIncl - count + 1;
        int length = endIncl - start + 1;
        if (start + count <= target) {
            // can flop right
            swap(start, target, count);
            flopRight(start, endIncl - count, count);
        } else {
            // don't have enough room recurse on remainder
            flopLeft(start, target, length - count);
        }
    }

    private void flopLeft(int start, int endIncl, int count) {
        if (start >= endIncl) {
            return;
        }
        int source = endIncl - count + 1;
        int length = endIncl - start + 1;
        if (start + count <= source) {
            // can flop left
            swap(source, start, count);
            flopLeft(start + count, endIncl, count);
        } else {
            // don't have enough room recurse on remainder
            flopRight(start, endIncl, length - count);
        }
    }

    void rotateLeft(int start, int endIncl, int count) {
        for (int i = 0; i < count; i++) {
            int first = redirect[start];
            for (int j = start; j < endIncl; j++) {
                redirect[j] = redirect[j + 1];
            }
            redirect[endIncl] = first;
        }
    }

    void rotateRight(int start, int endIncl, int count) {
        for (int i = 0; i < count; i++) {
            int last = redirect[endIncl];
            for (int j = endIncl; j > 0; j--) {
                redirect[j] = redirect[j - 1];
            }
            redirect[start] = last;
        }
    }

    void moveRight(int start, int count) {
        int temp;
        for (int i = 0; i < count; i++) {
            temp = redirect[start + i + 1];
            redirect[start + i + 1] = redirect[start + i];
            redirect[start + i] = temp;
        }
    }

    void moveLeft(int start, int count) {
        int temp;
        for (int i = 0; i < count; i++) {
            temp = redirect[start - i - 1];
            redirect[start - i - 1] = redirect[start - i];
            redirect[start - i] = temp;
        }
    }

    void swap(int source, int target, int len) {
        int temp;
        for (int i = 0; i < len; i++) {
            temp = redirect[target + i];
            redirect[target + i] = redirect[source + i];
            redirect[source + i] = temp;
        }
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int index) {
        int originalIndex = originalIndex(index);
        return original.charAt(originalIndex);
    }

    @Override
    public BackedSequence subSequence(int start, int end) {
        if (start < 0 || end > length) {
            throw new StringIndexOutOfBoundsException(end);
        }
        return new ChopSubsequence(start, end - start);
    }

    @Override
    public String toString() {
        return makeString(this);
    }

    private static String makeString(CharSequence that) {
        StringBuilder sb = new StringBuilder(that.length());
        for (int i = 0; i < that.length(); i++) {
            sb.append(that.charAt(i));
        }
        return sb.toString();
    }

    private class ChopSubsequence implements BackedSequence {

        private final int offset;
        private int length;

        private ChopSubsequence(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }

        @Override
        public int length() {
            return length;
        }

        @Override
        public char charAt(int index) {
            if (index >= length) {
                throw new StringIndexOutOfBoundsException(index);
            }
            int outerIdx = offset + index;
            return ChopSequence.this.charAt(outerIdx);
        }

        @Override
        public int originalIndex(int index) {
            int outerIdx = offset + index;
            return ChopSequence.this.originalIndex(outerIdx);
        }

        @Override
        public void flip(int pivot) {
            Preconditions.checkState(pivot < length);
            int outerStart = offset;
            int outerEnd = offset + length - 1;
            int outerPivot = offset + pivot;
            ChopSequence.this.flipRange(outerStart, outerPivot, outerEnd);
        }

        @Override
        public void remove(int start, int end) {
            int outerStart = offset + start;
            int outerEnd = offset + end;
            length -= (end - start);
            ChopSequence.this.remove(outerStart, outerEnd);
        }

        @Override
        public BackedSequence subSequence(int start, int end) {
            int outerStart = offset + start;
            int outerEnd = offset + end;
            return ChopSequence.this.subSequence(outerStart, outerEnd);
        }

        @Override
        public String toString() {
            return ChopSequence.makeString(this);
        }
    }
}
