package org.example.pub;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.example.pub.model.Author;
import org.example.pub.model.Blog;
import org.example.pub.model.Blog_;
import org.example.pub.model.Book;
import org.example.pub.model.Book_;
import org.example.pub.model.Publication;
import org.example.pub.model.Publication_;
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
		//generateXmlSchemaValidatorFromDom();

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

        // Display Publication(s) outside transaction,
        // authors were eager loaded using an inner join.
        Set<Author> authorSet = new HashSet<>();
        for ( Publication publication : publicationList )
        {
        	// Build set of authors.
        	authorSet.addAll(publication.getAuthors());
			// JAXB: marshal Publication.
        	String xmlPublication = marshalToString(publication);
			getLogger().info("Publication:\n\n{}", xmlPublication);
        }

        // Display Author(s) in a transaction to avoid LazyInitializationException.
        int count = displayAuthors(authorSet);
        getLogger().info("Authors displayed: {}", count);
	}

	protected String marshalToString(Publication publication) throws JAXBException, IOException
	{
    	String xmlPublication = null;
    	if ( publication instanceof Blog )
            xmlPublication = marshalToString(OF.createBlog((Blog) publication));
    	else if ( publication instanceof Book )
            xmlPublication = marshalToString(OF.createBook((Book) publication));
		return xmlPublication;
	}


	/**
	 * Display authors for the given detached set of authors.
	 *
	 * @param authorSet A detached set of authors.
	 *
	 * @return The count of authors displayed.
	 */
	protected int displayAuthors(Set<Author> authorSet) throws IOException
	{
		Transactional<Integer> tx = displayAuthorsTX(authorSet);
		// Auto-close transactional session.
		try ( EntityManager em = createEntityManager() )
		{
			return tx.transact(em);
		}
	}

	private Transactional<Integer> displayAuthorsTX(Set<Author> authorSet)
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<Integer> tx = (em) ->
		{
			Integer count = 0;
			try
			{
		        count = authorSet.size();
		        for ( Author author : authorSet )
		        {
		        	// Find the managed author using the current EM.
		        	Author emAuthor = em.find(Author.class, author.getId());

		        	String xmlAuthor = marshalToString(OF.createAuthor(emAuthor));
					getLogger().info("Author:\n\n{}", xmlAuthor);
			        // Note: Author.publications is XML transient, by design!!!
					for ( Publication publication : emAuthor.getPublications() )
					{
			        	String xmlPublication = marshalToString(publication);
						getLogger().info("Publication:\n\n{}", xmlPublication);
					}
		        }
			}
			catch (JAXBException | IOException ex)
			{
				count = -1;
				getLogger().error("displayAuthorsTX cannot be marshaled", ex);
			}
			return count;
		};
		return tx;
	}

	/**
	 * Select a limited list of publications for the given title.
	 *
	 * @param start The starting offset.
	 * @param count The count limit.
	 * @param title The publication title.
	 *
	 * @return A list of publications.
	 *
	 * @throws IOException When the list cannot be selected.
	 */
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

			// Force eager loading of authors using an inner join.
			// See https://thorben-janssen.com/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
			Root<Publication> fromPublication = cq.from(Publication.class);
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

	protected static Transactional<List<Blog>> selectBlogsTX(Integer start, Integer count, String title)
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<List<Blog>> tx = (em) ->
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);

			// Force eager loading of authors using an inner join.
			// See https://thorben-janssen.com/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
			Root<Blog> fromBlog = cq.from(Blog.class);
			fromBlog.fetch(Blog_.AUTHORS, JoinType.INNER);

			cq.select(fromBlog)
				.where(cb.equal(fromBlog.get(Blog_.TITLE), title));

			TypedQuery<Blog> query = em.createQuery(cq);
			query.setHint("eclipselink.query-results-cache", false);
			query.setHint("org.hibernate.cacheable", false);
			List<Blog> entities = query
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();

			return entities;
		};
		return tx;
	}


	protected static Transactional<List<Book>> selectBooksTX(Integer start, Integer count, String title)
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<List<Book>> tx = (em) ->
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Book> cq = cb.createQuery(Book.class);

			// Force eager loading of authors using an inner join.
			// See https://thorben-janssen.com/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
			Root<Book> fromBook = cq.from(Book.class);
			fromBook.fetch(Book_.AUTHORS, JoinType.INNER);

			cq.select(fromBook)
				.where(cb.equal(fromBook.get(Book_.TITLE), title));

			TypedQuery<Book> query = em.createQuery(cq);
			query.setHint("eclipselink.query-results-cache", false);
			query.setHint("org.hibernate.cacheable", false);
			List<Book> entities = query
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
