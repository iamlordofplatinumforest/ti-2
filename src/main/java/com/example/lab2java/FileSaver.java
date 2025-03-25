package com.example.lab2java;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSaver {

    public static void saveBytesToFile(byte[] data, String format, Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(format.toUpperCase() + " files", "*." + format));
        fileChooser.setInitialFileName("output." + format);

        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            } catch (IOException e) {
                showErrorAlert("Ошибка при сохранении файла");
            }
        }
    }

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}

