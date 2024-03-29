package Models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Book {

    public StringProperty isbn = new SimpleStringProperty();
    public StringProperty book_title = new SimpleStringProperty();
    public ObjectProperty<LocalDate> date_of_publication = new SimpleObjectProperty<>();

    public StringProperty isbnProperty() {
        return isbn;
    }
    public StringProperty book_titleProperty() {
        return book_title;
    }
    public ObjectProperty<LocalDate> date_of_publicationProperty() {
        return date_of_publication;
    }

    public Book(String isbn, String title, LocalDate publication) {
        this.isbn.setValue(isbn);
        this.book_title.setValue(title);
        this.date_of_publication.setValue(publication);
    }

    public Book() {

    }

    public Book(Book another) {

    }

}
