package Controllers.TableManagement;

import Models.Book;
import Models.ConnectionMethods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BooksTableController {

    private static Book selectedBook;
    private static final String INITIALIZEQUERY = "SELECT * FROM books;";
    private static String query;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField isbnTextField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button registerButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private TableView tableView;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        if (!(titleTextField.getText().isEmpty() && isbnTextField.getText().isEmpty() &&
                datePicker.getValue() == null)) {
            query = String.format("INSERT INTO books VALUES('%s', '%s', '%s');", isbnTextField.getText(),
                    titleTextField.getText(), datePicker.getValue());
            try {
                ConnectionMethods.executeUpdate(query);

                updateTable((query = INITIALIZEQUERY));

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro insertado");
                dialog.setHeaderText(null);
                dialog.setContentText("El libro ha sido insertado en la tabla correctamente.");
                dialog.showAndWait();
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error al insertar");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al insertar el libro. Excepción: " + sqlEx.getMessage());
                dialog.showAndWait();
            }
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Parámetros de registro");
            dialog.setHeaderText(null);
            dialog.setContentText("Deben ingresarse todos los parámetros de registro.");
            dialog.showAndWait();
        }

        clearButtonPressed(new ActionEvent());
    }

    @FXML
    private void deleteButtonPressed(ActionEvent event) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Borrar registro");
        confirmation.setHeaderText(null);
        confirmation.setContentText("¿Estás seguro de que deseas eliminar este libro?");
        confirmation.showAndWait();

        if (confirmation.getResult() == ButtonType.OK) {
            if (selectedBook != null) {
                String isbn = selectedBook.isbnProperty().getValue();

                query = String.format("DELETE FROM books WHERE isbn = '%s';", isbn);

                try {
                    ConnectionMethods.executeUpdate(query);

                    updateTable((query = INITIALIZEQUERY));

                    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setTitle("Borrar registro");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Se ha eliminado el libro seleccionado correctamente.");
                    dialog.showAndWait();
                } catch (SQLException sqlEx) {
                    Alert dialog = new Alert(Alert.AlertType.ERROR);
                    dialog.setTitle("Error al borrar");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Ha ocurrido un error al eliminar el libro. Excepción: " + sqlEx.getMessage());
                    dialog.showAndWait();
                }
            } else {
                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Selección nula");
                dialog.setHeaderText(null);
                dialog.setContentText("Debes seleccionar un objeto en la tabla para poder borrarlo.");
                dialog.showAndWait();
            }
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Borrar registro");
            dialog.setHeaderText(null);
            dialog.setContentText("No se efectuó ningún cambio.");
            dialog.showAndWait();
        }
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        updateTable((query = INITIALIZEQUERY));

        for (Node node : gridPane.getChildren()) {
            if (node instanceof TextField) {
                ((TextField) node).clear();
                ((TextField) node).setPromptText(((TextField) node).getText());
            } else if (node instanceof Button) {
                if (!node.getId().contains("clear")) {
                    node.setDisable(true);
                }
            } else if (node instanceof DatePicker) {
                ((DatePicker) node).setValue(null);
            }
        }
    }

    @FXML
    private void initialize() {
        updateTable((query = INITIALIZEQUERY));

        datePicker = new DatePicker();
        datePicker.setPrefWidth(isbnTextField.getPrefWidth());
        gridPane.add(datePicker, 1, 2);

        registerButton.setDisable(true);
        deleteButton.setDisable(true);

        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.isEmpty() && isbnTextField.getText().isEmpty() && datePicker.getValue() == null)) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }

            if (newValue.length() > 30) {
                titleTextField.setText(oldValue);
            }
        });

        isbnTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.isEmpty() && titleTextField.getText().isEmpty() && datePicker.getValue() == null)) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }

            if (newValue.length() > 20) {
                isbnTextField.setText(oldValue);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                deleteButton.setDisable(false);

                selectedBook = (Book) (newValue);

                titleTextField.setPromptText(selectedBook.book_titleProperty().getValue());
                isbnTextField.setPromptText(selectedBook.isbnProperty().getValue());
                datePicker.setValue(selectedBook.date_of_publicationProperty().getValue());
            } else {
                selectedBook = null;
                deleteButton.setDisable(true);
            }
        });

        datePicker.setConverter(new StringConverter<>() {
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) return "";
                return formatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) return null;
                return LocalDate.parse(dateString, formatter);
            }
        });
    }

    private void updateTable(String sql) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        try {
            ResultSet resultSet = ConnectionMethods.executeQuery(sql);

            if (resultSet != null) {
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "isbn":
                            column.setText("ISBN");
                            break;
                        case "book_title":
                            column.setText("Título");
                            break;
                        case "date_of_publication":
                            column.setText("Fecha de publicación");
                            break;
                        default:
                            column.setText(resultSet.getMetaData().getColumnName(i + 1));
                    }

                    column.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i + 1)));
                    tableView.getColumns().add(column);
                }

                tableView.setItems(data);
            }
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }

    private ArrayList<Book> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<Book> data = new ArrayList<>();

        while (resultSet.next()) {
            Book book = new Book();

            book.isbn.setValue(resultSet.getString("isbn"));
            book.book_title.setValue(resultSet.getString("book_title"));
            book.date_of_publication.setValue(resultSet.getObject("date_of_publication", LocalDate.class));

            data.add(book);
        }

        return data;
    }

}
