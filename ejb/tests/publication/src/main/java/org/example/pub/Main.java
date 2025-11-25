package org.example.pub;

import java.io.IOException;
import java.util.List;

import org.example.pub.model.Blog;
import org.example.pub.model.Blog_;
import org.example.pub.model.Book;
import org.example.pub.model.Book_;
import org.example.pub.model.Collection;
import org.example.pub.model.Collection_;
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

	public static final String SAMPLE_PUB_FILE = "src/test/samples/Collection01.xml";

    // Represents the unmarshalled Collection.
    private Collection collection = null;
    public Collection getCollection() { return collection; }
    public void setCollection(Collection collection) { this.collection = collection; }

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
		// generateXmlSchemaValidatorFromDom();

		// JAXB: unmarshal XML file by path name.
        setCollection(unmarshal(xmlFileName, Collection.class));
        getLogger().info("Collection: {}\n\n{}\n", getCollection().getName(), getCollection());

        // Persist a new collection.
		persist(getCollection(), true);

        // Select Collection by name, if any.
		Collection selection = selectCollection(getCollection().getName());

        String xmlCollection = marshalToString(selection);
        getLogger().info("Collection: {}\n\n{}\n", getCollection().getName(), xmlCollection);
	}

	/**
	 * Select a collection for the given name.
	 *
	 * @param name The unique collection name.
	 *
	 * @return A collection.
	 *
	 * @throws IOException When the collection cannot be selected.
	 */
	protected Collection selectCollection(String name)
		throws IOException
	{
		Transactional<Collection> tx = selectCollectionTX(name);
		// Auto-close transactional session.
		Collection entity;
		try ( EntityManager em = createEntityManager() )
		{
			entity = tx.transact(em);
		}
		return entity;
	}

	private void persist(Collection entity, boolean isNew)
		throws IOException
	{
		Transactional<Integer> tx = (em) ->
		{
			if ( isNew )
				em.persist(entity);
			else
				em.merge(entity);
		    return 1;
		};
		// Auto-close transactional session.
		try ( EntityManager em = createEntityManager() )
		{
		    Integer count = tx.transact(em);
			getLogger().info("Persisted {} {}(s)", count, Collection.class.getSimpleName());
		}
	}

	protected static Transactional<Collection> selectCollectionTX(String name)
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<Collection> tx = (em) ->
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Collection> cq = cb.createQuery(Collection.class);

			Root<Collection> fromCollection = cq.from(Collection.class);
			// Force eager loading of authors and publications using an inner join.
			// See https://thorben-janssen.com/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
			// fromCollection.fetch(Collection_.AUTHOR, JoinType.INNER);
			// fromCollection.fetch(Collection_.BLOG_OR_BOOK, JoinType.INNER);

			cq.select(fromCollection)
				.where(cb.equal(fromCollection.get(Collection_.NAME), name));

			TypedQuery<Collection> query = em.createQuery(cq);
			query.setHint("eclipselink.query-results-cache", false);
			query.setHint("org.hibernate.cacheable", false);
			Collection entity = query.getSingleResult();

			return entity;
		};
		return tx;
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

}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
