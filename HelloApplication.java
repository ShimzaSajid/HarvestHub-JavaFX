package com.example.fruitvegetablestall;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HelloApplication extends Application {
private Scene loginScene;
private static final File usersFile = new File("users.ser");
private List<User> users = new ArrayList<>(); 
private TextField productIdField;
private TextField productNameField;
private ComboBox<String> typeComboBox;
private TextField stockField;
private TextField priceField;
private ComboBox<String> statusComboBox;
private TableView<Product> tableView;
private ObservableList<Product> productList;

private Button addButton;
private Button updateButton;
private Button deleteButton;
private ObservableList<OrderItem> orderData = FXCollections.observableArrayList();
private Label totalLabel;
private Scene homeScene; // if you're switching back to it
// === Sales / Income ===
private double totalIncome = 0.0;
private Button salesButton;
private TextField usernameField;
private PasswordField passwordField;
private TextField imagePathField;
private Button uploadImageButton;



    @Override
    public void start(Stage primaryStage) throws FileNotFoundException{
        primaryStage.setTitle("Fresh Mart - Login");
        loadUsers();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // === Background Setup with Blur ===
        Image bgImage = new Image(getClass().getResourceAsStream("/Fruit-veg-back.jpg"));
        ImageView bgImageView = new ImageView(bgImage);
        bgImageView.setFitWidth(screenWidth);
        bgImageView.setFitHeight(screenHeight);
        bgImageView.setEffect(new GaussianBlur(10)); // lighter blur
        StackPane root = new StackPane(bgImageView);

        // === Login Box ===
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(30, 40, 40, 40));
        loginBox.setMaxWidth(400);
        loginBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2); " +
            "-fx-background-radius: 20; " +
            "-fx-border-radius: 20; " +
            "-fx-border-color: rgba(255, 255, 255, 0.3); " +
            "-fx-border-width: 1; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 20, 0, 0, 0);"
        );

        // === Logo ===
        try {
            Image logo = new Image(getClass().getResourceAsStream("/vegetable-and-fruit-fresh-logo-vector.jpg"));
            ImageView logoView = new ImageView(logo);
            logoView.setFitWidth(180);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
            loginBox.getChildren().add(new StackPane(logoView));
        } catch (Exception e) {
            Label fallback = new Label("FRESH MART");
            fallback.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            fallback.setTextFill(Color.WHITE);
            loginBox.getChildren().add(new StackPane(fallback));
        }

        // === Headings ===
        Label title = new Label("Welcome to HarvestHub");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 25));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Sign in to your account");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.WHITE);

        // === Input Fields ===
        VBox inputContainer = new VBox(5);
        inputContainer.setMaxWidth(320);

        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: black;");
        usernameField = createGlassTextField("Enter username");


        Label passwordLabel = new Label("Password");
         passwordLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: black;");
         HBox passwordFieldWithEye = createPasswordFieldWithToggle("Enter password");


       inputContainer.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordFieldWithEye);

        // === Remember Me and Login ===
        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");
        Button loginButton = createGlassButton();
        loginButton.setOnAction(e -> {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();

    if (username.isEmpty() || password.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password.");
        return;
    }

    for (User user : users) {
        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
            // showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + user.getUsername() + "!");

            // ✅ SWITCH TO HOME SCENE HERE
            homeScene = createHomeScene(primaryStage); // if you want to cache it
            primaryStage.setScene(homeScene);
            primaryStage.setMaximized(true);
            return;
        }
    }

    showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
});



        // === Links ===
        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.setStyle("-fx-text-fill: black; -fx-font-size: 13px; -fx-underline: true;");
        // Add handler for forgot password link
        forgotPassword.setOnAction(e -> {
            primaryStage.setScene(getForgotPasswordScene(primaryStage));
        });

        Hyperlink signUp = new Hyperlink("Create New Account");
        signUp.setOnAction(e -> primaryStage.setScene(getSignUpScene(primaryStage)));
        signUp.setStyle("-fx-text-fill: black; -fx-font-size: 13px; -fx-font-weight: bold; -fx-underline: true;");

        HBox linksBox = new HBox(20, forgotPassword, signUp);
        linksBox.setAlignment(Pos.CENTER);

        // === Assemble Login Box ===
        loginBox.getChildren().addAll(
            title, subtitle,
            inputContainer,
            rememberMe,
            loginButton,
            linksBox
        );

        root.getChildren().add(loginBox);
        loginScene = new Scene(root, screenWidth, screenHeight);
        primaryStage.setScene(loginScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
private HBox createPasswordFieldWithToggle(String prompt) {
    PasswordField hiddenField = new PasswordField();
    TextField visibleField = new TextField();
    visibleField.setManaged(false);
    visibleField.setVisible(false);

    hiddenField.setPromptText(prompt);
    visibleField.setPromptText(prompt);

    applyGlassStyle(hiddenField, prompt);
    applyGlassStyle(visibleField, prompt);

    visibleField.textProperty().bindBidirectional(hiddenField.textProperty());

    Image eyeOpen = new Image(getClass().getResourceAsStream("/eye.jpg"));
    Image eyeClosed = new Image(getClass().getResourceAsStream("/close-eye.png"));

    ImageView eyeIcon = new ImageView(eyeOpen);
    eyeIcon.setFitHeight(25);
    eyeIcon.setFitWidth(20);
    eyeIcon.setCursor(Cursor.HAND);

    eyeIcon.setOnMouseClicked(e -> {
        boolean isVisible = visibleField.isVisible();
        visibleField.setVisible(!isVisible);
        visibleField.setManaged(!isVisible);
        hiddenField.setVisible(isVisible);
        hiddenField.setManaged(isVisible);
        eyeIcon.setImage(!isVisible ? eyeClosed : eyeOpen);
    });

    passwordField = hiddenField;

    HBox fieldBox = new HBox(visibleField, hiddenField, eyeIcon);
    fieldBox.setAlignment(Pos.CENTER_RIGHT);
    fieldBox.setSpacing(5);
    return fieldBox;
}


    private TextField createGlassTextField(String prompt) {
        TextField field = new TextField();
        applyGlassStyle(field, prompt);
        return field;
    }

private void clearLoginFields() {
    if (usernameField != null) usernameField.clear();
    if (passwordField != null) passwordField.clear();
}


    private void applyGlassStyle(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setMinWidth(320);
        field.setMaxWidth(320);
        String base = "-fx-padding: 10px 15px; -fx-font-size: 14px; " +
                      "-fx-background-color:  rgba(34, 139, 34, 0.3); -fx-text-fill: white; " +
                      "-fx-background-radius: 8; -fx-border-radius: 8; " +
                      "-fx-border-color: black; -fx-border-width: 1;";
        field.setStyle(base);
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(base.replace("0.2", "0.3").replace("0.4", "0.8"));
            } else {
                field.setStyle(base);
            }
        });
    }

    private Button createGlassButton() {
        
        Button button = new Button("LOGIN");
        String baseStyle = "-fx-background-color: rgba(34, 139, 34, 0.3); " +
                         "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
                         "-fx-padding: 12px 40px; -fx-background-radius: 8; -fx-border-radius: 8; " +
                         "-fx-border-color:rgba(0, 0, 0, 0.8); -fx-border-width: 1; -fx-cursor: hand;";
        String hoverStyle = baseStyle
                .replace("0.3", "0.4")
                .replace("0.5", "0.8");

        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        return button;
    }
