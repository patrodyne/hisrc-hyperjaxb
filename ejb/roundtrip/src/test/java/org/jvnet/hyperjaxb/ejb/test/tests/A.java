package org.jvnet.hyperjaxb.ejb.test.tests;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jvnet.hyperjaxb.item.Item;
import org.jvnet.hyperjaxb.item.ItemUtils;
import org.jvnet.hyperjaxb.xml.bind.JAXBContextUtils;
import org.jvnet.hyperjaxb.xml.bind.JAXBElementUtils;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsDateTime;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XmlAdapterUtils;
import org.jvnet.basicjaxb.lang.Equals;
import org.jvnet.basicjaxb.lang.EqualsStrategy;
import org.jvnet.basicjaxb.lang.JAXBEqualsStrategy;
import org.jvnet.basicjaxb.locator.ObjectLocator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A", propOrder = { "id", "b", "b1", "b2", "d", "e", "eNillable", "f", "fNillable", "g", "h" })
@Entity
@Table(name = "table_a")
public class A implements Equals
{
	@XmlAttribute
	private String id;
	@Id
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	@Transient
	private boolean isIdSet() { return (id != null); }

	private B b;
	@ManyToOne(cascade = { CascadeType.ALL })
	public B getB() { return b; }
	public void setB(B b) { this.b = b; }
	@Transient
	private boolean isBSet() { return (b != null); }

