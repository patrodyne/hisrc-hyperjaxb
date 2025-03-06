package blog.anyelement.adapted.other;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "name", "parameters" })
@XmlRootElement
public class Method
{
    @XmlAttribute
    private String name;
    @XmlAnyElement
    private List<Parameter> parameters;
    
    public String getName()
    {
    	return name;
    }
    public void setName(String name)
    {
    	this.name = name;
    }

    public List<Parameter> getParameters()
    {
        return parameters;
    }
    public void setParameters(List<Parameter> parameters)
    {
        this.parameters = parameters;
    }
}