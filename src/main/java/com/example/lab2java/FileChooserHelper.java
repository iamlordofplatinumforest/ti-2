package com.example.lab2java;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class FileChooserHelper {

    public static File openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Все файлы", "*.*"));
        return fileChooser.showOpenDialog(new Stage());
    }
}
