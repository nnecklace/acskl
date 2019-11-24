package server.services;

import java.util.List;

import server.database.Database;
import server.models.Message;

public class MessageService {
    private Database database;

    public MessageService(Database database) {
        this.database = database;
    }

    public boolean create(Message message) {
        if (message.getContent().length() > 1000) return false;

        boolean created = database.establish()
                                .query("INSERT INTO messages (content,timestamp,userId) VALUES(?,?,?)")
                                .addValue(1, message.getContent())
                                .addValue(2, message.getTimestamp())
                                .addValue(3, message.getUserId())
                                .execute();
        database.close();

        return created;
    }

    public List<Message> getAll() {
        List<Message> messages = database.establish()
                                        .query("SELECT * FROM messages")
                                        .executeReturning()
                                        .asList(Message.class);
        database.close();

        return messages;
    }
}