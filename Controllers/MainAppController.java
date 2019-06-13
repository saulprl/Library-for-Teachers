package Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainAppController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem newLoanButton;

    @FXML
    private MenuItem newBookButton;

    @FXML
    private MenuItem newCategoryButton;

    @FXML
    private MenuItem payFineButton;

    @FXML
    private MenuItem returnBookButton;

    @FXML
    private MenuItem quitButton;

    @FXML
    private MenuItem byAuthorButton;

    @FXML
    private MenuItem byCategoryButton;

    @FXML
    private MenuItem onLoanButton;

    @FXML
    private MenuItem teachersButton;

    @FXML
    private MenuItem booksTableButton;

    @FXML
    private MenuItem authorsTableButton;

    @FXML
    private MenuItem categoriesTableButton;

    @FXML
    private MenuItem teachersTableButton;

    @FXML
    private MenuItem byAuthorsTableButton;

    @FXML
    private MenuItem byCategoryTableButton;

    @FXML
    private MenuItem onLoanTableButton;

    @FXML
    private MenuItem mostBorrowedButton;

    @FXML
    private MenuItem highestFinesButton;

    @FXML
    private void newLoanButtonPressed(ActionEvent event) {
        showStage(newLoanButton.getText());
    }

    @FXML
    private void newBookButtonPressed(ActionEvent event) {
        showStage(newBookButton.getText());
    }

    @FXML
    private void newCategoryButtonPressed(ActionEvent event) {
        showStage(newCategoryButton.getText());
    }

    @FXML
    private void payFineButtonPressed(ActionEvent event) {
        showStage(payFineButton.getText());
    }

    @FXML
    private void returnBookButtonPressed(ActionEvent event) {
        showStage(returnBookButton.getText());
    }

    @FXML
    private void quitButtonPressed(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void byAuthorButtonPressed(ActionEvent event) {
        showStage(byAuthorButton.getText());
    }

    @FXML
    private void byCategoryButtonPressed(ActionEvent event) {
        showStage(byCategoryButton.getText());
    }

    @FXML
    private void onLoanButtonPressed(ActionEvent event) {
        showStage(onLoanButton.getText());
    }

    @FXML
    private void teachersButtonPressed(ActionEvent event) {
        showStage(teachersButton.getText());
    }

    @FXML
    private void booksTableButtonPressed(ActionEvent event) {
        showStage(booksTableButton.getText());
    }

    @FXML
    private void authorsTableButtonPressed(ActionEvent event) {
        showStage(authorsTableButton.getText());
    }

    @FXML
    private void categoriesTableButtonPressed(ActionEvent event) {
        showStage(categoriesTableButton.getText());
    }

    @FXML
    private void teachersTableButtonPressed(ActionEvent event) {
        showStage(teachersTableButton.getText());
    }

    @FXML
    private void byAuthorsTableButtonPressed(ActionEvent event) {
        showStage(byAuthorsTableButton.getText());
    }

    @FXML
    private void byCategoryTableButtonPressed(ActionEvent event) {
        showStage(byCategoryTableButton.getText());
    }

    @FXML
    private void onLoanTableButtonPressed(ActionEvent event) {
        showStage(onLoanTableButton.getText());
    }

    @FXML
    private void mostBorrowedButtonPressed(ActionEvent event) {
        showStage(mostBorrowedButton.getText());
    }

    @FXML
    private void highestFinesButtonPressed(ActionEvent event) {
        showStage(highestFinesButton.getText());
    }

    @FXML
    private void initialize() {

    }

    private void showStage(String sourceButton) {
        String viewPath = "";

        switch (sourceButton) {
            case "Nuevo préstamo":
                viewPath = "/Views/File/NewLoanView.fxml";
                break;
            case "Nuevo libro":
                viewPath = "/Views/File/NewBookView.fxml";
                break;
            case "Nueva categoría":
                viewPath = "/Views/File/NewCategoryView.fxml";
                break;
            case "Pagar multa":
                viewPath = "/Views/File/PayFineView.fxml";
                break;
            case "Devolución":
                viewPath = "/Views/File/ReturnBookView.fxml";
                break;
            case "Libros por autor":
                viewPath = "/Views/Queries/ByAuthorView.fxml";
                break;
            case "Libros por categoría":
                viewPath = "/Views/Queries/ByCategoryView.fxml";
                break;
            case "Libros en préstamo":
                viewPath = "/Views/Queries/OnLoanView.fxml";
                break;
            case "Profesores con multa":
                viewPath = "/Views/Queries/FinedTeachersView.fxml";
                break;
            case "Tabla de libros":
                viewPath = "/Views/TableManagement/BooksTableView.fxml";
                break;
            case "Tabla de autores":
                viewPath = "/Views/TableManagement/AuthorsTableView.fxml";
                break;
            case "Tabla de categorías":
                viewPath = "/Views/TableManagement/CategoriesTableView.fxml";
                break;
            case "Tabla de profesores":
                viewPath = "/Views/TableManagement/TeachersTableView.fxml";
                break;
            case "Relación de libros-autores":
                viewPath = "/Views/TableManagement/ByAuthorTableView.fxml";
                break;
            case "Relación de libros-categorías":
                viewPath = "/Views/TableManagement/ByCategoryTableView.fxml";
                break;
            case "Relación de libros-profesores":
                viewPath = "/Views/TableManagement/OnLoanTableView.fxml";
                break;
            case "Libros más prestados":
                viewPath = "/Views/Reports/MostBorrowedBooksView.fxml";
                break;
            case "Multas":
                viewPath = "/Views/Reports/HighestFinesView.fxml";
                break;
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource(viewPath));
            Stage stage = new Stage();
            stage.setTitle(sourceButton);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image("/Resources/study.png"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            showErrorDialog(e.getMessage());
        }
    }

    private void showErrorDialog(String e) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("Error");
        dialog.setHeaderText(null);
        dialog.setContentText("Ocurrió un error inesperado. Excepción: " + e);
        dialog.showAndWait();
    }
}