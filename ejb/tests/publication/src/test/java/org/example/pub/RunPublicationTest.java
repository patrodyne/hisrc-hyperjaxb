package org.example.pub;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jvnet.basicjaxb.testing.Bogus.RANDOM;
import static org.jvnet.basicjaxb.testing.Bogus.firstName;
import static org.jvnet.basicjaxb.testing.Bogus.lastName;
import static org.jvnet.hyperjaxb.ejb.util.Transactional.CacheOption.REUSE;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.pub.model.Author;
import org.example.pub.model.Blog;
import org.example.pub.model.Book;
import org.example.pub.model.ObjectFactory;
import org.example.pub.model.Publication;
import org.junit.jupiter.api.Test;
import org.jvnet.basicjaxb.testing.Bogus;
import org.jvnet.hyperjaxb.ejb.test.AbstractEntityManagerTest;
import org.jvnet.hyperjaxb.ejb.util.Transactional;

import jakarta.persistence.EntityManager;

/**
 * Unit tests for Publication persistence.
 */
public class RunPublicationTest extends AbstractEntityManagerTest
{
	private static final int AUTHOR_PER_PUB_COUNT_MAX = 2;
	private static final int AUTHOR_REUSE_ODDS = 5;
	private static final int AUTHOR_COUNT_MIN = 10;
	private static final int BLOG_COUNT_MAX = 10;
	private static final int BOOK_COUNT_MAX = 10;
	private static final String TITLE_PREFIX = "Mystery of ";

	@Override
	public String getPersistenceUnitName()
	{
		return ObjectFactory.class.getPackageName();
	}

	/**
	 * <p>Test persistence of many-to-many relationship and entity inheritance.
	 * In the publication model, there is a many-to-many relationship between
	 * <code>Publication</code> and <code>Author</code>. This test generates
	 * the many-to-many instances to test JPA <em>persist</em> and <em>query</em>
	 * methods.</p>
	 *
	 * <p>Also, the <code>Publication</code> entity is inherited by to sub-entities:
	 * <code>Blog</code> and <code>Book</code>. This test generates both types of
	 * sub-entities for <em>persist</em> and <em>query</em> method testing.</p>
	 */
	@Test
	public void testPersistence()
	{
		List<Author> authorList = new ArrayList<>();

		try ( EntityManager entityManager = getEntityManagerFactory().createEntityManager() )
		{
			assertTrue(entityManager.isOpen(), "EntityManager instance is open");

			for ( int authorIdx = 0; authorIdx < AUTHOR_COUNT_MIN; ++authorIdx)
			{
				Transactional<Author> tx = (em) ->
				{
					Author author = generateAuthor(authorList);
					em.persist(author);
					return author;
				};

				Author author = tx.transact(entityManager, REUSE);
				assertTrue(author.getPid() > 0, "Author entity has non-zero id.");
			}

			int blogCount = 1 + RANDOM.nextInt(BLOG_COUNT_MAX);
			for ( int blogIdx = 0; blogIdx < blogCount; ++blogIdx)
			{
				Transactional<Blog> tx = (em) ->
				{
					Blog blog = new Blog();
					blog.setTitle(TITLE_PREFIX + Bogus.streetAddress());
					blog.setPublishingDate(OffsetDateTime.now());
					blog.setUrl("https://example.org/blog/"+Bogus.alpha(6));

					int authorCount = 1 + RANDOM.nextInt(2);
					for ( int authorIdx = 0; authorIdx < authorCount; ++authorIdx)
					{
						Author author = generateAuthor(authorList);
						if ( !blog.getAuthors().contains(author) )
						{
							blog.getAuthors().add(author);
							author.getPublications().add(blog);
						}
					}

					em.persist(blog);
					return blog;
				};

				Blog blog = tx.transact(entityManager, REUSE);
				assertTrue(blog.getPid() > 0, "Blog entity has non-zero id.");

				// Keep the managed authors for 'many' other publications.
				for ( Author author : blog.getAuthors() )
				{
					if ( !authorList.contains(author) )
						authorList.add(author);
				}
			}

			int bookCount = 1 + RANDOM.nextInt(BOOK_COUNT_MAX);
			for ( int bookIdx = 0; bookIdx < bookCount; ++bookIdx)
			{
				Transactional<Book> tx = (em) ->
				{
					Book book = new Book();
					book.setTitle(TITLE_PREFIX + Bogus.streetAddress());
					book.setPublishingDate(OffsetDateTime.now());
					book.setPages(100 + RANDOM.nextInt(400));

					int authorCount = 1 + RANDOM.nextInt(AUTHOR_PER_PUB_COUNT_MAX);
					for ( int authorIdx = 0; authorIdx < authorCount; ++authorIdx)
					{
						Author author = generateAuthor(authorList);
						if ( !book.getAuthors().contains(author) )
						{
							book.getAuthors().add(author);
							author.getPublications().add(book);
						}
					}

					em.persist(book);
					return book;
				};
				Book book = tx.transact(entityManager, REUSE);
				assertTrue(book.getPid() > 0, "Book entity has non-zero id.");

				// Keep the managed authors for 'many' other publications.
				for ( Author author : book.getAuthors() )
				{
					if ( !authorList.contains(author) )
						authorList.add(author);
				}
			}

			for ( Author author : authorList )
			{
				assertTrue(author.getPublications().size() > 0, "There must be at least 1 publication per author.");
				for ( Publication pub : author.getPublications() )
				{
					String title = pub.getTitle();
					Transactional<List<Publication>> txPub = Main.selectPublicationsTX(0, 1, title);
					List<Publication> publicationList = txPub.transact(entityManager, REUSE);
					if ( publicationList.isEmpty() )
					{
						// EclipseLink fails selection of TABLE_PER_CLASS inheritance; EL bug.
						getLogger().error("Publication with title '" + title + "' should exist. EclipseLink TABLE_PER_CLASS bug?");
						if ( pub instanceof Blog )
						{
							Transactional<List<Blog>> txBlog = Main.selectBlogsTX(0, 1, title);
							publicationList.addAll(txBlog.transact(entityManager, REUSE));
						}
						else if ( pub instanceof Book )
						{
							Transactional<List<Book>> txBook = Main.selectBooksTX(0, 1, title);
							publicationList.addAll(txBook.transact(entityManager, REUSE));
						}
					}
					assertTrue(publicationList.size() > 0, "Publication with title '" + title + "' should exist.");
					assertTrue(pub.getAuthors().size() > 0, "There must be at least 1 author per publication.");
				}
			}
		}

		assertTrue(authorList.size() > 0, "At least 1 author must exist.");
	}

	private Author generateAuthor(List<Author> authorList)
	{
		Author author = null;
		if ( !authorList.isEmpty() && (RANDOM.nextInt(AUTHOR_REUSE_ODDS) == 0) )
			author = authorList.get(RANDOM.nextInt(authorList.size()));
		else
		{
			author = new Author();
			author.setFirstName(firstName());
			author.setLastName(lastName());
		}
		return author;
	}
}
