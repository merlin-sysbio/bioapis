package pt.uminho.sysbio.common.bioapis.externalAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.junit.Test;

import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.NcbiAPI;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.NcbiTaxonStub_API;
import pt.uminho.sysbio.common.bioapis.externalAPI.uniprot.EntryData;
import pt.uminho.sysbio.common.bioapis.externalAPI.uniprot.UniProtAPI;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;
import uk.ac.ebi.kraken.uuw.services.remoting.Query;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;

public class TestBioapis{

	//	@Test
	//	public void uni() throws Exception{
	//		
	//		NcbiTaxonStub_API ncsa = new NcbiTaxonStub_API(10);
	//		Map<String,String[]> ncbi_ids= ncsa.getTaxonList(393595+"", 0);
	//		System.out.println(ncbi_ids);
	//	}

	//	@Test
	//	public void getLocusTest() throws Exception{
	//		
	//		Map<String, ProteinSequence> genome = FastaReaderHelper.readFastaProteinSequence(new File("D:/Dropbox/WORK/ProjectoRita/files/genome_a_adp1_1.faa"));
	//		genome = NcbiAPI.getNCBILocusTags(genome);
	//		
	//		System.out.println(genome.keySet());
	//	}

	
	@Test
	public void s() {
		
		EntryData entryData = UniProtAPI.getEntryData("NP_116711.1");
		System.out.println(entryData.getLocusTag());
	}
	
	//@Test
	public void t(){
		
		List<String> uniprotIDs = new ArrayList<String>();
		uniprotIDs.add("D4GW84");
		uniprotIDs.add("D4GW84");
		uniprotIDs.add("M0HTC3");
		uniprotIDs.add("M0H6T8");
		uniprotIDs.add("M0G1U9");
		uniprotIDs.add("M0F7V8");
		uniprotIDs.add("M0G0J8");
		uniprotIDs.add("M0I4K3");
		uniprotIDs.add("M0J778");
		uniprotIDs.add("M0G5K5");
		uniprotIDs.add("M0IBJ7");
		uniprotIDs.add("M0HBN8");
		uniprotIDs.add("I3R868");
		uniprotIDs.add("M0H497");
		uniprotIDs.add("M0HH44");
		uniprotIDs.add("E4NR62");
		uniprotIDs.add("E7QS61");
		uniprotIDs.add("G5C4L3");
		uniprotIDs.add("W5NYH2");
		uniprotIDs.add("L8IY38");
		uniprotIDs.add("G3WNM0");
		uniprotIDs.add("F7H6X2");
		uniprotIDs.add("Q32Q12");
		uniprotIDs.add("M0KTZ5");
		uniprotIDs.add("M0JIB6");
		uniprotIDs.add("D8J308");
		uniprotIDs.add("E9PZF0");
		uniprotIDs.add("M0KLS2");
		uniprotIDs.add("Q401C5");
		uniprotIDs.add("F6YGJ2");
		uniprotIDs.add("F6WF21");
		uniprotIDs.add("M0KJ03");
		uniprotIDs.add("G0HW59");
		uniprotIDs.add("V5TJX6");
		uniprotIDs.add("G7NHN3");
		uniprotIDs.add("E2RC20");
		uniprotIDs.add("M0LD49");
		uniprotIDs.add("G1LN32");
		uniprotIDs.add("M0JYC7");
		uniprotIDs.add("Q401C6");
		uniprotIDs.add("Q5V5M1");
		uniprotIDs.add("Q75U63");
		uniprotIDs.add("Q75U59");
		uniprotIDs.add("Q75U62");
		uniprotIDs.add("C7NZG2");
		uniprotIDs.add("Q75U57");
		uniprotIDs.add("Q75U64");
		uniprotIDs.add("Q75U61");
		uniprotIDs.add("G1KAF8");
		uniprotIDs.add("Q75U60");
		uniprotIDs.add("Q75U58");
		uniprotIDs.add("K7G5Z3");
		uniprotIDs.add("R4VSS2");
		uniprotIDs.add("J3JF97");
		uniprotIDs.add("I3LY75");
		uniprotIDs.add("H0ZGE2");
		uniprotIDs.add("M0DJ68");
		uniprotIDs.add("W5KKR1");
		uniprotIDs.add("U3JFA9");
		uniprotIDs.add("U3I4Q4");
		uniprotIDs.add("Q75WL0");
		uniprotIDs.add("R0LU38");
		uniprotIDs.add("M7BLZ6");
		uniprotIDs.add("D2HTU0");
		uniprotIDs.add("M0E928");
		uniprotIDs.add("M0FAZ3");
		uniprotIDs.add("M0NN01");
		uniprotIDs.add("M0DC48");
		uniprotIDs.add("M0F5K4");
		uniprotIDs.add("M0PEV6");
		uniprotIDs.add("V6DYF1");
		uniprotIDs.add("L5NES0");
		uniprotIDs.add("Q75WL4");
		uniprotIDs.add("Q75WL2");
		uniprotIDs.add("V4XUZ0");
		uniprotIDs.add("M0PCV0");
		uniprotIDs.add("B9LPY5");
		uniprotIDs.add("M0FBW0");
		uniprotIDs.add("U1PKE7");
		uniprotIDs.add("M0ESG9");
		uniprotIDs.add("Q75WL3");
		uniprotIDs.add("M0P585");
		uniprotIDs.add("M0E1G1");
		uniprotIDs.add("M0NHF3");
		uniprotIDs.add("V4J0V8");
		uniprotIDs.add("W0JZV4");
		uniprotIDs.add("U1R130");
		uniprotIDs.add("M0DZY1");
		uniprotIDs.add("Q18GB1");
		uniprotIDs.add("F7PP39");
		uniprotIDs.add("U1PMX2");
		uniprotIDs.add("Q75WL5");
		uniprotIDs.add("Q3IPM6");
		uniprotIDs.add("C7NTU9");
		uniprotIDs.add("U2YEX4");
		uniprotIDs.add("Q75WK9");
		uniprotIDs.add("U1P6G3");
		uniprotIDs.add("G2MM24");
		uniprotIDs.add("U1MRT0");
		uniprotIDs.add("B0R502");
		uniprotIDs.add("V4XDJ0");
		
		UniProtQueryService uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
		
		Query query = UniProtQueryBuilder.buildIDListQuery(uniprotIDs);
		
		EntryIterator<UniProtEntry> entries = uniProtQueryService.getEntryIterator(query);
		
		for (UniProtEntry entry : entries) {

			String primary_accession = entry.getPrimaryUniProtAccession().getValue();
		System.out.println(primary_accession);	
		}
	}
	