private Scene getLoginScene(Stage primaryStage) {
    return loginScene;
}

    // Add this method to your class
    private Scene getForgotPasswordScene(Stage primaryStage) {
        StackPane root = new StackPane();
        
        // Background Setup
        Image bgImage = new Image(getClass().getResourceAsStream("/Fruit-veg-back.jpg"));
        ImageView bgImageView = new ImageView(bgImage);
        bgImageView.setFitWidth(primaryStage.getWidth());
        bgImageView.setFitHeight(primaryStage.getHeight());
        bgImageView.setEffect(new GaussianBlur(10));
        root.getChildren().add(bgImageView);

        // Main Content Container
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(40));
        contentBox.setMaxWidth(450);
        contentBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2); " +
            "-fx-background-radius: 20; " +
            "-fx-border-radius: 20; " +
            "-fx-border-color: rgba(255, 255, 255, 0.3); " +
            "-fx-border-width: 1; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 20, 0, 0, 0);"
        );

        // Title
        Label title = new Label("Reset Password");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);
        VBox.setMargin(title, new Insets(0, 0, 20, 0));

        // Input Fields
        VBox inputContainer = new VBox(15);
        inputContainer.setAlignment(Pos.CENTER);
        inputContainer.setMaxWidth(350);

        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        TextField usernameField = createGlassTextField("Enter your username");

        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        TextField emailField = createGlassTextField("Enter your email");

        inputContainer.getChildren().addAll(
            usernameLabel, usernameField,
            emailLabel, emailField
        );

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button submitButton = new Button("SUBMIT");
        submitButton.setStyle(
            "-fx-background-color: rgba(34, 139, 34, 0.3); " +
            "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
            "-fx-padding: 12px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
            "-fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 1; " +
            "-fx-cursor: hand;"
        );
        
        submitButton.setOnMouseEntered(e -> {
            submitButton.setStyle(
                "-fx-background-color: rgba(34, 139, 34, 0.4); " +
                "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 30px; -fx-background-radius: 8; " +
                "-fx-border-color: rgba(255, 255, 255, 0.8);"
            );
        });
        
        submitButton.setOnMouseExited(e -> {
            submitButton.setStyle(
                "-fx-background-color: rgba(34, 139, 34, 0.3); " +
                "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
                "-fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 1;"
            );
        });

        Button backButton = new Button("BACK TO LOGIN");
        backButton.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2); " +
            "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
            "-fx-padding: 12px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
            "-fx-border-color: rgba(255, 255, 255, 0.4); -fx-border-width: 1; " +
            "-fx-cursor: hand;"
        );
        
        backButton.setOnMouseEntered(e -> {
            backButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.3); " +
                "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 30px; -fx-background-radius: 8; " +
                "-fx-border-color: rgba(255, 255, 255, 0.7);"
            );
        });
        
        backButton.setOnMouseExited(e -> {
            backButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.2); " +
                "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
                "-fx-border-color: rgba(255, 255, 255, 0.4); -fx-border-width: 1;"
            );
        });

        buttonBox.getChildren().addAll(submitButton, backButton);

        // Assemble Components
        contentBox.getChildren().addAll(
            title,
            inputContainer,
            buttonBox
        );

        // Event Handlers
        submitButton.setOnAction(e -> {
    String enteredUsername = usernameField.getText().trim();
    String enteredEmail = emailField.getText().trim();

    if (enteredUsername.isEmpty() || enteredEmail.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
        return;
    }

    for (User user : users) {
        if (user.getUsername().equals(enteredUsername) && user.getEmail().equalsIgnoreCase(enteredEmail)) {
            primaryStage.setScene(getResetPasswordScene(primaryStage, enteredUsername));
            return;
        }
    }

    showAlert(Alert.AlertType.ERROR, "Error", "No user found with given credentials.");
});

        
        backButton.setOnAction(e -> {
            clearLoginFields();
            primaryStage.setScene(loginScene);
            primaryStage.setMaximized(true);
        });

        root.getChildren().add(contentBox);
        return new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
    }
private Scene getResetPasswordScene(Stage primaryStage, String username) {
    StackPane root = new StackPane();

    // === Background Setup ===
    Image bgImage = new Image(getClass().getResourceAsStream("/Fruit-veg-back.jpg"));
    ImageView bgImageView = new ImageView(bgImage);
    bgImageView.setFitWidth(primaryStage.getWidth());
    bgImageView.setFitHeight(primaryStage.getHeight());
    bgImageView.setEffect(new GaussianBlur(10));
    root.getChildren().add(bgImageView);

    // === Reset Password Box ===
    VBox resetBox = new VBox(20);
    resetBox.setAlignment(Pos.CENTER);
    resetBox.setPadding(new Insets(40));
    resetBox.setMaxWidth(450);
    resetBox.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.2); " +
        "-fx-background-radius: 20; " +
        "-fx-border-radius: 20; " +
        "-fx-border-color: rgba(255, 255, 255, 0.3); " +
        "-fx-border-width: 1; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 20, 0, 0, 0);"
    );

    Label resetLabel = new Label("Reset Password");
    resetLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
    resetLabel.setTextFill(Color.WHITE);
