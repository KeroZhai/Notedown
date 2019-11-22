package com.keroz.notes.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.channels.FileChannel;

/**
 *
 * @author z21542
 * @Date 2019年11月15日上午11:52:44
 */
public class FileUtils {

    public static void copyFile(String src, String dst) throws FileNotFoundException {
        copyFile(new File(src), new File(dst));
    }

    public static void copyFile(File srcFile, File dstFile) throws FileNotFoundException {
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source image file doesn't exist.");
        }
        if (!dstFile.exists()) {
            try {
                dstFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileChannel inputChannel = new FileInputStream(srcFile).getChannel();
                FileChannel outputChannel = new FileOutputStream(dstFile).getChannel();) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readContentFromFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    
    public static String[] readLinesFromFile(String filePath) {
        return readLinesFromFile(new File(filePath));
    }
    public static String[] readLinesFromFile(File file) {
        return readContentFromFile(file).split("\n");
    }
    
    public static void writeContentToFile(String filePath, String content) {
        writeContentToFile(new File(filePath), content);
    }

    public static void writeContentToFile(File file, String content) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
                Writer writer = new BufferedWriter(outputStreamWriter);) {
            writer.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
