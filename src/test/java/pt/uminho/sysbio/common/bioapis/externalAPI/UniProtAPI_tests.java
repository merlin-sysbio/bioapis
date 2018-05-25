package pt.uminho.sysbio.common.bioapis.externalAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ebi.uniprot.UniProtAPI;

public class UniProtAPI_tests {

	public void uniTest(){
	
		System.out.println(UniProtAPI.getEntryData("CAG69663.1", 62977));
		//System.out.println(UniProtAPI.getEntryDataFromAccession("P07658"));
	}
	
	public void listTest(){
	
		List<String> l = new ArrayList<>();
		l.add("P07658");
		System.out.println(UniProtAPI.getEntriesFromUniProtIDs(l,0));
		
	}
	
	@Test	
	public void testUniprotStatus() throws Exception {
		
		List<String> refSeqIDs = new ArrayList<>();
		refSeqIDs.add("O31216.1");
		System.out.println(UniProtAPI.isStarred(UniProtAPI.getUniprotEntriesFromRefSeq(refSeqIDs, new AtomicBoolean(false), 2).get("O31216.1").get(0)));
	}

}
