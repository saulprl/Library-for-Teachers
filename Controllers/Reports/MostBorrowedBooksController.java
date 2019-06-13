package Controllers.Reports;

import Models.ConnectionMethods;
import Models.MostBorrowedBooks;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MostBorrowedBooksController {

    private static final String QUERY = "SELECT * FROM most_borrowed_books_view;";

    @FXML
    private TableView tableView;

    @FXML
    private void initialize() {
        updateTable();
    }

    private void updateTable() {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        try {
            ResultSet items = ConnectionMethods.executeQuery(QUERY);

            if (items != null) {
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(items));

                for (int i = 0; i < items.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (items.getMetaData().getColumnName(i + 1)) {
                        case "isbn":
                            column.setText("ISBN");
                            break;
                        case "book_title":
                            column.setText("Título");
                            break;
                        case "times_borrowed":
                            column.setText("Préstamos");
                            break;
                        default:
                            column.setText(items.getMetaData().getColumnName(i + 1));
                    }

                    column.setCellValueFactory(new PropertyValueFactory<>(items.getMetaData().getColumnName(i + 1)));
                    tableView.getColumns().add(column);
                }

                tableView.setItems(data);
            }
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }

    private ArrayList<MostBorrowedBooks> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<MostBorrowedBooks> data = new ArrayList<>();

        while (resultSet.next()) {
            MostBorrowedBooks item = new MostBorrowedBooks();

            item.isbn.setValue(resultSet.getString("isbn"));
            item.book_title.setValue(resultSet.getString("book_title"));
            item.times_borrowed.setValue(resultSet.getInt("times_borrowed"));

            data.add(item);
        }

        return data;
    }

}
