package org.jvnet.hyperjaxb3.ejb.tests.issues.pre;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.xml.bind.annotation.XmlElement;
import org.jvnet.jaxb2_commons.lang.Equals2;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy2;
import org.jvnet.jaxb2_commons.lang.HashCode2;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy2;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;


@MappedSuperclass
public abstract class IssueHJIII45SuperClass implements Serializable, Equals2, HashCode2{

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
	
	
	
	
    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy2 strategy) {
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

    public boolean equals(Object object) {
        final EqualsStrategy2 strategy = JAXBEqualsStrategy.INSTANCE2;
        return equals(null, null, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy2 strategy) {
        int currentHashCode = 1;
        {
            String theId;
            theId = this.getId();
            boolean valueSet = (theId != null);
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "id", theId), currentHashCode, theId, valueSet);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy2 strategy = JAXBHashCodeStrategy.INSTANCE2;
        return this.hashCode(null, strategy);
    }


}

