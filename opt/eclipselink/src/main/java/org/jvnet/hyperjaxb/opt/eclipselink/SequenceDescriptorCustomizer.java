package org.jvnet.hyperjaxb.opt.eclipselink;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * <p>Use the table name to generate individual table sequences on a given class.</p>
 * 
 * <p>Use the {@code eclipselink.descriptor.customizer} property as a prefix for a property
 * to configure a {@link DescriptorCustomizer}. Use this class's customize method, which
 * takes an {@link ClassDescriptor}, to programmatically access advanced EclipseLink
 * descriptor and mapping API for the descriptor associated with the JPA entity.</p>
 * 
 * {@code
 * <property
 *   name="eclipselink.descriptor.customizer.Order"
 *   value="acme.sessions.MyDescriptorCustomizer"
 * />}
 *
 */
public class SequenceDescriptorCustomizer implements DescriptorCustomizer
{
    /**
     * Customize the provided descriptor. This method is called after the
     * descriptor is populated from annotations/XML/defaults but before it is
     * initialized.
     * 
     * @param descriptor Defines persistence information on a class.
     */
    @Override
    public void customize(ClassDescriptor descriptor) throws Exception
	{
        descriptor.setSequenceNumberName(descriptor.getTableName());
    }
}
