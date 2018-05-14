package org.elastic.common.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class ClientHelper {
	private static final Logger logger = LoggerFactory.getLogger(ClientHelper.class);

	private static TransportClient tcClient;

	private static String url = "10.0.1.220";

	private static int port = 9300;

	private static String clusterName = "my-application";

	private static Settings settings = Settings.builder()
			.put("cluster.name", clusterName)
			.put("client.transport.sniff", true)
			.build();

	/**
	 * 获取client
	 * @return TransportClient
	 */
	public static TransportClient getTcClient() {
		if (tcClient == null) {
			synchronized (ClientHelper.class) {
				if (tcClient == null) {
					try {
						tcClient = new PreBuiltTransportClient(settings)
                                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(url), port))
                        ;
					} catch (UnknownHostException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}

		return tcClient;
	}

}
