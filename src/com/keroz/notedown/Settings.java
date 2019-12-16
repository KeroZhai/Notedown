package com.keroz.notedown;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;

import com.keroz.notedown.util.FileUtils;

/**
 *
 * @author z21542
 * @Date 2019年10月17日下午4:13:25
 */
public class Settings {
    
    public static final Setting<String> THEME = new Setting<>("theme", "Warm");
    public static final Setting<Boolean> SHOW_WELCOME = new Setting<>("showWelcome", true);
    public static final Setting<Boolean> DIRECTLY_EXIT = new Setting<>("directlyExit", false);
//  public static final String WORKSPACE_DIR = "workspaceDir";
    public static final Setting<String> FONT_TYPE = new Setting<>("fontType", "微软雅黑");
    public static final Setting<Integer> FONT_STYLE = new Setting<>("fontStyle", SWT.NORMAL);
    public static final Setting<Integer> FONT_SIZE = new Setting<>("fontSize", 14);

    private static final List<Setting<?>> SETTING_KEYS = Arrays
            .asList(new Setting<?>[] { THEME, SHOW_WELCOME, DIRECTLY_EXIT,
                    FONT_SIZE, FONT_STYLE, FONT_TYPE });

    private static Map<Setting<?>, Object> settings = new HashMap<>();

    private static final String SETTINGS_PATH = System.getProperty("user.dir") + File.separator + "settings.properties";

    public static void init() {
        for (String line : FileUtils.readLinesFromFile(SETTINGS_PATH)) {
            String[] entry = line.split("=");
            initSetting(entry[0], entry[1]);
        }
        initDefaultSettings();
    }

    private static void initSetting(String key, String value) {
        SETTING_KEYS.forEach(settingKey -> {
            if (settingKey.getKeyName().equals(key)) {
                settings.put(settingKey, settingKey.valueOf(value));
            }
        });
    }

    private static void initDefaultSettings() {
        SETTING_KEYS.forEach(settingKey -> {
            if (!settings.containsKey(settingKey)) {
                settings.put(settingKey, settingKey.getDefaultValue());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProperty(Setting<T> settingKey) {
        if (settings.containsKey(settingKey)) {
            return (T) settings.get(settingKey);
        }
        return null;
    }

    public static <T> void setProperty(Setting<T> key, T value) {
        if (settings.containsKey(key)) {
            settings.put(key, value);
        }
    }

    public static void updateSettings() {
        StringBuilder stringBuilder = new StringBuilder();
        settings.forEach((key, value) -> stringBuilder.append(key.getKeyName()).append("=").append(value + "\n"));
        FileUtils.writeContentToFile(SETTINGS_PATH, stringBuilder.toString());
    }

}

class Setting<T> {

    public Setting(String keyName, T defaultValue) {
        this.keyName = keyName;
        this.defalutValue = defaultValue;
    }

    private String keyName;
    private T defalutValue;

    public String getKeyName() {
        return keyName;
    }

    public T getDefaultValue() {
        return defalutValue;
    }

    @SuppressWarnings("unchecked")
    public T valueOf(String value) {
        Method valueOfMethod;
        try {
            valueOfMethod = defalutValue.getClass().getMethod("valueOf", String.class);
            return (T) valueOfMethod.invoke(null, value);
        } catch (NoSuchMethodException ignore) {
            /*
             * String doesn't have method "valueOf(String)"
             */
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return (T) value;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defalutValue == null) ? 0 : defalutValue.hashCode());
        result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Setting<?> other = (Setting<?>) obj;
        if (defalutValue == null) {
            if (other.defalutValue != null)
                return false;
        } else if (!defalutValue.equals(other.defalutValue))
            return false;
        if (keyName == null) {
            if (other.keyName != null)
                return false;
        } else if (!keyName.equals(other.keyName))
            return false;
        return true;
    }

}
