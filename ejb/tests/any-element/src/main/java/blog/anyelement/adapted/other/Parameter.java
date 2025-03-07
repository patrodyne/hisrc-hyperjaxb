package blog.anyelement.adapted.other;

import blog.anyelement.adapted.ParameterAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "name", "value" })
@XmlJavaTypeAdapter(ParameterAdapter.class)
@XmlRootElement
public class Parameter
{
	@XmlElement
	private String name;
	@XmlElement
	private Object value;
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public Object getValue() { return value; }
	public void setValue(Object value) { this.value = value; }
}