HBox newPasswordBox = createPasswordFieldWithToggle("Enter New Password");
HBox confirmPasswordBox = createPasswordFieldWithToggle("Confirm New Password");

// Extract the PasswordField references from each box if needed later
PasswordField newPasswordField = (PasswordField) newPasswordBox.getChildren().stream()
        .filter(node -> node instanceof PasswordField)
        .findFirst().orElse(null);

PasswordField confirmPasswordField = (PasswordField) confirmPasswordBox.getChildren().stream()
        .filter(node -> node instanceof PasswordField)
        .findFirst().orElse(null);

    // PasswordField newPasswordField = new PasswordField();
    newPasswordField.setPromptText("Enter New Password");
    newPasswordField.setMaxWidth(320);
    applyGlassStyle(newPasswordField, "Enter New Password");

    // PasswordField confirmPasswordField = new PasswordField();
    confirmPasswordField.setPromptText("Confirm New Password");
    confirmPasswordField.setMaxWidth(320);
    applyGlassStyle(confirmPasswordField, "Confirm New Password");
   
    // === Buttons ===
    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);

    Button submitButton = new Button("SUBMIT");
    submitButton.setStyle(
        "-fx-background-color: rgba(34, 139, 34, 0.3); " +
        "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-padding: 12px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
        "-fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 1; -fx-cursor: hand;"
    );
    submitButton.setOnMouseEntered(e -> submitButton.setStyle(
        "-fx-background-color: rgba(34, 139, 34, 0.4); " +
        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-padding: 12px 30px; -fx-background-radius: 8; " +
        "-fx-border-color: rgba(255, 255, 255, 0.8);"
    ));
    submitButton.setOnMouseExited(e -> submitButton.setStyle(
        "-fx-background-color: rgba(34, 139, 34, 0.3); " +
        "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-padding: 12px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
        "-fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 1;"
    ));

    Button backButton = new Button("BACK TO LOGIN");
    backButton.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.2); " +
        "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-padding: 12px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
        "-fx-border-color: rgba(255, 255, 255, 0.4); -fx-border-width: 1; -fx-cursor: hand;"
    );
    backButton.setOnMouseEntered(e -> backButton.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.3); " +
        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-padding: 12px 30px; -fx-background-radius: 8; " +
        "-fx-border-color: rgba(255, 255, 255, 0.7);"
    ));
    backButton.setOnMouseExited(e -> backButton.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.2); " +
        "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-padding: 12px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
        "-fx-border-color: rgba(255, 255, 255, 0.4); -fx-border-width: 1;"
    ));

    buttonBox.getChildren().addAll(submitButton, backButton);

    // === Add components ===
   resetBox.getChildren().addAll(resetLabel, newPasswordBox, confirmPasswordBox, buttonBox);
    root.getChildren().add(resetBox);

    // === Event Handling ===
    submitButton.setOnAction(e -> {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Error", "Password must be at least 6 characters.");
            return;
        }

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setPassword(newPassword);
                saveUsers();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password reset successfully!");
                primaryStage.setScene(getLoginScene(primaryStage));
                return;
            }
        }

        showAlert(Alert.AlertType.ERROR, "Error", "User not found.");
    });

    backButton.setOnAction(e -> {
    clearLoginFields();  // optional, but can stay
    primaryStage.setScene(getLoginScene(primaryStage));
    primaryStage.setMaximized(true);  // keep fullscreen

});

    return new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
}


/////////////////////// SAVE USER INFO ///////////////////////////////////////////
private void saveUsers() {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(usersFile))) {
        out.writeObject(users);
    } catch (IOException e) {
        System.err.println("Failed to save users: " + e.getMessage());
        // Consider showing an alert to the user here
    }
}

/////////////////////// READ USER INFO ///////////////////////////////////////////
private void loadUsers() {
    if (!usersFile.exists()) {
        return; // No existing user data
    }
    
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(usersFile))) {
        Object obj = in.readObject();
        if (obj instanceof List) {
            users = (List<User>) obj;
        }
    } catch (IOException | ClassNotFoundException e) {
        System.err.println("Failed to load users: " + e.getMessage());
        // Consider showing an alert to the user here
        users = new ArrayList<>(); // Initialize empty list if loading fails
    }
}

        ////////////////////////START OF SIGN UP FUNCTIONALITY/////////////////////////////////////////
