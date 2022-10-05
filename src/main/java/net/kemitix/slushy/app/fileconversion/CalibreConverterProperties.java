package net.kemitix.slushy.app.fileconversion;

public interface CalibreConverterProperties {

    String SMARTEN_PUNCTUATION = "smarten-punctuation";
    String CHANGE_JUSTIFICATION = "change-justification";
    String INSERT_BLANK_LINE = "insert-blank-line";
    String REMOVE_PARAGRAPH_SPACING = "remove-paragraph-spacing";
    String ENABLE_HEURISTICS = "enable-heuristics";
    String INSERT_METADATA = "insert-metadata";
    String USE_AUTO_TOC = "use-auto-toc";

    /**
     * Convert plain quotes, dashes and ellipsis to their
     * typographically correct equivalents. For details, see
     * https://daringfireball.net/projects/smartypants
     */
    boolean smartenPunctuation();

    /**
     * Change text justification. A value of "left" converts
     * all justified text in the source to left aligned (i.e.
     * unjustified) text. A value of "justify" converts all
     * unjustified text to justified. A value of "original"
     * (the default) does not change justification in the
     * source file. Note that only some output formats
     * support justification.
     */
    String changeJustification();

    /**
     * Insert a blank line between paragraphs. Will not work
     * if the source file does not use paragraphs (<p> or
     * <div> tags).
     */
    boolean insertBlankLine();

    /**
     * Remove spacing between paragraphs. Also sets an indent
     * on paragraphs of 1.5em. Spacing removal will not work
     * if the source file does not use paragraphs (<p> or
     * <div> tags).
     */
    boolean removeParagraphSpacing();

    /**
     * Enable heuristic processing. This option must be set
     * for any heuristic processing to take place.
     */
    boolean enableHeuristics();

    /**
     * Insert the book metadata at the start of the book.
     * This is useful if your e-book reader does not support
     * displaying/searching metadata directly.
     */
    boolean insertMetadata();

    /**
     * Normally, if the source file already has a Table of
     * Contents, it is used in preference to the auto-
     * generated one. With this option, the auto-generated
     * one is always used.
     */
    boolean useAutoToc();
}
