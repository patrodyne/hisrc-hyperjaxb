package org.jvnet.hyperjaxb.xjc.model;

import jakarta.activation.MimeType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.glassfish.jaxb.core.v2.ClassFactory;
import org.glassfish.jaxb.core.v2.model.core.ID;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStringLiteral;
import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XmlString;

public class DefaultTypeUse implements TypeUse {
	private final CNonElement coreType;
	private final boolean collection;
	private final CAdapter adapter;
	private final ID id;
	private final MimeType expectedMimeType;

	public DefaultTypeUse(CNonElement itemType, boolean collection, ID id,
			MimeType expectedMimeType, CAdapter adapter) {
		this.coreType = itemType;
		this.collection = collection;
		this.id = id;
		this.expectedMimeType = expectedMimeType;
		this.adapter = adapter;
	}

	@Override
	public boolean isCollection() {
		return collection;
	}

	@Override
	public CNonElement getInfo() {
		return coreType;
	}

	@Override
	public CAdapter getAdapterUse() {
		return adapter;
	}

	@Override
	public ID idUse() {
		return id;
	}

	@Override
	public MimeType getExpectedMimeType() {
		return expectedMimeType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TypeUse))
			return false;

		final TypeUse that = (TypeUse) o;

		if (collection != that.isCollection())
			return false;
		if (this.id != that.idUse())
			return false;
		if (adapter != null ? !adapter.equals(that.getAdapterUse()) : that
				.getAdapterUse() != null)
			return false;
		if (coreType != null ? !coreType.equals(that.getInfo()) : that
				.getInfo() != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		result = (coreType != null ? coreType.hashCode() : 0);
		result = 29 * result + (collection ? 1 : 0);
		result = 29 * result + (adapter != null ? adapter.hashCode() : 0);
		return result;
	}

	@Override
	public JExpression createConstant(Outline outline, XmlString lexical) {
		if (isCollection())
			return null;

		if (adapter == null)
			return coreType.createConstant(outline, lexical);

		// [RESULT] new Adapter().unmarshal(CONSTANT);
		JExpression cons = coreType.createConstant(outline, lexical);
		@SuppressWarnings("unchecked")
		Class<? extends XmlAdapter<?, ?>> atype = (Class<? extends XmlAdapter<?, ?>>) adapter
				.getAdapterIfKnown();

		// try to run the adapter now rather than later.
		if (cons instanceof JStringLiteral && atype != null) {
			JStringLiteral scons = (JStringLiteral) cons;
			@SuppressWarnings("unchecked")
			XmlAdapter<Object, String> a = (XmlAdapter<Object, String>) ClassFactory
					.create(atype);
			try {
				Object value = a.unmarshal(scons.str);
				if (value instanceof String) {
					return JExpr.lit((String) value);
				}
			} catch (Exception e) {
				// assume that we can't eagerly bind this
			}
		}

		return JExpr._new(adapter.getAdapterClass(outline)).invoke("unmarshal")
				.arg(cons);
	}
}
