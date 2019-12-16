package com.keroz.notedown;

import java.io.File;

/**
 *
 * @author z21542
 * @Date 2019年10月23日下午6:02:19
 */
public class Resources {
	
    /**
     * URI形式的 所以用 / 
     */
	public static final String RESOURCE_PATH = System.getProperty("user.dir") + File.separator + "resources";
	public static final String RESOURCE_URI = "file:///" + RESOURCE_PATH.replace("\\", "/");
    public static final String WARM_MD_CSS_URI = RESOURCE_URI + "/markdown_warm.css";
	public static final String LIGHT_MD_CSS_URI = RESOURCE_URI + "/markdown_light.css";
	public static final String DARK_MD_CSS_URI = RESOURCE_URI + "/markdown_dark.css";
	public static final String CODE_CSS_URI = RESOURCE_URI + "/rainbow.css";
	public static final String CODE_JS_URI = RESOURCE_URI + "/highlight.pack.js";
	public static final String TOC_JS_URI = RESOURCE_URI + "/toc.js";
	public static final String LIGHT_TOC_CSS_URI = RESOURCE_URI + "/toc_light.css";
	public static final String DARK_TOC_CSS_URI = RESOURCE_URI + "/toc_dark.css";
	public static final String ICON_PATH = RESOURCE_PATH + File.separator + "notes.ico";

}
