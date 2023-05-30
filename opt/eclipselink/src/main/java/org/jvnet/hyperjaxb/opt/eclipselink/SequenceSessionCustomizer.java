package org.jvnet.hyperjaxb.opt.eclipselink;

import java.sql.SQLException;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;

/**
 * <p>Use the table name to generate individual table sequences for all session classes.</p>
 * 
 * <p>Use the {@code eclipselink.session.customizer} property to configure a
 * {@link SessionCustomizer}. Use this class's customize method, which
 * takes an {@link Session}, to programmatically access advanced EclipseLink
 * descriptor and mapping API for the descriptor associated with each JPA entity.</p>
 * 
 * {@code
 * <property
 *   name="eclipselink.session.customizer"
 *   value="foo.bar.MySessionCustomizer" />}
 */
public class SequenceSessionCustomizer implements SessionCustomizer
{
    /**
     * Customize the all session descriptors. This method is called after the
     * descriptor is populated from annotations/XML/defaults but before it is
     * initialized.
     * 
     * @param session Defines the EclipseLink session public interface.
     */
	@Override
	public void customize(Session session) throws SQLException
	{
		for (ClassDescriptor descriptor : session.getDescriptors().values())
	        descriptor.setSequenceNumberName(descriptor.getTableName());
	}
}
