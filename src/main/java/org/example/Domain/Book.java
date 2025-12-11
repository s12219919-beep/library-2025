package org.example.Domain;

public class Book extends MediaItem {

    public Book(String isbn, String title, String author) {
        super(isbn, title, author);
    }

    public boolean matchesSearch(String query) {
        return id.contains(query) || title.contains(query) || author.contains(query);
    }
}
