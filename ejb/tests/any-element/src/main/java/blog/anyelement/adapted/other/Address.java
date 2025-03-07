package blog.anyelement.adapted.other;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "address", propOrder = { "street", "city" })
public class Address implements Serializable
{
	private static final long serialVersionUID = 20250301L;
	
	@XmlElement(required = true)
	private String street;
	public String getStreet() { return street; }
	public void setStreet(String street) { this.street = street; }

	@XmlElement(required = true)
	private String city;
	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }
}