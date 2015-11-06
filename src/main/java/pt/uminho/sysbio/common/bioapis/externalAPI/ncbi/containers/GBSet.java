package pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
	
@Root(name="GBSet", strict=false)
public class GBSet {
	
	@ElementList(name="GBSeq", inline=true)
	public List<GBSeq> gBSeq = new ArrayList<> ();
}
