package arduino_serial.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BuferredArduinoOutput { //fixme: implementar con un listener

    private char caracterDeCorte;

    private ArrayList<String> cadenas;
    private List<Byte> list_chunk;
    private boolean primera_llamada;

    public BuferredArduinoOutput(char caracterDeCorte){
        cadenas = new ArrayList<String>();
        list_chunk = new ArrayList<Byte>();
        this.caracterDeCorte = caracterDeCorte;
    }

    public boolean hasNext(){
        if(cadenas.isEmpty()){
            return false;
        }else{
            if(primera_llamada)
                primera_llamada = false;
            else
                cadenas.remove(0);
            if(cadenas.isEmpty())
                return false;
        }
        return true;
    }

    public String next(){
        return cadenas.get(0);
    }


    public void setChuck(byte[] chunk){
        primera_llamada = true;
        chuckToList(chunk);
        searchStrings();
    }

    private void searchStrings() {
        int posicion_de_corte = -1;
        for(int i = 0 ; i < list_chunk.size() ; i++){ // busco el corte
            if(((byte)list_chunk.get(i)) == caracterDeCorte){
                posicion_de_corte = i;
                break;
            }
        }
        if(posicion_de_corte != -1){ // si encontro el corte
            int list_chunk_size = list_chunk.size();
            byte[] correct = new byte[posicion_de_corte + 1];
            for(int i = 0; i < posicion_de_corte + 1 ; i++){ // copio a correct antes del corte y elimino
                if(i != posicion_de_corte){
                    correct[i] = (list_chunk.get(0));
                }
                list_chunk.remove(0);
            }
            cadenas.add(new String(correct).trim());
            if(posicion_de_corte < list_chunk_size){
                searchStrings(); // funcion recursiva para seguir la comprobacion
            }
        }

    }

    private void chuckToList(byte[] chuck){
        for(int i = 0; i < chuck.length ; i++){
            list_chunk.add(chuck[i]);
        }
    }




}
