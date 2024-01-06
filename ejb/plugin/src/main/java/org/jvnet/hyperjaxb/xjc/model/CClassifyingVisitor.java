package org.jvnet.hyperjaxb.xjc.model;

import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;

import java.util.Collection;
import java.util.Set;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.slf4j.Logger;

import com.sun.tools.xjc.model.CArrayInfo;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElement;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CPropertyVisitor;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.model.CWildcardTypeInfo;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.impl.ElementDecl;

public class CClassifyingVisitor<U> implements CPropertyVisitor<U> {

	private final ProcessModel context;
	private final CClassifier<U> classifier;
	
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }
	
	public Logger getLogger()
	{
		return getPlugin().getLogger();
	}
	
	public CClassifyingVisitor(ProcessModel context, CClassifier<U> classifier, EJBPlugin plugin)
	{
		this.context = context;
		this.classifier = classifier;
		setPlugin(plugin);
	}

	@Override
	public U onAttribute(CAttributePropertyInfo attributePropertyInfo) {

		final CNonElement type = context.getGetTypes().getTarget(context,
				attributePropertyInfo);
		if (type instanceof CBuiltinLeafInfo) {
			return onBuiltinAttribute(attributePropertyInfo);
		} else if (type instanceof CEnumLeafInfo) {
			return onEnumAttribute(attributePropertyInfo);
		} else {
			return onOtherAttribute(attributePropertyInfo);
		}
	}

	@Override
	public U onValue(CValuePropertyInfo valuePropertyInfo) {
		final CNonElement type = context.getGetTypes().getTarget(context,
				valuePropertyInfo);
		if (type instanceof CBuiltinLeafInfo) {
			return onBuiltinValue(valuePropertyInfo);
		} else if (type instanceof CEnumLeafInfo) {
			return onEnumValue(valuePropertyInfo);
		} else {
			return onOtherValue(valuePropertyInfo);
		}
	}

	@Override
	public U onElement(CElementPropertyInfo elementPropertyInfo) {
		final Collection<? extends CTypeInfo> types = context.getGetTypes()
				.process(context, elementPropertyInfo);
		if (types.size() == 1) {
			final CTypeInfo type = types.iterator().next();
			if (type instanceof CBuiltinLeafInfo) {
				return onBuiltinElement(elementPropertyInfo);
			} else if (type instanceof CEnumLeafInfo) {
				return onEnumElement(elementPropertyInfo);
			} else if (type instanceof CArrayInfo) {
				return onArrayElement(elementPropertyInfo);
			} else if (type instanceof CClass) {
				return onClassElement(elementPropertyInfo);
			} else {
				throw new UnsupportedOperationException("Unexpected type.");
			}
		} else {
			return onHeteroElement(elementPropertyInfo);
		}
	}

	@Override
	public U onReference(CReferencePropertyInfo referencePropertyInfo) {

		final Collection<? extends CTypeInfo> types = context.getGetTypes()
				.process(context, referencePropertyInfo);
		final Set<CElement> elements = context.getGetTypes().getElements(
				context, referencePropertyInfo);
		if (types.size() == 1) {
			final CTypeInfo type = types.iterator().next();
			
			getLogger().trace("Type............: {}", type);
			getLogger().trace("  Location........: {}", toLocation(referencePropertyInfo));
			if (type instanceof CWildcardTypeInfo
					|| type.equals(CBuiltinLeafInfo.ANYTYPE)) {
				assert elements.isEmpty();
				assert referencePropertyInfo.getWildcard() != null;
				assert !referencePropertyInfo.isMixed();
				return onWildcardReference(referencePropertyInfo);
			} else {
				assert type instanceof CElement;
				assert !elements.isEmpty();
				assert referencePropertyInfo.getWildcard() == null;
				assert !referencePropertyInfo.isMixed();

				if (type instanceof CClass) {
					return onClassReference(referencePropertyInfo);
				} else if (type instanceof CElementInfo) {
					final CElementInfo elementInfo = (CElementInfo) type;
					final CNonElement contentType = elementInfo.getContentType();

					getLogger().trace("  ContextType.....: {}", contentType);
					if (contentType instanceof CBuiltinLeafInfo) {
						assert referencePropertyInfo.getWildcard() == null;
						if ( getLogger().isTraceEnabled())
						{
							getLogger().trace("  Parent..........: {}", referencePropertyInfo.parent());
							getLogger().trace("  Property........: {}", referencePropertyInfo.getName(false));
						}
						boolean isMixed = referencePropertyInfo.isMixed();
						boolean isNillable = false;
						if ( referencePropertyInfo.getSchemaComponent() instanceof XSParticle )
						{
							XSParticle xp = (XSParticle) referencePropertyInfo.getSchemaComponent();
							if ( getLogger().isTraceEnabled())
							{
								getLogger().trace("  MinOccurs.: {}", xp.getMinOccurs());
								getLogger().trace("  MaxOccurs.: {}", xp.getMaxOccurs());
							}
							if ( xp.getTerm().isElementDecl() )
							{
								ElementDecl ed = (ElementDecl) xp.getTerm();
								getLogger().trace("  Nillable..: {}", ed.isNillable());
								isNillable = ed.isNillable();
							}
						}
						else if ( referencePropertyInfo.getSchemaComponent() instanceof XSComplexType )
						{
							XSComplexType xc = (XSComplexType) referencePropertyInfo.getSchemaComponent();
							isMixed = xc.isMixed();
						}
						if ( getLogger().isTraceEnabled())
						{
							getLogger().trace("    isMixed............: {}", isMixed);
							getLogger().trace("    elements.isEmpty().: {}", elements.isEmpty());
							getLogger().trace("    isNillable.........: {}", isNillable);
							getLogger().trace("    XOR................: {}", (isMixed ^ elements.isEmpty() ^ isNillable));
						}
						assert isMixed ^ elements.isEmpty() ^ isNillable;
						return onBuiltinElementReference(referencePropertyInfo);
					} else if (contentType instanceof CEnumLeafInfo) {
						assert !elements.isEmpty();
						assert referencePropertyInfo.getWildcard() == null;
						assert !referencePropertyInfo.isMixed();
						return onEnumElementReference(referencePropertyInfo);
					} else if (contentType instanceof CArrayInfo) {
						assert !elements.isEmpty();
						assert referencePropertyInfo.getWildcard() == null;
						assert !referencePropertyInfo.isMixed();
						return onArrayElementReference(referencePropertyInfo);
					} else if (contentType instanceof CClass) {
						assert !elements.isEmpty();
						assert referencePropertyInfo.getWildcard() == null;
						assert !referencePropertyInfo.isMixed();
						return onClassElementReference(referencePropertyInfo);
					} else {
						throw new UnsupportedOperationException(
								"Unexpected type in property ["
										+ referencePropertyInfo.getName(true)
										+ "] of the class ["
										+ ((CClassInfo) referencePropertyInfo
												.parent()).getSqueezedName()
										+ "].");
					}
				} else {
					throw new UnsupportedOperationException(
							"Unexpected type in property ["
									+ referencePropertyInfo.getName(true)
									+ "] of the class ["
									+ ((CClassInfo) referencePropertyInfo
											.parent()).getSqueezedName() + "].");
				}
			}
			// This covers also mixed non-wildcard
		} else {

			if (referencePropertyInfo.getWildcard() == null
					&& !referencePropertyInfo.isMixed()) {

				if (elements.size() == 1) {
					final CElement element = elements.iterator().next();

					if (element instanceof CElementInfo) {
						return onSubstitutedElementReference(referencePropertyInfo);
					} else {
						throw new UnsupportedOperationException(
								"Unexpected type.");
					}
				} else {
					return onHeteroReference(referencePropertyInfo);
				}

			} else {
				return onHeteroReference(referencePropertyInfo);
			}
		}
	}

	public U onBuiltinAttribute(CAttributePropertyInfo attributePropertyInfo) {
		return !attributePropertyInfo.isCollection() ? classifier
				.onSingleBuiltinAttribute(attributePropertyInfo) : classifier
				.onCollectionBuiltinAttribute(attributePropertyInfo);
	}

	public U onEnumAttribute(CAttributePropertyInfo attributePropertyInfo) {
		return !attributePropertyInfo.isCollection() ? classifier
				.onSingleEnumAttribute(attributePropertyInfo) : classifier
				.onCollectionEnumAttribute(attributePropertyInfo);
	}

	public U onOtherAttribute(CAttributePropertyInfo attributePropertyInfo) {
		return !attributePropertyInfo.isCollection() ? classifier
				.onSingleOtherAttribute(attributePropertyInfo) : classifier
				.onCollectionOtherAttribute(attributePropertyInfo);
	}

	public U onBuiltinValue(CValuePropertyInfo valuePropertyInfo) {
		return !valuePropertyInfo.isCollection() ? classifier
				.onSingleBuiltinValue(valuePropertyInfo) : classifier
				.onCollectionBuiltinValue(valuePropertyInfo);
	}

	public U onEnumValue(CValuePropertyInfo valuePropertyInfo) {
		return !valuePropertyInfo.isCollection() ? classifier
				.onSingleEnumValue(valuePropertyInfo) : classifier
				.onCollectionEnumValue(valuePropertyInfo);
	}

	public U onOtherValue(CValuePropertyInfo valuePropertyInfo) {
		return !valuePropertyInfo.isCollection() ? classifier
				.onSingleOtherValue(valuePropertyInfo) : classifier
				.onCollectionOtherValue(valuePropertyInfo);
	}

	public U onBuiltinElement(CElementPropertyInfo elementPropertyInfo) {
		return !elementPropertyInfo.isCollection() ? classifier
				.onSingleBuiltinElement(elementPropertyInfo) : classifier
				.onCollectionBuiltinElement(elementPropertyInfo);
	}

	public U onEnumElement(CElementPropertyInfo elementPropertyInfo) {
		return !elementPropertyInfo.isCollection() ? classifier
				.onSingleEnumElement(elementPropertyInfo) : classifier
				.onCollectionEnumElement(elementPropertyInfo);
	}

	public U onArrayElement(CElementPropertyInfo elementPropertyInfo) {
		return !elementPropertyInfo.isCollection() ? classifier
				.onSingleArrayElement(elementPropertyInfo) : classifier
				.onCollectionArrayElement(elementPropertyInfo);
	}

	public U onClassElement(CElementPropertyInfo elementPropertyInfo) {
		return !elementPropertyInfo.isCollection() ? classifier
				.onSingleClassElement(elementPropertyInfo) : classifier
				.onCollectionClassElement(elementPropertyInfo);
	}

	public U onHeteroElement(CElementPropertyInfo elementPropertyInfo) {
		return !elementPropertyInfo.isCollection() ? classifier
				.onSingleHeteroElement(elementPropertyInfo) : classifier
				.onCollectionHeteroElement(elementPropertyInfo);
	}

	public U onBuiltinElementReference(
			CReferencePropertyInfo referencePropertyInfo) {
		return !referencePropertyInfo.isCollection() ? classifier
				.onSingleBuiltinElementReference(referencePropertyInfo)
				: classifier
						.onCollectionBuiltinElementReference(referencePropertyInfo);
	}

	public U onEnumElementReference(CReferencePropertyInfo referencePropertyInfo) {
		return !referencePropertyInfo.isCollection() ? classifier
				.onSingleEnumElementReference(referencePropertyInfo)
				: classifier
						.onCollectionEnumElementReference(referencePropertyInfo);
	}

	public U onArrayElementReference(
			CReferencePropertyInfo referencePropertyInfo) {
		return !referencePropertyInfo.isCollection() ? classifier
				.onSingleArrayElementReference(referencePropertyInfo)
				: classifier
						.onCollectionArrayElementReference(referencePropertyInfo);
	}

	public U onClassElementReference(
			CReferencePropertyInfo referencePropertyInfo) {
		return !referencePropertyInfo.isCollection() ? classifier
				.onSingleClassElementReference(referencePropertyInfo)
				: classifier
						.onCollectionClassElementReference(referencePropertyInfo);
	}

	public U onSubstitutedElementReference(
			CReferencePropertyInfo referencePropertyInfo) {
		return !referencePropertyInfo.isCollection() ? classifier
				.onSingleSubstitutedElementReference(referencePropertyInfo)
				: classifier
						.onCollectionSubstitutedElementReference(referencePropertyInfo);
	}

	public U onClassReference(CReferencePropertyInfo referencePropertyInfo) {
		return !referencePropertyInfo.isCollection() ? classifier
				.onSingleClassReference(referencePropertyInfo) : classifier
				.onCollectionClassReference(referencePropertyInfo);
	}

	public U onWildcardReference(CReferencePropertyInfo referencePropertyInfo) {
		return !referencePropertyInfo.isCollection() ? classifier
				.onSingleWildcardReference(referencePropertyInfo) : classifier
				.onCollectionWildcardReference(referencePropertyInfo);
	}

	public U onHeteroReference(CReferencePropertyInfo referencePropertyInfo) {
		return !referencePropertyInfo.isCollection() ? classifier
				.onSingleHeteroReference(referencePropertyInfo) : classifier
				.onCollectionHeteroReference(referencePropertyInfo);
	}

}