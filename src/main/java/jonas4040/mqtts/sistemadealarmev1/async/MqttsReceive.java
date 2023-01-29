package jonas4040.mqtts.sistemadealarmev1.async;

import jonas4040.mqtts.sistemadealarmev1.config.MqttsConfig;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MqttsReceive implements MqttCallback {
    private static final Logger log = LoggerFactory.getLogger(MqttsAsyncClient.class);
    @Autowired
    @Lazy
    private MqttsConfig mqttsConfig;

    private static IMqttAsyncClient mqttsClient;
    public static String msgRecebida;

    @Override
    public void connectionLost(Throwable throwable) {
        log.info("Desconectado, pode ser reconectado");
        if(Objects.nonNull(mqttsClient)){
            mqttsConfig.getAsyncClient();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        System.out.println("Mensagem "+new String(mqttMessage.getPayload())+" de QoS "+mqttMessage.getQos()+" foi recebida");
        msgRecebida = new String(mqttMessage.getPayload());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        String estado = iMqttDeliveryToken.isComplete() ? "foi" : "NAO foi";
        log.info("Entrega "+estado+" completada");
    }
}
