package de.consort.it.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentContext {

	final Logger logger = LoggerFactory.getLogger(EnvironmentContext.class);

	private static EnvironmentContext context = null;
	private Dotenv dotenv = null;

	private EnvironmentContext() {
		initEnvironment();
	}

	public static EnvironmentContext getInstance() {
		if (context == null) {
			context = new EnvironmentContext();
		}

		return context;
	}

	private void initEnvironment() {
		try {
			dotenv = Dotenv.configure().load();
		} catch (Exception e) {
			logger.info("INFO: Dotenv configuration failed! Ignore if running on prod!");
		}
	}

	public String getenv(final String propertyName) {
		if (dotenv != null) {
			return dotenv.get(propertyName);
		} else {
			final String systemProperty = System.getenv(propertyName);
			if (StringUtils.isBlank(systemProperty)) {
				logger.warn("WARNING: Could not find system property -> {} !", propertyName);
			}
			return systemProperty;
		}
	}
}