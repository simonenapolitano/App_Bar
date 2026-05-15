package it.argbar;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.fxml.*;
import com.google.firebase.auth.UserRecord;

import io.opencensus.metrics.export.Summary.Snapshot;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.io.InputStream;

public class LoginController {
    private Stage stage;
    private Scene scene;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    private void setErrorMessage(String message){
        javafx.application.Platform.runLater(() -> errorLabel.setText(message));
    }

    public String getApiKey() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            if (input == null) {
                System.err.println("Spiacente, config.properties non trovato!");
                return null;
            }
            prop.load(input);
            
            return prop.getProperty("firebase.api.key");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void loginUtente(){
        setErrorMessage("");
        String email = emailField.getText();
        String password = passwordField.getText();
        if(email.isEmpty()){
            setErrorMessage("Il campo email non può essere vuoto!");
            return;
        } else if(password.isEmpty()){
            setErrorMessage("Il campo password non può essere vuoto!");
            return;
        }

        String apiKey = getApiKey();
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

        // Prepare the JSON body
        String jsonPayload = String.format(
            "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
            email, password
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            int statusCode = response.statusCode();
            String body = response.body();

            if (statusCode == 200) {
                Platform.runLater(() -> {
                    // Codice per cambiare scena o mostrare successo
                    System.out.println("Login con successo");
                });
            } else {
                String message = "";
                if (body.contains("INVALID_LOGIN_CREDENTIALS")) {
                    message = "Email o Password errata!";
                } else if (body.contains("EMAIL_NOT_FOUND")) {
                    message = "Utente non trovato!";
                } else if (body.contains("INVALID_PASSWORD")) {
                    message = "Password errata!";
                } else if (body.contains("USER_DISABLED")) {
                    message = "Account disabilitato.";
                } else if (body.contains("TOO_MANY_ATTEMPTS_TRY_LATER")) {
                    message = "Troppi tentativi falliti. Riprova più tardi.";
                }

                final String finaleMessage = message;
                Platform.runLater(() -> {
                    setErrorMessage(finaleMessage);
                });
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public void vaiARegistrazione(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Registrazione.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void initialize(){
        backgroundImage.setImage(new Image("file:assets/images/background.png"));
    }
}
