package blog.anyelement.adapted;

import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static java.lang.String.format;

import java.io.File;

import blog.anyelement.adapted.other.Address;
import blog.anyelement.adapted.other.Method;
import blog.anyelement.adapted.other.Parameter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class Demo
{
	private static File INPUT_FILE = new File("src/main/samples/input.xml");
	
	public static void main(String[] args)
		throws Exception
	{
		JAXBContext jc = JAXBContext.newInstance(Method.class, Parameter.class, Address.class);
		
		// Create an unmarshaller with a JAXB adapter.
		ParameterAdapter adapter = new ParameterAdapter(jc);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setAdapter(adapter);
		
		// Unmarshal the input file into an action instance of Method type.
		Method action = (Method) unmarshaller.unmarshal(INPUT_FILE);
		
		println(format("Action Name: [%s]", action.getName()));
		for ( Parameter parm : action.getParameters() )
		{
			println(format("\tParameter Name..: [%s]", parm.getName()));
			println(format("\tParameter Value.: [%s]", parm.getValue()));
			if ( parm.getValue() instanceof Address )
			{
				Address address = (Address) parm.getValue();
				println(format("\t\tAddress Street.: [%s]", address.getStreet()));
				println(format("\t\tAddress City...: [%s]", address.getCity()));
			}
		}
		
		// Create a marshaller with the JAXB adapter with formatted output.
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setAdapter(adapter);
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
		
		// Marshal the action instance to the console.
		marshaller.marshal(action, System.out);
	}

	private static void println(String value)
	{
		System.out.println(value);
	}
}