	private List<B> b1 = new LinkedList<B>();
	@OneToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "A_B1")
	public List<B> getB1() { return b1; }
	public void setB1(List<B> b1) { this.b1 = b1; }
	@Transient
	private boolean isB1Set() { return (b1 != null); }

	private List<B> b2 = new LinkedList<B>();
	@OneToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "A_B2")
	public List<B> getB2() { return b2; }
	public void setB2(List<B> b2) { this.b2 = b2; }
	@Transient
	private boolean isB2Set() { return (b2 != null); }

	private String d;
	public String getD() { return d; }
	public void setD(String d) { this.d = d; }
	@Transient
	private boolean isDSet() { return (d != null); }

	protected List<String> e;
	@Transient
	public List<String> getE()
	{
		if (eItems == null)
			eItems = new ArrayList<EItem>();
		
		if (ItemUtils.shouldBeWrapped(e))
			e = ItemUtils.wrap(e, eItems, EItem.class);
		
		return this.e;
	}
	@Transient
	private boolean isESet() { return (e != null); }

	@XmlTransient
	private List<EItem> eItems;
	@OneToMany(cascade = CascadeType.ALL)
	public List<EItem> getEItems()
	{
		if (eItems == null)
			eItems = new ArrayList<EItem>();
		
		if (ItemUtils.shouldBeWrapped(e))
			e = ItemUtils.wrap(e, eItems, EItem.class);
		
		return this.eItems;
	}
	public void setEItems(List<EItem> eItems)
	{
		this.eItems = eItems;
		if (ItemUtils.shouldBeWrapped(e))
			e = ItemUtils.wrap(e, eItems, EItem.class);
	}
	@Transient
	private boolean isEItemsSet() { return (eItems != null); }

	@XmlElementRef(name = "eNillable", type = JAXBElement.class)
	protected List<JAXBElement<String>> eNillable;
	@Transient
	public List<JAXBElement<String>> getENillable()
	{
		if (eNillableItems == null)
			eNillableItems = new ArrayList<ENillableItem>();
		
		if (ItemUtils.shouldBeWrapped(eNillable))
			eNillable = ItemUtils.wrap(eNillable, eNillableItems, ENillableItem.class);
		
		return this.eNillable;
	}
	@Transient
	private boolean isENillableSet() { return (eNillable != null); }

	@XmlTransient
	private List<ENillableItem> eNillableItems;
	@OneToMany(cascade = CascadeType.ALL)
	public List<ENillableItem> getENillableItems()
	{
		if (eNillableItems == null)
			eNillableItems = new ArrayList<ENillableItem>();
		
		if (ItemUtils.shouldBeWrapped(eNillable))
			eNillable = ItemUtils.wrap(eNillable, eNillableItems, ENillableItem.class);
		
		return this.eNillableItems;
	}
	public void setENillableItems(List<ENillableItem> eNillableItems)
	{
		this.eNillableItems = eNillableItems;
		if (ItemUtils.shouldBeWrapped(eNillable))
			eNillable = ItemUtils.wrap(eNillable, eNillableItems, ENillableItem.class);
	}
	@Transient
	private boolean isENillableItemsSet() { return (eNillableItems != null); }

	protected List<XMLGregorianCalendar> f;
	@Transient
	public List<XMLGregorianCalendar> getF()
	{
		if (fItems == null)
			fItems = new ArrayList<FItem>();
		
		if (ItemUtils.shouldBeWrapped(f))
			f = ItemUtils.wrap(f, fItems, FItem.class);

		return this.f;
	}
	@Transient
	private boolean isFSet() { return (f != null); }

	@XmlTransient
	private List<FItem> fItems;
	@OneToMany(cascade = CascadeType.ALL)
	public List<FItem> getFItems()
	{
		if (fItems == null)
			fItems = new ArrayList<FItem>();
		
		if (ItemUtils.shouldBeWrapped(f))
			f = ItemUtils.wrap(f, fItems, FItem.class);
		
		return this.fItems;
	}
	public void setFItems(List<FItem> fItems)
	{
		this.fItems = fItems;
		if (ItemUtils.shouldBeWrapped(f))
			f = ItemUtils.wrap(f, fItems, FItem.class);
	}
	@Transient
	private boolean isFItemsSet() { return (fItems != null); }

	@XmlElementRef(name = "fNillable", type = JAXBElement.class)
	protected List<JAXBElement<XMLGregorianCalendar>> fNillable;
	@Transient
	public List<JAXBElement<XMLGregorianCalendar>> getFNillable()
	{
		if (fNillableItems == null)
			fNillableItems = new ArrayList<FNillableItem>();
		
		if (ItemUtils.shouldBeWrapped(fNillable))
			fNillable = ItemUtils.wrap(fNillable, fNillableItems, FNillableItem.class);
		
		return this.fNillable;
	}
	@Transient
	private boolean isFNillableSet() { return (fNillable != null); }

	@XmlTransient
	private List<FNillableItem> fNillableItems;
	@OneToMany(cascade = CascadeType.ALL)
	public List<FNillableItem> getFNillableItems()
	{
		if (fNillableItems == null)
			fNillableItems = new ArrayList<FNillableItem>();
		
		if (ItemUtils.shouldBeWrapped(fNillable))
			fNillable = ItemUtils.wrap(fNillable, fNillableItems, FNillableItem.class);
		
		return this.fNillableItems;
	}
	public void setFNillableItems(List<FNillableItem> fNillableItems)
	{
		this.fNillableItems = fNillableItems;
		if (ItemUtils.shouldBeWrapped(fNillable))
			fNillable = ItemUtils.wrap(fNillable, fNillableItems, FNillableItem.class);
	}
	@Transient
	private boolean isFNillableItemsSet() { return (fNillableItems != null); }

	@Override
	public boolean equals(Object obj)
	{
		return equals(null, null, obj, JAXBEqualsStrategy.getInstance());
	}

	@Override
	public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy)
	{
		if (!(object instanceof A))
			return false;
		
		if (this == object)
			return true;
		
		final A that = (A) object;
		return strategy.equals(thisLocator, thatLocator, this.getId(), that.getId(), this.isIdSet(), that.isIdSet())
			&& strategy.equals(thisLocator, thatLocator, this.getB(), that.getB(), this.isBSet(), that.isBSet())
			&& strategy.equals(thisLocator, thatLocator, this.getB1(), that.getB1(), this.isB1Set(), that.isB1Set())
			&& strategy.equals(thisLocator, thatLocator, this.getB2(), that.getB2(), this.isB2Set(), that.isB2Set())
			&& strategy.equals(thisLocator, thatLocator, this.getD(), that.getD(), this.isDSet(), that.isDSet())
			&& strategy.equals(thisLocator, thatLocator, this.getE(), that.getE(), this.isESet(), that.isESet())
			&& strategy.equals(thisLocator, thatLocator, this.getENillable(), that.getENillable(), this.isENillableSet(), that.isENillableSet())
			&& strategy.equals(thisLocator, thatLocator, this.getF(), that.getF(), this.isFSet(), that.isFSet())
			&& strategy.equals(thisLocator, thatLocator, this.getFNillable(), that.getFNillable(), this.isFNillableSet(), that.isFNillableSet())
			&& strategy.equals(thisLocator, thatLocator, this.getG(), that.getG(), this.isGSet(), that.isGSet());
	}

	@Override
	public int hashCode()
	{
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "EItem")
	@Entity
	@Table(name = "A_EITEM")
	public static class EItem extends PrimitiveItem<String, String>
	{
		@XmlAttribute
		protected Long hjid;
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		public Long getHjid() { return hjid; }
		public void setHjid(Long value) { this.hjid = value; }

		@Override
		@Transient
		public String getItem() { return getValue(); }
		@Override
		public void setItem(String value) { setValue(value); }
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "ENillableItem")
	@Entity
	@Table(name = "A_ENILLABLEITEM")
	public static class ENillableItem extends PrimitiveItem<String, JAXBElement<String>>
	{
		@XmlAttribute
		protected Long hjid;
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		public Long getHjid() { return hjid; }
		public void setHjid(Long value) { this.hjid = value; }

		@Override
		@Transient
		public JAXBElement<String> getItem()
		{
			return XmlAdapterUtils.marshallJAXBElement(String.class, new QName("eNillable"), A.class, getValue());
		}
		@Override
		public void setItem(JAXBElement<String> value)
		{
			setValue(XmlAdapterUtils.unmarshallJAXBElement(value));
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "FItem")
	@Entity
	@Table(name = "A_FITEM")
	public static class FItem extends PrimitiveItem<Date, XMLGregorianCalendar>
	{
		@XmlAttribute
		protected Long hjid;
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		public Long getHjid() { return hjid; }
		public void setHjid(Long value) { this.hjid = value; }

		@Temporal(TemporalType.TIMESTAMP)
		public Date getItemAsDate()
		{
			return XmlAdapterUtils.unmarshall(XMLGregorianCalendarAsDateTime.class, this.getItem());
		}
		public void setItemAsDate(Date target)
		{
			this.setItem(XmlAdapterUtils.marshall(XMLGregorianCalendarAsDateTime.class, target));
		}

		@Override
		@Transient
		public XMLGregorianCalendar getItem()
		{
			return XmlAdapterUtils.marshall(XMLGregorianCalendarAsDateTime.class, getValue());
		}
		@Override
		public void setItem(XMLGregorianCalendar value)
		{
			setValue(XmlAdapterUtils.unmarshall(XMLGregorianCalendarAsDateTime.class, value));
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "FItemNillable")
	@Entity
	@Table(name = "A_FNILLABLEITEM")
	public static class FNillableItem extends PrimitiveItem<Date, JAXBElement<XMLGregorianCalendar>>
	{
		@XmlAttribute
		protected Long hjid;
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		public Long getHjid() { return hjid; }
		public void setHjid(Long value) { this.hjid = value; }

		@Temporal(TemporalType.TIMESTAMP)
		public Date getItemAsDate()
		{
			return XmlAdapterUtils.unmarshallJAXBElement(XMLGregorianCalendarAsDateTime.class, this.getItem());
		}
		public void setItemAsDate(Date target)
		{
			this.setItem(XmlAdapterUtils.marshallJAXBElement(XMLGregorianCalendarAsDateTime.class,
				XMLGregorianCalendar.class, new QName("fNillable"), A.class, target));
		}

		@Override
		@Transient
		public JAXBElement<XMLGregorianCalendar> getItem()
		{
			return XmlAdapterUtils.marshallJAXBElement(XMLGregorianCalendarAsDateTime.class, XMLGregorianCalendar.class,
				new QName("fNillable"), A.class, getValue());
		}
		@Override
		public void setItem(JAXBElement<XMLGregorianCalendar> value)
		{
			setValue(XmlAdapterUtils.unmarshallJAXBElement(XMLGregorianCalendarAsDateTime.class, value));
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "G", propOrder = { "h" })
	@XmlSeeAlso({ G1.class, G2.class })
	@Entity
	@Table(name = "A_G")
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class G implements Equals
	{
		@XmlAttribute
		protected Long hjid;
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		public Long getHjid() { return hjid; }
		public void setHjid(Long value) { this.hjid = value; }

		private String h;
		@Basic
		public String getH() { return h; }
		public void setH(String h) { this.h = h; }
		@Transient
		private boolean isHSet() { return (h != null); }

		@Override
		public boolean equals(Object obj)
		{
			return equals(null, null, obj, JAXBEqualsStrategy.getInstance());
		}

		@Override
		public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator,
			Object object, EqualsStrategy strategy)
		{
			if (!(object instanceof G))
				return false;
			
			if (this == object)
				return true;
			
			final G that = (G) object;
			return strategy.equals(thisLocator, thatLocator, this.getH(), that.getH(), this.isHSet(), that.isHSet());
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "G1", propOrder = { "h1" })
	@Entity
	@Table(name = "A_G1")
	public static class G1 extends G
	{
		private String h1;
		@Basic
		public String getH1() { return h1; }
		public void setH1(String h1) { this.h1 = h1; }
		@Transient
		private boolean isH1Set() { return (h1 != null); }

		@Override
		public boolean equals(Object obj)
		{
			return equals(null, null, obj, JAXBEqualsStrategy.getInstance());
		}

		@Override
		public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy)
		{
			if (!(object instanceof G1))
				return false;
			
			if (this == object)
				return true;
			
			final G1 that = (G1) object;
			return super.equals(thisLocator, thatLocator, object, strategy)
				&& strategy.equals(thisLocator, thatLocator, this.getH1(), that.getH1(), this.isH1Set(), that.isH1Set());
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "g2", propOrder = { "h2" })
	@Entity
	@Table(name = "A_G2")
	public static class G2 extends G
	{
		private String h2;
		@Basic
		public String getH2() { return h2; }
		public void setH2(String h2) { this.h2 = h2; }
		@Transient
		private boolean isH2Set() { return (h2 != null); }

		@Override
		public boolean equals(Object obj)
		{
			return equals(null, null, obj, JAXBEqualsStrategy.getInstance());
		}

		@Override
		public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator,
			Object object, EqualsStrategy strategy)
		{
			if (!(object instanceof G2))
				return false;
			
			if (this == object)
				return true;
			
			final G2 that = (G2) object;
			return super.equals(thisLocator, thatLocator, object, strategy)
				&& strategy.equals(thisLocator, thatLocator, this.getH2(), that.getH2(), this.isH2Set(), that.isH2Set());
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "GItem")
	@Entity
	@Table(name = "A_GITEM")
	public static class GItem implements Item<JAXBElement<? extends G>>, Equals
	{
		@XmlAttribute
		protected Long hjid;
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		public Long getHjid() { return hjid; }
		public void setHjid(Long value) { this.hjid = value; }

		private String itemName;
		@Basic
		public String getItemName() { return itemName; }
		public void setItemName(String name) { this.itemName = name; }

		private G itemValue;
		@ManyToOne(cascade = { CascadeType.ALL })
		public G getItemValue() { return itemValue; }
		public void setItemValue(G g) { this.itemValue = g; }
		@Transient
		private boolean isItemValueSet() { return (itemValue != null); }

		private JAXBElement<? extends G> item;
		@Override
		@Transient
		public JAXBElement<? extends G> getItem()
		{
			if (JAXBElementUtils.shouldBeWrapped(item, itemName, itemValue))
				item = JAXBElementUtils.wrap(item, itemName, itemValue);
			return item;
		}
		@Override
		public void setItem(JAXBElement<? extends G> value)
		{
			this.itemName = value.getName().toString();
			this.itemValue = value.getValue();
			this.item = value;
		}
		@Transient
		private boolean isItemSet() { return (item != null); }

		@Override
		public int hashCode()
		{
			return HashCodeBuilder.reflectionHashCode(this);
		}

		@Override
		public boolean equals(Object obj)
		{
			return equals(null, null, obj, JAXBEqualsStrategy.getInstance());
		}

		@Override
		public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator,
			Object object, EqualsStrategy strategy)
		{
			if (!(object instanceof GItem))
				return false;
			
			if (this == object)
				return true;
			
			final GItem that = (GItem) object;
			return strategy.equals(thisLocator, thatLocator,
				this.getItemValue(), that.getItemValue(),
				this.isItemValueSet(), that.isItemValueSet());
		}
	}

	@XmlElementRef(name = "g", namespace = "", type = JAXBElement.class)
	protected List<JAXBElement<? extends G>> g;
	@Transient
	public List<JAXBElement<? extends G>> getG()
	{
		if (gItems == null)
			gItems = new ArrayList<GItem>();
		
		if (ItemUtils.shouldBeWrapped(g))
			g = ItemUtils.wrap(g, gItems, GItem.class);
		
		return this.g;
	}
	@Transient
	private boolean isGSet() { return (g != null); }

	@XmlTransient
	private List<GItem> gItems;
	@OneToMany(cascade = CascadeType.ALL)
	public List<GItem> getGItems()
	{
		if (gItems == null)
			gItems = new ArrayList<GItem>();
		
		if (ItemUtils.shouldBeWrapped(g))
			g = ItemUtils.wrap(g, gItems, GItem.class);

		return this.gItems;
	}
	public void setGItems(List<GItem> gItems)
	{
		this.gItems = gItems;
		if (ItemUtils.shouldBeWrapped(g))
			g = ItemUtils.wrap(g, gItems, GItem.class);
	}
	@Transient
	private boolean isGItemsSet() { return (gItems != null); }

	@XmlAnyElement(lax = true)
	protected Object h;
	@Transient
	public Object getH() { return h; }
	public void setH(Object h) { this.h = h; }
	@Transient
	private boolean isHSet() { return (h != null); }

	@Basic
	public String getHItem()
	{
		return JAXBContextUtils.marshal("org.jvnet.hyperjaxb.ejb.test.tests", getH());
	}
	public void setHItem(String h)
	{
		setH(JAXBContextUtils.unmarshal("org.jvnet.hyperjaxb.ejb.test.tests", h));
	}
}
