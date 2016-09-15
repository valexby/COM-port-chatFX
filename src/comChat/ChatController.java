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

    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private TextField message1, message2, portField1, portField2;

    @FXML
    private Button sendButton1, sendButton2, connectButton1, connectButton2;

    @FXML
    private TextArea chatArea;

    private PortThread portThread1, portThread2;

    @FXML
    private void onSend1() {
        sendMessage(portThread1.descriptor, message1.getText(), message1.getText().length());
    }

    @FXML
    private void onSend2() {
        sendMessage(portThread2.descriptor, message2.getText(), message2.getText().length());
    }

    void onStop()
    {
        if (portThread1 != null && !portThread1.isInterrupted()) portThread1.interrupt();
        if (portThread2 != null && !portThread2.isInterrupted()) portThread2.interrupt();
    }

    @FXML
    void onConnect1()
    {
        portThread1 = onConnect(message1, sendButton1, portField1, connectButton1, portThread1);
    }

    @FXML
    void onConnect2()
    {
        portThread2 = onConnect(message2, sendButton2, portField2, connectButton2, portThread2);
    }

    private PortThread onConnect(TextField message, Button sendButton,
                                 TextField portField, Button connectButton, PortThread portThread)
    {
        if (portThread != null && portThread.isAlive())
        {
            portThread.interrupt();
            Platform.runLater(() -> {
                message.setDisable(true);
                sendButton.setDisable(true);
                portField.setDisable(false);
                connectButton.setText("Connect");
            });
            return portThread;
        }
        else {
            try {
                portThread = new PortThread(portField.getText());
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

    native private static int initPort(String portName);
    native private static void sendMessage(int descriptor, String message, int length);
    native private static void closePort(int descriptor);
    native private static char readSymbol(int descriptor);

    private class PortThread extends Thread {
        int descriptor = -1;
        String portName;

        PortThread(String portName) throws Exception{
            descriptor = initPort(portName);
            if (descriptor == -1) throw new Exception("Serial port connect error");
            this.portName = portName;
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
                        stringBuffer = portName + ": " + stringBuffer;
                        final String out = stringBuffer;
                        Platform.runLater(() -> chatArea.setText(chatArea.getText() + out));
                    }
                    sleep(100);
                } catch (InterruptedException ex) {
                    closePort(descriptor);
                    break;
                }
            }
        }
    }

    static {
        System.loadLibrary("com_helper");
    }
}