private Scene getSignUpScene(Stage primaryStage) {
    StackPane root = new StackPane();

    // === Background with blur ===
    Image bgImage = new Image(getClass().getResourceAsStream("/Fruit-veg-back.jpg"));
    ImageView bgImageView = new ImageView(bgImage);
    bgImageView.setFitWidth(primaryStage.getWidth());
    bgImageView.setFitHeight(primaryStage.getHeight());
    bgImageView.setPreserveRatio(false);
    bgImageView.setEffect(new GaussianBlur(10));
    root.getChildren().add(bgImageView);

    // === Sign-Up Box ===
    VBox formBox = new VBox(20);
    formBox.setAlignment(Pos.CENTER);
    formBox.setPadding(new Insets(30));
    formBox.setMaxWidth(440);
    formBox.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.2); " +
        "-fx-background-radius: 20; " +
        "-fx-border-radius: 20; " +
        "-fx-border-color: rgba(255, 255, 255, 0.3); " +
        "-fx-border-width: 1; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 20, 0, 0, 0);"
    );

    Label title = new Label("Create Your Account");
    title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
    title.setTextFill(Color.WHITE);

    // === Fields ===

    VBox usernameBox = new VBox(5);
    Label usernameLabel = new Label("Username:");
    usernameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
    TextField usernameField = createGlassTextField("Enter username");
    usernameBox.getChildren().addAll(usernameLabel, usernameField);

    VBox emailBox = new VBox(5);
    Label emailLabel = new Label("Email:");
    emailLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
    TextField emailField = createGlassTextField("Enter email");
    emailBox.getChildren().addAll(emailLabel, emailField);

    // Gender (Horizontal layout)
    HBox genderBox = new HBox(10);
    genderBox.setAlignment(Pos.CENTER_LEFT);
    Label genderLabel = new Label("Gender:");
    genderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
    ChoiceBox<String> genderChoice = new ChoiceBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
    genderChoice.setMinWidth(80);
    genderChoice.setStyle("-fx-background-color: rgba(34, 139, 34, 0.3); -fx-text-fill: white; -fx-border-color: black; -fx-border-radius: 8;");
    genderBox.getChildren().addAll(genderLabel, genderChoice);

    VBox passwordBox = new VBox(5);
    Label passwordLabel = new Label("Password:");
    passwordLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
    HBox passwordFieldBox = createPasswordFieldWithToggle("Enter password");
    PasswordField passwordField = (PasswordField) passwordFieldBox.getChildren().stream()
            .filter(node -> node instanceof PasswordField)
            .findFirst().orElse(null);
    passwordBox.getChildren().addAll(passwordLabel, passwordFieldBox);

    // === Buttons ===
    Button createButton = createGlassButton();
    createButton.setText("SUBMIT");
    createButton.setStyle(
        "-fx-background-color: rgba(34, 139, 34, 0.3); " +
        "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-padding: 12px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
        "-fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 1; " +
        "-fx-cursor: hand;"
    );

    createButton.setOnMouseEntered(e -> createButton.setStyle(
        "-fx-background-color: rgba(34, 139, 34, 0.4); " +
        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-padding: 12px 30px; -fx-background-radius: 8; " +
        "-fx-border-color: rgba(255, 255, 255, 0.8);"
    ));

    createButton.setOnMouseExited(e -> createButton.setStyle(
        "-fx-background-color: rgba(34, 139, 34, 0.3); " +
        "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-padding: 12px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
        "-fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 1; " +
        "-fx-cursor: hand;"
    ));

    Button backButton = new Button("BACK TO LOGIN");
    backButton.setStyle(
        "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: black; -fx-font-size: 14px; " +
        "-fx-font-weight: bold; -fx-padding: 10px 30px; -fx-background-radius: 8; -fx-border-radius: 8; " +
        "-fx-border-color: rgba(255,255,255,0.4); -fx-border-width: 1; -fx-cursor: hand;"
    );
    backButton.setOnMouseEntered(e -> backButton.setStyle(
        "-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: white; -fx-font-size: 14px; " +
        "-fx-font-weight: bold; -fx-padding: 10px 30px; -fx-border-color: rgba(255,255,255,0.7);"
    ));
    backButton.setOnMouseExited(e -> backButton.setStyle(
        "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: black; -fx-font-size: 14px; " +
        "-fx-font-weight: bold; -fx-padding: 10px 30px; -fx-border-color: rgba(255,255,255,0.4);"
    ));

    HBox buttonsBox = new HBox(20, createButton, backButton);
    buttonsBox.setAlignment(Pos.CENTER);

    // === Form Assembly ===
    formBox.getChildren().addAll(
        title,
        usernameBox,
        emailBox,
        genderBox,
        passwordBox,
        buttonsBox
    );

    root.getChildren().add(formBox);

    // === Logic ===
    createButton.setOnAction(e -> {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String gender = genderChoice.getValue();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || gender == null || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }
        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Error", "Password must be at least 6 characters.");
            return;
        }
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
                return;
            }
        }

        users.add(new User(username, email, gender, password));
        saveUsers();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully.");
        primaryStage.setScene(getLoginScene(primaryStage));
        primaryStage.setMaximized(true);
    });

    backButton.setOnAction(e -> {
        clearLoginFields();  // optional, but can stay
    primaryStage.setScene(getLoginScene(primaryStage));
    primaryStage.setMaximized(true);  // keep fullscreen
    });

    return new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
}

////////////////////////////END OF SIGN UP SCENE///////////////////////////////////////////////////
/// HOME PAGE///////

private Scene createHomeScene(Stage primaryStage) {
    loadProductsFromFile();

    // === Root Container ===
    StackPane root = new StackPane();

    // === Background with blur ===
    Image bgImage = new Image(getClass().getResourceAsStream("/Fruit-veg-back.jpg"));
    ImageView bgImageView = new ImageView(bgImage);
    bgImageView.setFitWidth(primaryStage.getWidth());
    bgImageView.setFitHeight(primaryStage.getHeight());
    bgImageView.setPreserveRatio(false);
    bgImageView.setEffect(new GaussianBlur(10));
    root.getChildren().add(bgImageView);

    // === Main Container ===
    VBox mainBox = new VBox(40);
    mainBox.setAlignment(Pos.CENTER);
    mainBox.setPadding(new Insets(40));

    // === Welcome Text ===
    Label welcomeLabel = new Label("Welcome to the Fruit & Vegetable Shop");
    welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
    welcomeLabel.setTextFill(Color.WHITE);

    // === Buttons Grid ===
    GridPane buttonGrid = new GridPane();
    buttonGrid.setAlignment(Pos.CENTER);
    buttonGrid.setHgap(40);
    buttonGrid.setVgap(30);

    // === Styled Buttons ===
    Button inventoryButton = createHomeButton("Inventory");
    inventoryButton.setOnAction(e -> {
        Scene inventoryScene = createInventoryScene(primaryStage);
        primaryStage.setScene(inventoryScene);
        primaryStage.setMaximized(true);
    });

    Button purchaseButton = createHomeButton("Purchase");
    purchaseButton.setOnAction(e -> primaryStage.setScene(createPurchaseScene(primaryStage)));

    salesButton = createHomeButton("Sales (Total Income: Rs. " + totalIncome + ")");

    Button logoutButton = createHomeButton("Logout");
  logoutButton.setOnAction(e -> {
    clearLoginFields();
    primaryStage.setScene(loginScene);
});


    // Add to Grid
    buttonGrid.add(inventoryButton, 0, 0);
    buttonGrid.add(purchaseButton, 0, 1);
    buttonGrid.add(salesButton, 1, 0);
    buttonGrid.add(logoutButton, 1, 1);

    mainBox.getChildren().addAll(welcomeLabel, buttonGrid);
    root.getChildren().add(mainBox);

    return new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
}

