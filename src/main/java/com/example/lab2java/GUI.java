package com.example.lab2java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GUI extends Application {
    private TextArea leftText;
    private TextArea textResult;
    private TextField seedEntry;
    private TextArea keyEntry;
    private ComboBox<String> formatBox;
    private Stage primaryStage;
    private byte[] fileBytes;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPosition(0, 0.5);

        VBox leftPanel = createLeftPanel();
        splitPane.getItems().add(leftPanel);

        VBox rightPanel = createRightPanel();
        splitPane.getItems().add(rightPanel);

        Scene scene = new Scene(splitPane, 700, 430);
        primaryStage.setTitle("LFSR");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new javafx.geometry.Insets(10));

        Label seedLbl = new Label("Начальное состояние регистра:");
        leftPanel.getChildren().add(seedLbl);

        seedEntry = new TextField();
        seedEntry.setPrefWidth(200);
        leftPanel.getChildren().add(seedEntry);
        seedEntry.setTextFormatter(new TextFormatter<String>(change -> {
            String str = change.getControlNewText();
            if (str.matches("[01]*") && str.length() <= 23) {
                return change;
            }
            return null;
        }));

        Label textLbl = new Label("Исходные данные:");
        leftPanel.getChildren().add(textLbl);

        leftText = new TextArea();
        leftText.setWrapText(true);
        leftText.setPrefSize(350, 150);
        leftPanel.getChildren().add(leftText);

        Button fileSeedButton = new Button("Загрузить файл");
        fileSeedButton.setOnAction(event -> openFile());
        leftPanel.getChildren().add(fileSeedButton);

        Button cipherButton = new Button("Шифровать");
        cipherButton.setOnAction(event -> CipherClicked());

        Button decipherButton = new Button("Дешифровать");
        decipherButton.setOnAction(event -> CipherClicked());

        HBox leftButtons = new HBox(30, cipherButton, decipherButton);
        leftButtons.setAlignment(Pos.CENTER);
        leftPanel.getChildren().add(leftButtons);

        return leftPanel;
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(10);
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setPadding(new javafx.geometry.Insets(10));

        Label keyLbl = new Label("Сгенерированный ключ:");
        rightPanel.getChildren().add(keyLbl);

        keyEntry = new TextArea();
        keyEntry.setWrapText(true);
        keyEntry.setPrefSize(350, 150);
        rightPanel.getChildren().add(keyEntry);

        Label resultLbl = new Label("Результат:");
        rightPanel.getChildren().add(resultLbl);

        textResult = new TextArea();
        textResult.setWrapText(true);
        textResult.setPrefSize(350, 150);
        rightPanel.getChildren().add(textResult);

        Button changeButton = new Button("← →");
        changeButton.setOnAction(event -> ChangeTexts());
        rightPanel.getChildren().add(changeButton);

        formatBox = new ComboBox<>();
        formatBox.getItems().addAll("jpg", "png", "mov", "aiff", "txt", "docx", "mp3", "aif");
        formatBox.setValue("jpg");

        Button saveFileButton = new Button("Сохранить в файл");
        saveFileButton.setOnAction(event -> saveToFile());

        HBox rightButtons = new HBox(30, formatBox, saveFileButton);
        rightButtons.setAlignment(Pos.CENTER);
        rightPanel.getChildren().add(rightButtons);

        return rightPanel;
    }

    private byte[] Ciphering(byte[] message, byte[] key) {
        int len = Math.min(message.length, key.length);
        byte[] resultBytes = new byte[len];
        for (int i = 0; i < len; i++) {
            resultBytes[i] = (byte) (message[i] ^ key[i]);
        }
        return resultBytes;
    }

    private String bytesToBinaryString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return sb.toString();
    }

    private byte[] binaryStringToBytes(String binary) {
        int byteCount = binary.length() / 8;
        byte[] data = new byte[byteCount];
        for (int i = 0; i < byteCount; i++) {
            String byteStr = binary.substring(i * 8, (i + 1) * 8);
            data[i] = (byte) Integer.parseInt(byteStr, 2);
        }
        return data;
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            Task<byte[]> fileReadTask = new Task<>() {
                @Override
                protected byte[] call() throws Exception {
                    return Files.readAllBytes(file.toPath());
                }
            };

            fileReadTask.setOnSucceeded(event -> {
                fileBytes = fileReadTask.getValue();
                leftText.setText(bytesToBinaryString(fileBytes));
            });
            fileReadTask.setOnFailed(event -> {
                showErrorAlert("Ошибка при чтении файла");
            });

            new Thread(fileReadTask).start();
        }
    }

    private void CipherClicked() {
        String plainKey = seedEntry.getText();
        if (fileBytes == null) {
            showErrorAlert("Файл не загружен");
            return;
        } else if (plainKey.isEmpty()) {
            showErrorAlert("Ключ пуст");
            return;
        } else if (plainKey.length() < 23) {
            showErrorAlert("Длина ключа должна быть 23 бита");
            return;
        }
        Task<Void> cipherTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String keyBinary;
                int lenM = fileBytes.length;
                String key = keyEntry.getText();
                if (key.isEmpty()) {
                    keyBinary = LFSR.GenerateKey(plainKey, lenM * 8);
                } else {
                    keyBinary = key;
                }
                byte[] keyBytes = binaryStringToBytes(keyBinary);
                fileBytes = Ciphering(fileBytes, keyBytes);
                Platform.runLater(() -> {
                    keyEntry.setText(keyBinary);
                    textResult.setText(bytesToBinaryString(fileBytes));
                });
                return null;
            }
        };

        cipherTask.setOnFailed(event -> {
            showErrorAlert("Ошибка при шифровании");
        });
        new Thread(cipherTask).start();
    }

    private void saveToFile() {
        String selectedFormat = formatBox.getValue();
        if (fileBytes == null) {
            showErrorAlert("Нет данных для сохранения!");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("output." + selectedFormat);
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                Files.write(Paths.get(file.getPath()), fileBytes);
            } catch (IOException e) {
                showErrorAlert("Ошибка при сохранении файла");
            }
        }
    }

    private void ChangeTexts() {
        String m = leftText.getText();
        String c = textResult.getText();
        leftText.setText(c);
        textResult.setText(m);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
