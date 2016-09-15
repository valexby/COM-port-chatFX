package comChat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    private TextField message1, message2, portField1, portField2;

    @FXML
    private Button sendButton1, sendButton2, connectButton1, connectButton2;

    @FXML
    private TextArea chatArea;

    private PortThread portThread1, portThread2;

    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void onSend1() {
        portThread1.sendMessage(message1.getText());
    }

    @FXML
    private void onSend2() {
        portThread2.sendMessage(message2.getText());
    }

    void onStop() {
        if (portThread1 != null && !portThread1.isInterrupted()) portThread1.interrupt();
        if (portThread2 != null && !portThread2.isInterrupted()) portThread2.interrupt();
    }

    @FXML
    void onConnect1() {
        portThread1 = onConnect(message1, sendButton1, portField1, connectButton1, portThread1);
    }

    @FXML
    void onConnect2() {
        portThread2 = onConnect(message2, sendButton2, portField2, connectButton2, portThread2);
    }

    void addMessageToArea(String message)
    {
        Platform.runLater(() -> chatArea.setText(chatArea.getText() + message));
    }

    private PortThread onConnect(TextField message, Button sendButton,
                                 TextField portField, Button connectButton, PortThread portThread) {
        if (portThread != null && portThread.isAlive()) {
            portThread.interrupt();
            Platform.runLater(() -> {
                message.setDisable(true);
                sendButton.setDisable(true);
                portField.setDisable(false);
                connectButton.setText("Connect");
            });
            return portThread;
        } else {
            try {
                portThread = new PortThread(this, portField.getText());
            } catch (Exception ex) {
                System.out.print(ex.getMessage());
                return null;
            }
            Platform.runLater(() -> {
                message.setDisable(false);
                sendButton.setDisable(false);
                portField.setDisable(true);
                connectButton.setText("Disconnect");
            });
            portThread.start();
            return portThread;
        }
    }
}