package com.adminpanel.zmauto.controller;

import com.adminpanel.zmauto.model.Driver;
import com.adminpanel.zmauto.model.Reservation;
import com.adminpanel.zmauto.model.User;
import com.adminpanel.zmauto.model.Vehicle;
import com.adminpanel.zmauto.service.ReservationService;
import javafx.beans.property.SimpleObjectProperty;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the reservations view.
 */
public class ReservationsController {

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView<Reservation> reservationsTable;

    @FXML
    private TableColumn<Reservation, Long> idColumn;

    @FXML
    private TableColumn<Reservation, String> userColumn;

    @FXML
    private TableColumn<Reservation, String> vehicleColumn;

    @FXML
    private TableColumn<Reservation, LocalDate> startDateColumn;

    @FXML
    private TableColumn<Reservation, LocalDate> endDateColumn;

    @FXML
    private TableColumn<Reservation, String> statusColumn;

    @FXML
    private TableColumn<Reservation, Double> totalCostColumn;

    @FXML
    private TableColumn<Reservation, LocalDateTime> createdAtColumn;

    @FXML
    private Button viewDetailsButton;

    @FXML
    private Button approveButton;

    @FXML
    private Button rejectButton;

    @FXML
    private Button createButton;

    private DashboardController dashboardController;
    private ReservationService reservationService;
    private ObservableList<Reservation> reservations;

    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        // Initialize the reservation service
        reservationService = new ReservationService();

