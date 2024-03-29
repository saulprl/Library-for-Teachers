package Controllers.Reports;

import Models.ConnectionMethods;
import Models.TeachersFinedViewModel;
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

public class HighestFinesController {

    private static final String QUERY = "SELECT teacher_id, full_name, fine FROM fined_teachers_view" +
            " ORDER BY fine DESC;";

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
            ResultSet fined = ConnectionMethods.executeQuery(QUERY);

            if (fined != null) {
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(fined));

                for (int i = 0; i < fined.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (fined.getMetaData().getColumnName(i + 1)) {
                        case "teacher_id":
                            column.setText("ID");
                            break;
                        case "full_name":
                            column.setText("Nombre");
                            break;
                        case "fine":
                            column.setText("Multa");
                            break;
                        default:
                            column.setText(fined.getMetaData().getColumnName(i + 1));
                    }

                    column.setCellValueFactory(new PropertyValueFactory<>(fined.getMetaData().getColumnName(i + 1)));
                    tableView.getColumns().add(column);
                }

                tableView.setItems(data);
            }
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexi�n. Excepci�n: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }

    private ArrayList<TeachersFinedViewModel> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<TeachersFinedViewModel> data = new ArrayList<>();

        while (resultSet.next()) {
            TeachersFinedViewModel item = new TeachersFinedViewModel();

            item.teacher_id.setValue(resultSet.getInt("teacher_id"));
            item.full_name.setValue(resultSet.getString("full_name"));
            item.fine.setValue(resultSet.getDouble("fine"));

            data.add(item);
        }

        return data;
    }

}
