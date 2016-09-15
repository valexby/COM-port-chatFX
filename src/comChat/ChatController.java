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
    private int descriptor1 = -1, descriptor2 = -1;

    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private TextField message1, message2, portField1, portField2;

    @FXML
    private Button sendButton1, sendButton2, connectButton1, connectButton2;

    @FXML
    private TextArea chatArea;

    private ReaderThread readerThread1, readerThread2;

    @FXML
    private void onSend1() {
        sendMessage(descriptor1, message1.getText(), message1.getText().length());
    }

    @FXML
    private void onSend2() {
        sendMessage(descriptor2, message2.getText(), message2.getText().length());
    }

    void onStop()
    {
        if (readerThread1 != null && !readerThread1.isInterrupted()) readerThread1.interrupt();
        if (readerThread2 != null && !readerThread2.isInterrupted()) readerThread2.interrupt();
        closePort(descriptor1);
        closePort(descriptor2);
    }

    @FXML
    void onConnect1()
    {
        if (descriptor1 != -1)
        {
            readerThread1.interrupt();
            closePort(descriptor1);
            descriptor1 = -1;
            Platform.runLater(() -> {
                message1.setDisable(true);
                sendButton1.setDisable(true);
                portField1.setDisable(false);
                connectButton1.setText("Connect");
            });
            return;
        }
        if ((descriptor1 = initPort(portField1.getText())) != -1)
        {
            Platform.runLater(() -> {
                message1.setDisable(false);
                sendButton1.setDisable(false);
                portField1.setDisable(true);
                connectButton1.setText("Disconnect");
            });
            readerThread1 = new ReaderThread(descriptor1);
            readerThread1.start();
        }
    }

    @FXML
    void onConnect2()
    {
        if (descriptor2 != -1)
        {
            readerThread2.interrupt();
            closePort(descriptor2);
            descriptor2 = -1;
            Platform.runLater(() -> {
                message2.setDisable(true);
                sendButton2.setDisable(true);
                portField2.setDisable(false);
                connectButton2.setText("Connect");
            });
            return;
        }
        if ((descriptor2 = initPort(portField2.getText())) != -1)
        {
            Platform.runLater(() -> {
                message2.setDisable(false);
                sendButton2.setDisable(false);
                portField2.setDisable(true);
                connectButton2.setText("Disconnect");
            });
            readerThread2 = new ReaderThread(descriptor2);
            readerThread2.start();
        }
    }

    native private static int initPort(String portName);
    native private static void sendMessage(int descriptor, String message, int length);
    native private static void closePort(int descriptor);
    native private static char readSymbol(int descriptor);

    private class ReaderThread extends Thread {
        int descriptor = -1;

        ReaderThread(int descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public void run() {
            String stringBuffer;
            while(true) {
                try {
                    char buffer;
                    if ((buffer = readSymbol(descriptor)) != 0) {
                        stringBuffer = "";
                        while (buffer != 0) {
                            stringBuffer += buffer;
                            buffer = readSymbol(descriptor);
                        }
                        if (descriptor == descriptor1) stringBuffer = portField2.getText() + ": " + stringBuffer;
                        if (descriptor == descriptor2) stringBuffer = portField1.getText() + ": " + stringBuffer;
                        final String out = stringBuffer;
                        Platform.runLater(() -> chatArea.setText(chatArea.getText() + out));
                    }
                    sleep(100);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }

    static {
        System.loadLibrary("com_helper");
    }
}