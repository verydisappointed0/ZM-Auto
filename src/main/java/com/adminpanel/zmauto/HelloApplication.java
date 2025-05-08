package com.adminpanel.zmauto;

import com.adminpanel.zmauto.util.DatabaseInitializer;
import com.adminpanel.zmauto.util.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize the database
        DatabaseInitializer.initialize();

        // Load the login view
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("ZM-Auto Admin Panel - Login");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Close the database connection pool when the application exits
        DatabaseUtil.closePool();
    }

    public static void main(String[] args) {
        launch();
    }
}
