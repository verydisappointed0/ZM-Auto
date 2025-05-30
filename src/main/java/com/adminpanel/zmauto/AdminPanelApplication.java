package com.adminpanel.zmauto;

import com.adminpanel.zmauto.controller.DashboardController;
import com.adminpanel.zmauto.model.User;
import com.adminpanel.zmauto.util.DatabaseInitializer;
import com.adminpanel.zmauto.util.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPanelApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize the database
        DatabaseInitializer.initialize();

        // Load the dashboard view directly (no login required)
        FXMLLoader fxmlLoader = new FXMLLoader(AdminPanelApplication.class.getResource("dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        // Get the controller and set a default users
        DashboardController dashboardController = fxmlLoader.getController();
        User defaultUser = new User();
        defaultUser.setUsername("admin");
        defaultUser.setFirstName("System");
        defaultUser.setLastName("Administrator");
        defaultUser.setEmail("admin@zmauto.com");
        defaultUser.setRole("ADMIN");
        dashboardController.setUser(defaultUser);

        stage.setTitle("ZM-Auto Admin Panel - Dashboard");
        stage.setScene(scene);
        stage.setMaximized(true);
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
