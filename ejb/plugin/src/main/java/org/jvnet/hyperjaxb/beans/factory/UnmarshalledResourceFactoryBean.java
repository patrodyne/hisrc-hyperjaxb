package org.jvnet.hyperjaxb.beans.factory;

import jakarta.xml.bind.JAXBContext;

public class UnmarshalledResourceFactoryBean
{
	private JAXBContext context;
	public JAXBContext getContext() { return context; }
	public void setContext(JAXBContext context) { this.context = context; }

//	private Resource resource;
//	public Resource getResource() { return resource; }
//	public void setResource(Resource resource) { this.resource = resource; }

	public Class<?> getObjectType()
	{
		return Object.class;
	}

	public boolean isSingleton()
	{
		return false;
	}

//	protected Object createInstance() throws Exception
//	{
//		final Resource resource = getResource();
//		final InputSource inputSource = new InputSource(resource .getInputStream());
//		try
//		{
//			inputSource.setSystemId(resource.getURL().toString());
//		} catch (IOException ignored) {  }
//		
//		final Object unmarshallingResult =
//			getContext().createUnmarshaller().unmarshal(inputSource);
//		if (unmarshallingResult instanceof JAXBElement<?>)
//			return ((JAXBElement<?>) unmarshallingResult).getValue();
//		else
//			return unmarshallingResult;
//	}
}
