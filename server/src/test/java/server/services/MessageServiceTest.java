package server.services;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import server.database.Database;
import server.models.Message;
import server.models.Model;

public class MessageServiceTest {
    private MessageService m;

    @Before
    public void setUp() {
        Database db = new Database() {
            List<Message> messages = new ArrayList<>();
            private boolean fail = false;
            private String content;
            private int timestamp;
            private int userId;
            private boolean inserting;
            public Database start() {
                return this;
            }
            public Database establish() { return this; }
            public Database query(String q) {
                if (q.contains("SELECT")) {
                    inserting = false;
                    messages.add(new Message("Lol", 4324, 1));
                    messages.add(new Message("Hah", 5050, 3));
                    messages.add(new Message("Nöff", 00001, 6));
                }
                if (q.contains("INSERT")) {
                    inserting = true;
                }
                return this; 
            }
            public <T> Database addValue(int p, T v) {
                if (p == 1 && v.equals("fail")) {
                    fail = true;
                }
                if (inserting) {
                    if (p == 1) content = (String) v;
                    if (p == 2) timestamp = (Integer) v;
                    if (p == 3) userId = (Integer) v;
                }
                return this;
            }
            public Database executeReturning() {
                return this;
            }
            public int execute() {
                if (fail) {
                    return 0;
                }
                return 1;
            }
            public <T extends Object & Model> T asValue(Class<T> model) {
                return (T) new Message(content, timestamp, userId);
            }
            public <T extends Object & Model> List<T> asList(Class<T> model) {
                return (List<T>) messages;
            }
            public void close() {}
        }.start();
        m = new MessageService(db);
    }

    @Test
    public void testMessageShouldBeUnder1000Characters() {
        Message pass = m.create("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. Why do we use it? It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like). Where does it come from? Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet.., comes from a line in section 1.10.32. The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.", 
        123, 1);

        assertTrue("Message save should fail if message too long", pass == null);
    }

    @Test
    public void testCreateMessage() {
        Message message = m.create("lmao", 22222, 10);

        assertTrue("Message should have be correct content", "lmao".equals(message.getContent()));
        assertTrue("Message should have be correct timestamp", 22222 == message.getTimestamp());
        assertTrue("Message should have be correct userId", 10 == message.getUserId());
    }

    @Test
    public void testFailMessage() {
        Message pass = m.create("fail", 1, 2);

        assertTrue("Message should have failed", pass == null);
    }

    @Test
    public void testRetrieveAllMessages() {
        List<Message> messages = m.getAll();
        assertTrue("Messages should contain 3 messages but contained " + messages.size(), messages.size() == 3);
    }

}