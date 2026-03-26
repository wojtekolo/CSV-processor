module app.module {
    requires javafx.controls;
    requires javafx.fxml;
    requires api.module;

    opens pl.wojtekolo.studia.app to javafx.fxml;

    exports pl.wojtekolo.studia.app;
}