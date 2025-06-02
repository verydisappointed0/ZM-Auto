package com.adminpanel.zmauto.controller;

import com.adminpanel.zmauto.model.User;
import com.adminpanel.zmauto.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for the login view.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private UserService userService;

    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        // Initialize the users service
        userService = new UserService();

        // Hide the error label initially
        errorLabel.setVisible(false);
    }

    /**
     * Handle login button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String lastName = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input
        if (lastName.isEmpty() || password.isEmpty()) {
            showError("Last name and password are required");
            return;
        }

        try {
            // Get all users and find the one with matching last name
            List<User> users = userService.getAllUsers();
            User authenticatedUser = null;

            for (User user : users) {
                if (user.getLastName().equalsIgnoreCase(lastName) && user.verifyPassword(password)) {
                    authenticatedUser = user;
                    break;
                }
            }

            if (authenticatedUser != null) {
                // Navigate to dashboard
                navigateToDashboard(authenticatedUser);
            } else {
                showError("Invalid last name or password");
            }
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Show an error message.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Navigate to the dashboard view.
     * 
     * @param user The authenticated users
     */
    private void navigateToDashboard(User user) {
        try {
            // Load the dashboard view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adminpanel/zmauto/dashboard-view.fxml"));
            Parent dashboardRoot = loader.load();

            // Get the controller and pass the authenticated users
            DashboardController dashboardController = loader.getController();
            dashboardController.setUser(user);

            // Create a new scene with the dashboard view
            Scene dashboardScene = new Scene(dashboardRoot);

            // Get the current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Set the new scene on the stage
            stage.setScene(dashboardScene);
            stage.setTitle("ZM-Auto Admin Panel - Dashboard");
            stage.setMaximized(true);

        } catch (IOException e) {
            showError("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
