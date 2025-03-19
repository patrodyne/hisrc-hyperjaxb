package org.example.embeddable_jpa_batch;

import static java.lang.String.format;
import static org.jvnet.hyperjaxb.ejb.util.Transactional.CacheOption.CLEAN;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jvnet.hyperjaxb.ejb.util.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.stackoverflow.embeddable_jpa_batch.AnEnum;
import com.stackoverflow.embeddable_jpa_batch.MyEntity;
import com.stackoverflow.embeddable_jpa_batch.MyEntityBatch;
import com.stackoverflow.embeddable_jpa_batch.MyEntityBatch_;
import com.stackoverflow.embeddable_jpa_batch.MyPk;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.xml.bind.JAXBException;

/**
 * Example of unmarshalling and persisting MyEntity instances.
 * 
 * Hint: In Eclipse, disable the check box for:
 *       'Run Configuration / Dependencies / exclude test code'.
 */
public class Main extends Context
{
	public static final String SAMPLE_BATCH_FILE = "src/test/samples/batch01.xml";
	public static final int SAMPLE_BATCH_COUNT = 2;
	public static final int SAMPLE_BATCH_SIZE = 20;
	private static final String SAMPLE_DATA = "xx1";;
	
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	public static Logger getLogger() { return logger; }

    // Represents the unmarshalled batch.
    private MyEntityBatch batch = null;
    public MyEntityBatch getBatch() { return batch; }
    public void setBatch(MyEntityBatch batch) { this.batch = batch; }

    // Represents a list of batches
    private List<MyEntityBatch> batchList = null;
	public List<MyEntityBatch> getBatchList()
	{
		if ( batchList == null )
			setBatchList(new ArrayList<>());
		return batchList;
	}
	public void setBatchList(List<MyEntityBatch> batchList)
	{
		this.batchList = batchList;
	}
	
	/**
	 * Command Line Invocation.
	 * 
	 * @param args CLI argument(s)
	 */
	public static void main(String[] args)
	{
		try
		{
			Main main = new Main();
			for ( String command : args )
			{
				switch (command)
				{
					case "connect1": main.connect1(); break;
					case "sample1": main.sample1(SAMPLE_BATCH_COUNT, SAMPLE_BATCH_SIZE); break;
					case "insert1": main.insert1(); break;
					case "insert2": main.insert2(); break;
					case "execute0": 
					default: main.execute0(SAMPLE_BATCH_FILE); break;
				}
			}
		}
		catch (JAXBException | IOException | SAXException ex)
		{
			getLogger().error("Aborting " + Main.class.getName(), ex);
		}
	}
	
	/**
	 * Connect 1, connect to the database.
	 * 
	 * @throws IOException When the connection cannot be established.
	 */
	public void connect1() throws IOException
	{
		// Prepare a transaction to select the current date and time.
		Transactional<Integer> tx = (em) ->
		{
			Query query = em.createQuery("select CURRENT_TIMESTAMP");
			Timestamp result = (Timestamp) query.getSingleResult();
			getLogger().info("connect1: Current Time is {}", result);
		    return 0;
		};
		
		perform("connect1", tx);
	}
	
	/**
	 * Sample 1, generate a {@link MyEntityBatch}. Store result
	 * in the {@code batch} property.
	 * 
	 * @param batchCount The number of {@link MyEntityBatch} to generate.
	 * @param batchSize The number of {@link MyEntity} to generate.
	 */
	public void sample1(int batchCount, int batchSize)
	{
		IdFactory idFactory = new IdFactory();
		for ( int batchNo=0; batchNo < batchCount; ++batchNo )
		{
			setBatch(new MyEntityBatch().useId(format("batch%04d",batchNo+1)));
			for ( int index=0; index < batchSize; ++index)
			{
				MyPk id = new MyPk(idFactory.nextId(), AnEnum.I, true);
				getBatch().getMyEntity().add(new MyEntity(SAMPLE_DATA, id, true));
			}
			getBatchList().add(getBatch());
		}
	}
	
	/**
	 * Insert 1, persist a list of {@link MyEntityBatch}.
	 * 
	 * @throws IOException When batch cannot be persisted.
	 */
	public void insert1() throws IOException
	{
		long ms1 = System.currentTimeMillis();
		for ( MyEntityBatch batch : getBatchList() )
			persist(batch);
		long ms2 = System.currentTimeMillis();
		long tot = ms2 - ms1;
		long cnt = getBatchList().size();
		double avg = (double) tot / (double) cnt;
		String id = "ALL";
		getLogger().info(format("%s: cnt=%d; avg=%.4f ms; tot=%d ms", id, cnt, avg, tot));
	}

