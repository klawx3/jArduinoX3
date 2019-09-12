package arduino_serial;

import arduino_serial.core.Arduino;
import arduino_serial.core.ArduinoListener;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.Scanner;


public class App implements ArduinoListener {

    public App(){
        Arduino arduino = new Arduino("COM5");
        arduino.addListener(this);
        if(arduino.connect()){
            aplicationDo();          
        }else{
            System.err.println("Sin conexión");
        }
    }

    private void aplicationDo() {

    }

    public static void main(String[] args){
        new App();
    }

    @Override
    public void message(String val) {
        System.out.println(val);
    }
}
