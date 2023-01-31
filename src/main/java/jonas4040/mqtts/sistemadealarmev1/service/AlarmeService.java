package jonas4040.mqtts.sistemadealarmev1.service;

import jonas4040.mqtts.sistemadealarmev1.async.MqttsAsyncClient;
import jonas4040.mqtts.sistemadealarmev1.async.MqttsReceive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void resetarAlarme(){
        //TODO JSON
        String estadoAlarme = "{" +
                "\"ligado\":"+
                "\"false\""+
                "}";
        mqttsAsyncClient.publish(1,false,"casa/temperatura/#",estadoAlarme);
    }

    //TODO implementar
    public void enviaEmail(){

    }
}
