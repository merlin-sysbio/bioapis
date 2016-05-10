package pt.uminho.sysbio.common.bioapis.externalAPI;

import org.junit.Test;

import pt.uminho.sysbio.common.bioapis.externalAPI.uniprot.UniProtAPI;

public class UniProtAPI_tests {



	@Test
	public void uniTest(){
	
		System.out.println(UniProtAPI.getEntryDataFromAccession("P07658"));
		
	}

}
