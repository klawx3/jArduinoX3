package arduino_serial.core;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Arduino implements SerialPortDataListener {

    private final static char ARDUINO_END_CHAR = '\n';

    private String portName;
    private SerialPort arduinoPort;
    private List<ArduinoListener> listener;
    private int baudRate;
    private BuferredArduinoOutput charBuffer;

    public Arduino(String portName,int baudRate){
        __construct(portName,baudRate);
    }

    public Arduino(String portName){
        __construct(portName,9600);
    }

    private void __construct(String portName,int baudRate){
        this.portName = portName;
        this.baudRate = baudRate;
        arduinoPort = null;
        charBuffer = new BuferredArduinoOutput(ARDUINO_END_CHAR);
    }

    public boolean connect(){
        SerialPort[] commPorts = SerialPort.getCommPorts();
        for(SerialPort port : commPorts){
            String systemPortName = port.getSystemPortName();
            if(systemPortName.equals(portName)){
                arduinoPort = port;
            }
        }

        if(arduinoPort != null){
            arduinoPort.setBaudRate(baudRate);
            arduinoPort.addDataListener(this);
            return arduinoPort.openPort();
        }
        return false;
    }

    public void send(char character){
        if(arduinoPort != null){
            if(arduinoPort.isOpen()){
                OutputStream outputStream = arduinoPort.getOutputStream();
                try {
                    outputStream.write(character);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addListener(ArduinoListener listener){
        if(this.listener == null) {
            this.listener = new ArrayList<>();
        }
        this.listener.add(listener);
    }

    public void removeListener(ArduinoListener listener){
        if(this.listener != null){
            this.listener.remove(listener);
        }
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if(serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE){
            return;
        }
        byte[] buffer = new byte[arduinoPort.bytesAvailable()];
        arduinoPort.readBytes(buffer,buffer.length);
        charBuffer.setChuck(buffer);
        while(charBuffer.hasNext()){
            fireEvent(charBuffer.next());
        }
    }

    private void fireEvent(final String eventString){
        for(ArduinoListener l : listener){
            l.message(eventString);
        }
    }
}
