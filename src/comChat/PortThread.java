package comChat;

/**
 * Port class, providing listening connected port. Deal by RAII.
 */
class PortThread extends Thread {

    static {
        System.loadLibrary("com_helper");
    }

    private int descriptor;
    private String portName;
    private ChatController controller;

    PortThread(ChatController controller, String portName) throws Exception {
        descriptor = initPort(portName);
        if (descriptor == -1) throw new Exception("Serial port connect error");
        this.portName = portName;
        this.controller = controller;
    }

    void sendMessage(String message) {
        sendMessage(descriptor, message, message.length());
    }

    @Override
    public void run() {
        String stringBuffer;
        while (true) {
            try {
                char buffer;
                if ((buffer = readSymbol(descriptor)) != 0) {
                    stringBuffer = "";
                    while (buffer != 0) {
                        stringBuffer += buffer;
                        buffer = readSymbol(descriptor);
                    }
                    stringBuffer = portName + ": " + stringBuffer;
                    controller.addMessageToArea(stringBuffer);
                }
                sleep(100);
            } catch (InterruptedException ex) {
                closePort(descriptor);
                break;
            }
        }
    }

    native private int initPort(String portName);

    native private void sendMessage(int descriptor, String message, int length);

    native private void closePort(int descriptor);

    native private char readSymbol(int descriptor);
}