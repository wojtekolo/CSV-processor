package pl.wojtekolo.studia.app;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationLauncher extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws Exception {
        var resource = getClass().getResource("/MainView.fxml");

        FXMLLoader loader = new FXMLLoader(resource);
        javafx.scene.Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Lab 01 - Archiwizator");
        stage.show();
    }
}