        // Initialize the status filter combo box
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "PENDING", "APPROVED", "REJECTED", "CANCELLED"));
        statusFilterComboBox.getSelectionModel().selectFirst();

        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        userColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getUser().getUsername()));

        vehicleColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getVehicle().getMake() + " " + 
                        cellData.getValue().getVehicle().getModel()));

        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<Reservation, String>() {
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
                        case "PENDING":
                            getStyleClass().add("status-pending");
                            break;
                        case "APPROVED":
                            getStyleClass().add("status-approved");
                            break;
                        case "REJECTED":
                            getStyleClass().add("status-rejected");
                            break;
                        case "CANCELLED":
                            getStyleClass().add("status-cancelled");
                            break;
                    }
                }
            }
        });

        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setCellFactory(column -> new TableCell<Reservation, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // Add listener to status filter combo box
        statusFilterComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> loadReservations());

        // Add listener to table selection
        reservationsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateButtonStates());

        // Load reservations
        loadReservations();
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
        loadReservations();
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
        loadReservations();
    }

    /**
     * Handle view details button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onViewDetailsButtonClick(ActionEvent event) {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            showReservationDetails(selectedReservation);
        }
    }

    /**
     * Handle approve button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onApproveButtonClick(ActionEvent event) {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            approveReservation(selectedReservation);
        }
    }

    /**
     * Handle reject button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onRejectButtonClick(ActionEvent event) {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            rejectReservation(selectedReservation);
        }
    }

    /**
     * Handle create button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onCreateButtonClick(ActionEvent event) {
        showCreateReservationDialog();
    }

    /**
     * Load reservations based on the current filter and search criteria.
     */
    private void loadReservations() {
        try {
            List<Reservation> reservationList;
            String statusFilter = statusFilterComboBox.getValue();
            String searchText = searchField.getText().trim();

            if (statusFilter.equals("All")) {
                reservationList = reservationService.getAllReservations();
            } else {
                reservationList = reservationService.getReservationsByStatus(statusFilter);
            }

            // Apply search filter if provided
            if (!searchText.isEmpty()) {
                reservationList.removeIf(reservation -> 
                        !reservation.getUser().getUsername().toLowerCase().contains(searchText.toLowerCase()) &&
                        !reservation.getVehicle().getMake().toLowerCase().contains(searchText.toLowerCase()) &&
                        !reservation.getVehicle().getModel().toLowerCase().contains(searchText.toLowerCase()));
            }

            reservations = FXCollections.observableArrayList(reservationList);
            reservationsTable.setItems(reservations);

            updateButtonStates();

        } catch (SQLException e) {
            showError("Error loading reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update the button states based on the selected reservation.
     */
    private void updateButtonStates() {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        boolean hasSelection = selectedReservation != null;

        viewDetailsButton.setDisable(!hasSelection);

        if (hasSelection) {
            String status = selectedReservation.getStatus();
            approveButton.setDisable(!status.equals("PENDING"));
            rejectButton.setDisable(!status.equals("PENDING"));
        } else {
            approveButton.setDisable(true);
            rejectButton.setDisable(true);
        }
    }

    /**
     * Show reservation details.
     * 
     * @param reservation The reservation to show details for
     */
    private void showReservationDetails(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reservation Details");
        alert.setHeaderText("Reservation #" + reservation.getId());

        StringBuilder content = new StringBuilder();
        content.append("User: ").append(reservation.getUser().getUsername())
               .append(" (").append(reservation.getUser().getFullName()).append(")\n");
        content.append("Vehicle: ").append(reservation.getVehicle().getMake())
               .append(" ").append(reservation.getVehicle().getModel())
               .append(" (").append(reservation.getVehicle().getLicensePlate()).append(")\n");

        // Add driver information if a driver is needed
        if (reservation.getDriverNeeded() != null && reservation.getDriverNeeded()) {
            content.append("Driver Needed: Yes\n");
            if (reservation.getDriver() != null) {
                content.append("Driver: ").append(reservation.getDriver().getFirstName())
                       .append(" ").append(reservation.getDriver().getLastName())
                       .append(" (Rating: ").append(reservation.getDriver().getRating()).append(")\n");
            } else {
                content.append("Driver: Not assigned\n");
            }
        } else {
            content.append("Driver Needed: No\n");
        }

        content.append("Start Date: ").append(reservation.getStartDate()).append("\n");
        content.append("End Date: ").append(reservation.getEndDate()).append("\n");
        content.append("Status: ").append(reservation.getStatus()).append("\n");
        content.append("Total Cost: $").append(String.format("%.2f", reservation.getTotalCost())).append("\n");
        content.append("Created At: ").append(reservation.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");

        if (reservation.getUpdatedAt() != null) {
            content.append("Updated At: ").append(reservation.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        }

        if (reservation.getNotes() != null && !reservation.getNotes().isEmpty()) {
            content.append("\nNotes: ").append(reservation.getNotes());
        }

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    /**
     * Approve a reservation.
     * 
     * @param reservation The reservation to approve
     */
    private void approveReservation(Reservation reservation) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Approve Reservation");
        dialog.setHeaderText("Approve Reservation #" + reservation.getId());
        dialog.setContentText("Notes (optional):");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(notes -> {
            try {
                boolean success = reservationService.approveReservation(reservation.getId(), notes);

                if (success) {
                    showInfo("Reservation approved successfully.");
                    loadReservations();
                } else {
                    showError("Failed to approve reservation.");
                }
            } catch (SQLException e) {
                showError("Error approving reservation: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Reject a reservation.
     * 
     * @param reservation The reservation to reject
     */
    private void rejectReservation(Reservation reservation) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Reservation");
        dialog.setHeaderText("Reject Reservation #" + reservation.getId());
        dialog.setContentText("Reason for rejection:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(notes -> {
            try {
                boolean success = reservationService.rejectReservation(reservation.getId(), notes);

                if (success) {
                    showInfo("Reservation rejected successfully.");
                    loadReservations();
                } else {
                    showError("Failed to reject reservation.");
                }
            } catch (SQLException e) {
                showError("Error rejecting reservation: " + e.getMessage());
                e.printStackTrace();
            }
        });
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

    /**
     * Show a dialog to create a new reservation.
     */
    private void showCreateReservationDialog() {
        try {
            // Create a dialog
            Dialog<Reservation> dialog = new Dialog<>();
            dialog.setTitle("Create Reservation");
            dialog.setHeaderText("Create a new reservation");

            // Set the button types
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Create the form fields
            ComboBox<User> userComboBox = new ComboBox<>();
            List<User> users = dashboardController.getUserService().getAllUsers();
            userComboBox.setItems(FXCollections.observableArrayList(users));
            userComboBox.setConverter(new StringConverter<User>() {
                @Override
                public String toString(User user) {
                    return user == null ? "" : user.getUsername() + " (" + user.getFullName() + ")";
                }

                @Override
                public User fromString(String string) {
                    return null; // Not needed for this use case
                }
            });

            ComboBox<Vehicle> vehicleComboBox = new ComboBox<>();
            List<Vehicle> vehicles = dashboardController.getVehicleService().getAvailableVehicles();
            vehicleComboBox.setItems(FXCollections.observableArrayList(vehicles));
            vehicleComboBox.setConverter(new StringConverter<Vehicle>() {
                @Override
                public String toString(Vehicle vehicle) {
                    return vehicle == null ? "" : vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")";
                }

                @Override
                public Vehicle fromString(String string) {
                    return null; // Not needed for this use case
                }
            });

            DatePicker startDatePicker = new DatePicker();
            startDatePicker.setValue(LocalDate.now());

            DatePicker endDatePicker = new DatePicker();
            endDatePicker.setValue(LocalDate.now().plusDays(1));

            CheckBox driverNeededCheckBox = new CheckBox("Driver Needed");

            ComboBox<com.adminpanel.zmauto.model.Driver> driverComboBox = new ComboBox<>();
            List<com.adminpanel.zmauto.model.Driver> drivers = dashboardController.getDriverService().getAvailableDrivers();
            driverComboBox.setItems(FXCollections.observableArrayList(drivers));
            driverComboBox.setConverter(new StringConverter<com.adminpanel.zmauto.model.Driver>() {
                @Override
                public String toString(com.adminpanel.zmauto.model.Driver driver) {
                    return driver == null ? "" : driver.getFirstName() + " " + driver.getLastName() + " (Rating: " + driver.getRating() + ")";
                }

                @Override
                public com.adminpanel.zmauto.model.Driver fromString(String string) {
                    return null; // Not needed for this use case
                }
            });
            driverComboBox.setDisable(true); // Initially disabled

            // Add listener to driverNeededCheckBox to enable/disable driverComboBox
            driverNeededCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                driverComboBox.setDisable(!newValue);
                if (!newValue) {
                    driverComboBox.getSelectionModel().clearSelection();
                }
                updateTotalCost(vehicleComboBox.getValue(), driverNeededCheckBox.isSelected(), driverComboBox.getValue(), 
                        startDatePicker.getValue(), endDatePicker.getValue());
            });

            // Add listeners to update total cost when selection changes
            vehicleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                updateTotalCost(newValue, driverNeededCheckBox.isSelected(), driverComboBox.getValue(), 
                        startDatePicker.getValue(), endDatePicker.getValue());
            });

            driverComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                updateTotalCost(vehicleComboBox.getValue(), driverNeededCheckBox.isSelected(), newValue, 
                        startDatePicker.getValue(), endDatePicker.getValue());
            });

            startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                updateTotalCost(vehicleComboBox.getValue(), driverNeededCheckBox.isSelected(), driverComboBox.getValue(), 
                        newValue, endDatePicker.getValue());
            });

            endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                updateTotalCost(vehicleComboBox.getValue(), driverNeededCheckBox.isSelected(), driverComboBox.getValue(), 
                        startDatePicker.getValue(), newValue);
            });

            TextField notesField = new TextField();

            // Create and set the total cost label
            totalCostLabel = new Label("$0.00");

            // Create the layout
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            grid.add(new Label("User:"), 0, 0);
            grid.add(userComboBox, 1, 0);
            grid.add(new Label("Vehicle:"), 0, 1);
            grid.add(vehicleComboBox, 1, 1);
            grid.add(new Label("Start Date:"), 0, 2);
            grid.add(startDatePicker, 1, 2);
            grid.add(new Label("End Date:"), 0, 3);
            grid.add(endDatePicker, 1, 3);
            grid.add(driverNeededCheckBox, 0, 4, 2, 1);
            grid.add(new Label("Driver:"), 0, 5);
            grid.add(driverComboBox, 1, 5);
            grid.add(new Label("Notes:"), 0, 6);
            grid.add(notesField, 1, 6);
            grid.add(new Label("Total Cost:"), 0, 7);
            grid.add(totalCostLabel, 1, 7);

            dialog.getDialogPane().setContent(grid);

            // Request focus on the user field by default
            userComboBox.requestFocus();

            // Convert the result to a reservation when the save button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        // Validate input
                        if (userComboBox.getValue() == null ||
                                vehicleComboBox.getValue() == null ||
                                startDatePicker.getValue() == null ||
                                endDatePicker.getValue() == null) {
                            showError("User, vehicle, start date, and end date are required.");
                            return null;
                        }

                        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
                            showError("Start date cannot be after end date.");
                            return null;
                        }

                        if (driverNeededCheckBox.isSelected() && driverComboBox.getValue() == null) {
                            showError("Please select a driver or uncheck 'Driver Needed'.");
                            return null;
                        }

                        // Calculate total cost
                        double totalCost = calculateTotalCost(vehicleComboBox.getValue(), driverNeededCheckBox.isSelected(), 
                                driverComboBox.getValue(), startDatePicker.getValue(), endDatePicker.getValue());

                        // Create a new reservation
                        Reservation newReservation = new Reservation(
                                userComboBox.getValue(),
                                vehicleComboBox.getValue(),
                                driverNeededCheckBox.isSelected(),
                                driverNeededCheckBox.isSelected() ? driverComboBox.getValue() : null,
                                startDatePicker.getValue(),
                                endDatePicker.getValue(),
                                "PENDING",
                                notesField.getText().trim(),
                                totalCost
                        );

                        reservationService.createReservation(newReservation);
                        showInfo("Reservation created successfully.");
                        loadReservations();

                        return newReservation;

                    } catch (SQLException e) {
                        showError("Error creating reservation: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait();

        } catch (SQLException e) {
            showError("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calculate the total cost of a reservation.
     * 
     * @param vehicle The selected vehicle
     * @param driverNeeded Whether a driver is needed
     * @param driver The selected driver
     * @param startDate The start date
     * @param endDate The end date
     * @return The total cost
     */
    private double calculateTotalCost(Vehicle vehicle, boolean driverNeeded, com.adminpanel.zmauto.model.Driver driver, 
                                     LocalDate startDate, LocalDate endDate) {
        if (vehicle == null || startDate == null || endDate == null) {
            return 0.0;
        }

        // Calculate number of days
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1; // Include both start and end days

        // Calculate vehicle cost
        double vehicleCost = vehicle.getDailyRate() * days;

        // Calculate driver cost if needed
        double driverCost = 0.0;
        if (driverNeeded && driver != null && driver.getDailyWage() != null) {
            driverCost = driver.getDailyWage() * days;
        }

        return vehicleCost + driverCost;
    }

    // Reference to the total cost label
    private Label totalCostLabel;

    /**
     * Update the total cost label.
     * 
     * @param vehicle The selected vehicle
     * @param driverNeeded Whether a driver is needed
     * @param driver The selected driver
     * @param startDate The start date
     * @param endDate The end date
     */
    private void updateTotalCost(Vehicle vehicle, boolean driverNeeded, Driver driver, 
                                LocalDate startDate, LocalDate endDate) {
        if (totalCostLabel != null) {
            double totalCost = calculateTotalCost(vehicle, driverNeeded, driver, startDate, endDate);
            totalCostLabel.setText(String.format("$%.2f", totalCost));
        }
    }
}
