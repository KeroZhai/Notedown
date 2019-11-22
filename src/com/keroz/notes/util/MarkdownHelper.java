package com.keroz.notes.util;

/**
 *
 * @author z21542
 * @Date 2019年11月15日下午2:11:11
 */
public class MarkdownHelper {

    public static final String STAR_BOLD_MARK = "**";
    public static final String UNDERLINE_BOLD_MARK = "__";
    public static final String STAR_ITALIC_MARK = "*";
    public static final String UNDERLINE_ITALIC_MARK = "_";
    public static final String CODE_MARK = "`";
    public static final String CODE_BLOCK_MARK = "```\n";
    public static final String STRIKE_THROUGH_MARK = "~~";

    public static final MarkdownHelper BOLD = new MarkdownHelper("Bold Mark\t\"**\"", STAR_BOLD_MARK, UNDERLINE_BOLD_MARK);
    public static final MarkdownHelper ITALIC = new MarkdownHelper("Italic Mark\t\"*\"", STAR_ITALIC_MARK,
            UNDERLINE_ITALIC_MARK);
    public static final MarkdownHelper CODE = new MarkdownHelper("Code Mark\t\"`\"", CODE_MARK);
    public static final MarkdownHelper CODE_BLOCK = new MarkdownHelper("Code Block Mark\t\"```\"", CODE_BLOCK_MARK);
    public static final MarkdownHelper STRIKE_THROUGH = new MarkdownHelper("Strike-Through Mark\t\"~~\"",
            STRIKE_THROUGH_MARK);
    public static final MarkdownHelper OTHER = new MarkdownHelper("Other...", "");

    public static String setBold(String source) {
        return wrapWith(source, STAR_BOLD_MARK);
    }

    public static String setItalic(String source) {
        return wrapWith(source, STAR_ITALIC_MARK);
    }

    public static String setCode(String source) {
        return wrapWith(source, CODE_MARK);
    }

    public static String setCodeBlock(String source) {
        return wrapWith(source, CODE_BLOCK_MARK);
    }

    public static String setStrikeThrough(String source) {
        return wrapWith(source, STRIKE_THROUGH_MARK);
    }

    public static String wrapWith(String source, String mark) {
        return mark + source + mark;
    }

    private String type;
    private String mark;
    private String oppositeMark;
    private String optionalMark;

    private MarkdownHelper(String type, String mark) {
        this.type = type;
        this.mark = mark;
        this.optionalMark = mark;
        this.oppositeMark = new StringBuilder(mark).reverse().toString();
    }

    private MarkdownHelper(String type, String mark, String optionalMark) {
        this(type, mark);
        this.optionalMark = optionalMark;
    }

    public String wrap(String source) {
        if (isAlreadyStyled(source)) {
            return source.substring(mark.length(), source.length() - mark.length());
        }
        return mark + source + oppositeMark;
    }
    
    private boolean isAlreadyStyled(String source) {
        return ((source.startsWith(mark.replaceAll("\n", "")) && source.endsWith(oppositeMark.replaceAll("\n", ""))) || (source.startsWith(optionalMark) && source.endsWith(optionalMark)));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
        this.optionalMark = mark;
        this.oppositeMark = new StringBuilder(mark).reverse().toString();
    }

}
