package org.example.pub;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jvnet.basicjaxb.test.Bogus.RANDOM;
import static org.jvnet.basicjaxb.test.Bogus.alpha;
import static org.jvnet.basicjaxb.test.Bogus.firstName;
import static org.jvnet.basicjaxb.test.Bogus.lastName;
import static org.jvnet.basicjaxb.test.Bogus.streetAddress;
import static org.jvnet.hyperjaxb.ejb.util.Transactional.CacheOption.REUSE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.jvnet.hyperjaxb.ejb.test.AbstractEntityManagerTest;
import org.jvnet.hyperjaxb.ejb.util.Transactional;

import jakarta.persistence.EntityManager;

/**
 * Unit tests for Publication persistence.
 */
public class PublicationTest extends AbstractEntityManagerTest
{
	private static final int AUTHOR_PER_PUB_COUNT_MAX = 2;
	private static final int AUTHOR_REUSE_ODDS = 5;
	private static final int AUTHOR_COUNT_MIN = 10;
	private static final int BLOG_COUNT_MAX = 10;
	private static final int BOOK_COUNT_MAX = 10;
	private static final String TITLE_PREFIX = "Mystery of ";
	
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
				assertTrue(author.getId() > 0, "Author entity has non-zero id.");
			}

			int blogCount = 1 + RANDOM.nextInt(BLOG_COUNT_MAX);
			for ( int blogIdx = 0; blogIdx < blogCount; ++blogIdx)
			{
				Transactional<Blog> tx = (em) ->
				{
					Blog blog = new Blog();
					blog.setTitle(TITLE_PREFIX + streetAddress());
					blog.setPublishingDateItem(new Date());
					blog.setUrl("https://example.org/blog/"+alpha(6));
					
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
				assertTrue(blog.getId() > 0, "Blog entity has non-zero id.");
				
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
					book.setTitle(TITLE_PREFIX + streetAddress());
					book.setPublishingDateItem(new Date());
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
				assertTrue(book.getId() > 0, "Book entity has non-zero id.");
				
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
					Transactional<List<Publication>> tx = Main.selectPublicationsTX(0, 1, title);
					List<Publication> publicationList = tx.transact(entityManager, REUSE);
					assertTrue(publicationList.size() > 0, "Publication with '" + title + "' should exist.");
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
