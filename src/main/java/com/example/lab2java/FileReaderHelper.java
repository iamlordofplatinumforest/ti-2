package com.example.lab2java;

import java.io.*;
import java.nio.file.*;

public class FileReaderHelper {

    public static String readFileAsBinaryString(File file) throws IOException {
        Path path = file.toPath();
        StringBuilder binaryString = new StringBuilder();

        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(path))) {
            int byteRead;
            while ((byteRead = inputStream.read()) != -1) {
                binaryString.append(String.format("%8s", Integer.toBinaryString(byteRead & 0xFF)).replace(' ', '0'));
            }
        }

        return binaryString.toString();
    }
}