private Button createHomeButton(String text) {
    Button button = new Button(text);
    button.setPrefWidth(280);
    button.setPrefHeight(120);
    button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
    button.setStyle(
        "-fx-background-color: rgba(0, 0, 0, 0.6); " +
        "-fx-text-fill: white; -fx-background-radius: 12; " +
        "-fx-cursor: hand; -fx-border-color: white; -fx-border-radius: 12;"
    );

    button.setOnMouseEntered(e -> button.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.2); " +
        "-fx-text-fill: black; -fx-background-radius: 12; " +
        "-fx-border-color: white; -fx-border-radius: 12;"
    ));

    button.setOnMouseExited(e -> button.setStyle(
        "-fx-background-color: rgba(0, 0, 0, 0.6); " +
        "-fx-text-fill: white; -fx-background-radius: 12; " +
        "-fx-cursor: hand; -fx-border-color: white; -fx-border-radius: 12;"
    ));

    return button;
}
private void updateTotalIncome(double amount) {
        totalIncome += amount;
        // Update the text of the sales button to display the updated total income
        salesButton.setText("Total Income: Rs. " + totalIncome + ")");
    }


private Scene createInventoryScene(Stage primaryStage) {
    // === Root Setup with Glass UI ===
    StackPane root = new StackPane();
Rectangle2D screenBounds = Screen.getPrimary().getBounds();

    
    // === Background with Blur ===
    Image bgImage = new Image(getClass().getResourceAsStream("/Fruit-veg-back.jpg"));
    ImageView bgImageView = new ImageView(bgImage);
    bgImageView.setFitWidth(screenBounds.getWidth());
    bgImageView.setFitHeight(screenBounds.getHeight());
    bgImageView.setPreserveRatio(false);
    bgImageView.setEffect(new GaussianBlur(12));
    root.getChildren().add(bgImageView);

    VBox contentBox = new VBox(20);
contentBox.setAlignment(Pos.CENTER);
contentBox.setPadding(new Insets(20));
contentBox.setMaxWidth(Double.MAX_VALUE); // Let it grow full width


    // === Title ===
    Label title = new Label("Inventory Management");
    title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 34));
    title.setTextFill(Color.WHITE);

    // === Table ===
    tableView = new TableView<>();
    tableView.setPrefHeight(300);
    productList = FXCollections.observableArrayList(loadProductsFromFile());
    tableView.setItems(productList);

    TableColumn<Product, String> productIdColumn = new TableColumn<>("Product ID");
    productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));

    TableColumn<Product, String> productNameColumn = new TableColumn<>("Product Name");
    productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));

    TableColumn<Product, String> typeColumn = new TableColumn<>("Type");
    typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

    TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock (kg)");
    stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

    TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

    TableColumn<Product, String> statusColumn = new TableColumn<>("Status");
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

    TableColumn<Product, String> dateColumn = new TableColumn<>("Date");
    dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    TableColumn<Product, String> imageCol = new TableColumn<>("Image");
imageCol.setPrefWidth(100);
imageCol.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

imageCol.setCellFactory(col -> new TableCell<>() {
    private final ImageView imageView = new ImageView();

    {
        imageView.setFitWidth(80);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);
    }

    @Override
    protected void updateItem(String path, boolean empty) {
        super.updateItem(path, empty);
        if (empty || path == null || path.isEmpty()) {
            setGraphic(null);
        } else {
            try {
                imageView.setImage(new Image(new FileInputStream(path)));
                setGraphic(imageView);
            } catch (Exception e) {
                setGraphic(null); // or optionally show default image
            }
        }
    }
});


    tableView.getColumns().addAll(productIdColumn, productNameColumn, typeColumn, stockColumn, priceColumn, statusColumn, dateColumn);
    tableView.getColumns().add(imageCol); // or imagePathColumn if using text

    // === Form Grid ===
    GridPane formGrid = new GridPane();
    formGrid.setVgap(10);
    formGrid.setHgap(15);
    formGrid.setAlignment(Pos.CENTER);

    productIdField = new TextField();
    productIdField.setPromptText("Product ID");
    formGrid.add(styledLabel("Product ID:"), 0, 0);
    formGrid.add(productIdField, 1, 0);

    productNameField = new TextField();
    productNameField.setPromptText("Product Name");
    formGrid.add(styledLabel("Product Name:"), 0, 1);
    formGrid.add(productNameField, 1, 1);

    typeComboBox = new ComboBox<>(FXCollections.observableArrayList("Fruit", "Vegetable"));
    typeComboBox.setPromptText("Choose Type...");
    formGrid.add(styledLabel("Type:"), 0, 2);
    formGrid.add(typeComboBox, 1, 2);

    stockField = new TextField();
    stockField.setPromptText("0");
    formGrid.add(styledLabel("Stock (kg):"), 2, 0);
    formGrid.add(stockField, 3, 0);

    priceField = new TextField();
    priceField.setPromptText("0.0");
    formGrid.add(styledLabel("Price (Rs.):"), 2, 1);
    formGrid.add(priceField, 3, 1);

    statusComboBox = new ComboBox<>(FXCollections.observableArrayList("Available", "Unavailable"));
    statusComboBox.setPromptText("Choose Status...");
    formGrid.add(styledLabel("Status:"), 2, 2);
    formGrid.add(statusComboBox, 3, 2);
    imagePathField = new TextField();
imagePathField.setPromptText("Image path");
imagePathField.setEditable(false);

uploadImageButton = createGlassButton("Upload Image");
uploadImageButton.setOnAction(e -> {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose Product Image");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
    );
    File selectedFile = fileChooser.showOpenDialog(primaryStage);
    if (selectedFile != null) {
        imagePathField.setText(selectedFile.getAbsolutePath());
    }
});

