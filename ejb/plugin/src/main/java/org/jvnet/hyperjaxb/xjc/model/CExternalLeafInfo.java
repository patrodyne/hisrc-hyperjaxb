package org.jvnet.hyperjaxb.xjc.model;

import jakarta.activation.MimeType;
import org.jvnet.hyperjaxb.xml.XMLConstants;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;

import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.model.core.ID;
import org.glassfish.jaxb.core.v2.runtime.Location;
import org.jvnet.hyperjaxb.ejb.Constants;
import org.xml.sax.Locator;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.model.nav.NavigatorImpl;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XmlString;

public class CExternalLeafInfo implements CNonElement, Location
{
	private final NType type;
	@Override
	public NType getType() { return type; }

	private final QName typeName;
	@Override
	public QName getTypeName() { return typeName; }

	private final CAdapter adapter;
	@Override
	public final CAdapter getAdapterUse() { return adapter; }

	@Override
	public boolean isCollection() { return false; }

	@Override
	public MimeType getExpectedMimeType() { return null; }

	@Override
	public final CExternalLeafInfo getInfo() { return this; }

	@Override
	public Locator getLocator() { return Constants.EMPTY_LOCATOR; }

	@Override
	public final XSComponent getSchemaComponent()
	{
		throw new UnsupportedOperationException( "TODO. If you hit this, let us know.");
	}

	@Override
	public boolean isSimpleType() { return true; }

	@Override
	public Location getLocation() { return this; }

	@Override
	public Locatable getUpstream() { return null; }

	@Override
	public CCustomizations getCustomizations() { return CCustomizations.EMPTY; }

	public CExternalLeafInfo(Class<?> c, String typeName, Class<? extends XmlAdapter<?, ?>> adapterClass)
	{
		this(c, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, typeName), new CAdapter(adapterClass, false));
	}

	public CExternalLeafInfo(Class<?> c, QName typeName, CAdapter adapter)
	{
		this.type = NavigatorImpl.create(c);
		this.typeName = typeName;
		this.adapter = adapter;
	}

	@Override
	public ID idUse()
	{
		return ID.NONE;
	}

	@Override
	public JType toType(Outline o, Aspect aspect)
	{
		return type.toType(o, aspect);
	}

	@Override
	public boolean canBeReferencedByIDREF()
	{
		return false;
	}

	@Override
	public JExpression createConstant(Outline arg0, XmlString arg1)
	{
		return null;
	}
}
