package Models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class OnLoanViewModel {

    public StringProperty book_title = new SimpleStringProperty();
    public StringProperty isbn = new SimpleStringProperty();
    public StringProperty full_name = new SimpleStringProperty();
    public ObjectProperty<LocalDate> date_issued = new SimpleObjectProperty<>();
    public ObjectProperty<LocalDate> date_due = new SimpleObjectProperty<>();
    public DoubleProperty fine = new SimpleDoubleProperty();

    public StringProperty book_titleProperty() {
        return book_title;
    }
    public StringProperty isbnProperty() {
        return isbn;
    }
    public StringProperty full_nameProperty() {
        return full_name;
    }
    public ObjectProperty<LocalDate> date_issuedProperty() {
        return date_issued;
    }
    public ObjectProperty<LocalDate> date_dueProperty() {
        return date_due;
    }
    public DoubleProperty fineProperty() {
        return fine;
    }

    public OnLoanViewModel(String title, String isbn, String full_name, LocalDate date_issued, LocalDate date_due,
                           double fine) {
        this.book_title.setValue(title);
        this.isbn.setValue(isbn);
        this.full_name.setValue(full_name);
        this.date_issued.setValue(date_issued);
        this.date_due.setValue(date_due);
        this.fine.setValue(fine);
    }

    public OnLoanViewModel() {

    }

    public OnLoanViewModel(OnLoanViewModel another) {

    }

}
