package client.models;

public class Message {
    private int id;
    private String content;
    private long timestamp;
    private String author;

    public Message(int id, String content, long timestamp, String author) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return id + " " + content + " " + timestamp + " " + author;
    }

}