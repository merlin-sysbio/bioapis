package pt.uminho.sysbio.common.bioapis.externalAPI;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pt.uminho.sysbio.common.bioapis.externalAPI.ebi.uniprot.UniProtAPI;

public class UniProtAPI_tests {



	//@Test
	public void uniTest(){
	
		System.out.println(UniProtAPI.getEntryDataFromAccession("P07658"));
		
	}
	
	@Test
	public void listTest(){
	
		List<String> l = new ArrayList<>();
		l.add("P07658");
		System.out.println(UniProtAPI.getEntriesFromUniProtIDs(l,0));
		
	}

}
