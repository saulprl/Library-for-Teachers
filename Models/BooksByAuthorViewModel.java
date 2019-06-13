package Models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class BooksByAuthorViewModel {

    public StringProperty book_title = new SimpleStringProperty();
    public StringProperty author_fullname = new SimpleStringProperty();
    public StringProperty isbn = new SimpleStringProperty();
    public ObjectProperty<LocalDate> date_of_publication = new SimpleObjectProperty<>();

    public StringProperty book_titleProperty() {
        return book_title;
    }
    public StringProperty author_fullnameProperty() {
        return author_fullname;
    }
    public StringProperty isbnProperty() {
        return isbn;
    }
    public ObjectProperty<LocalDate> date_of_publicationProperty() {
        return date_of_publication;
    }

    public BooksByAuthorViewModel(String title, String name, String isbn, LocalDate date) {
        this.book_title.setValue(title);
        this.author_fullname.setValue(name);
        this.isbn.setValue(isbn);
        this.date_of_publication.setValue(date);
    }

    public BooksByAuthorViewModel() {

    }

    public BooksByAuthorViewModel(BooksByAuthorViewModel another) {
    }

}