	/**
	 * 
	 */
	//@Test
	public void getEntry() {
		
		List<String> uniprotIDs = new ArrayList<String>();
		uniprotIDs.add("AGI21893.1");
		uniprotIDs.add("AGI21889");
		uniprotIDs.add("AGI21891");
		uniprotIDs.add("T2E8P7");
		uniprotIDs.add("Q9I0B1");
		uniprotIDs.add("T2ER45");
		uniprotIDs.add("M2ADX4");
		
		Query query = UniProtQueryBuilder.buildIDListQuery(uniprotIDs);
		
		System.out.println("query "+query);
		
		UniProtQueryService uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();

		EntryIterator<UniProtEntry> entries = uniProtQueryService.getEntryIterator(query);

		for (UniProtEntry entry : entries) {

			String locus = null;
			
			System.out.println(entry.getUniProtId()+" "+entry.getGenes().size());

			if(entry.getGenes().get(0).getOrderedLocusNames().size()>0) {
				
				locus = entry.getGenes().get(0).getOrderedLocusNames().get(0).getValue();
				System.out.println("oln "+entry.getGenes().get(0).getOrderedLocusNames().get(0).getValue());
			}

			if(locus==null) {

				if(entry.getGenes().get(0).getORFNames().size()>0) {
					
					locus = entry.getGenes().get(0).getORFNames().get(0).getValue();
					System.out.println("orf "+entry.getGenes().get(0).getORFNames().get(0).getValue());
				}

			}
			System.out.println("\t"+locus);
		}
	}
	
	//@Test
	public void  getEntryTest() {
		
		String query = "AGI21890.1";
				
		EntryData entryData = UniProtAPI.getEntryData(query);
		System.out.println(entryData.getEcnumber());
		System.out.println(entryData.getUniprotReviewStatus());
		System.out.println(entryData.getEntryID());
		System.out.println(entryData.getLocusTag());
	}

	
	public void getTaxa() throws Exception{

		String t = "1544797";
		NcbiTaxonStub_API ncsa = new NcbiTaxonStub_API(10);

		Map<String,String[]> ncbi_ids = ncsa.getTaxonList(t);

		for (String k : ncbi_ids.keySet()) {

			System.out.println(k);

			for (String s : ncbi_ids.get(k))
				System.out.println("\t"+s);
		}
	}

	//	@Test
	//	public void uni(){
	//		
	//		EntryData a = UniProtAPI.getEntryData("NP_418195");
	//		
	//		System.out.println(a);
	//	}

