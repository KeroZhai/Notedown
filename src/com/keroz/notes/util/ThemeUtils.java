package com.keroz.notes.util;

import org.eclipse.swt.graphics.RGB;

/**
 *
 * @author z21542
 * @Date 2019年11月20日下午4:44:14
 */
public class ThemeUtils {
    
    public static final String WARM_THEME = "Warm";
    public static final String LIGHT_THEME = "Light";
    public static final String DARK_THEME = "Dark";
    
    private static final RGB LIGHT_TREE_FG = new RGB(0, 0, 0);
    private static final RGB LIGHT_TREE_BG = new RGB(253, 252, 248);
    private static final RGB LIGHT_SRC_FG = new RGB(0, 0, 0);
    private static final RGB LIGHT_SRC_BG = new RGB(255, 255, 255);
    private static final RGB LIGHT_GLOBAL_FG = new RGB(0, 0, 0);
    private static final RGB LIGHT_GLOBAL_BG= new RGB(240, 240, 240);
    
    private static final RGB DARK_TREE_FG = new RGB(200, 200, 200);
    private static final RGB DARK_TREE_BG = new RGB(50, 50, 50);
    private static final RGB DARK_SRC_FG = new RGB(200, 200, 200);
    private static final RGB DARK_SRC_BG = new RGB(45, 45, 45);
    private static final RGB DARK_GLOBAL_FG = new RGB(200, 200, 200);
    private static final RGB DARK_GLOBAL_BG= new RGB(50, 50, 50);
    
    
    public static RGB getTreeForegroundRgb() {
        return !getCurrentTheme().equals(DARK_THEME) ? LIGHT_TREE_FG : DARK_TREE_FG;
    }
    
    public static RGB getTreeBackgroundRgb() {
        return !getCurrentTheme().equals(DARK_THEME) ? LIGHT_TREE_BG : DARK_TREE_BG;
    }
    
    public static RGB getSourceForegroundRgb() {
        return !getCurrentTheme().equals(DARK_THEME) ? LIGHT_SRC_FG : DARK_SRC_FG;
    }
    
    public static RGB getSourceBackgroundRgb() {
        return !getCurrentTheme().equals(DARK_THEME) ? LIGHT_SRC_BG : DARK_SRC_BG;
    }
    
    public static RGB getGlobalForegroundRGB() {
        return !getCurrentTheme().equals(DARK_THEME) ? LIGHT_GLOBAL_FG : DARK_GLOBAL_FG;
    }
    
    public static RGB getGlobalBackgroundRGB() {
        return !getCurrentTheme().equals(DARK_THEME) ? LIGHT_GLOBAL_BG : DARK_GLOBAL_BG;
    }
    
    public static String getMarkdownCSSURI() {
        switch (getCurrentTheme()) {
            case WARM_THEME:
                return Resources.WARM_MD_CSS_URI;
            case LIGHT_THEME:
                return Resources.LIGHT_MD_CSS_URI;
            case DARK_THEME:
                return Resources.DARK_MD_CSS_URI;
            default:
                return Resources.WARM_MD_CSS_URI;
        }
    }
    
    public static String getTocCSSURI() {
        return !getCurrentTheme().equals(DARK_THEME) ? Resources.LIGHT_TOC_CSS_URI : Resources.DARK_TOC_CSS_URI;
    }
    
    private static String getCurrentTheme() {
        return Settings.getProperty(Settings.THEME);
    }
}
