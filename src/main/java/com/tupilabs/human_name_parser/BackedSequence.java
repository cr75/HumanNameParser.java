package com.tupilabs.human_name_parser;

/**
 * @author Steve Ash
 */
public interface BackedSequence extends CharSequence {

    @Override
    BackedSequence subSequence(int start, int end);

    void remove(int start, int end);

    int originalIndex(int i);

    void flip(int pivot);
}
