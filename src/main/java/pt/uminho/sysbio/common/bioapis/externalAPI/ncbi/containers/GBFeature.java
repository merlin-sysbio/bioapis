package pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="GBFeature", strict=false)
public class GBFeature {
	
	@Element(name="GBFeature_key", required=false)
	public String featureKey;
	
	@ElementList(name="GBFeature_quals")
	public List<GBQualifier> qualifiers = new ArrayList<> ();
	
}
