package server.models;

public class Message implements Model {
    private int id;
    private String content;
    // Unix timestamp
    private long timestamp;
    private int userId;
    private String name;

    public Message(){}

    public Message(
        String content,
        long timestamp,
        int userId
    ) {
        this.content = content;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getId() + "|" + getContent() + "|" + getTimestamp() + "|" + getName();
    }
}