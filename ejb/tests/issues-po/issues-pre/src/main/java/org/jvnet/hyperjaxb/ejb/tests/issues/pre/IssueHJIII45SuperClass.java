package org.jvnet.hyperjaxb.ejb.tests.issues.pre;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.xml.bind.annotation.XmlElement;
import org.jvnet.basicjaxb.lang.Equals;
import org.jvnet.basicjaxb.lang.EqualsStrategy;
import org.jvnet.basicjaxb.lang.HashCode;
import org.jvnet.basicjaxb.lang.HashCodeStrategy;
import org.jvnet.basicjaxb.lang.JAXBEqualsStrategy;
import org.jvnet.basicjaxb.lang.JAXBHashCodeStrategy;
import org.jvnet.basicjaxb.locator.ObjectLocator;
import org.jvnet.basicjaxb.locator.util.LocatorUtils;


@MappedSuperclass
public abstract class IssueHJIII45SuperClass implements Serializable, Equals, HashCode{

    private static final long serialVersionUID = 7724857660567518243L;

	
    @XmlElement(name = "Id")
    @Id	
    @Column(name = "Id", updatable = false, nullable = false)
    private String id = "";
    

    public String getId() {
		return this.id;
	}
	
    
    private void setId(String uuid) {
		this.id = uuid;
	}
    
	@PrePersist
	private void prePersist() {
		if (getId().trim().length() == 0) {
			setId(UUID.randomUUID().toString());
		}
	}
	
	
	
	
    @Override
	public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof IssueHJIII45SuperClass)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final IssueHJIII45SuperClass that = ((IssueHJIII45SuperClass) object);
        {
            String lhsId;
            lhsId = this.getId();
            String rhsId;
            rhsId = that.getId();
            boolean lhsSet = (lhsId != null);
            boolean rhsSet = (rhsId != null);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "id", lhsId), LocatorUtils.property(thatLocator, "id", rhsId), lhsId, rhsId, lhsSet, rhsSet)) {
                return false;
            }
        }
        return true;
    }

    @Override
	public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.getInstance();
        return equals(null, null, object, strategy);
    }

    @Override
	public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        int currentHashCode = 1;
        {
            String theId;
            theId = this.getId();
            boolean valueSet = (theId != null);
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "id", theId), currentHashCode, theId, valueSet);
        }
        return currentHashCode;
    }

    @Override
	public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.getInstance();
        return this.hashCode(null, strategy);
    }


}

