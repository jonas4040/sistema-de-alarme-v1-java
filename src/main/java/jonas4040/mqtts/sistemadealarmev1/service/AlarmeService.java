package jonas4040.mqtts.sistemadealarmev1.service;

import jonas4040.mqtts.sistemadealarmev1.async.MqttsAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmeService {
    @Autowired
    private MqttsAsyncClient mqttsAsyncClient;

    public String subscrever(String topico, Integer qos){
        return mqttsAsyncClient.subscribe(topico,qos);
    }

    /**
     * Publica num topico padrao e reseta o alarme
     */
    public void resetarAlarme(){
        //TODO JSON
        String estadoAlarme = "{" +
                "\"ligado: \""+
                "\"false\""+
                "}";
        mqttsAsyncClient.publish(1,false,"camara/",estadoAlarme);
    }

    //TODO implementar
    public void enviaEmail(){

    }
}
