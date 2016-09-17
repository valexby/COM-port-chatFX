package comChat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    private TextField message1, message2, portField1, portField2;

    @FXML
    private Button sendButton1, sendButton2, connectButton1, connectButton2;

    @FXML
    private TextArea chatArea;

    @FXML
    Slider inSpeedSlider1, inSpeedSlider2, outSpeedSlider1, outSpeedSlider2;

    @FXML
    CheckBox parityCheck1, parityCheck2;

    private PortThread portThread1, portThread2;

    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void onSend1() {
        portThread1.sendMessage(message2.getText());
    }

    @FXML
    private void onSend2() {
        portThread2.sendMessage(message1.getText());
    }

    void onStop() {
        if (portThread1 != null && !portThread1.isInterrupted()) portThread1.interrupt();
        if (portThread2 != null && !portThread2.isInterrupted()) portThread2.interrupt();
    }

    @FXML
    void onConnect1() {
        portThread1 = onConnect(message1, sendButton1, portField1, connectButton1, portThread1,
                inSpeedSlider1, outSpeedSlider1, parityCheck1);
    }

    @FXML
    void onConnect2() {
        portThread2 = onConnect(message2, sendButton2, portField2, connectButton2, portThread2,
                inSpeedSlider2, outSpeedSlider2, parityCheck2);
    }

    @FXML
    void onInSpeedChange1()
    {
        portThread1.setInSpeed((int)inSpeedSlider1.getValue());
    }

    @FXML
    void onInSpeedChange2()
    {
        portThread2.setInSpeed((int)inSpeedSlider2.getValue());
    }

    @FXML
    void onOutSpeedChange1()
    {
        portThread1.setOutSpeed((int)outSpeedSlider1.getValue());
    }

    @FXML
    void onOutSpeedChange2()
    {
        portThread2.setOutSpeed((int)outSpeedSlider2.getValue());
    }

    @FXML
    void onParitySet1() {
        portThread1.setParityBit(parityCheck1.isSelected());
    }

    @FXML
    void onParitySet2() {
        portThread2.setParityBit(parityCheck2.isSelected());
    }

    void addMessageToArea(String message)
    {
        Platform.runLater(() -> chatArea.setText(chatArea.getText() + message));
    }

    private PortThread onConnect(TextField message, Button sendButton,
                                 TextField portField, Button connectButton, PortThread portThread,
                                 Slider inSpeedSlider, Slider outSpeedSlider, CheckBox parityCheck) {
        if (portThread != null && portThread.isAlive()) {
            portThread.interrupt();
            Platform.runLater(() -> {
                message.setDisable(true);
                sendButton.setDisable(true);
                inSpeedSlider.setDisable(true);
                outSpeedSlider.setDisable(true);
                parityCheck.setDisable(true);
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
            portThread.setInSpeed((int)inSpeedSlider.getValue());
            portThread.setOutSpeed((int)outSpeedSlider.getValue());
            Platform.runLater(() -> {
                message.setDisable(false);
                sendButton.setDisable(false);
                inSpeedSlider.setDisable(false);
                outSpeedSlider.setDisable(false);
                parityCheck.setDisable(false);
                portField.setDisable(true);
                connectButton.setText("Disconnect");
            });
            portThread.start();
            return portThread;
        }
    }
}