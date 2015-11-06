package pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="GBQualifier", strict=false)
public class GBQualifier {

	@Element(name="GBQualifier_name", required=false)
	public String name;
	
	@Element(name="GBQualifier_value", required=false)
	public String value;
	
}
