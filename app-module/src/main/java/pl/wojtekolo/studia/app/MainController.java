package pl.wojtekolo.studia.app;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import processing.Status;
import processing.StatusListener;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    //    TextFields
    @FXML private TextField tfColumn;

    //    Paths table
    @FXML private TableView<String> tablePaths;
    @FXML private TableColumn<String, String> colPaths;

    //    Columns to leave table
    @FXML private TableView<String> tableColumns;
    @FXML private TableColumn<String, String> colColumns;

    //    ComboBox with processors
    @FXML private ComboBox<ProcessorInfoDto> cbProcessor;

    //    ProgressBar
    @FXML private ProgressBar pbProgress;

    List<String> selectedFiles = new ArrayList<>();

    private final Service service = new Service(new MyClassLoader("processor-module/target/classes"));

    @FXML
    public void initialize() {
        colPaths.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()));
        colColumns.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()));
        try {
            cbProcessor.getItems().addAll(service.getProcessors());
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void addFiles() {
        FileChooser chooser = new FileChooser();
        List<File> selectedFiles = chooser.showOpenMultipleDialog(null);

        if (selectedFiles != null) {
            List<String> paths = selectedFiles.stream()
                    .map(File::getAbsolutePath)
                    .toList();

            tablePaths.getItems().addAll(paths);
            this.selectedFiles.addAll(paths);
        }
    }

    @FXML
    public void resetFiles() {
        selectedFiles = new ArrayList<>();
        tablePaths.getItems().clear();
    }

    @FXML
    public void addColumn() {
        if (tfColumn.getText().isEmpty()) {
            showErrorMessage("Nie podano nazwy kolumny");
            return;
        }
        if (tfColumn.getText().contains(";") || tfColumn.getText().contains(",")) {
            showErrorMessage("Nielegalna nazwa kolumny");
            return;
        }
        tableColumns.getItems().addAll(tfColumn.getText());
    }

    @FXML
    public void resetColumns() {
        tableColumns.getItems().clear();
    }

    @FXML
    public void submitTask() {
        if (pbProgress.getProgress() < 1 && pbProgress.getProgress() > 0) {
            showErrorMessage("Poprzednie zadanie się nie zakończyło");
            return;
        }
        if (selectedFiles.isEmpty()) {
            showErrorMessage("Nie wybrano plików");
            return;
        }
        if (tableColumns.getItems().isEmpty()) {
            showErrorMessage("Nie wybrano kolumn");
            return;
        }
        if (cbProcessor.getValue() == null) {
            showErrorMessage("Nie wybrano processora");
            return;
        }


        StatusListener listener = new StatusListener() {
            @Override
            public void statusChanged(Status s) {
                javafx.application.Platform.runLater(() -> {
                    pbProgress.setProgress(s.getProgress()/100.0);
                });
            }
        };
        new Thread(() -> {
            Object processor = service.submit(
                    tablePaths.getItems(),
                    tableColumns.getItems(),
                    cbProcessor.getValue(),
                    listener);

            if (processor == null) {
                javafx.application.Platform.runLater(() -> showErrorMessage("Błąd przy uruchamianiu zadania."));
            }

            String result = null;
            while (result == null){
                try {
                    Thread.sleep(800);
                    result = service.getResult(processor);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String finalResult = result;
            javafx.application.Platform.runLater(() -> showSuccessMessage("Zadanie zakończone. Wynik w pliku: "+ finalResult));

        }
        ).start();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
