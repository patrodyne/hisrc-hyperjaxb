package org.example.root_header;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.jvnet.hyperjaxb.ejb.util.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.xml.bind.JAXBException;

/**
 * Example of unmarshalling and persisting a Root
 */
public class Main extends Context
{
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	public static Logger getLogger() { return logger; }

	public static final String SAMPLE_ROOT_HEADER_FILE = "src/test/samples/root_header_01.xml";
	
    // Represents the unmarshalled Root.
    private Root root = null;
    public Root getRoot() { return root; }
    public void setRoot(Root root) { this.root = root; }
	
	/**
	 * Command Line Invocation.
	 * 
	 * @param args CLI argument(s)
	 */
	public static void main(String[] args)
	{
		String xmlFileName = args.length > 0 ? args[0] : SAMPLE_ROOT_HEADER_FILE;
		try
		{
			Main main = new Main();
			main.execute(xmlFileName);
		}
		catch (JAXBException | IOException | SAXException ex)
		{
			getLogger().error("Aborting " + Main.class.getName(), ex);
		}
	}
	
	/**
	 * Execute the JAXB and JPA actions.
	 * 
	 * @param xmlFileName The path to an XML data file.
	 * 
	 * @throws JAXBException When a JAXB action fails.
	 * @throws IOException When an I/O actions fails.
	 * @throws SAXException When XmlSchemaValidator cannot be generated from DOM.
	 */
	public void execute(String xmlFileName) throws JAXBException, IOException, SAXException
	{
		// Enable JAXB schema validation on XML instances.
		generateXmlSchemaValidatorFromDom();
		
		// JAXB: unmarshal XML file by path name.
        setRoot(unmarshal(xmlFileName, Root.class));
        getLogger().info("Root: {}\n\n{}\n", getRoot().getExported(), getRoot());

        // Select Purchase Order(s) by order date.
        List<Root> rootList = selectRoots(0, 10, getRoot().getExportedItem());

        // Persist Purchase Order, if missing.
        if ( rootList.isEmpty() )
        {
    		// JPA: persist Root to database.
            persist(getRoot());
            // Again, select Purchase Order(s) by order date.
            rootList = selectRoots(0, 1, getRoot().getExportedItem());
        }

        // Display Purchase Order(s).
        for ( Root root : rootList )
        {
			// JAXB: marshal Root.
            String xmlRoot = marshalToString(root);
			getLogger().info("Root:\n\n{}", xmlRoot);
        }
	}
	
	private List<Root> selectRoots(Integer start, Integer count, Date exported)
		throws IOException
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<List<Root>> tx = (em) ->
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Root> cq = cb.createQuery(Root.class);
			jakarta.persistence.criteria.Root<Root> fromRoot = cq.from(Root.class);
			
			cq.select(fromRoot)
				.where(cb.equal(fromRoot.get(Root_.EXPORTED_ITEM), exported));
			
			TypedQuery<Root> query = em.createQuery(cq);
			query.setHint("org.hibernate.cacheable", false);
			List<Root> entities = query
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();

			return entities;
		};
		// Auto-close transactional session.
		List<Root> rootList;
		try ( EntityManager em = createEntityManager() )
		{
			rootList = tx.transact(em);
		}
		return rootList;
	}
	
	private void persist(Root root)
		throws IOException
	{
		Transactional<Integer> tx = (em) ->
		{
		    em.persist(root);
		    return 1;
		};
		// Auto-close transactional session.
		try ( EntityManager em = createEntityManager() )
		{
		    Integer count = tx.transact(em);
			getLogger().info("Persisted {} {}(s)", count, Root.class.getSimpleName());
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
