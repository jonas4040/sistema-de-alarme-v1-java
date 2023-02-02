package jonas4040.mqtts.sistemadealarmev1.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jonas4040.mqtts.sistemadealarmev1.async.MqttsAsyncClient;
import jonas4040.mqtts.sistemadealarmev1.async.MqttsReceive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

@Service
public class AlarmeService {
    @Autowired
    private MqttsAsyncClient mqttsAsyncClient;

    public String subscrever(String topico, Integer qos){
        mqttsAsyncClient.subscribe(topico,qos);
        return MqttsReceive.msgRecebida;
    }

    /**
     * Publica num topico padrao e reseta o alarme
     */
    public void resetarAlarme() {
        //TODO JSON
        JsonObject json = new JsonObject();
        json.addProperty("ligado",false);
        mqttsAsyncClient.publish(1,false,"casa/janela",json.toString());
    }

    //TODO implementar
    public void enviaEmail(){

    }
}
