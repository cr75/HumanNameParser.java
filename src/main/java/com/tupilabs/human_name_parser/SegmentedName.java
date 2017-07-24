package com.tupilabs.human_name_parser;

/**
 * The tagged name segmented (and appended when multiple tokens in a segment)
 * @author Steve Ash
 */
public class SegmentedName {

    private String leadingInit;
    private String first;
    private String nicknames;
    private String middle;
    private String last;
    private String suffix;
    private String salutation;
    private String postnominal;

    public String getLeadingInit() {
        return leadingInit;
    }

    public void setLeadingInit(String leadingInit) {
        this.leadingInit = leadingInit;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getNicknames() {
        return nicknames;
    }

    public void setNicknames(String nicknames) {
        this.nicknames = nicknames;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getPostnominal() {
        return postnominal;
    }

    public void setPostnominal(String postnominal) {
        this.postnominal = postnominal;
    }
}
