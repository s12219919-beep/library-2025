package org.example.Domain;

public abstract class MediaItem {
    protected String id;      // ISBN أو CD ID
    protected String title;
    protected String author;
    protected boolean available = true;

    public MediaItem(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
