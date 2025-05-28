package com.adminpanel.zmauto.controller;

import com.adminpanel.zmauto.model.User;
import com.adminpanel.zmauto.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the users view.
 */
public class UsersController {

    @FXML
    private ComboBox<String> roleFilterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Long> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> phoneNumberColumn;

    @FXML
    private TableColumn<User, String> addressColumn;

    @FXML
    private TableColumn<User, String> birthdayColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> createdAtColumn;

    @FXML
    private TableColumn<User, String> updatedAtColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button resetPasswordButton;

    @FXML
    private Button deleteButton;

    private DashboardController dashboardController;
    private UserService userService;
    private ObservableList<User> users;

    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        // Initialize the user service
        userService = new UserService();

        // Initialize the role filter combo box
        roleFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "ADMIN", "USER"));
        roleFilterComboBox.getSelectionModel().selectFirst();

        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Format birthday as a date string
        birthdayColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getBirthday() != null) {
                return new SimpleStringProperty(cellData.getValue().getBirthday().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Format created_at and updated_at as date strings
        createdAtColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedAt() != null) {
                return new SimpleStringProperty(cellData.getValue().getCreatedAt().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        updatedAtColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getUpdatedAt() != null) {
                return new SimpleStringProperty(cellData.getValue().getUpdatedAt().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        // Add listener to role filter combo box
        roleFilterComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> loadUsers());

        // Add listener to table selection
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateButtonStates());

        // Load users
        loadUsers();
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
        loadUsers();
    }

    /**
     * Handle clear button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onClearButtonClick(ActionEvent event) {
        searchField.clear();
        roleFilterComboBox.getSelectionModel().selectFirst();
        loadUsers();
    }

    /**
     * Handle add button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onAddButtonClick(ActionEvent event) {
        showUserDialog(null);
    }

    /**
     * Handle edit button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onEditButtonClick(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            showUserDialog(selectedUser);
        }
    }

    /**
     * Handle reset password button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onResetPasswordButtonClick(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            resetPassword(selectedUser);
        }
    }

    /**
     * Handle delete button click.
     * 
     * @param event The action event
     */
    @FXML
    protected void onDeleteButtonClick(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            deleteUser(selectedUser);
        }
    }

    /**
     * Load users based on the current filter and search criteria.
     */
    private void loadUsers() {
        try {
            List<User> userList = userService.getAllUsers();
            String roleFilter = roleFilterComboBox.getValue();
            String searchText = searchField.getText().trim();

            // Apply role filter if not "All"
            if (!roleFilter.equals("All")) {
                userList.removeIf(user -> !user.getRole().equals(roleFilter));
            }

            // Apply search filter if provided
            if (!searchText.isEmpty()) {
                userList.removeIf(user -> 
                        !user.getUsername().toLowerCase().contains(searchText.toLowerCase()) &&
                        !user.getEmail().toLowerCase().contains(searchText.toLowerCase()) &&
                        !user.getFirstName().toLowerCase().contains(searchText.toLowerCase()) &&
                        !user.getLastName().toLowerCase().contains(searchText.toLowerCase()));
            }

            users = FXCollections.observableArrayList(userList);
            usersTable.setItems(users);

            updateButtonStates();

        } catch (SQLException e) {
            showError("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update the button states based on the selected user.
     */
    private void updateButtonStates() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        boolean hasSelection = selectedUser != null;

        editButton.setDisable(!hasSelection);
        resetPasswordButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);

        // Disable delete button for current user
        if (hasSelection && dashboardController != null) {
            User currentUser = dashboardController.getCurrentUser();
            if (currentUser != null && currentUser.getId().equals(selectedUser.getId())) {
                deleteButton.setDisable(true);
            }
        }
    }

    /**
     * Show a dialog to add or edit a user.
     * 
     * @param user The user to edit, or null to add a new user
     */
    private void showUserDialog(User user) {
        // Create a dialog
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(user == null ? "Add User" : "Edit User");
        dialog.setHeaderText(user == null ? "Add a new user" : "Edit user");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        if (user != null) {
            usernameField.setText(user.getUsername());
            usernameField.setDisable(true); // Don't allow changing username
        }

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        if (user != null) {
            passwordField.setPromptText("Leave blank to keep current password");
        }

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        if (user != null) firstNameField.setText(user.getFirstName());

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        if (user != null) lastNameField.setText(user.getLastName());

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        if (user != null) emailField.setText(user.getEmail());

        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("Phone Number");
        if (user != null && user.getPhoneNumber() != null) phoneNumberField.setText(user.getPhoneNumber());

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        if (user != null && user.getAddress() != null) addressField.setText(user.getAddress());

        // Date picker for birthday
        DatePicker birthdayPicker = new DatePicker();
        birthdayPicker.setPromptText("Birthday");
        if (user != null && user.getBirthday() != null) {
            birthdayPicker.setValue(user.getBirthday().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        }

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.setItems(FXCollections.observableArrayList("ADMIN", "USER"));
        roleComboBox.getSelectionModel().select(user != null ? user.getRole() : "USER");

        // Create the layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("First Name:"), 0, 2);
        grid.add(firstNameField, 1, 2);
        grid.add(new Label("Last Name:"), 0, 3);
        grid.add(lastNameField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(new Label("Phone Number:"), 0, 5);
        grid.add(phoneNumberField, 1, 5);
        grid.add(new Label("Address:"), 0, 6);
        grid.add(addressField, 1, 6);
        grid.add(new Label("Birthday:"), 0, 7);
        grid.add(birthdayPicker, 1, 7);
        grid.add(new Label("Role:"), 0, 8);
        grid.add(roleComboBox, 1, 8);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default
        if (user == null) {
            usernameField.requestFocus();
        } else {
            firstNameField.requestFocus();
        }

        // Convert the result to a user when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validate input
                    if (usernameField.getText().trim().isEmpty() ||
                            (user == null && passwordField.getText().trim().isEmpty()) ||
                            firstNameField.getText().trim().isEmpty() ||
                            lastNameField.getText().trim().isEmpty() ||
                            emailField.getText().trim().isEmpty()) {
                        showError("All fields except password (when editing) are required.");
                        return null;
                    }

                    // Create or update the user
                    if (user == null) {
                        // Convert LocalDate to Date for birthday
                        Date birthday = null;
                        if (birthdayPicker.getValue() != null) {
                            birthday = Date.from(birthdayPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        }

                        // Create a new user
                        User newUser = new User(
                                usernameField.getText().trim(),
                                passwordField.getText().trim(),
                                firstNameField.getText().trim(),
                                lastNameField.getText().trim(),
                                emailField.getText().trim(),
                                roleComboBox.getValue(),
                                null, // picture
                                birthday,
                                phoneNumberField.getText().trim(),
                                addressField.getText().trim()
                        );

                        userService.createUser(newUser);
                        showInfo("User added successfully.");
                        loadUsers();

                    } else {
                        // Convert LocalDate to Date for birthday
                        Date birthday = null;
                        if (birthdayPicker.getValue() != null) {
                            birthday = Date.from(birthdayPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        }

                        // Update the existing user
                        user.setFirstName(firstNameField.getText().trim());
                        user.setLastName(lastNameField.getText().trim());
                        user.setEmail(emailField.getText().trim());
                        user.setRole(roleComboBox.getValue());
                        user.setPhoneNumber(phoneNumberField.getText().trim());
                        user.setAddress(addressField.getText().trim());
                        user.setBirthday(birthday);
                        user.setUpdatedAt(new Date()); // Set updated_at to current time

                        userService.updateUser(user);

                        // Update password if provided
                        if (!passwordField.getText().trim().isEmpty()) {
                            userService.updatePassword(user.getId(), passwordField.getText().trim());
                        }

                        showInfo("User updated successfully.");
                        loadUsers();
                    }

                    return user;

                } catch (SQLException e) {
                    showError("Error saving user: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Reset a user's password.
     * 
     * @param user The user to reset password for
     */
    private void resetPassword(User user) {
        // Create a dialog for password reset
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Reset password for user: " + user.getUsername());

        // Set the button types
        ButtonType resetButtonType = new ButtonType("Reset", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);

        // Create the password fields
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        // Create the layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("New Password:"), 0, 0);
        grid.add(passwordField, 1, 0);
        grid.add(new Label("Confirm Password:"), 0, 1);
        grid.add(confirmPasswordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the password field
        passwordField.requestFocus();

        // Convert the result when the reset button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resetButtonType) {
                String password = passwordField.getText().trim();
                String confirmPassword = confirmPasswordField.getText().trim();

                // Validate input
                if (password.isEmpty()) {
                    showError("Password cannot be empty.");
                    return null;
                }

                if (!password.equals(confirmPassword)) {
                    showError("Passwords do not match.");
                    return null;
                }

                return password;
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(password -> {
            try {
                boolean success = userService.updatePassword(user.getId(), password);

                if (success) {
                    showInfo("Password reset successfully.");
                } else {
                    showError("Failed to reset password.");
                }
            } catch (SQLException e) {
                showError("Error resetting password: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Delete a user.
     * 
     * @param user The user to delete
     */
    private void deleteUser(User user) {
        // Don't allow deleting the current user
        if (dashboardController != null) {
            User currentUser = dashboardController.getCurrentUser();
            if (currentUser != null && currentUser.getId().equals(user.getId())) {
                showError("You cannot delete your own account.");
                return;
            }
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete the user: " + 
                user.getUsername() + " (" + user.getFirstName() + " " + user.getLastName() + ")?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = userService.deleteUser(user.getId());

                if (success) {
                    showInfo("User deleted successfully.");
                    loadUsers();
                } else {
                    showError("Failed to delete user.");
                }
            } catch (SQLException e) {
                showError("Error deleting user: " + e.getMessage());
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
