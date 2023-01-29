package jonas4040.mqtts.sistemadealarmev1.async;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class MqttsAsync implements MqttCallback
{
	//final AtomicInteger msgChegandoç = new AtomicInteger();

	static {
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) ->
			true//hostname.equals(pcHostname)
		);
	}

	public Properties brokerConfig(String configFile){
		Properties prop = new Properties();
		try(FileInputStream fileInputStream = new FileInputStream(configFile)){
			prop.load(fileInputStream);
		}catch (FileNotFoundException e){
			throw new RuntimeException("Arquivo de configuracao nao encontrado "+e.getMessage());
		}
		catch (IOException e) {
			throw new RuntimeException("Ocorreu um erro ao abrir o arquivo de configuracao "+e.getMessage());
		}
		return prop;
	}
	public void conectar() {
		String caCertFile = "ca.crt";
		String configFile = "broker.config";
		Properties properties = brokerConfig(configFile);

		String topic = "camara/";
		String content = "Teste Java";
		int qos = 1;
		String pcHostname = properties.getProperty("broker.hostname") ;
		String broker = "ssl://"+pcHostname+":8883";
		String clientId = "JavaSample";
		MemoryPersistence persistence = new MemoryPersistence();

		try
		{
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setHttpsHostnameVerificationEnabled(false);
			IMqttAsyncClient sampleClient = new MqttAsyncClient(broker, clientId, persistence);
			SSLSocketFactory socketFactory = getSocketFactory(caCertFile);
			connOpts.setUserName(properties.getProperty("broker.user"));
			connOpts.setPassword(properties.getProperty("broker.password").toCharArray());
			connOpts.setSocketFactory(socketFactory);
			connOpts.setCleanSession(true);
			connOpts.setAutomaticReconnect(true);

			System.out.println("Connecting to broker: " + broker+" . . . ");
			sampleClient.setCallback(this);
			IMqttToken token = sampleClient.connect(connOpts);
			token.waitForCompletion();

			if (sampleClient.isConnected())
			{
				System.out.println("Connected");

				IMqttToken tknSub = sampleClient.subscribe(topic, 1, (topic1, mqttMessage) -> {
					//msgChegando.incrementAndGet();

					System.out.println(mqttMessage);
					String msgEsp32 = mqttMessage.toString().replace("{\"Contando\":\"", "").replace("\"}", "");
					System.out.println("Client Subscribed");
					if(!msgEsp32.equals(content) && Integer.TYPE.isInstance(msgEsp32)){
						if (Integer.parseInt(msgEsp32) % 10 >= 6) {
							System.out.println("Publishing message: " + content);
							MqttMessage message = new MqttMessage(content.getBytes());
							message.setQos(qos);
							sampleClient.publish(topic, message);
							System.out.println("Message published");
						}
					}else{
						msgEsp32="0";
					}
				});

			}
			else
			{
				System.out.println("Client not connected");
			}
			//sampleClient.disconnect();
			//System.out.println("Disconnected");
			sampleClient.close();
		}
		catch (MqttException me)
		{
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
		catch (Exception e)
		{
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

	@Override
	public void connectionLost(Throwable throwable) {

	}

	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

	}
}