formGrid.add(styledLabel("Image:"), 0, 3);
formGrid.add(imagePathField, 1, 3);
formGrid.add(uploadImageButton, 2, 3);


    // === Buttons ===
    HBox buttonBox = new HBox(15);
    buttonBox.setAlignment(Pos.CENTER);

    addButton = createGlassButton("Add");
    updateButton = createGlassButton("Update");
    deleteButton = createGlassButton("Delete");
    Button clearButton = createGlassButton("Clear");
    Button goBackButton = createGlassButton("Go Back");

    buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton, goBackButton);

    // === Logic ===
    goBackButton.setOnAction(e -> primaryStage.setScene(homeScene));
    addButton.setOnAction(e -> addProduct());
    updateButton.setOnAction(e -> updateProduct());
    deleteButton.setOnAction(e -> {
        Product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            productList.remove(selected);
            saveProductsToFile(productList);
        }
    });
    clearButton.setOnAction(e -> clearFields());
    tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
        if (newSel != null) {
            productIdField.setText(newSel.getProductId());
            productNameField.setText(newSel.getProductName());
            typeComboBox.setValue(newSel.getType());
            stockField.setText(String.valueOf(newSel.getStock()));
            priceField.setText(String.valueOf(newSel.getPrice()));
            statusComboBox.setValue(newSel.getStatus());
            imagePathField.setText(newSel.getImagePath());

        }
    });

    // === Final Assembly ===
    contentBox.getChildren().addAll(title, formGrid, buttonBox, tableView);
    root.getChildren().add(contentBox);

    Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
    primaryStage.setScene(scene);
    primaryStage.setMaximized(true);
    primaryStage.show();
    return scene;
}
private Label styledLabel(String text) {
    Label label = new Label(text);
    label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
    label.setTextFill(Color.WHITE);
    return label;
}

private Button createGlassButton(String text) {
    Button button = new Button(text);
    button.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 8; -fx-border-color: white; -fx-border-radius: 8; -fx-cursor: hand;");
    button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-color: white; -fx-background-radius: 8; -fx-border-radius: 8;"));
    button.setOnMouseExited(e -> button.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 8; -fx-border-color: white; -fx-border-radius: 8;"));
    return button;
}

private void addProduct() {
    String productId = productIdField.getText().trim();
    String productName = productNameField.getText().trim();
    String type = typeComboBox.getValue();
    String status = statusComboBox.getValue();
    String stockText = stockField.getText().trim();
    String priceText = priceField.getText().trim();
    String imagePath = imagePathField.getText().trim();

    if (productId.isEmpty() || productName.isEmpty() || type == null || status == null || stockText.isEmpty() || priceText.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
        return;
    }
    if (imagePath.isEmpty()) {
    showAlert(Alert.AlertType.ERROR, "Input Error", "Please choose an image.");
    return;
}

    try {
        int stock = Integer.parseInt(stockText);
        double price = Double.parseDouble(priceText);
        String date = LocalDate.now().toString();

      Product product = new Product(productId, productName, type, stock, price, status, date, imagePath);
        productList.add(product);
        saveProductsToFile(productList);
        clearFields();

        showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.ERROR, "Format Error", "Stock must be an integer and Price must be a number.");
    }
}
private void updateProduct() {
    Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
    if (selectedProduct == null) {
        showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a product to update.");
        return;
    }

    String productId = productIdField.getText().trim();
    String productName = productNameField.getText().trim();
    String type = typeComboBox.getValue();
    String status = statusComboBox.getValue();
    String stockText = stockField.getText().trim();
    String priceText = priceField.getText().trim();

    if (productId.isEmpty() || productName.isEmpty() || type == null || status == null || stockText.isEmpty() || priceText.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
        return;
    }

    try {
        int stock = Integer.parseInt(stockText);
        double price = Double.parseDouble(priceText);

        selectedProduct.setProductId(productId);
        selectedProduct.setProductName(productName);
        selectedProduct.setType(type);
        selectedProduct.setStock(stock);
        selectedProduct.setPrice(price);
        selectedProduct.setStatus(status);
        selectedProduct.setDate(LocalDate.now().toString());

        tableView.refresh();
        saveProductsToFile(productList);
        clearFields();

        showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.ERROR, "Format Error", "Stock must be an integer and Price must be a number.");
    }
}

private void clearFields() {
        productIdField.clear();
        productNameField.clear();
        typeComboBox.setValue(null);
        stockField.clear();
        priceField.clear();
        statusComboBox.setValue(null);
        imagePathField.clear();

    }