	/**
	 * Insert 2, persist a list of {@link MyEntity}.
	 * 
	 * @throws IOException When entities cannot be persisted.
	 */
	public void insert2() throws IOException
	{
		long ms1 = System.currentTimeMillis();
		for ( MyEntityBatch batch : getBatchList() )
			persist(batch.getId(), batch.getMyEntity());
		long ms2 = System.currentTimeMillis();
		long tot = ms2 - ms1;
		long cnt = getBatchList().size();
		double avg = (double) tot / (double) cnt;
		String id = "ALL";
		getLogger().info(format("%s: cnt=%d; avg=%.4f ms; tot=%d ms", id, cnt, avg, tot));
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
	public void execute0(String xmlFileName) throws JAXBException, IOException, SAXException
	{
		// Enable JAXB schema validation on XML instances.
		// generateXmlSchemaValidatorFromDom();
		
		// JAXB: unmarshal XML file by path name.
        setBatch(unmarshal(xmlFileName, MyEntityBatch.class));
        getLogger().info("Batch: {}\n\n{}\n", getBatch().getId());

        // Select Batch(s) by batch id.
        List<MyEntityBatch> batchList = selectBatches(0, 10, getBatch().getId());

        // Persist Batch, if missing.
        if ( batchList.isEmpty() )
        {
    		// JPA: persist Batch to database.
            persist(getBatch());
            // Again, select Batch(es) by id.
            batchList = selectBatches(0, 1, getBatch().getId());
        }

        // Display Batch(es) outside transaction,
        // entities were eager loaded using an inner join.
        Set<MyEntity> entitySet = new HashSet<>();
        for ( MyEntityBatch batch : batchList )
        {
        	// Build set of entities.
        	entitySet.addAll(batch.getMyEntity());
			// JAXB: marshal Batch.
        	String xmlBatch = marshalToString(batch);
			getLogger().info("Batch:\n\n{}", xmlBatch);
        }
        
        // Display Entities(s) in a transaction to avoid LazyInitializationException.
        int count = displayEntities(entitySet);
        getLogger().info("Entities displayed: {}", count);
	}
	
	/**
	 * Display entities for the given detached set of entities.
	 * 
	 * @param entitySet A detached set of entities.
	 * 
	 * @return The count of entities displayed.
	 */
	protected int displayEntities(Set<MyEntity> entitySet) throws IOException
	{
		Transactional<Integer> tx = displayEntitiesTX(entitySet);
		// Auto-close transactional session.
		try ( EntityManager em = createEntityManager() )
		{
			return tx.transact(em);
		}
	}

	private Transactional<Integer> displayEntitiesTX(Set<MyEntity> entitySet)
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<Integer> tx = (em) ->
		{
			Integer count = 0;
			try
			{
		        count = entitySet.size();
		        for ( MyEntity entity : entitySet )
		        {
		        	// Find the managed entity using the current EM.
		        	MyEntity emEntity = em.find(MyEntity.class, entity.getId());
		        	
		        	String xmlEntity = marshalToString(OF.createMyEntity(emEntity));
					getLogger().info("Entity:\n\n{}", xmlEntity);
		        }
			}
			catch (JAXBException | IOException ex)
			{
				count = -1;
				getLogger().error("displayEntitiesTX cannot be marshaled", ex);
			}
			return count;
		};
		return tx;
	}
	
	/**
	 * Select a limited list of batch(es) for the given id.
	 * 
	 * @param start The starting offset.
	 * @param count The count limit.
	 * @param id The batch title.
	 * 
	 * @return A list of batch(es).
	 * 
	 * @throws IOException When the list cannot be selected.
	 */
	protected List<MyEntityBatch> selectBatches(Integer start, Integer count, String id)
		throws IOException
	{
		Transactional<List<MyEntityBatch>> tx = selectBatchesTX(start, count, id);
		// Auto-close transactional session.
		List<MyEntityBatch> batchList;
		try ( EntityManager em = createEntityManager() )
		{
			batchList = tx.transact(em);
		}
		return batchList;
	}
	
	protected static Transactional<List<MyEntityBatch>> selectBatchesTX(Integer start, Integer count, String id)
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<List<MyEntityBatch>> tx = (em) ->
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MyEntityBatch> cq = cb.createQuery(MyEntityBatch.class);
			
			// Force eager loading of entities using an inner join.
			// See https://thorben-janssen.com/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
			Root<MyEntityBatch> fromBatch = cq.from(MyEntityBatch.class);
			fromBatch.fetch(MyEntityBatch_.MY_ENTITY, JoinType.INNER);
			
			cq.select(fromBatch)
				.where(cb.equal(fromBatch.get(MyEntityBatch_.ID), id));
			
			TypedQuery<MyEntityBatch> query = em.createQuery(cq);
			query.setHint("eclipselink.query-results-cache", false);
			query.setHint("org.hibernate.cacheable", false);
			List<MyEntityBatch> entities = query
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();

			return entities;
		};
		return tx;
	}
	
	private void persist(MyEntityBatch batch)
		throws IOException
	{
		// The batch is new if it contain at least one new entity.
		batch.setNew(false);
		for ( MyEntity entity : batch.getMyEntity() )
		{
			if ( entity.getNew() )
			{
				batch.setNew(true);
				break;
			}
		}
		
		// Prepare the transaction to persist or merge one batch.
		Transactional<Integer> tx = (em) ->
		{
			if ( batch.getNew() )
				em.persist(batch);
			else
				em.merge(batch);
		    return batch.getMyEntity().size() + 1;
		};
		
		// Perform the transaction
		perform(batch.getId(), tx);
	}
	
	private void persist(String batchName, List<MyEntity> entityList)
		throws IOException
	{
		// Prepare the transaction to persist or merge one batch.
		Transactional<Integer> tx = (em) ->
		{
			for ( MyEntity entity : entityList )
			{
				if ( entity.getNew() )
					em.persist(entity);
				else
					em.merge(entity);
			}
		    return entityList.size();
		};
		
		// Perform the transaction
		perform(batchName, tx);
	}
	
	private void perform(String id, Transactional<Integer> tx)
		throws IOException
	{
		// Execute the transaction with auto-close.
		long cnt = 0;
		long ms1 = System.currentTimeMillis();
		try ( EntityManager em = createEntityManager() )
		{
		    cnt = tx.transact(em, CLEAN);
		}
		long ms2 = System.currentTimeMillis();
		long tot = ms2 - ms1;
		double avg = (cnt != 0) ? (double) tot / (double) cnt : tot;
		getLogger().info(format("%s: cnt=%d; avg=%.4f ms; tot=%d ms", id, cnt, avg, tot));
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
