package com.adminpanel.zmauto.controller;

import com.adminpanel.zmauto.model.Reservation;
import com.adminpanel.zmauto.service.ReservationService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
}