	//@Test
	//	public void tests() {
	//		   // Create UniProt query service
	//	    UniProtQueryService uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
	//	    
	//	    //Create a list of accession numbers (both primary and seconday are acceptable)
	//	    List<String> accList = new ArrayList<String>();
	//	    accList.add("O60243");
	//	    accList.add("Q8IZP7");
	//	    accList.add("P02070");
	//	    //Isoform IDs are acceptable as well 
	//	    accList.add("Q4R572-1");
	//	    //as well as entry IDs 
	//	    accList.add("14310_ARATH");
	//	    
	//	    Query query = UniProtQueryBuilder.buildIDListQuery(accList);
	//	    
	//	    EntryIterator<UniProtEntry> entries = uniProtQueryService.getEntryIterator(query);
	//	    for (UniProtEntry entry : entries) {
	//	        System.out.println("entry.getUniProtId() = " + entry.getUniProtId());
	//	    }
	//	}

	////	@Test
	////	public void testBioapis() throws Exception{
	////
	////		NcbiEFetchSequenceStub_API stub = new NcbiEFetchSequenceStub_API(1000);
	////
	////		System.out.println(stub.getTaxonomy("158668216"));
	////	}
	////
	////	@Test()
	////	public void testLocus() throws Exception {
	////		
	////		System.out.println(NcbiAPI.newOrgLocusID("NP_116711.1"));
	////	}
	////	
	////	@Test
	////	public void testTaxon() throws Exception {
	////
	////		NcbiTaxonStub_API ncbi = new NcbiTaxonStub_API(1);
	////
	////		System.out.println(ncbi.getTaxonList("131567", 0));
	////		System.out.println(ncbi.getTaxonList("158668216", 0));
	////	}
	////	
	////	@Test
	////	public void testHomologyData() throws Exception {
	////
	////		Set
	////		<String> query = new 
	////		HashSet
	////		<>();
	////		query.add("50307355");
	////		query.add("6321888"); 
	////		query.add("323308728"); 
	////		query.add("323354654"); 
	////		query.add("207344643"); 
	////		query.add("365984491"); 
	////		query.add("367014367");
	////		query.add("302309608");
	////		query.add("366996168"); 
	////		query.add("156837580");
	////		query.add("367014365");
	////		query.add("365760357"); 
	////		query.add("323305437");
	////		query.add("401625444");
	////		query.add("323309661"); 
	////		query.add("363748302"); 
	////		query.add("255714647");
	////		query.add("6320552");
	////		query.add("50284865");
	////
	////		query.add("349577396"); 
	////		query.add("158668216"); 
	////		query.add("574143223"); 
	////		query.add("574143224"); 
	////		query.add("574143226"); 
	////		query.add("574143227"); 
	////		query.add("1730047"); 
	////		query.add("158668220"); 
	////		query.add("15645451");
	////
	////
	////		NcbiEFetchSequenceStub_API s = new NcbiEFetchSequenceStub_API(100);
	////		System.out.println(s.getLocusFromID(query, query.size()));
	////
	////	}
	////
	////	@Test
	////	public void testHomologyData2() throws Exception {
	////
	////		List <String> query = new ArrayList<>();
	////		query.add("50307355");
	////		query.add("6321888"); 
	////		query.add("323308728"); 
	////		query.add("323354654"); 
	////		query.add("207344643"); 
	////		query.add("365984491"); 
	////		query.add("367014367");
	////		query.add("302309608");
	////		query.add("366996168"); 
	////		query.add("156837580");
	////		query.add("367014365");
	////		query.add("365760357"); 
	////		query.add("323305437");
	////		query.add("401625444");
	////		query.add("323309661"); 
	////		query.add("363748302"); 
	////		query.add("255714647");
	////		query.add("6320552");
	////		query.add("50284865");
	////
	////		query.add("349577396"); 
	////		query.add("158668216"); 
	////		query.add("574143223"); 
	////		query.add("574143224"); 
	////		query.add("574143226"); 
	////		query.add("574143227"); 
	////		query.add("1730047"); 
	////		query.add("158668220"); 
	////		query.add("15645451");
	////
	////
	////		HomologuesData ncbiData = new HomologuesData();
	////		ncbiData.setRefSeqGI("302309608");
	////		ncbiData.getBits().put("XP_453656", 1.0);
	////		ncbiData.getBits().put("XP_444845", 1.0);
	////		ncbiData.getBits().put("XP_002553605.1", 1.0);
	////		ncbiData.getBits().put("BAO41018.1", 1.0);
	////		ncbiData.getBits().put("P53387.1", 1.0);
	////		ncbiData.getBits().put("CAGL0A01826g",1.0);
	////
	////		NcbiAPI.getNcbiData(ncbiData, query, 50, 1, true, new AtomicBoolean(false),true);
	////	}
	////
	////	@Test
	////	public void testGetDatabaseIDs() throws RemoteException {
	////
	////		List <String> query = new ArrayList <>();
	////		query.add("NP_207625");
	////		query.add("XP_453656.1");
	////		//		query.add("NC_012759.1");
	////		//		query.add("NC_010473.1");
	////		//		query.add("NC_000913.3");
	////		//		query.add("NC_017633.1");
	////		//		query.add("NC_007613.1");
	////		//		query.add("NC_020163.1");
	////		//		query.add("NC_018661.1");
	////		//		query.add("NC_018658.1");
	////		//		query.add("NC_018650.1");
	////		//		query.add("NC_013353.1");
	////		//		query.add("NC_012967.1");
	////		//		query.add("NC_012947.1");
	////		//		query.add("NC_011741.1");
	////		//		query.add("NC_011748.1");
	////		//		query.add("NC_009801.1");
	////		NcbiESearchStub_API es_stub = new NcbiESearchStub_API(50);
	////
	////		System.out.println(es_stub.getDatabaseIDs("protein", query, 100,0));
	////	}
	////
	////	@Test
	////	public void testUniprot() {
	////		List <String> query = new ArrayList <>();
	////		query.add("EEU04176.1");
	////		query.add("EEU04176.1");
	////		query.add("EEU04176.1");
	////		query.add("EEU04176.1");
	////		query.add("CDK24838.1");
	////		query.add("CDK24838.1");
	////		query.add("CDK24838.1");
	////		query.add("CDK24838.1");
	////		query.add("EJP63171.1");
	////		query.add("EJP63171.1");
	////		query.add("EJP63171.1");
	////		query.add("EJP63171.1");
	////		query.add("EJP63171.1");
	////		query.add("EJP63171.1");
	////		query.add("EJP63171.1");
	////		query.add("XP_001597079.1");
	////		query.add("XP_001597079.1");
	////		query.add("XP_001597079.1");
	////		query.add("XP_001597079.1");
	////		query.add("XP_001597079.1");
	////		query.add("XP_001597079.1");
	////		query.add("XP_001597079.1");
	////
	////		Query q = UniProtQueryBuilder.buildIDListQuery(query);
	////		UniProtQueryService uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
	////		EntryIterator<UniProtEntry> entries = uniProtQueryService.getEntryIterator(q);
	////		for (UniProtEntry entry : entries) {
	////			System.out.println(entry.getUniProtId().getValue());
	////
	////		}
	////		//		System.out.println(UniProtAPI.getUniprotEntriesFromRefSeq(queryL));
	////
	////		//		UniProtEntry entry = UniProtAPI.getEntryFromUniProtID("P13181",0);
	////		//		//System.out.println(entry.getProteinDescription().getSection().getNames().get(0).getFieldsByType(FieldType.FULL).get(0).getValue());
	////		//		System.out.println(entry.getGenes().get(0).getOrderedLocusNames().get(0).getValue());
	////		//		System.out.println(entry.getProteinDescription().getSubNames());
	////		//		System.out.println(entry.getProteinDescription().getEcNumbers());
	////		//		System.out.println(entry.getTaxonomy());
	////		//		System.out.println(entry.getOrganism().getScientificName().getValue());
	////
	////		System.out.println(UniProtAPI.getEntryData("NP_116711.*"));
	////
	////		//UniProtEntry en = getUniProtEntryID("Q9P903");
	////		//System.out.println(en.getOrganism());
	//		//System.out.println(en.getOrganism().getScientificName());
	//		//System.out.println(en.getOrganism().getCommonName());
	//		//System.out.println(en.getOrganism().getOrganismNames());
	//		//UniProtEntry entry = UniProtAPI.getEntry("SP670_1270", 0);
	//		//System.out.println(entry.getGenes().get(0).getOrderedLocusNames());
	//		//System.out.println("is starred "+UniProtAPI.isStarred("NP_414542"));
	//		//System.out.println("is starred "+UniProtAPI.isStarred("RSAG_02623"));
	//
	//		//			String f = FileUtils.read("C:\\Users\\ODias\\Desktop\\test.txt");
	//		//			String[] l = f.split("\n");
	//		//			
	//		//			for(String s : l)
	//		//				System.out.println(s+"\tis starred "+UniProtAPI.isStarred(s));
	//
	//		//System.out.println(UniProtAPI.getUniProtEntryID("158668216", 0));
	//	}

}