private List<Product> loadProductsFromFile() {
    File file = new File("products.ser");
    if (!file.exists()) return new ArrayList<>();

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        return (List<Product>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
}

private void saveProductsToFile(ObservableList<Product> products) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("products.ser"))) {
        oos.writeObject(new ArrayList<>(products));
    } catch (IOException e) {
        e.printStackTrace();
    }
}
private Scene createPurchaseScene(Stage primaryStage) {
    StackPane root = new StackPane();

    // === Background with blur ===
    Image bgImage = new Image(getClass().getResourceAsStream("/Fruit-veg-back.jpg"));
    ImageView bgImageView = new ImageView(bgImage);
    bgImageView.setFitWidth(1350);
    bgImageView.setFitHeight(800);
    bgImageView.setPreserveRatio(false);
    bgImageView.setEffect(new GaussianBlur(10));
    root.getChildren().add(bgImageView);

    // === Main layout ===
    HBox mainLayout = new HBox(20);
    mainLayout.setPadding(new Insets(20));
    mainLayout.setAlignment(Pos.CENTER);

    // === Left: Product Menu ===
    GridPane menuGrid = createMenuGrid();
loadProductsToMenuGrid(menuGrid);

menuGrid.setPadding(new Insets(15));
menuGrid.setHgap(20);
menuGrid.setVgap(20);

ScrollPane menuScrollPane = new ScrollPane(menuGrid);
menuScrollPane.setFitToWidth(true);
menuScrollPane.setFitToHeight(true);
menuScrollPane.setPrefViewportWidth(950);
menuScrollPane.setStyle(
    "-fx-background-color: transparent;" +
    "-fx-background-insets: 0;" +
    "-fx-padding: 15;" +
    "-fx-control-inner-background: transparent;" +
    "-fx-background: transparent;" +
    "-fx-border-color: transparent;" +
    "-fx-focus-color: transparent;" +
    "-fx-faint-focus-color: transparent;"
);



    // === Right: Order Summary ===
    VBox orderSummary = createOrderSummary(primaryStage);
    orderSummary.setStyle(
        "-fx-background-color: rgba(0, 100, 0, 0.3);" +
        "-fx-background-radius: 20; -fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 0);"
    );
    orderSummary.setMaxWidth(350);

    mainLayout.getChildren().addAll(menuScrollPane, orderSummary);
    root.getChildren().add(mainLayout);

    Scene scene = new Scene(root, 1350, 800);
    return scene;
}
private VBox createOrderSummary(Stage primaryStage) {
    VBox vbox = new VBox(15);
    vbox.setPadding(new Insets(20));
    vbox.setAlignment(Pos.TOP_CENTER);
    vbox.setPrefWidth(400);
    vbox.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.2);" +
        "-fx-background-radius: 15;" +
        "-fx-border-radius: 15;" +
        "-fx-border-color: rgba(255,255,255,0.3);" +
        "-fx-border-width: 1;" +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 0);"
    );

    // === Order Table ===
    TableView<OrderItem> table = new TableView<>(orderData);
    table.setPrefHeight(250);
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    TableColumn<OrderItem, String> productNameCol = new TableColumn<>("Product");
    productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

    TableColumn<OrderItem, Integer> quantityCol = new TableColumn<>("Qty (kg)");
    quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

    TableColumn<OrderItem, Double> priceCol = new TableColumn<>("Price (Rs.)");
    priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

    table.getColumns().addAll(productNameCol, quantityCol, priceCol);

    // === Labels & Fields ===
    totalLabel = new Label("Total: Rs. 0.0");
    totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
    totalLabel.setTextFill(Color.WHITE);

    Label amountLabel = new Label("Amount Given:");
    amountLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
    amountLabel.setTextFill(Color.WHITE);

    TextField amountField = new TextField();
    amountField.setPromptText("Enter amount");
    amountField.setStyle("-fx-background-color: white; -fx-border-radius: 8;");

    Label changeLabel = new Label("Change: Rs. 0.0");
    changeLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
    changeLabel.setTextFill(Color.WHITE);

    // === Buttons ===
    Button payButton = createStyledButton("PAY");
    Button removeButton = createStyledButton("REMOVE");
    Button receiptButton = createStyledButton("RECEIPT");
    Button backButton = createStyledButton("GO BACK");

    // === Button Actions ===
    backButton.setOnAction(e -> primaryStage.setScene(homeScene));

    receiptButton.setOnAction(e -> showReceiptWindow(table));

    removeButton.setOnAction(e -> {
        orderData.clear();
        updateTotal();
    });

    payButton.setOnAction(e -> {
        if (orderData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Orders", "There are no items in the order.");
            return;
        }

        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter an amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid amount entered.");
            return;
        }

        double total = orderData.stream().mapToDouble(OrderItem::getPrice).sum();

        Optional<ButtonType> resultOptional = showAlert(Alert.AlertType.CONFIRMATION, "Confirmation", "Are you sure you want to proceed with the payment?");
        resultOptional.ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (amount >= total) {
                    updateTotalIncome(total);
                    boolean stockSufficient = true;

                    for (OrderItem item : orderData) {
                        Product product = getProductByName(item.getProductName());
                        if (product != null && item.getQuantity() > product.getStock()) {
                            stockSufficient = false;
                            showAlert(Alert.AlertType.ERROR, "Stock Error", "Requested for " + product.getProductName() + " exceeds available stock.");
                            break;
                        }
                    }

                    if (stockSufficient) {
                        changeLabel.setText("Change: Rs. " + String.format("%.2f", (amount - total)));
                        for (OrderItem item : orderData) {
                            Product product = getProductByName(item.getProductName());
                            if (product != null) {
                                product.setStock(product.getStock() - item.getQuantity());
                            }
                        }

                        updateInventoryInterface();
                        updateTotal();
                    }
                } else {
                    changeLabel.setText("Insufficient amount");
                }
            }
        });
    });

    // === Assemble Buttons ===
    VBox buttonBox = new VBox(10, payButton, removeButton, receiptButton, backButton);
    buttonBox.setAlignment(Pos.CENTER);

    vbox.getChildren().addAll(table, totalLabel, amountLabel, amountField, changeLabel, buttonBox);
    return vbox;
}
private Button createStyledButton(String text) {
    Button button = new Button(text);
    button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
    button.setStyle(
        "-fx-background-color: rgba(0,0,0,0.8); " +
        "-fx-text-fill: white; " +
        "-fx-padding: 10 20; " +
        "-fx-background-radius: 10; " +
        "-fx-cursor: hand;"
    );
    button.setOnMouseEntered(e -> button.setStyle(
        "-fx-background-color: rgba(255,255,255,0.3); " +
        "-fx-text-fill: black; " +
        "-fx-padding: 10 20; " +
        "-fx-background-radius: 10; " +
        "-fx-cursor: hand;"
    ));
    button.setOnMouseExited(e -> button.setStyle(
        "-fx-background-color: rgba(0,0,0,0.8); " +
        "-fx-text-fill: white; " +
        "-fx-padding: 10 20; " +
        "-fx-background-radius: 10; " +
        "-fx-cursor: hand;"
    ));
    return button;
}
private Product getProductByName(String productName) {
    for (Product product : productList) {
        if (product.getProductName().equals(productName)) {
            return product;
        }
    }
    return null; // Product not found
}
private void updateInventoryInterface() {
    if (tableView != null) {
        tableView.refresh();
        saveProductsToFile(productList);
    }
}
private GridPane createMenuGrid() {
    GridPane gridPane = new GridPane();
    gridPane.setHgap(15);
    gridPane.setVgap(15);
    gridPane.setPadding(new Insets(20));

    // ✅ Apply glass-like style to background
    gridPane.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.1);" +
        "-fx-background-radius: 15;" +
        "-fx-border-radius: 15;" +
        "-fx-border-color: rgba(255, 255, 255, 0.3);" +
        "-fx-border-width: 1;" +
        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0, 0);"
    );

    return gridPane;
}

