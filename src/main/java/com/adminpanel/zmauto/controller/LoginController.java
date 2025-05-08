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
        // Initialize the user service
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
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }
        
        // Attempt to authenticate
        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                // Authentication successful, navigate to dashboard
                navigateToDashboard(user);
            } else {
                // Authentication failed
                showError("Invalid username or password");
            }
        } catch (Exception e) {
            // Handle authentication error
            showError("Error during authentication: " + e.getMessage());
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
     * @param user The authenticated user
     */
    private void navigateToDashboard(User user) {
        try {
            // Load the dashboard view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adminpanel/zmauto/dashboard-view.fxml"));
            Parent dashboardRoot = loader.load();
            
            // Get the controller and pass the authenticated user
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