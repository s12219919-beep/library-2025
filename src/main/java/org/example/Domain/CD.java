package org.example.Domain;

public class CD extends MediaItem {
    public CD(String id, String title, String author) {
        super(id, title, author);
    }

    public boolean matchesSearch(String query) {
        return id.contains(query) || title.contains(query) || author.contains(query);
    }
}
