package Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;

public class OnLoan {

    public IntegerProperty teacher_id = new SimpleIntegerProperty();
    public StringProperty isbn = new SimpleStringProperty();
    public ObjectProperty<LocalDate> date_issued = new SimpleObjectProperty<>();
    public ObjectProperty<LocalDate> date_due = new SimpleObjectProperty<>();
    public ObjectProperty<LocalDate> date_returned = new SimpleObjectProperty<>();
    public DoubleProperty amount_of_fine = new SimpleDoubleProperty();

    public IntegerProperty teacher_idProperty() {
        return teacher_id;
    }
    public StringProperty isbnProperty() {
        return isbn;
    }
    public ObjectProperty<LocalDate> date_issuedProperty() {
        return date_issued;
    }
    public ObjectProperty<LocalDate> date_dueProperty() {
        return date_due;
    }
    public ObjectProperty<LocalDate> date_returnedProperty() {
        return date_returned;
    }
    public DoubleProperty amount_of_fineProperty() {
        return amount_of_fine;
    }

    public OnLoan(int id, String isbn, LocalDate issued, LocalDate due, LocalDate returned,
                  double fine) {
        this.teacher_id.setValue(id);
        this.isbn.setValue(isbn);
        this.date_issued.setValue(issued);
        this.date_due.setValue(due);
        this.date_returned.setValue(returned);
        this.amount_of_fine.setValue(fine);
    }

    public OnLoan() {

    }
    public OnLoan(OnLoan another) {

    }

}