private void loadProductsToMenuGrid(GridPane menuGrid) {
    List<Product> products = loadProductsFromFile(); // Load from file
    menuGrid.getChildren().clear(); // Clear any previous UI

    final int NUM_COLUMNS = 3; // Adjust based on your layout
    int rowIndex = 0;
    int colIndex = 0;

    for (Product product : products) {
        VBox productBox = createProductBox(product); // You should already have this method

        // Add to grid
        menuGrid.add(productBox, colIndex, rowIndex);

        // Track column/row placement
        colIndex++;
        if (colIndex == NUM_COLUMNS) {
            colIndex = 0;
            rowIndex++;
        }
    }
}
private void showReceiptWindow(TableView<OrderItem> table) {
    Customer customer = new Customer(); // Assuming customer ID is auto-generated
    Stage receiptStage = new Stage();
    receiptStage.setTitle("Receipt");

    // === Receipt Layout Container ===
    VBox receiptVbox = new VBox(20);
    receiptVbox.setAlignment(Pos.CENTER);
    receiptVbox.setPadding(new Insets(30));
    receiptVbox.setStyle("-fx-background-color: white;");

    // === Header Labels ===
    Label shopNameLabel = new Label("Fruit and Vegetable Stall");
    shopNameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
    shopNameLabel.setTextFill(Color.BLACK);

    Label customerIdLabel = new Label("Customer ID: " + customer.getCustomerId());
    customerIdLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    customerIdLabel.setTextFill(Color.BLACK);

    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    Label dateLabel = new Label("Date: " + dateFormatter.format(now));
    dateLabel.setTextFill(Color.BLACK);
    Label timeLabel = new Label("Time: " + timeFormatter.format(now));
    timeLabel.setTextFill(Color.BLACK);

    // === Receipt Table ===
    TableView<OrderItem> receiptTable = new TableView<>(FXCollections.observableArrayList(table.getItems()));
    receiptTable.setMaxHeight(250);

    TableColumn<OrderItem, String> receiptProductNameCol = new TableColumn<>("Product");
    receiptProductNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
    receiptProductNameCol.setPrefWidth(160);

    TableColumn<OrderItem, String> receiptTypeCol = new TableColumn<>("Type");
    receiptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    receiptTypeCol.setPrefWidth(80);

    TableColumn<OrderItem, Integer> receiptQuantityCol = new TableColumn<>("Qty");
    receiptQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    receiptQuantityCol.setPrefWidth(80);

    TableColumn<OrderItem, Double> receiptPriceCol = new TableColumn<>("Price (Rs.)");
    receiptPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    receiptPriceCol.setPrefWidth(100);

    receiptTable.getColumns().addAll(receiptProductNameCol, receiptTypeCol, receiptQuantityCol, receiptPriceCol);

    // === Total Label ===
    double total = table.getItems().stream().mapToDouble(OrderItem::getPrice).sum();
    Label receiptTotalLabel = new Label("Total: Rs. " + total);
    receiptTotalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    receiptTotalLabel.setTextFill(Color.BLACK);

    // === Thank You Label ===
    Label thankYouLabel = new Label("Thank you for visiting!");
    thankYouLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
    thankYouLabel.setTextFill(Color.BLACK);

    receiptVbox.getChildren().addAll(
        shopNameLabel,
        customerIdLabel,
        dateLabel,
        timeLabel,
        receiptTable,
        receiptTotalLabel,
        thankYouLabel
    );

    Scene receiptScene = new Scene(receiptVbox, 500, 400);
    receiptStage.setScene(receiptScene);
    receiptStage.setResizable(false);
    receiptStage.show();
}

private VBox createProductBox(Product product) {
    VBox productBox = new VBox(10);
    productBox.setPadding(new Insets(15));
    productBox.setAlignment(Pos.CENTER);
    productBox.setPrefWidth(250);
    productBox.setStyle(
        "-fx-background-color: rgba(0, 100, 0, 0.25); " +
        "-fx-background-radius: 15; " +
        "-fx-border-radius: 15; " +
        "-fx-border-color: rgba(255, 255, 255, 0.2); " +
        "-fx-border-width: 1; " +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.5, 0, 0);"
    );

    // === Product Image ===
    if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
        try {
            ImageView imageView = new ImageView(new Image(new FileInputStream(product.getImagePath())));
            imageView.setFitWidth(120);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            productBox.getChildren().add(imageView);
        } catch (Exception e) {
            System.out.println("Image not found for product [" + product.getProductName() + "]: " + e.getMessage());
        }
    }

    // === Product Name Label ===
    Label nameLabel = new Label(product.getProductName());
    nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
    nameLabel.setTextFill(Color.WHITE);
    nameLabel.setWrapText(true);
    nameLabel.setAlignment(Pos.CENTER);

    // === Product Price Label ===
    Label priceLabel = new Label(String.format("Price: Rs. %.2f", product.getPrice()));
    priceLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    priceLabel.setTextFill(Color.WHITE);

    // === Quantity Spinner ===
    Spinner<Integer> quantitySpinner = new Spinner<>(0, 100, 0);
    quantitySpinner.setEditable(true);
    quantitySpinner.setPrefWidth(80);
    quantitySpinner.setStyle("-fx-background-color: black; -fx-border-radius: 5;");

    // === Add Button ===
    Button addButton = new Button("Add");
    addButton.setStyle(
        "-fx-background-color: black; -fx-text-fill: white; " +
        "-fx-font-weight: bold; -fx-background-radius: 8;"
    );
    addButton.setOnMouseEntered(e -> addButton.setStyle(
        "-fx-background-color: #333333; -fx-text-fill: white; " +
        "-fx-font-weight: bold; -fx-background-radius: 8;"
    ));
    addButton.setOnMouseExited(e -> addButton.setStyle(
        "-fx-background-color: black; -fx-text-fill: white; " +
        "-fx-font-weight: bold; -fx-background-radius: 8;"
    ));

    // === Add Button Logic ===
    addButton.setOnAction(e -> {
        try {
            int quantity = quantitySpinner.getValue();
            if (quantity > 0) {
                if (quantity <= product.getStock()) {
                    orderData.add(new OrderItem(
                        product.getProductName(),
                        product.getType(),
                        quantity,
                        product.getPrice() * quantity
                    ));
                    updateTotal();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Stock Error", "Requested quantity exceeds stock.");
                }
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid quantity entered.");
        }
    });

    // === Final Assembly ===
    productBox.getChildren().addAll(nameLabel, priceLabel, quantitySpinner, addButton);
    return productBox;
}

private void updateTotal() {
    double total = orderData.stream()
                            .mapToDouble(OrderItem::getPrice)
                            .sum();

    totalLabel.setText(String.format("Total: Rs. %.2f", total));
}


private Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType); // Create an Alert with the specified type (INFO, ERROR, etc.)
    alert.setTitle(title);              // Set the title of the alert window
    alert.setHeaderText(null); // Optional
    alert.setContentText(message);      // Set the message to be shown
    return alert.showAndWait();         // Show the alert and return user's response
}
    public static void main(String[] args) {
        launch(args);
    }
}
