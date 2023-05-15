package org.example.pub;

import java.io.IOException;
import java.util.List;

import org.jvnet.hyperjaxb.ejb.util.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.xml.bind.JAXBException;

/**
 * Example of unmarshalling and persisting Authors and Publications
 */
public class Main extends Context
{
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	public static Logger getLogger() { return logger; }

	public static final String SAMPLE_PUB_FILE = "src/test/samples/Blog01.xml";
	
    // Represents the unmarshalled Publication.
    private Publication publication = null;
    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }
	
	/**
	 * Command Line Invocation.
	 * 
	 * @param args CLI argument(s)
	 */
	public static void main(String[] args)
	{
		String xmlFileName = args.length > 0 ? args[0] : SAMPLE_PUB_FILE;
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
        setPublication(unmarshal(xmlFileName, Publication.class));
        getLogger().info("Publication: {}\n\n{}\n", getPublication().getTitle(), getPublication());

        // Select Publication(s) by order date.
        List<Publication> publicationList = selectPublications(0, 10, getPublication().getTitle());

        // Persist Publication, if missing.
        if ( publicationList.isEmpty() )
        {
    		// JPA: persist Publication to database.
            persist(getPublication());
            // Again, select Publication(s) by order date.
            publicationList = selectPublications(0, 1, getPublication().getTitle());
        }

        // Display Publication(s).
        for ( Publication publication : publicationList )
        {
			// JAXB: marshal Publication.
        	String xmlPublication = null;
        	
        	if ( publication instanceof Blog )
                xmlPublication = marshalToString(OF.createBlog((Blog) publication));
        	else if ( publication instanceof Book )
                xmlPublication = marshalToString(OF.createBook((Book) publication));
        	
			getLogger().info("Publication:\n\n{}", xmlPublication);
        }
	}
	
	protected List<Publication> selectPublications(Integer start, Integer count, String title)
		throws IOException
	{
		Transactional<List<Publication>> tx = selectPublicationsTX(start, count, title);
		// Auto-close transactional session.
		List<Publication> publicationList;
		try ( EntityManager em = createEntityManager() )
		{
			publicationList = tx.transact(em);
		}
		return publicationList;
	}
	
	protected static Transactional<List<Publication>> selectPublicationsTX(Integer start, Integer count, String title)
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<List<Publication>> tx = (em) ->
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Publication> cq = cb.createQuery(Publication.class);
			Root<Publication> fromPublication = cq.from(Publication.class);
			
			// Force eager loading of authors using an inner join.
			// See https://thorben-janssen.com/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
			fromPublication.fetch(Publication_.AUTHORS, JoinType.INNER);
			
			cq.select(fromPublication)
				.where(cb.equal(fromPublication.get(Publication_.TITLE), title));
			
			TypedQuery<Publication> query = em.createQuery(cq);
			query.setHint("eclipselink.query-results-cache", false);
			query.setHint("org.hibernate.cacheable", false);
			List<Publication> entities = query
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();

			return entities;
		};
		return tx;
	}
	
	private void persist(Publication po)
		throws IOException
	{
		Transactional<Integer> tx = (em) ->
		{
		    em.persist(po);
		    return 1;
		};
		// Auto-close transactional session.
		try ( EntityManager em = createEntityManager() )
		{
		    Integer count = tx.transact(em);
			getLogger().info("Persisted {} {}(s)", count, Publication.class.getSimpleName());
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
