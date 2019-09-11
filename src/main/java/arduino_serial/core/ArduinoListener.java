package arduino_serial.core;

@FunctionalInterface
public interface ArduinoListener {
    void message(String val);
}
