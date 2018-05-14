package org.elastic.mappings;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Created by LL on 2017/4/11.
 */
public class MappingsProperties extends PropertyPlaceholderConfigurer {

	private static Map<String, String> properties;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		properties = new HashMap<>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String valueStr = props.getProperty(keyStr);
			valueStr = StringUtils.trimToEmpty(valueStr);
			properties.put(keyStr, valueStr);
		}
	}

	private MappingsProperties() {
	}

	public static String getString(String key) {
		return properties.get(key);
	}
}
