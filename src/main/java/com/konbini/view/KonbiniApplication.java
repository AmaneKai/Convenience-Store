package com.konbini.view;

import com.konbini.infrastructure.bootstrap.ApplicationBootstrap;
import com.konbini.view.login.LoginView;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * JavaFX entry point for the Convenience Store. Builds the Guice container via
 * the {@link ApplicationBootstrap} and presents the login screen.
 */
public class KonbiniApplication extends Application {

    /**
     * The application title.
     */
    public static final String TITLE = "Konbini Store";

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) {
        Path dataDirectory = Path.of(System.getProperty("konbini.data.dir", "data"));
        var injector = ApplicationBootstrap.createInjector(dataDirectory);

        LoginView loginView = new LoginView(injector, stage);
        stage.setTitle(TITLE);
        Scene scene = new Scene(loginView.getRoot(), 420, 540);
        scene.getStylesheets().add(Fx.stylesheet());
        stage.setScene(scene);
        stage.setMinWidth(360);
        stage.setMinHeight(460);
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event ->
                loginView.shutdown());
        stage.show();
    }

    /**
     * Launches the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
