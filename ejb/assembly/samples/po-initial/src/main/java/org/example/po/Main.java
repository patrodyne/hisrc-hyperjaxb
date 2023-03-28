package org.example.po;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.jvnet.hyperjaxb.ejb.util.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.xml.bind.JAXBException;

/**
 * Example of unmarshalling and persisting a PurchaseOrder
 */
public class Main extends Context
{
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	public static Logger getLogger() { return logger; }

	public static final String SAMPLE_PO_FILE = "src/test/samples/po.xml";
	
    // Represents the unmarshalled PurchaseOrder.
    private PurchaseOrder purchaseOrder = null;
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }
	
	/**
	 * Command Line Invocation.
	 * 
	 * @param args CLI argument(s)
	 */
	public static void main(String[] args)
	{
		String xmlFileName = args.length > 0 ? args[0] : SAMPLE_PO_FILE;
		try
		{
			Main main = new Main();
			main.execute(xmlFileName);
		}
		catch (JAXBException | IOException ex)
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
	 */
	public void execute(String xmlFileName) throws JAXBException, IOException
	{
		// JAXB: unmarshal XML file by path name.
        setPurchaseOrder(unmarshal(xmlFileName, PurchaseOrder.class));
        getLogger().info("PurchaseOrder: {}\n\n{}\n", getPurchaseOrder().getOrderDate(), getPurchaseOrder());

        // Select Purchase Order(s) by order date.
        List<PurchaseOrder> purchaseOrderList = selectPurchaseOrders(0, 10, getPurchaseOrder().getOrderDateItem());

        // Persist Purchase Order, if missing.
        if ( purchaseOrderList.isEmpty() )
        {
    		// JPA: persist PurchaseOrder to database.
            persist(getPurchaseOrder());
            // Again, select Purchase Order(s) by order date.
            purchaseOrderList = selectPurchaseOrders(0, 1, getPurchaseOrder().getOrderDateItem());
        }

        // Display Purchase Order(s).
        for ( PurchaseOrder purchaseOrder : purchaseOrderList )
        {
			// JAXB: marshal PurchaseOrder.
            String xmlPurchaseOrder = marshalToString(purchaseOrder);
			getLogger().info("PurchaseOrder:\n\n{}", xmlPurchaseOrder);
        }
	}
	
	private List<PurchaseOrder> selectPurchaseOrders(Integer start, Integer count, Date orderDate)
		throws IOException
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<List<PurchaseOrder>> tx = (em) ->
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PurchaseOrder> cq = cb.createQuery(PurchaseOrder.class);
			Root<PurchaseOrder> queryRoot = cq.from(PurchaseOrder.class);
			
			cq.select(queryRoot)
				.where(cb.equal(queryRoot.get(PurchaseOrder_.ORDER_DATE_ITEM), orderDate));
			
			TypedQuery<PurchaseOrder> query = em.createQuery(cq);
			query.setHint("org.hibernate.cacheable", false);
			List<PurchaseOrder> entities = query
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();

			return entities;
		};
		List<PurchaseOrder> purchaseOrderList = tx.transact(createEntityManager());
		return purchaseOrderList;
	}
	
	private void persist(PurchaseOrder po)
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
			getLogger().info("Persisted {} {}(s)", count, PurchaseOrder.class.getSimpleName());
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
