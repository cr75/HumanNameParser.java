package com.tupilabs.human_name_parser;

import static org.apache.commons.lang3.StringUtils.indexOf;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Ok fine I give up. I'm going to go ahead and refactor the original code to at least make it useable by syngen
 * and not be completely ridiculous in its implementation. I am not contributing any intellectual property to its
 * intelligence, it's still a naive name parser based on simple regular expressions
 *
 * Usage:
 * Construct a new parser then call {@link #parse(String)} which returns a ParsedName which has each token detected
 * and its correpsonding labels. If you want to collapse/segment those tagged strings then call
 * {@link ParsedName#toSegmented()} to get a segmented name back
 */
public class HumanNameParser {

    private static final CharMatcher WS = CharMatcher.whitespace();
    private static final Pattern MULTI_WS = Pattern.compile("\\s{2,}");
    private static final CharMatcher TRIM_CHARS = CharMatcher.anyOf("()\"',;*\\/| ");

    private final Pattern nicknamesPat;
    private final Pattern postnominalPat;
    private final Pattern suffixPat;
    private final Pattern lastPat;
    private final Pattern salutationsPat;
    private final Pattern leadingInitPat;
    private final Pattern firstPat;
    private final Pattern middlePat;
    private final Pattern middleInitPat;
    private final boolean trimTokens;

    public HumanNameParser() {this(true);}

    public HumanNameParser(boolean trimTokens) {
        List<String> salutations1 = Arrays.asList("mr", "master", "mister",
                "mrs", "miss", "ms", "dr", "prof", "rev", "fr", "judge", "honorable", "hon");
        List<String> suffixes1 = Arrays.asList("jr", "sr", "2", "ii",
                "iii", "iv", "v", "senior", "junior", "2d", "2nd", "3d", "3rd", "4th");
        List<String> postnominals1 = Arrays.asList("phd", "ph.d.", "ph.d",
                "esq", "esquire", "apr", "rph", "pe", "md", "ma", "dmd", "cme",
                "dds", "cpa", "dvm", "rdh", "r.d.h.", "d.d.s.", "d.m.d.");
        List<String> prefixes1 = Arrays.asList("bar", "ben", "bin", "da", "dal",
                "de la", "de", "del", "der", "di", "ibn", "la", "le",
                "san", "st", "ste", "van", "van der", "van den", "vel",
                "von");

        String suffixes = StringUtils.join(suffixes1, "\\.*|") + "\\.*";
        String postnominals = StringUtils.join(postnominals1, "\\.*|") + "\\.*";
        String salutations = StringUtils.join(salutations1, "\\.*|") + "\\.*";
        String prefixes = StringUtils.join(prefixes1, " |") + " ";

        // The regex use is a bit tricky.  *Everything* matched by the regex will be replaced,
        // but you can select a particular parenthesized submatch to be returned.
        // Also, note that each regex requires that the preceding ones have been run, and matches chopped out.
        String nicknamesRegex = "(?:(['*\"|\\\\]{1,2})([a-z A-Z']+?)\\1|\\((['*\"|\\\\]{1,2})([a-z A-Z']+?)\\3\\)|\\(([a-z A-Z']+?)\\))"; // names that starts or end w/ an apostrophe break this
        String postnominalRegex = "[,| ]+((" + postnominals + ")$)";
        String suffixRegex = "[,| ]+((" + suffixes + ")$)";
        String lastRegex = "(?!^)\\b([^ ]+ y |" + prefixes + ")*[^ ]+$";
        String salutationsRegex = "^(" + salutations + "\\b)(\\.|\\s)+"; //salutation plus a word boundary \b
        String leadingInitRegex = "(^(.\\.*)(?= \\p{L}{2}))"; // note the lookahead, which isn't returned or replaced
        String firstRegex = "^([^ ]+)";
        String middleRegex = "^(\\w{2,})";
        String middleInitRegex = "^(\\w\\.?)(?!\\w)";

        nicknamesPat = Pattern.compile(nicknamesRegex, Pattern.CASE_INSENSITIVE);
        postnominalPat = Pattern.compile(postnominalRegex, Pattern.CASE_INSENSITIVE);
        suffixPat = Pattern.compile(suffixRegex, Pattern.CASE_INSENSITIVE);
        lastPat = Pattern.compile(lastRegex, Pattern.CASE_INSENSITIVE);
        salutationsPat = Pattern.compile(salutationsRegex, Pattern.CASE_INSENSITIVE);
        leadingInitPat = Pattern.compile(leadingInitRegex, Pattern.CASE_INSENSITIVE);
        firstPat = Pattern.compile(firstRegex, Pattern.CASE_INSENSITIVE);
        middlePat = Pattern.compile(middleRegex, Pattern.CASE_INSENSITIVE);
        middleInitPat = Pattern.compile(middleInitRegex, Pattern.CASE_INSENSITIVE);
        this.trimTokens = trimTokens;
    }

    public ParsedName parse(String fullName) {

        ParseWork work = new ParseWork(fullName);
        chopWithRegex(work, nicknamesPat, 0, Label.Nickname);
        chopWithRegex(work, postnominalPat, 0, Label.Postnominal);
        chopWithRegex(work, suffixPat, 0, Label.Suffix);

        // flip the before-comma and after-comma parts of the name
        int commaIndex = indexOf(work.current, ",");
        if (commaIndex >= 0) {
            work.current.flip(commaIndex);
            // now remove the comma, which likely moved
            commaIndex = indexOf(work.current, ",");
            // we want to make this comma a space for the purpose of regexes; this will be marked by trim later
            work.current.setCharAt(commaIndex, ' ');
        }

        chopWithRegex(work, lastPat, 0, Label.Last);
        chopWithRegex(work, salutationsPat, 0, Label.Salutation);
        chopWithRegex(work, leadingInitPat, 0, Label.FirstInitial);
        chopWithRegex(work, firstPat, 0, Label.First);

        boolean matchedLast;
        do {
            matchedLast = chopWithRegex(work, middleInitPat, 0, Label.MiddleInital);
            matchedLast |= chopWithRegex(work, middlePat, 0, Label.Middle);
        } while (matchedLast);

        return makeParsedNameFrom(work, trimTokens);
    }

    static ParsedName makeParsedNameFrom(ParseWork work, boolean trimTokens) {
        List<String> tokens = Lists.newArrayList();
        List<Label> labels = Lists.newArrayList();
        // splt seq out
        Preconditions.checkState(work.fullString.length() == work.posToLabel.length);
        Label label = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < work.fullString.length(); i++) {
            char c = work.fullString.charAt(i);
            Label next = work.posToLabel[i];
            if (next != label) {
                if (label != null) {
                    String lastToken = sb.toString();
                    if (label != Label.Whitespace) {
                        if (trimTokens) {
                            lastToken = TRIM_CHARS.trimFrom(lastToken);
                        }
                        tokens.add(lastToken);
                        labels.add(label);
                    }
                    sb.delete(0, sb.length());
                }
                label = next;
            }
            sb.append(c);
        }
        // might have some buffer left over
        if (sb.length() > 0) {
            String lastToken = sb.toString();
            if (label != null && label != Label.Whitespace) {
                if (trimTokens) {
                    lastToken = TRIM_CHARS.trimFrom(lastToken);
                }
                tokens.add(lastToken);
                labels.add(label);
            }
        }

        return new ParsedName(work.fullString, tokens, labels);
    }

    boolean chopWithRegex(ParseWork work, Pattern pattern, int group, Label label) {
        Matcher matcher = pattern.matcher(work.current);
        if (matcher.find()) {
            Preconditions.checkState(matcher.groupCount() > group, "wrong group match %s in pattern %s", work, pattern, group);
            work.mark(matcher.start(group), matcher.end(group) - 1, label);
            work.remove(matcher.start(), matcher.end() - 1);
            work.normalizeCurrent();
            return true;
        }
        return false;
    }

    static class ParseWork {
        final String fullString;
        final Label[] posToLabel;
        final ChopSequence current;

        ParseWork(String fullString) {
            this.fullString = fullString;
            posToLabel = new Label[fullString.length()];
            for (int i = 0; i < fullString.length(); i++) {
                posToLabel[i] = Label.Unknown;
            }
            this.current = new ChopSequence(fullString);
        }

        public void remove(int startIncl, int endIncl) {
            current.remove(startIncl, endIncl + 1); // incl to excl
        }

        public void normalizeCurrent() {
            if (current.length() == 0) return;

            while (current.length() > 0) {
                if (current.charAt(0) == ' ') {
                    markFromCurrent(0);
                    current.remove(0, 1);
                } else {
                    break;
                }
            }
            while (current.length() > 0) {
                int len = current.length();
                if (current.charAt(len - 1) == ' ') {
                    markFromCurrent(len - 1);
                    current.remove(len - 1, len);
                } else {
                    break;
                }
            }
            int i = 0;
            boolean inWs = false;
            while (i < current.length()) {
                if (current.charAt(i) == ' ') {
                    if (inWs) {
                        markFromCurrent(i);
                        current.remove(i, i + 1);
                        continue; // dont increment
                    } else {
                        inWs = true;
                    }
                } else {
                    inWs = false;
                }
                i += 1;
            }
        }

        private void markFromCurrent(int i) {
            int orig = current.originalIndex(i);
            Preconditions.checkState(posToLabel[orig] == Label.Unknown, "overwriting ", posToLabel[orig]);
            posToLabel[orig] = Label.Whitespace;
        }

        public void mark(int startIncl, int endIncl, Label label) {
            // indexes passed in are relative to _current_ not to whole thing
            for (int i = startIncl; i <= endIncl; i++) {
                int orig = current.originalIndex(i);
                Preconditions.checkState(posToLabel[orig] == Label.Unknown, "overwriting ", posToLabel[orig]);
                posToLabel[orig] = label;
            }
        }

        @Override
        public String toString() {
            return "ParseWork{" +
                    "fullString='" + fullString + '\'' +
                    ", current=" + current +
                    '}';
        }
    }
}
