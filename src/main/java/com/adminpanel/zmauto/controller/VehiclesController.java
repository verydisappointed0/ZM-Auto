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

        // Create the form fields
        TextField makeField = new TextField();
        makeField.setPromptText("Make");
        if (vehicle != null) makeField.setText(vehicle.getMake());

        TextField modelField = new TextField();
        modelField.setPromptText("Model");
        if (vehicle != null) modelField.setText(vehicle.getModel());

        TextField yearField = new TextField();
        yearField.setPromptText("Year");
        if (vehicle != null) yearField.setText(vehicle.getYear().toString());

        TextField licensePlateField = new TextField();
        licensePlateField.setPromptText("License Plate");
        if (vehicle != null) licensePlateField.setText(vehicle.getLicensePlate());

        TextField colorField = new TextField();
        colorField.setPromptText("Color");
        if (vehicle != null) colorField.setText(vehicle.getColor());

        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList("AVAILABLE", "RESERVED", "MAINTENANCE"));
        statusComboBox.getSelectionModel().select(vehicle != null ? vehicle.getStatus() : "AVAILABLE");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        if (vehicle != null && vehicle.getDescription() != null) descriptionField.setText(vehicle.getDescription());

        TextField dailyRateField = new TextField();
        dailyRateField.setPromptText("Daily Rate");
        if (vehicle != null) dailyRateField.setText(vehicle.getDailyRate().toString());

        // Create the layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Make:"), 0, 0);
        grid.add(makeField, 1, 0);
        grid.add(new Label("Model:"), 0, 1);
        grid.add(modelField, 1, 1);
        grid.add(new Label("Year:"), 0, 2);
        grid.add(yearField, 1, 2);
        grid.add(new Label("License Plate:"), 0, 3);
        grid.add(licensePlateField, 1, 3);
        grid.add(new Label("Color:"), 0, 4);
        grid.add(colorField, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusComboBox, 1, 5);
        grid.add(new Label("Description:"), 0, 6);
        grid.add(descriptionField, 1, 6);
        grid.add(new Label("Daily Rate:"), 0, 7);
        grid.add(dailyRateField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the make field by default
        makeField.requestFocus();

        // Convert the result to a vehicle when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validate input
                    if (makeField.getText().trim().isEmpty() ||
                            modelField.getText().trim().isEmpty() ||
                            yearField.getText().trim().isEmpty() ||
                            licensePlateField.getText().trim().isEmpty() ||
                            colorField.getText().trim().isEmpty() ||
                            dailyRateField.getText().trim().isEmpty()) {
                        showError("All fields except description are required.");
                        return null;
                    }

                    // Parse year and daily rate
                    int year;
                    double dailyRate;

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

                    // Create or update the vehicle
                    if (vehicle == null) {
                        // Create a new vehicle
                        Vehicle newVehicle = new Vehicle(
                                makeField.getText().trim(),
                                modelField.getText().trim(),
                                year,
                                licensePlateField.getText().trim(),
                                colorField.getText().trim(),
                                statusComboBox.getValue(),
                                descriptionField.getText().trim(),
                                dailyRate
                        );

                        vehicleService.createVehicle(newVehicle);
                        showInfo("Vehicle added successfully.");
                        loadVehicles();

                    } else {
                        // Update the existing vehicle
                        vehicle.setMake(makeField.getText().trim());
                        vehicle.setModel(modelField.getText().trim());
                        vehicle.setYear(year);
                        vehicle.setLicensePlate(licensePlateField.getText().trim());
                        vehicle.setColor(colorField.getText().trim());
                        vehicle.setStatus(statusComboBox.getValue());
                        vehicle.setDescription(descriptionField.getText().trim());
                        vehicle.setDailyRate(dailyRate);

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
