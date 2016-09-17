package comChat;

/**
 * Port class, providing listening connected port. Deal by RAII.
 */
class PortThread extends Thread {

    private static final int[] bauds= {0, 50, 75, 110, 134, 150, 200, 300, 600, 1200, 1800, 2400,
            4800, 9600, 19200, 38400, 57600, 115200, 230400};

    private int inBaud = 115200, outBaud = 115200;
    private boolean parityBit = true;

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

    void setInSpeed(int newBaud) {
        inBaud = findNormalBaud(newBaud);
        setOptions(descriptor, inBaud, outBaud, parityBit);
    }

    void setParityBit(boolean value) {
        parityBit = value;
        setOptions(descriptor, inBaud, outBaud, parityBit);
    }

    void setOutSpeed(int newBaud) {
        outBaud = findNormalBaud(newBaud);
        setOptions(descriptor, inBaud, outBaud, parityBit);
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

    private int findNormalBaud(int baud) {
        int min = 230402, res = 0;
        for (int i : bauds) {
            if (Math.abs(baud - i) < min) {
                min = Math.abs(baud - i);
                res = i;
            }
        }
        return res;
    }

    native private int initPort(String portName);

    native private void sendMessage(int descriptor, String message, int length);

    native private void closePort(int descriptor);

    native private char readSymbol(int descriptor);

    native private void setOptions(int descriptor, int inBaud, int outBaud, boolean parityBit);
}