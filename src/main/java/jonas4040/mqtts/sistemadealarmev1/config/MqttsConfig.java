package jonas4040.mqtts.sistemadealarmev1.config;

import jonas4040.mqtts.sistemadealarmev1.async.MqttsAsyncClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("mqtt")
@Setter
@Getter
@NoArgsConstructor
public class MqttsConfig {
    @Autowired
    private MqttsAsyncClient mqttsAsyncClient;
    /**
     * User name
     */
    @Value("username")
    private String username;
    /**
     * Password
     */
    @Value("password")
    private String password;
    /**
     * Connection address
     */
    @Value("broker-url")
    private String brokerUrl;
    /**
     * Customer Id
     */

    @Value("clientID")
    private String clientID;
    /**
     * Default connection topic
     */
    @Value("default-topic")
    private String topicoPadrao;
    /**
     * Timeout time
     */
    //@Value("timeout")
    private int timeout;
    /**
     * Keep connected
     */
    //@Value("keepalive")
    private int keepalive;

    @Bean
    public MqttsAsyncClient getAsyncClient(){
        mqttsAsyncClient.conectar(getBrokerUrl(),getClientID(),getUsername(),getPassword());
        mqttsAsyncClient.subscribe(topicoPadrao,1);
        return mqttsAsyncClient;
    }
}
