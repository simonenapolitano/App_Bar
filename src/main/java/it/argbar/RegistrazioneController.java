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


public class RegistrazioneController {
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
    
    public void registraUtente(){
        setErrorMessage("");
        String email = emailField.getText();
        String password = passwordField.getText();
        if(email.isEmpty()){
            setErrorMessage("Il campo email non può essere vuoto!");
            return;
        } else if(password.isEmpty()){
            setErrorMessage("Il campo password non può essere vuoto!");
            return;
        } else if(password.length() < 6){
            setErrorMessage("La password deve essere almeno di 6 caratteri!");
            return;
        }
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(email).setPassword(password).setDisplayName("Nuovo utente").setDisabled(false);
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("Utente creato con successo: " + userRecord.getUid());
        } catch (FirebaseAuthException e) {
            if(e.getAuthErrorCode() == AuthErrorCode.EMAIL_ALREADY_EXISTS){
                setErrorMessage("L'utente esiste già!");
            } else{
                e.printStackTrace();
            }
        }
    }
    public void vaiALogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize(){
        backgroundImage.setImage(new Image("file:assets/images/backgroundR.png"));
    }
}
