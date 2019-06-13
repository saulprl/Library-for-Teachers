package Controllers.TableManagement;

import Models.ByAuthor;
import Models.ConnectionMethods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ByAuthorTableController {

    private static ByAuthor selectedByAuthor;
    private static final String INITIALIZEQUERY = "SELECT * FROM books_by_author;";
    private static final String AUTHORSQUERY = "SELECT author_id FROM authors;";
    private static final String BOOKSQUERY = "SELECT isbn FROM books;";
    private static String query;

    @FXML
    private ComboBox idComboBox;

    @FXML
    private ComboBox isbnComboBox;

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
        if (idComboBox.getValue() != null && isbnComboBox.getValue() != null) {
            String id = idComboBox.getValue().toString();
            String isbn = isbnComboBox.getValue().toString();
            query = String.format("INSERT INTO books_by_author VALUES('%s', '%s');", id, isbn);

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro insertado");
                dialog.setHeaderText(null);
                dialog.setContentText("La relación se ha insertado correctamente.");
                dialog.showAndWait();

                clearButtonPressed(new ActionEvent());
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error al insertar");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al insertar el registro. Excepción: " + sqlEx.getMessage());
                dialog.showAndWait();
            }
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Parámetros de registro");
            dialog.setHeaderText(null);
            dialog.setContentText("Asegúrate de haber ingresado todos los parámetros de registro.");
            dialog.showAndWait();
        }
    }

    @FXML
    private void deleteButtonPressed(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Borrar registro");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro de que deseas eliminar esta relación?");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.OK) {
            query = String.format("DELETE FROM books_by_author WHERE author_id = %s AND isbn = '%s';",
                    selectedByAuthor.author_idProperty().getValue(), selectedByAuthor.isbnProperty().getValue());

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Borrar registro");
                dialog.setHeaderText(null);
                dialog.setContentText("La relación ha sido borrado correctamente.");
                dialog.showAndWait();

                clearButtonPressed(new ActionEvent());
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error al borrar");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al borrar la relación. Excepción: " + sqlEx.getMessage());
                dialog.showAndWait();
            }
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Borrar registro");
            dialog.setHeaderText(null);
            dialog.setContentText("No se ha efectuado ningún cambio.");
            dialog.showAndWait();
        }
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        updateData(INITIALIZEQUERY);

        for (Node node : gridPane.getChildren()) {
            if (node instanceof ComboBox) {
                ((ComboBox) node).getSelectionModel().select(null);
            } else if (node instanceof Button) {
                if (!node.getId().contains("clear")) {
                    node.setDisable(true);
                }
            }
        }
    }

    @FXML
    private void initialize() {
        updateData(INITIALIZEQUERY);
        registerButton.setDisable(true);
        deleteButton.setDisable(true);

        idComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && isbnComboBox.getValue() != null) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }
        });

        isbnComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && idComboBox.getValue() != null) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedByAuthor = (ByAuthor) (newValue);

                idComboBox.getSelectionModel().select(selectedByAuthor.author_idProperty().getValue());
                isbnComboBox.getSelectionModel().select(selectedByAuthor.isbnProperty().getValue());

                deleteButton.setDisable(false);
            } else {
                selectedByAuthor = null;

                deleteButton.setDisable(true);
            }
        });
    }

    private void updateData(String sql) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        idComboBox.getItems().clear();
        isbnComboBox.getItems().clear();

        try {
            ResultSet resultSet = ConnectionMethods.executeQuery(sql);
            ResultSet authorSet = ConnectionMethods.executeQuery(AUTHORSQUERY);
            ResultSet booksSet = ConnectionMethods.executeQuery(BOOKSQUERY);

            if (resultSet != null) {
                while (authorSet.next())
                    idComboBox.getItems().add(authorSet.getInt("author_id"));

                while (booksSet.next())
                    isbnComboBox.getItems().add(booksSet.getString("isbn"));

                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "author_id":
                            column.setText("ID de autor");
                            break;
                        case "isbn":
                            column.setText("ISBN");
                            break;
                        default:
                            column.setText(resultSet.getMetaData().getColumnName(i + 1));
                    }

                    column.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i + 1)));
                    tableView.getColumns().add(column);
                }

                tableView.setItems(data);
            }

            ConnectionMethods.close();
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }

    private ArrayList<ByAuthor> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<ByAuthor> data = new ArrayList<>();

        while (resultSet.next()) {
            ByAuthor item = new ByAuthor();

            item.author_id.setValue(resultSet.getInt("author_id"));
            item.isbn.setValue(resultSet.getString("isbn"));

            data.add(item);
        }

        return data;
    }

}
