//Saúl Alberto Ramos Laborín.

package LibraryForTeachers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/Views/MainAppView.fxml"));
        primaryStage.setTitle("Biblioteca para profesores");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);
        primaryStage.getIcons().add(new Image("/Resources/study.png"));
        primaryStage.setOnCloseRequest(event -> Platform.exit());
        primaryStage.show();

        root = FXMLLoader.load(getClass().getResource("/Views/LoginView.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Iniciar sesión");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.getIcons().add(new Image("/Resources/login.png"));
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
