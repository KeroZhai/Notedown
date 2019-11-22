package com.keroz.notes.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.emoji.EmojiImageType;
import com.vladsch.flexmark.ext.emoji.EmojiShortcutType;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
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
    

    /* 666 */
    public static void main(String[] args) {
//		System.out.println(toStyled("#Test ```Java System.out.println(); ```"));
//		System.out.println(toToc("# Title1 ## Title 2"));
        System.out.println(toPlain("[^1]"));
    }

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
//		StringBuilder styleContent = new StringBuilder();
//		try {
//			FileReader fileReader = new FileReader(CSS_PATH);
//			BufferedReader bufferedReader = new BufferedReader(fileReader);
//			String line;
//			while ((line = bufferedReader.readLine()) != null) {
//				styleContent.append(line + "\n");
//			}
//			bufferedReader.close();
//			htmlStructBuilder.append(String.format("<style type=\"text/css\"> %s </style>", styleContent));
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
//			PrintStream printStream = new PrintStream(
//					new FileOutputStream(new File("C:\\Users\\z21542\\Desktop\\Notes\\Test.html")), true);
//			printStream.println(htmlStructBuilder.toString());
//			printStream.close();
        return htmlStructBuilder.toString();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return "";
    }

    public static String toPlain(String source) {
//	    MutableDataSet OPTIONS = new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(
//	            AutolinkExtension.create(),
//	            EmojiExtension.create(),
//	            StrikethroughExtension.create(),
//	            TaskListExtension.create(),
//	            TablesExtension.create()
//	            ))
//	            // set GitHub table parsing options
//	            .set(TablesExtension.WITH_CAPTION, false)
//	            .set(TablesExtension.COLUMN_SPANS, false)
//	            .set(TablesExtension.MIN_HEADER_ROWS, 1)
//	            .set(TablesExtension.MAX_HEADER_ROWS, 1)
//	            .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
//	            .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
//	            .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)
//	            // setup emoji shortcut options
//	            // uncomment and change to your image directory for emoji images if you have it setup
//	            //.set(EmojiExtension.ROOT_IMAGE_PATH, emojiInstallDirectory())
//	            .set(EmojiExtension.USE_SHORTCUT_TYPE, EmojiShortcutType.GITHUB)
//	            .set(EmojiExtension.USE_IMAGE_TYPE, EmojiImageType.IMAGE_ONLY)
//	            // other options
//	            ;

        Parser parser = Parser.builder(OPTIONS).build();
        HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS).build();

        Node document = parser.parse(source);
//        System.out.println(renderer.render(document));
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
