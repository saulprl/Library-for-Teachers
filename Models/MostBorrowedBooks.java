package Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MostBorrowedBooks {

    public StringProperty isbn = new SimpleStringProperty();
    public StringProperty book_title = new SimpleStringProperty();
    public IntegerProperty times_borrowed = new SimpleIntegerProperty();

    public StringProperty isbnProperty() {
        return isbn;
    }
    public StringProperty book_titleProperty() {
        return book_title;
    }
    public IntegerProperty times_borrowedProperty() {
        return times_borrowed;
    }

    public MostBorrowedBooks() {

    }
}
