package org.example.pub;

import org.example.pub.model.Author;
import org.example.pub.model.Publication;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.Unmarshaller.Listener;

/**
 * Publication Unmarshaller Listener
 *
 * <p>
 * Register an instance of an implementation of this class with {@link Unmarshaller} to externally
 * listen for unmarshal events.
 * </p>
 *
 * <p>
 * This class enables 'before' and 'after' processing of an instance of a Jakarta XML Binding mapped
 * class as XML data is unmarshalled into it. The event callbacks are called when unmarshalling XML
 * content into a JAXBElement instance or a Jakarta XML Binding mapped class that represents a complex
 * type definition. The event callbacks are not called when unmarshalling to an instance of a Java
 * data type that represents a simple type definition.
 * </p>
 */
public class PublicationUL extends Listener
{
	/**
	 * <p>Callback method invoked after unmarshalling XML data into <code>target</code>.</p>
	 *
	 * <p>
	 * This method is invoked after all the properties (except IDREF) are unmarshalled into
	 * <code>target</code>, but before <code>target</code> is set into its <code>parent</code>
	 * object.
	 * </p>
	 *
	 * <p>
	 * <b>Note:</b> If the class of <code>target</code> defines its own <code>afterUnmarshal</code>
	 * method, the class specific callback method is invoked before this method is invoked.
	 * </p>
	 *
	 * @param target Non-null instance of JAXB mapped class after to unmarshalling into it.
	 * @param parent Instance of JAXB mapped class that will reference <code>target</code>,
	 *               <code>null</code> when <code>target</code> is root element.
	 */
	@Override
	public void afterUnmarshal(Object target, Object parent)
	{
		if ( target instanceof Author && parent instanceof Publication )
		{
			Author author = (Author) target;
//			Publication publication = (Publication) parent;
//			author.getPublications().add(publication);
		}
	}
}
