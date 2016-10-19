package pt.uminho.sysbio.common.bioapis.externalAPI;

import org.junit.Test;

import pt.uminho.sysbio.common.bioapis.externalAPI.kegg.KeggAPI;
import pt.uminho.sysbio.common.bioapis.externalAPI.kegg.KeggOperation;
import pt.uminho.sysbio.common.bioapis.externalAPI.kegg.KeggRestful;

public class KeggTests {

	@Test
	public void test() throws Exception {
		
		System.out.println(KeggAPI.getProduct("PP_0001"));
	}

	//@Test
	public void main() throws Exception {
		
		System.out.println(KeggRestful.fetch(KeggOperation.get, "cpd:C15025"));
		KeggRestful.fetch(KeggOperation.get, "cpd:C15025");
	}
}
