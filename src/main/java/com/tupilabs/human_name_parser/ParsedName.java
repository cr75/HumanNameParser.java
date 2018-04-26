package com.tupilabs.human_name_parser;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of a parsing operation, which is a set of name tokens and corresponding semantic labels
 * If you want a segmented name with one string per segment then @see {@link SegmentedName}
 */
public class ParsedName {

    private final String inputName;
    private final List<String> tokens;
    private final List<Label> labels;

    public ParsedName(String inputName, List<String> tokens, List<Label> labels) {
        if (tokens.size() != labels.size()) {
            throw new IllegalArgumentException("labels dont match tokens; " + tokens + " " + labels);
        }
        this.inputName = inputName;
        this.tokens = tokens;
        this.labels = labels;
    }

    public List<String> getTokensWithLabel(Label label) {
        ArrayList<String> output = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i) == label) {
                output.add(tokens.get(i));
            }
        }
        return output;
    }

    public String getToken(int i) {
        return tokens.get(i);
    }

    public Label getLabel(int i) {
        return labels.get(i);
    }

    public String getInputName() {
        return inputName;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public int size() {
        return tokens.size();
    }

    public SegmentedName toSegmented() {
        SegmentedName result = new SegmentedName();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            Label label = labels.get(i);

            switch (label) {
                case FirstInitial:
                    result.setLeadingInit(append(result.getLeadingInit(), token));
                    break;
                case First:
                    result.setFirst(append(result.getFirst(), token));
                    break;
                case Nickname:
                    result.setNicknames(append(result.getNicknames(), token));
                    break;
                case Middle:
                case MiddleInitial:
                    result.setMiddle(append(result.getMiddle(), token));
                    break;
                case Last:
                    result.setLast(append(result.getLast(), token));
                    break;
                case Suffix:
                    result.setSuffix(append(result.getSuffix(), token));
                    break;
                case Salutation:
                    result.setSalutation(append(result.getSalutation(), token));
                    break;
                case Postnominal:
                    result.setPostnominal(append(result.getPostnominal(), token));
                    break;
                case Unknown:
                case Whitespace:
                    // skip
                    break;
                default:
                    throw new IllegalArgumentException("Dont know how to segment label " + label);
            }
        }
        return result;
    }

    private String append(String existing, String toAppend) {
        if (existing == null) {
            return toAppend;
        }
        return existing + " " + toAppend;
    }

    @Override
    public String toString() {
        return "ParsedName{" +
                "inputName='" + inputName + '\'' +
                ", tokens=" + tokens +
                ", labels=" + labels +
                '}';
    }
}
