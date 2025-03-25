package com.example.lab2java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileAction {
    public static String readFileAsBinaryString(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);

        StringBuilder binaryString = new StringBuilder();

        int byteRead;
        while ((byteRead = fis.read()) != -1) {
            String byteAsBinary = String.format("%8s", Integer.toBinaryString(byteRead)).replace(' ', '0');
            binaryString.append(byteAsBinary);
        }

        fis.close();

        return binaryString.toString();
    }
}
