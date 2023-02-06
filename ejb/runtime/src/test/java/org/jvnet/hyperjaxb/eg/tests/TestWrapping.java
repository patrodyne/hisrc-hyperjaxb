/**
 * 
 */
package org.jvnet.hyperjaxb.eg.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.Collection;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test using XmlElementWrapper with XmlAnyElement.
 * 
 * @author bspies.
 */
public class TestWrapping
{
	private Logger logger = LoggerFactory.getLogger(TestWrapping.class);
	public Logger getLogger() { return logger; }

	@Test
	public void testWrapping()
		throws Exception
	{
		JAXBContext ctx = JAXBContext.newInstance(Job.class, Node.class, UserTask.class, AutoTask.class);
		try ( InputStream is = TestWrapping.class.getResourceAsStream("test.xml") )
		{
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			Job job = (Job) unmarshaller.unmarshal(is);
			Collection<Node> nodes = job.getNodes();
			assertNotNull(nodes, "Nodes are null!");
			assertTrue(nodes.size() > 0, "There are no nodes!");
			
			for (Node node : nodes)
				getLogger().debug("Name: " + node.getName());
		}
	}

//	public static void main(String[] args)
//	{
//		junit.textui.TestRunner.run(TestWrapping.class);
//	}
}
