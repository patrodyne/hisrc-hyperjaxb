package org.jvnet.hyperjaxb.xsom;

import static org.jvnet.hyperjaxb.ejb.Constants.TODO_LOG_LEVEL;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.jvnet.hyperjaxb.ejb.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.visitor.XSVisitor;

public class SimpleTypeVisitor implements XSVisitor {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private List<QName> typeNames = new LinkedList<QName>();

	public List<QName> getTypeNames() {
		return typeNames;
	}

	public void annotation(XSAnnotation ann) {
		// todo("Annotation.");
	}

	public void attGroupDecl(XSAttGroupDecl decl) {
		todo("Attribute group declaration [" + decl.getName() + "].");
	}

	public void attributeDecl(XSAttributeDecl decl) {
		decl.getType().visit(this);
	}

	public void attributeUse(XSAttributeUse use) {
		use.getDecl().visit(this);
	}

	public void complexType(XSComplexType type) {
		// todo("Complex type [" + type.getName() + "].");
	}

	public void facet(XSFacet facet) {
		todo("Facet.");
	}

	public void identityConstraint(XSIdentityConstraint decl) {
		todo("Identity constraint.");
	}

	public void notation(XSNotation notation) {
		todo("Notation.");
	}

	public void schema(XSSchema schema) {
		todo("Schema.");
	}

	public void xpath(XSXPath xp) {
		todo("XPath.");
	}

	public void elementDecl(XSElementDecl decl) {
		decl.getType().visit(this);
	}

	public void modelGroup(XSModelGroup group) {
		for (XSParticle child : group.getChildren()) {
			child.visit(this);
		}
	}

	public void modelGroupDecl(XSModelGroupDecl decl) {
		todo("Model group declaration.");
	}

	public void wildcard(XSWildcard wc) {
		todo("Wildcard.");
	}

	public void empty(XSContentType empty) {
		todo("Empty.");
	}

	public void particle(XSParticle particle) {
		particle.getTerm().visit(this);
	}

	public void simpleType(XSSimpleType simpleType) {
		if (simpleType.getName() != null) {
			typeNames.add(new QName(simpleType.getTargetNamespace(), simpleType
					.getName()));
			if (simpleType.isRestriction()) {
				final XSType baseType = simpleType.asRestriction()
						.getBaseType();
				if (baseType != null) {
					baseType.visit(this);
				}
			}

			if (simpleType.isList()) {
				final XSSimpleType itemType = simpleType.asList().getItemType();
				if (itemType != null) {
					itemType.visit(this);
				}
			}
			// simpleType.getSimpleBaseType()
		}
	}

	private void todo(String comment) {
        String msg = "TODO " + (comment == null ? "Not yet supported." : comment);
		String level = System.getProperty(TODO_LOG_LEVEL);
		if ( "DEBUG".equalsIgnoreCase(level) ) logger.debug(msg);
		else if ( "INFO".equalsIgnoreCase(level) ) logger.info(msg);
		else if ( "WARN".equalsIgnoreCase(level) ) logger.warn(msg);
		else if ( "ERROR".equalsIgnoreCase(level) ) logger.error(msg);
		else logger.error(msg);
	}
}
