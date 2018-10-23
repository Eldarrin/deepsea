package io.ensure.deepsea.common.config;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;

public class ConfigRetrieverHelper {
	
	private static final String NAME = "name";
	private static final String NAMESPACE = "namespace";
	private static final String HOCON = "hocon";
	private static final String CONFIGMAP = "configmap";
	private static final String OPTIONAL = "optional";
	
	public ConfigRetrieverOptions getOptions(String namespaceName, String configMapName) {
		ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
		if (System.getenv().containsKey("OPENSHIFT_BUILD_NAMESPACE")) {
			ConfigStoreOptions kubeConfig = new ConfigStoreOptions()
					.setType(CONFIGMAP)
					.setFormat(HOCON)
					.setConfig(new JsonObject()
							.put(OPTIONAL, true)
							.put(NAMESPACE, namespaceName)
							.put(NAME, configMapName));
			configRetrieverOptions.addStore(kubeConfig); // Values here will override identical keys from above
		}
		return configRetrieverOptions;
	}

}
