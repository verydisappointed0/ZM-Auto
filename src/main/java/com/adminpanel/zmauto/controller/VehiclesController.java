package com.adminpanel.zmauto.controller;

import com.adminpanel.zmauto.model.Vehicle;
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
import java.util.List;
import java.util.Optional;

/**
 * Controller for the vehicles view.
 */
public class VehiclesController {

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView<Vehicle> vehiclesTable;

    @FXML
    private TableColumn<Vehicle, Long> idColumn;

    @FXML
    private TableColumn<Vehicle, String> makeColumn;

    @FXML
    private TableColumn<Vehicle, String> modelColumn;

    @FXML
    private TableColumn<Vehicle, Integer> yearColumn;

    @FXML
    private TableColumn<Vehicle, String> licensePlateColumn;

    @FXML
    private TableColumn<Vehicle, String> colorColumn;

    @FXML
    private TableColumn<Vehicle, String> statusColumn;

    @FXML
    private TableColumn<Vehicle, Double> dailyRateColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private DashboardController dashboardController;
    private VehicleService vehicleService;
    private ObservableList<Vehicle> vehicles;

    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        // Initialize the vehicle service
        vehicleService = new VehicleService();

        // Initialize the status filter combo box
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "AVAILABLE", "RESERVED", "MAINTENANCE"));
        statusFilterComboBox.getSelectionModel().selectFirst();

        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        licensePlateColumn.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<Vehicle, String>() {
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
                        case "AVAILABLE":
                            getStyleClass().add("status-available");
                            break;
                        case "RESERVED":
                            getStyleClass().add("status-reserved");
                            break;
                        case "MAINTENANCE":
                            getStyleClass().add("status-maintenance");
                            break;
                    }
                }
            }
        });

        dailyRateColumn.setCellValueFactory(new PropertyValueFactory<>("dailyRate"));
        dailyRateColumn.setCellFactory(column -> new TableCell<Vehicle, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        // Add listener to status filter combo box
        statusFilterComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> loadVehicles());

        // Add listener to table selection
        vehiclesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateButtonStates());

        // Load vehicles
        loadVehicles();
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
        loadVehicles();
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
        loadVehicles();
    }

    /**
     * Handle add button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onAddButtonClick(ActionEvent event) {
        showVehicleDialog(null);
    }

    /**
     * Handle edit button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onEditButtonClick(ActionEvent event) {
        Vehicle selectedVehicle = vehiclesTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            showVehicleDialog(selectedVehicle);
        }
    }

    /**
     * Handle delete button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onDeleteButtonClick(ActionEvent event) {
        Vehicle selectedVehicle = vehiclesTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            deleteVehicle(selectedVehicle);
        }
    }

    /**
     * Load vehicles based on the current filter and search criteria.
     */
    private void loadVehicles() {
        try {
            List<Vehicle> vehicleList;
            String statusFilter = statusFilterComboBox.getValue();
            String searchText = searchField.getText().trim();

            if (statusFilter.equals("All")) {
                vehicleList = vehicleService.getAllVehicles();
            } else {
                // Filter by status
                vehicleList = vehicleService.getAllVehicles();
                vehicleList.removeIf(vehicle -> !vehicle.getStatus().equals(statusFilter));
            }

            // Apply search filter if provided
            if (!searchText.isEmpty()) {
                vehicleList.removeIf(vehicle -> 
                        !vehicle.getMake().toLowerCase().contains(searchText.toLowerCase()) &&
                        !vehicle.getModel().toLowerCase().contains(searchText.toLowerCase()) &&
                        !vehicle.getLicensePlate().toLowerCase().contains(searchText.toLowerCase()));
            }

            vehicles = FXCollections.observableArrayList(vehicleList);
            vehiclesTable.setItems(vehicles);

            updateButtonStates();

        } catch (SQLException e) {
            showError("Error loading vehicles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update the button states based on the selected vehicle.
     */
    private void updateButtonStates() {
        Vehicle selectedVehicle = vehiclesTable.getSelectionModel().getSelectedItem();
        boolean hasSelection = selectedVehicle != null;

        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
    }

    /**
     * Show a dialog to add or edit a vehicle.
     * 
     * @param vehicle The vehicle to edit, or null to add a new vehicle
     */
    private void showVehicleDialog(Vehicle vehicle) {
        // Create a dialog
        Dialog<Vehicle> dialog = new Dialog<>();
        dialog.setTitle(vehicle == null ? "Add Vehicle" : "Edit Vehicle");
        dialog.setHeaderText(vehicle == null ? "Add a new vehicle" : "Edit vehicle");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields - Basic Information
        TextField licensePlateField = new TextField();
        licensePlateField.setPromptText("License Plate");
        if (vehicle != null) licensePlateField.setText(vehicle.getLicensePlate());

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        if (vehicle != null && vehicle.getDescription() != null) descriptionField.setText(vehicle.getDescription());

        TextField pictureField = new TextField();
        pictureField.setPromptText("Picture URL");
        if (vehicle != null && vehicle.getPicture() != null) pictureField.setText(vehicle.getPicture());

        TextField makeField = new TextField();
        makeField.setPromptText("Brand/Make");
        if (vehicle != null) makeField.setText(vehicle.getMake());

        TextField conditionField = new TextField();
        conditionField.setPromptText("Condition");
        if (vehicle != null && vehicle.getCondition() != null) conditionField.setText(vehicle.getCondition());

        TextField modelField = new TextField();
        modelField.setPromptText("Model");
        if (vehicle != null) modelField.setText(vehicle.getModel());

        TextField mileageField = new TextField();
        mileageField.setPromptText("Mileage");
        if (vehicle != null && vehicle.getMileage() != null) mileageField.setText(vehicle.getMileage().toString());

        TextField typeField = new TextField();
        typeField.setPromptText("Type");
        if (vehicle != null && vehicle.getType() != null) typeField.setText(vehicle.getType());

        TextField yearField = new TextField();
        yearField.setPromptText("Year");
        if (vehicle != null) yearField.setText(vehicle.getYear().toString());

        TextField colorField = new TextField();
        colorField.setPromptText("Color");
        if (vehicle != null) colorField.setText(vehicle.getColor());

        TextField transmissionField = new TextField();
        transmissionField.setPromptText("Transmission");
        if (vehicle != null && vehicle.getTransmission() != null) transmissionField.setText(vehicle.getTransmission());

        TextField fuelField = new TextField();
        fuelField.setPromptText("Fuel Type");
        if (vehicle != null && vehicle.getFuel() != null) fuelField.setText(vehicle.getFuel());

        TextField seatingCapacityField = new TextField();
        seatingCapacityField.setPromptText("Seating Capacity");
        if (vehicle != null && vehicle.getSeatingCapacity() != null) seatingCapacityField.setText(vehicle.getSeatingCapacity().toString());

        TextField dailyRateField = new TextField();
        dailyRateField.setPromptText("Daily Rate");
        if (vehicle != null) dailyRateField.setText(vehicle.getDailyRate().toString());

        TextField hourlyRateField = new TextField();
        hourlyRateField.setPromptText("Hourly Rate");
        if (vehicle != null && vehicle.getHourlyRate() != null) hourlyRateField.setText(vehicle.getHourlyRate().toString());

        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList("AVAILABLE", "RESERVED", "MAINTENANCE"));
        statusComboBox.getSelectionModel().select(vehicle != null ? vehicle.getStatus() : "AVAILABLE");

        TextField locationField = new TextField();
        locationField.setPromptText("Current Location");
        if (vehicle != null && vehicle.getCurrentLocation() != null) locationField.setText(vehicle.getCurrentLocation());

        DatePicker lastServiceDatePicker = new DatePicker();
        lastServiceDatePicker.setPromptText("Last Service Date");
        if (vehicle != null && vehicle.getLastServiceDate() != null) 
            lastServiceDatePicker.setValue(vehicle.getLastServiceDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());

        DatePicker nextServiceDatePicker = new DatePicker();
        nextServiceDatePicker.setPromptText("Next Service Date");
        if (vehicle != null && vehicle.getNextServiceDate() != null) 
            nextServiceDatePicker.setValue(vehicle.getNextServiceDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());

        DatePicker insuranceExpiryDatePicker = new DatePicker();
        insuranceExpiryDatePicker.setPromptText("Insurance Expiry Date");
        if (vehicle != null && vehicle.getInsuranceExpiryDate() != null) 
            insuranceExpiryDatePicker.setValue(vehicle.getInsuranceExpiryDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());

        CheckBox gpsEnabledCheckBox = new CheckBox("GPS Enabled");
        if (vehicle != null && vehicle.getGpsEnabled() != null) gpsEnabledCheckBox.setSelected(vehicle.getGpsEnabled());

        TextField ratingField = new TextField();
        ratingField.setPromptText("Rating (0-5)");
        if (vehicle != null && vehicle.getRating() != null) ratingField.setText(vehicle.getRating().toString());

        // Create the layout with tabs for better organization
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Basic Info Tab
        Tab basicInfoTab = new Tab("Basic Info");
        GridPane basicInfoGrid = new GridPane();
        basicInfoGrid.setHgap(10);
        basicInfoGrid.setVgap(10);
        basicInfoGrid.setPadding(new Insets(20, 20, 20, 20));

        basicInfoGrid.add(new Label("License Plate:*"), 0, 0);
        basicInfoGrid.add(licensePlateField, 1, 0);
        basicInfoGrid.add(new Label("Brand/Make:*"), 0, 1);
        basicInfoGrid.add(makeField, 1, 1);
        basicInfoGrid.add(new Label("Model:*"), 0, 2);
        basicInfoGrid.add(modelField, 1, 2);
        basicInfoGrid.add(new Label("Year:*"), 0, 3);
        basicInfoGrid.add(yearField, 1, 3);
        basicInfoGrid.add(new Label("Color:*"), 0, 4);
        basicInfoGrid.add(colorField, 1, 4);
        basicInfoGrid.add(new Label("Type:"), 0, 5);
        basicInfoGrid.add(typeField, 1, 5);
        basicInfoGrid.add(new Label("Condition:"), 0, 6);
        basicInfoGrid.add(conditionField, 1, 6);
        basicInfoGrid.add(new Label("Description:"), 0, 7);
        basicInfoGrid.add(descriptionField, 1, 7);
        basicInfoGrid.add(new Label("Picture URL:"), 0, 8);
        basicInfoGrid.add(pictureField, 1, 8);
        basicInfoTab.setContent(basicInfoGrid);

        // Technical Details Tab
        Tab technicalTab = new Tab("Technical Details");
        GridPane technicalGrid = new GridPane();
        technicalGrid.setHgap(10);
        technicalGrid.setVgap(10);
        technicalGrid.setPadding(new Insets(20, 20, 20, 20));

        technicalGrid.add(new Label("Mileage:"), 0, 0);
        technicalGrid.add(mileageField, 1, 0);
        technicalGrid.add(new Label("Transmission:"), 0, 1);
        technicalGrid.add(transmissionField, 1, 1);
        technicalGrid.add(new Label("Fuel Type:"), 0, 2);
        technicalGrid.add(fuelField, 1, 2);
        technicalGrid.add(new Label("Seating Capacity:"), 0, 3);
        technicalGrid.add(seatingCapacityField, 1, 3);
        technicalGrid.add(new Label("GPS Enabled:"), 0, 4);
        technicalGrid.add(gpsEnabledCheckBox, 1, 4);
        technicalTab.setContent(technicalGrid);

        // Rental Details Tab
        Tab rentalTab = new Tab("Rental Details");
        GridPane rentalGrid = new GridPane();
        rentalGrid.setHgap(10);
        rentalGrid.setVgap(10);
        rentalGrid.setPadding(new Insets(20, 20, 20, 20));

        rentalGrid.add(new Label("Daily Rate:*"), 0, 0);
        rentalGrid.add(dailyRateField, 1, 0);
        rentalGrid.add(new Label("Hourly Rate:"), 0, 1);
        rentalGrid.add(hourlyRateField, 1, 1);
        rentalGrid.add(new Label("Status:*"), 0, 2);
        rentalGrid.add(statusComboBox, 1, 2);
        rentalGrid.add(new Label("Current Location:"), 0, 3);
        rentalGrid.add(locationField, 1, 3);
        rentalGrid.add(new Label("Rating:"), 0, 4);
        rentalGrid.add(ratingField, 1, 4);
        rentalTab.setContent(rentalGrid);

        // Maintenance Tab
        Tab maintenanceTab = new Tab("Maintenance & Insurance");
        GridPane maintenanceGrid = new GridPane();
        maintenanceGrid.setHgap(10);
        maintenanceGrid.setVgap(10);
        maintenanceGrid.setPadding(new Insets(20, 20, 20, 20));

        maintenanceGrid.add(new Label("Last Service Date:"), 0, 0);
        maintenanceGrid.add(lastServiceDatePicker, 1, 0);
        maintenanceGrid.add(new Label("Next Service Date:"), 0, 1);
        maintenanceGrid.add(nextServiceDatePicker, 1, 1);
        maintenanceGrid.add(new Label("Insurance Expiry Date:"), 0, 2);
        maintenanceGrid.add(insuranceExpiryDatePicker, 1, 2);
        maintenanceTab.setContent(maintenanceGrid);

        // Add tabs to the tab pane
        tabPane.getTabs().addAll(basicInfoTab, technicalTab, rentalTab, maintenanceTab);

        // Add the tab pane to the dialog
        dialog.getDialogPane().setContent(tabPane);

        // Request focus on the make field by default
        makeField.requestFocus();

        // Convert the result to a vehicle when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validate required input
                    if (makeField.getText().trim().isEmpty() ||
                            modelField.getText().trim().isEmpty() ||
                            yearField.getText().trim().isEmpty() ||
                            licensePlateField.getText().trim().isEmpty() ||
                            colorField.getText().trim().isEmpty() ||
                            dailyRateField.getText().trim().isEmpty()) {
                        showError("Fields marked with * are required.");
                        return null;
                    }

                    // Parse numeric values
                    int year;
                    double dailyRate;
                    Integer mileage = null;
                    Integer seatingCapacity = null;
                    Double hourlyRate = null;
                    Double rating = null;

                    try {
                        year = Integer.parseInt(yearField.getText().trim());
                    } catch (NumberFormatException e) {
                        showError("Year must be a valid integer.");
                        return null;
                    }

                    try {
                        dailyRate = Double.parseDouble(dailyRateField.getText().trim());
                    } catch (NumberFormatException e) {
                        showError("Daily rate must be a valid number.");
                        return null;
                    }

                    // Parse optional numeric values
                    if (!mileageField.getText().trim().isEmpty()) {
                        try {
                            mileage = Integer.parseInt(mileageField.getText().trim());
                        } catch (NumberFormatException e) {
                            showError("Mileage must be a valid integer.");
                            return null;
                        }
                    }

                    if (!seatingCapacityField.getText().trim().isEmpty()) {
                        try {
                            seatingCapacity = Integer.parseInt(seatingCapacityField.getText().trim());
                        } catch (NumberFormatException e) {
                            showError("Seating capacity must be a valid integer.");
                            return null;
                        }
                    }

                    if (!hourlyRateField.getText().trim().isEmpty()) {
                        try {
                            hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());
                        } catch (NumberFormatException e) {
                            showError("Hourly rate must be a valid number.");
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

                    // Convert date pickers to java.util.Date
                    java.util.Date lastServiceDate = null;
                    if (lastServiceDatePicker.getValue() != null) {
                        lastServiceDate = java.util.Date.from(lastServiceDatePicker.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                    }

                    java.util.Date nextServiceDate = null;
                    if (nextServiceDatePicker.getValue() != null) {
                        nextServiceDate = java.util.Date.from(nextServiceDatePicker.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                    }

                    java.util.Date insuranceExpiryDate = null;
                    if (insuranceExpiryDatePicker.getValue() != null) {
                        insuranceExpiryDate = java.util.Date.from(insuranceExpiryDatePicker.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                    }

                    // Create or update the vehicle
                    if (vehicle == null) {
                        // Create a new vehicle with full constructor
                        Vehicle newVehicle = new Vehicle(
                                licensePlateField.getText().trim(),
                                descriptionField.getText().trim(),
                                pictureField.getText().trim(),
                                makeField.getText().trim(),
                                conditionField.getText().trim(),
                                modelField.getText().trim(),
                                mileage,
                                typeField.getText().trim(),
                                year,
                                colorField.getText().trim(),
                                transmissionField.getText().trim(),
                                fuelField.getText().trim(),
                                seatingCapacity,
                                dailyRate,
                                hourlyRate,
                                statusComboBox.getValue(),
                                locationField.getText().trim(),
                                lastServiceDate,
                                nextServiceDate,
                                insuranceExpiryDate,
                                gpsEnabledCheckBox.isSelected(),
                                rating
                        );

                        vehicleService.createVehicle(newVehicle);
                        showInfo("Vehicle added successfully.");
                        loadVehicles();

                    } else {
                        // Update the existing vehicle
                        vehicle.setLicensePlate(licensePlateField.getText().trim());
                        vehicle.setDescription(descriptionField.getText().trim());
                        vehicle.setPicture(pictureField.getText().trim());
                        vehicle.setMake(makeField.getText().trim());
                        vehicle.setCondition(conditionField.getText().trim());
                        vehicle.setModel(modelField.getText().trim());
                        vehicle.setMileage(mileage);
                        vehicle.setType(typeField.getText().trim());
                        vehicle.setYear(year);
                        vehicle.setColor(colorField.getText().trim());
                        vehicle.setTransmission(transmissionField.getText().trim());
                        vehicle.setFuel(fuelField.getText().trim());
                        vehicle.setSeatingCapacity(seatingCapacity);
                        vehicle.setDailyRate(dailyRate);
                        vehicle.setHourlyRate(hourlyRate);
                        vehicle.setStatus(statusComboBox.getValue());
                        vehicle.setCurrentLocation(locationField.getText().trim());
                        vehicle.setLastServiceDate(lastServiceDate);
                        vehicle.setNextServiceDate(nextServiceDate);
                        vehicle.setInsuranceExpiryDate(insuranceExpiryDate);
                        vehicle.setGpsEnabled(gpsEnabledCheckBox.isSelected());
                        vehicle.setRating(rating);
                        vehicle.setUpdatedAt(new java.util.Date());

                        vehicleService.updateVehicle(vehicle);
                        showInfo("Vehicle updated successfully.");
                        loadVehicles();
                    }

                    return vehicle;

                } catch (SQLException e) {
                    showError("Error saving vehicle: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Delete a vehicle.
     * 
     * @param vehicle The vehicle to delete
     */
    private void deleteVehicle(Vehicle vehicle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Vehicle");
        alert.setHeaderText("Delete Vehicle");
        alert.setContentText("Are you sure you want to delete the vehicle: " + 
                vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = vehicleService.deleteVehicle(vehicle.getId());

                if (success) {
                    showInfo("Vehicle deleted successfully.");
                    loadVehicles();
                } else {
                    showError("Failed to delete vehicle.");
                }
            } catch (SQLException e) {
                showError("Error deleting vehicle: " + e.getMessage());
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
