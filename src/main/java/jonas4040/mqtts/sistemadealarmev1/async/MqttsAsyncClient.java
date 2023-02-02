package jonas4040.mqtts.sistemadealarmev1.async;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

/**
 * Class for mqtts client
 */
@Component
public class MqttsAsyncClient {
	private static final Logger log = LoggerFactory.getLogger(MqttsAsyncClient.class);

	@Autowired
	private MqttsReceive mqttsReceive;

	private IMqttAsyncClient mqttsClient;

	private static void messageArrived(String topic1, MqttMessage mqttMessage) {
		System.out.println("Client Subscribed");

	}

	public IMqttAsyncClient getmqttsClient() {
		return mqttsClient;
	}

	public void setmqttsClient(IMqttAsyncClient mqttsClient) {
		this.mqttsClient = mqttsClient;
	}

	static {
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) ->
			true//hostname.equals(pcHostname)
		);
	}

	/**
	 * Metodo de conexao ao broker
	 * @param brokerURL
	 * @param clientID
	 * @param username
	 * @param password
	 */
	public void conectar(String brokerURL,String clientID, String username, String password) {
		String caCertFile = "ca.crt";

//		String topic = "camara/";
//		String content = "Teste Java";
//		int qos = 1;
		MemoryPersistence persistence = new MemoryPersistence();

		try
		{
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setHttpsHostnameVerificationEnabled(false);
			this.mqttsClient = new MqttAsyncClient(brokerURL, clientID, persistence);
			SSLSocketFactory socketFactory = getSocketFactory(caCertFile);
			connOpts.setUserName(username);
			connOpts.setPassword(password.toCharArray());
			connOpts.setSocketFactory(socketFactory);
			connOpts.setCleanSession(true);
			connOpts.setAutomaticReconnect(true);
			this.setmqttsClient(mqttsClient);

			System.out.println("Connecting to broker: " + brokerURL+" . . . ");
			mqttsClient.setCallback(mqttsReceive);
			IMqttToken token = mqttsClient.connect(connOpts);
			token.waitForCompletion();

			if (mqttsClient.isConnected())
			{
				System.out.println("Connected");

			}
			else
			{
				System.out.println("Client not connected");
			}
			//mqttsClient.disconnect();
			//System.out.println("Disconnected");
			//mqttsClient.close();
		}catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assinar/fazer subscribe em um topico
	 * @param topic
	 * @param qos
	 */
	public void subscribe(String topic, Integer qos){
		log.info("Fazendo subscribing no topico " + topic);

		try {
			this.getmqttsClient().subscribe(topic, qos);
			//clientSubscribed.waitForCompletion();

		}catch (MqttException me) {
			log.error("reason " + me.getReasonCode());
			log.error("msg " + me.getMessage());
			log.error("loc " + me.getLocalizedMessage());
			log.error("cause " + me.getCause());
			log.error("excep " + me);
			me.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Publicar em um topico
	 * @param qos
	 * @param retained
	 * @param topic
	 * @param msg
	 * @return
	 */
	public void publish(int qos, Boolean retained, String topic, String msg){
		MqttMessage message = new MqttMessage(msg.getBytes());
		message.setQos(qos);
		message.setRetained(retained);
		//message.setPayload(msg.getBytes(StandardCharsets.UTF_8));


		IMqttDeliveryToken token;

		try{
			token = mqttsClient.publish(topic, message);
			token.waitForCompletion();
		}catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static SSLSocketFactory getSocketFactory(final String caCrtFile) throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());

		// load CA certificate
		X509Certificate caCert = null;

		FileInputStream fis = new FileInputStream(caCrtFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		while (bis.available() > 0)
		{
			caCert = (X509Certificate) cf.generateCertificate(bis);
			// System.out.println(caCert.toString());
		}

		// CA certificate is used to authenticate server
		KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
		caKs.load(null, null);
		caKs.setCertificateEntry("ca-certificate", caCert);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
		tmf.init(caKs);

		// finally, create SSL socket factory
		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(null, tmf.getTrustManagers(), null);

		return context.getSocketFactory();
	}
}
