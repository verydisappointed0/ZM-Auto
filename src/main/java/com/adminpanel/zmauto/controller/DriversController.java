package com.adminpanel.zmauto.controller;

import com.adminpanel.zmauto.model.Driver;
import com.adminpanel.zmauto.model.Vehicle;
import com.adminpanel.zmauto.service.DriverService;
import com.adminpanel.zmauto.service.VehicleService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the drivers view.
 */
public class DriversController {

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView<Driver> driversTable;

    @FXML
    private TableColumn<Driver, Long> idColumn;

    @FXML
    private TableColumn<Driver, String> firstNameColumn;

    @FXML
    private TableColumn<Driver, String> lastNameColumn;

    @FXML
    private TableColumn<Driver, String> phoneNumberColumn;

    @FXML
    private TableColumn<Driver, String> emailColumn;

    @FXML
    private TableColumn<Driver, String> statusColumn;

    @FXML
    private TableColumn<Driver, Double> ratingColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private DashboardController dashboardController;
    private DriverService driverService;
    private VehicleService vehicleService;
    private ObservableList<Driver> drivers;

    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        // Initialize the driver service
        driverService = new DriverService();
        vehicleService = new VehicleService();

        // Initialize the status filter combo box
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "ACTIVE", "INACTIVE", "ON_LEAVE"));
        statusFilterComboBox.getSelectionModel().selectFirst();

        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<Driver, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    // Set style based on status
                    switch (item) {
                        case "ACTIVE":
                            getStyleClass().add("status-available");
                            break;
                        case "INACTIVE":
                            getStyleClass().add("status-maintenance");
                            break;
                        case "ON_LEAVE":
                            getStyleClass().add("status-reserved");
                            break;
                    }
                }
            }
        });

        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingColumn.setCellFactory(column -> new TableCell<Driver, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", item));
                }
            }
        });

        // Add listener to status filter combo box
        statusFilterComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> loadDrivers());

        // Add listener to table selection
        driversTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateButtonStates());

        // Load drivers
        loadDrivers();
    }

    /**
     * Set the dashboard controller.
     * 
     * @param dashboardController The dashboard controller
     */
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /**
     * Handle search button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onSearchButtonClick(ActionEvent event) {
        loadDrivers();
    }

    /**
     * Handle clear button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onClearButtonClick(ActionEvent event) {
        searchField.clear();
        statusFilterComboBox.getSelectionModel().selectFirst();
        loadDrivers();
    }

    /**
     * Handle add button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onAddButtonClick(ActionEvent event) {
        showDriverDialog(null);
    }

    /**
     * Handle edit button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onEditButtonClick(ActionEvent event) {
        Driver selectedDriver = driversTable.getSelectionModel().getSelectedItem();
        if (selectedDriver != null) {
            showDriverDialog(selectedDriver);
        }
    }

    /**
     * Handle delete button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onDeleteButtonClick(ActionEvent event) {
        Driver selectedDriver = driversTable.getSelectionModel().getSelectedItem();
        if (selectedDriver != null) {
            deleteDriver(selectedDriver);
        }
    }

    /**
     * Load drivers based on the current filter and search criteria.
     */
    private void loadDrivers() {
        try {
            List<Driver> driverList;
            String statusFilter = statusFilterComboBox.getValue();
            String searchText = searchField.getText().trim();

            if (statusFilter.equals("All")) {
                driverList = driverService.getAllDrivers();
            } else {
                // Filter by status
                driverList = driverService.getAllDrivers();
                driverList.removeIf(driver -> !driver.getStatus().equals(statusFilter));
            }

            // Apply search filter if provided
            if (!searchText.isEmpty()) {
                driverList.removeIf(driver -> 
                        !driver.getFirstName().toLowerCase().contains(searchText.toLowerCase()) &&
                        !driver.getLastName().toLowerCase().contains(searchText.toLowerCase()) &&
                        !driver.getPhoneNumber().toLowerCase().contains(searchText.toLowerCase()) &&
                        (driver.getEmail() == null || !driver.getEmail().toLowerCase().contains(searchText.toLowerCase())));
            }

            drivers = FXCollections.observableArrayList(driverList);
            driversTable.setItems(drivers);

            updateButtonStates();

        } catch (SQLException e) {
            showError("Error loading drivers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update the button states based on the selected driver.
     */
    private void updateButtonStates() {
        Driver selectedDriver = driversTable.getSelectionModel().getSelectedItem();
        boolean hasSelection = selectedDriver != null;

        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
    }

    /**
     * Show a dialog to add or edit a driver.
     * 
     * @param driver The driver to edit, or null to add a new driver
     */
    private void showDriverDialog(Driver driver) {
        // Create a dialog
        Dialog<Driver> dialog = new Dialog<>();
        dialog.setTitle(driver == null ? "Add Driver" : "Edit Driver");
        dialog.setHeaderText(driver == null ? "Add a new driver" : "Edit driver");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        TextField pictureField = new TextField();
        pictureField.setPromptText("Picture URL");
        if (driver != null && driver.getPicture() != null) pictureField.setText(driver.getPicture());

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        if (driver != null) firstNameField.setText(driver.getFirstName());

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        if (driver != null) lastNameField.setText(driver.getLastName());

        DatePicker birthdayPicker = new DatePicker();
        birthdayPicker.setPromptText("Birthday");
        if (driver != null && driver.getBirthday() != null) birthdayPicker.setValue(driver.getBirthday());

        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("Phone Number");
        if (driver != null) phoneNumberField.setText(driver.getPhoneNumber());

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        if (driver != null && driver.getAddress() != null) addressField.setText(driver.getAddress());

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        if (driver != null && driver.getEmail() != null) emailField.setText(driver.getEmail());

        TextField dailyWageField = new TextField();
        dailyWageField.setPromptText("Daily Wage");
        if (driver != null && driver.getDailyWage() != null) dailyWageField.setText(driver.getDailyWage().toString());

        TextField hourlyWageField = new TextField();
        hourlyWageField.setPromptText("Hourly Wage");
        if (driver != null && driver.getHourlyWage() != null) hourlyWageField.setText(driver.getHourlyWage().toString());

        CheckBox availabilityCheckBox = new CheckBox("Available");
        if (driver != null && driver.getAvailability() != null) availabilityCheckBox.setSelected(driver.getAvailability());
        else availabilityCheckBox.setSelected(true);

        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE", "ON_LEAVE"));
        statusComboBox.getSelectionModel().select(driver != null ? driver.getStatus() : "ACTIVE");

        TextField yearsOfExperienceField = new TextField();
        yearsOfExperienceField.setPromptText("Years of Experience");
        if (driver != null && driver.getYearsOfExperience() != null) yearsOfExperienceField.setText(driver.getYearsOfExperience().toString());

        // Get all vehicles for the car selection
        ComboBox<Vehicle> carComboBox = new ComboBox<>();
        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            carComboBox.setItems(FXCollections.observableArrayList(vehicles));
            carComboBox.setConverter(new StringConverter<Vehicle>() {
                @Override
                public String toString(Vehicle vehicle) {
                    return vehicle == null ? "" : vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")";
                }

                @Override
                public Vehicle fromString(String string) {
                    return null; // Not needed for this use case
                }
            });

            if (driver != null && driver.getCarId() != null) {
                for (Vehicle vehicle : vehicles) {
                    if (vehicle.getId().equals(driver.getCarId())) {
                        carComboBox.getSelectionModel().select(vehicle);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            showError("Error loading vehicles: " + e.getMessage());
            e.printStackTrace();
        }

        TextField ratingField = new TextField();
        ratingField.setPromptText("Rating (0-5)");
        if (driver != null && driver.getRating() != null) ratingField.setText(driver.getRating().toString());

        // Create the layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Picture URL:"), 0, 0);
        grid.add(pictureField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Birthday:"), 0, 3);
        grid.add(birthdayPicker, 1, 3);
        grid.add(new Label("Phone Number:"), 0, 4);
        grid.add(phoneNumberField, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressField, 1, 5);
        grid.add(new Label("Email:"), 0, 6);
        grid.add(emailField, 1, 6);
        grid.add(new Label("Daily Wage:"), 0, 7);
        grid.add(dailyWageField, 1, 7);
        grid.add(new Label("Hourly Wage:"), 0, 8);
        grid.add(hourlyWageField, 1, 8);
        grid.add(new Label("Availability:"), 0, 9);
        grid.add(availabilityCheckBox, 1, 9);
        grid.add(new Label("Status:"), 0, 10);
        grid.add(statusComboBox, 1, 10);
        grid.add(new Label("Years of Experience:"), 0, 11);
        grid.add(yearsOfExperienceField, 1, 11);
        grid.add(new Label("Assigned Car:"), 0, 12);
        grid.add(carComboBox, 1, 12);
        grid.add(new Label("Rating:"), 0, 13);
        grid.add(ratingField, 1, 13);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first name field by default
        firstNameField.requestFocus();

        // Convert the result to a driver when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validate input
                    if (firstNameField.getText().trim().isEmpty() ||
                            lastNameField.getText().trim().isEmpty() ||
                            phoneNumberField.getText().trim().isEmpty()) {
                        showError("First name, last name, and phone number are required.");
                        return null;
                    }

                    // Parse numeric fields
                    Double dailyWage = null;
                    Double hourlyWage = null;
                    Integer yearsOfExperience = null;
                    Double rating = null;

                    if (!dailyWageField.getText().trim().isEmpty()) {
                        try {
                            dailyWage = Double.parseDouble(dailyWageField.getText().trim());
                        } catch (NumberFormatException e) {
                            showError("Daily wage must be a valid number.");
                            return null;
                        }
                    }

                    if (!hourlyWageField.getText().trim().isEmpty()) {
                        try {
                            hourlyWage = Double.parseDouble(hourlyWageField.getText().trim());
                        } catch (NumberFormatException e) {
                            showError("Hourly wage must be a valid number.");
                            return null;
                        }
                    }

                    if (!yearsOfExperienceField.getText().trim().isEmpty()) {
                        try {
                            yearsOfExperience = Integer.parseInt(yearsOfExperienceField.getText().trim());
                        } catch (NumberFormatException e) {
                            showError("Years of experience must be a valid integer.");
                            return null;
                        }
                    }

                    if (!ratingField.getText().trim().isEmpty()) {
                        try {
                            rating = Double.parseDouble(ratingField.getText().trim());
                            if (rating < 0 || rating > 5) {
                                showError("Rating must be between 0 and 5.");
                                return null;
                            }
                        } catch (NumberFormatException e) {
                            showError("Rating must be a valid number.");
                            return null;
                        }
                    }

                    // Get selected car ID
                    Long carId = null;
                    Vehicle selectedCar = carComboBox.getValue();
                    if (selectedCar != null) {
                        carId = selectedCar.getId();
                    }

                    // Create or update the driver
                    if (driver == null) {
                        // Create a new driver
                        Driver newDriver = new Driver(
                                pictureField.getText().trim(),
                                firstNameField.getText().trim(),
                                lastNameField.getText().trim(),
                                birthdayPicker.getValue(),
                                phoneNumberField.getText().trim(),
                                addressField.getText().trim(),
                                emailField.getText().trim(),
                                dailyWage,
                                hourlyWage,
                                availabilityCheckBox.isSelected(),
                                statusComboBox.getValue(),
                                yearsOfExperience,
                                carId,
                                rating
                        );

                        driverService.createDriver(newDriver);
                        showInfo("Driver added successfully.");
                        loadDrivers();

                    } else {
                        // Update the existing driver
                        driver.setPicture(pictureField.getText().trim());
                        driver.setFirstName(firstNameField.getText().trim());
                        driver.setLastName(lastNameField.getText().trim());
                        driver.setBirthday(birthdayPicker.getValue());
                        driver.setPhoneNumber(phoneNumberField.getText().trim());
                        driver.setAddress(addressField.getText().trim());
                        driver.setEmail(emailField.getText().trim());
                        driver.setDailyWage(dailyWage);
                        driver.setHourlyWage(hourlyWage);
                        driver.setAvailability(availabilityCheckBox.isSelected());
                        driver.setStatus(statusComboBox.getValue());
                        driver.setYearsOfExperience(yearsOfExperience);
                        driver.setCarId(carId);
                        driver.setRating(rating);

                        driverService.updateDriver(driver);
                        showInfo("Driver updated successfully.");
                        loadDrivers();
                    }

                    return driver;

                } catch (SQLException e) {
                    showError("Error saving driver: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Delete a driver.
     * 
     * @param driver The driver to delete
     */
    private void deleteDriver(Driver driver) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Driver");
        alert.setHeaderText("Delete Driver");
        alert.setContentText("Are you sure you want to delete the driver: " + 
                driver.getFirstName() + " " + driver.getLastName() + "?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = driverService.deleteDriver(driver.getDriverId());

                if (success) {
                    showInfo("Driver deleted successfully.");
                    loadDrivers();
                } else {
                    showError("Failed to delete driver.");
                }
            } catch (SQLException e) {
                showError("Error deleting driver: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Show an information dialog.
     * 
     * @param message The message to display
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}