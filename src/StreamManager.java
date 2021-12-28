import com.sun.javafx.scene.shape.ArcHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class StreamManager {

    private final Socket socket = SocketConnection.getSocket();

    /**
     * Exchange carries out the whole process of sending a message and receiving a reply from the server
     * @param message user command
     * @return the JabberMessage object that the server returns
     */
    public JabberMessage exchangeMessages(final String message) {
        sendMessage(message);
        return getReply();
    }

    public JabberMessage exchangeMessages(final String message, final ArrayList<ArrayList<String>> data) {
        sendMessage(message, data);
        return getReply();
    }

    /**
     * Sends a message to the server
     * @param message user command
     */
    private void sendMessage(final String message) {
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());

            outStream.writeObject(new JabberMessage(message));
            outStream.flush();
        }
        catch (IOException e) { e.printStackTrace(); }

    }

    /**
     * Sends a message and data to the server
     * @param message user command
     * @param data user information
     */
    private void sendMessage(final String message, final ArrayList<ArrayList<String>> data) {
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());

            outStream.writeObject(new JabberMessage(message, data));
            outStream.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Gets the reply from the server
     * @return the mystuff.JabberMessage object that the server returns
     */
    private JabberMessage getReply() {
        try {
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            return (JabberMessage) inStream.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
