package com.adminpanel.zmauto.controller;

import com.adminpanel.zmauto.model.Reservation;
import com.adminpanel.zmauto.model.User;
import com.adminpanel.zmauto.model.Vehicle;
import com.adminpanel.zmauto.service.DriverService;
import com.adminpanel.zmauto.service.ReservationService;
import com.adminpanel.zmauto.service.UserService;
import com.adminpanel.zmauto.service.VehicleService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for the dashboard view.
 */
public class DashboardController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button usersButton;

    @FXML
    private Button vehiclesButton;

    @FXML
    private Button driversButton;

    @FXML
    private Button reservationsButton;

    @FXML
    private StackPane contentArea;

    private User currentUser;
    private UserService userService;
    private VehicleService vehicleService;
    private DriverService driverService;
    private ReservationService reservationService;

    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        // Initialize services
        userService = new UserService();
        vehicleService = new VehicleService();
        driverService = new DriverService();
        reservationService = new ReservationService();
    }

    /**
     * Set the current users and update the UI accordingly.
     * 
     * @param user The authenticated users
     */
    public void setUser(User user) {
        this.currentUser = user;

        // Update welcome label with users's name
        if (welcomeLabel != null && user != null) {
            welcomeLabel.setText("Welcome, " + user.getFirstName() + " " + user.getLastName());
        }

        // Show reservations by default
        showReservations();
    }

    /**
     * Handle users button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onUsersButtonClick(ActionEvent event) {
        showUsers();
    }

    /**
     * Handle vehicles button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onVehiclesButtonClick(ActionEvent event) {
        showVehicles();
    }

    /**
     * Handle driver button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onDriversButtonClick(ActionEvent event) {
        showDrivers();
    }

    /**
     * Handle reservations button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onReservationsButtonClick(ActionEvent event) {
        showReservations();
    }


    /**
     * Show the users view.
     */
    private void showUsers() {
        try {
            // Load the users view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adminpanel/zmauto/users-view.fxml"));
            Parent usersView = loader.load();

            // Get the controller and pass the current users
            UsersController usersController = loader.getController();
            usersController.setDashboardController(this);

            // Set the users view in the content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(usersView);

            // Update active button
            setActiveButton(usersButton);

        } catch (IOException e) {
            showError("Error loading users view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show the vehicles view.
     */
    private void showVehicles() {
        try {
            // Load the vehicles view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adminpanel/zmauto/vehicles-view.fxml"));
            Parent vehiclesView = loader.load();

            // Get the controller and pass the current users
            VehiclesController vehiclesController = loader.getController();
            vehiclesController.setDashboardController(this);

            // Set the vehicles view in the content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(vehiclesView);

            // Update active button
            setActiveButton(vehiclesButton);

        } catch (IOException e) {
            showError("Error loading vehicles view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show the reservations view.
     */
    private void showReservations() {
        try {
            // Load the reservations view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adminpanel/zmauto/reservations-view.fxml"));
            Parent reservationsView = loader.load();

            // Get the controller and pass the current users
            ReservationsController reservationsController = loader.getController();
            reservationsController.setDashboardController(this);

            // Set the reservations view in the content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(reservationsView);

            // Update active button
            setActiveButton(reservationsButton);

        } catch (IOException e) {
            showError("Error loading reservations view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show the driver view.
     */
    private void showDrivers() {
        try {
            // Load the driver view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adminpanel/zmauto/drivers-view.fxml"));
            Parent driversView = loader.load();

            // Get the controller and pass the current users
            DriversController driversController = loader.getController();
            driversController.setDashboardController(this);

            // Set the driver view in the content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(driversView);

            // Update active button
            setActiveButton(driversButton);

        } catch (IOException e) {
            showError("Error loading driver view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Set the active sidebar button.
     * 
     * @param activeButton The button to set as active
     */
    private void setActiveButton(Button activeButton) {
        // Remove active class from all buttons
        usersButton.getStyleClass().remove("sidebar-button-active");
        vehiclesButton.getStyleClass().remove("sidebar-button-active");
        driversButton.getStyleClass().remove("sidebar-button-active");
        reservationsButton.getStyleClass().remove("sidebar-button-active");

        // Add active class to the active button
        activeButton.getStyleClass().add("sidebar-button-active");
    }

    /**
     * Show an error dialog.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Get the current users.
     * 
     * @return The current users
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get the users service.
     * 
     * @return The users service
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * Get the vehicle service.
     * 
     * @return The vehicle service
     */
    public VehicleService getVehicleService() {
        return vehicleService;
    }

    /**
     * Get the reservation service.
     * 
     * @return The reservation service
     */
    public ReservationService getReservationService() {
        return reservationService;
    }

    /**
     * Get the driver service.
     * 
     * @return The driver service
     */
    public DriverService getDriverService() {
        return driverService;
    }
}
