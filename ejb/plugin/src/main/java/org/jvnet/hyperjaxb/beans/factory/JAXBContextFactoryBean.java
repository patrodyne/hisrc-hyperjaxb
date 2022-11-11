package org.jvnet.hyperjaxb.beans.factory;

import java.util.Map;

import jakarta.xml.bind.JAXBContext;

public class JAXBContextFactoryBean {

	private String contextPath;
	public String getContextPath() { return contextPath; }
	public void setContextPath(String contextPath) { this.contextPath = contextPath; }

	private ClassLoader classLoader;
	public ClassLoader getClassLoader() { return classLoader; }
	public void setClassLoader(ClassLoader classLoader) { this.classLoader = classLoader; }

	private Map<String, ?> properties;
	public Map<String, ?> getProperties() { return properties; }
	public void setProperties(Map<String, ?> properties) { this.properties = properties; }

	protected Object createInstance() throws Exception {
		if (getClassLoader() == null && getProperties() == null) {
			return JAXBContext.newInstance(getContextPath());
		} else if (getClassLoader() != null && getProperties() == null) {
			return JAXBContext.newInstance(getContextPath(), getClassLoader());
		} else if (getClassLoader() == null && getProperties() != null) {
			return JAXBContext.newInstance(getContextPath(), Thread
					.currentThread().getContextClassLoader(), getProperties());
		} else {
			return JAXBContext.newInstance(getContextPath(), getClassLoader(),
					getProperties());
		}
	}

	public Class<?> getObjectType() {
		return JAXBContext.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
