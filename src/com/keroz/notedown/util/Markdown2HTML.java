package com.keroz.notedown.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import com.keroz.notedown.Resources;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.emoji.EmojiImageType;
import com.vladsch.flexmark.ext.emoji.EmojiShortcutType;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 *
 * @author z21542
 * @Date 2019年10月18日下午3:10:44
 */
public class Markdown2HTML {
    
    private static DataHolder OPTIONS = PegdownOptionsAdapter
            .flexmarkOptions(true, Extensions.ALL, EmojiExtension.create(), TaskListExtension.create(),
                    FootnoteExtension.create())
            .toMutable().set(EmojiExtension.USE_IMAGE_TYPE, EmojiShortcutType.GITHUB)
            .set(EmojiExtension.USE_IMAGE_TYPE, EmojiImageType.UNICODE_FALLBACK_TO_IMAGE);
    
    public static void export(String source, String destPath) {
        if (destPath.contains(".pdf")) {
            String plain = toPlain(source);
            String toc = toToc(source);
            StringBuilder htmlStructBuilder = new StringBuilder();
            htmlStructBuilder.append("<html>");
            htmlStructBuilder.append("<head>");
            htmlStructBuilder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
            htmlStructBuilder
                    .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + Resources.LIGHT_MD_CSS_URI + "\"/>");
            htmlStructBuilder.append("</head>");
            htmlStructBuilder.append(String.format("<body class='markdown-body'>%s</body>", toc + plain));
            htmlStructBuilder.append("</html>");
            PdfConverterExtension.exportToPdf(destPath, htmlStructBuilder.toString(), "", OPTIONS);
        } else if (destPath.contains(".html")) {
            File file = new File(destPath);
            try (PrintWriter printWriter = new PrintWriter(file);
                    BufferedWriter writer = new BufferedWriter(printWriter);) {
                writer.write(toStyled(source));
            } catch (FileNotFoundException e) {
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static String toStyled(String source) {
        String plain = toPlain(source);
        String toc = toToc(source);
        StringBuilder htmlStructBuilder = new StringBuilder();
        htmlStructBuilder.append("<html>");
        htmlStructBuilder.append("<head>");
        htmlStructBuilder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        htmlStructBuilder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + ThemeUtils.getMarkdownCSSURI() + "\"/>");
        htmlStructBuilder
                .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + Resources.CODE_CSS_URI + "\"/>");
        htmlStructBuilder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + ThemeUtils.getTocCSSURI() + "\"/>");
        htmlStructBuilder.append("<script src=\"" + Resources.CODE_JS_URI + "\"></script>");
        htmlStructBuilder.append("<script type=\"text/javascript\">hljs.initHighlightingOnLoad();</script>");
        htmlStructBuilder.append("</head>");
        htmlStructBuilder.append(String.format("<body class='markdown-body'>%s</body>", toc + plain));
        htmlStructBuilder.append("</html>");
        htmlStructBuilder.append("<script src=\"" + Resources.TOC_JS_URI + "\"></script>");
        return htmlStructBuilder.toString();
    }

    public static String toPlain(String source) {
        Parser parser = Parser.builder(OPTIONS).build();
        HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS).build();
        Node document = parser.parse(source);
        return renderer.render(document);
    }

    public static String toToc(String source) {
        MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(TocExtension.create()))
                .set(TocExtension.LEVELS, 255).set(TocExtension.TITLE, "Quick Access")
                .set(TocExtension.DIV_CLASS, "toc");
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse("[TOC]\n" + source);
        String html = renderer.render(document);
        if (html.indexOf("<div class=\"toc\">") == -1) {
            return "";
        } else {
            return "<div id=\"tocDiv\" class=\"tocDiv\">" + html.substring(0, 6 + html.indexOf("</div>")) + "</div>";
        }
    }
}
