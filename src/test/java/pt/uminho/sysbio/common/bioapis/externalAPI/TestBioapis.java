package pt.uminho.sysbio.common.bioapis.externalAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.datatypes.EntryData;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.datatypes.HomologuesData;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ebi.uniprot.UniProtAPI;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.CreateGenomeFile;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.EntrezTaxonomy;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.NcbiAPI;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.pair.Pair;
import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

public class TestBioapis{

	//@Test
	public void testeEspecial() throws IOException {
		
		//System.out.println(UniProtAPI.getEntryFromUniProtID("P40886", 0).getUniProtId());
		
		//System.out.println(UniProtAPI.getUniProtEntryFromXRef("P40886.2", 0).getUniProtId());
		
		//List<String> l = FileUtils.readLines("C:/Users/Oscar/Desktop/Set.txt");
		
		//System.out.println(UniProtAPI.getEntriesFromUniProtIDs(new HashSet<String> (l),0));
		
		
		
		//UniProtAPI.stopUniProtService();
	}
	
//	public void testGenome() throws Exception{
//		
//		String p = FileUtils.getCurrentTempDirectory()+"../../../merlin-core/temp/";
//		System.out.println(p);
//		
//		Map<String, ProteinSequence> po = CreateGenomeFile.getGenomeFromID("genome_SPE_R6_1",p,".faa");
//		
//		System.out.println(po.keySet());	
//		
//		po = NcbiAPI.getNCBILocusTags(po, 500);
//		
//		System.out.println(po.keySet());		
//	}
	
	////@Test
	public void ncbiTest() throws Exception {
		
		Set<String> test = new HashSet<>();
		test.add("NP_358236.1");
		test.add("NP_358709.1");
		test.add("NP_359056.1");
		test.add("NP_357844.1");
		test.add("NP_359506.1");
		test.add("NP_358860.1");
		test.add("NP_359287.2");
		test.add("NP_358191.1");
		test.add("NP_358300.1");
		test.add("NP_359123.1");
		test.add("NP_358783.1");
		
		System.out.println(NcbiAPI.getNCBILocusTags(test));
	}
	
	//@Test
	public void teet() throws ServiceException{

		UniProtEntry entry  = UniProtAPI.getEntry("Q0045", 0);
		System.out.println(entry.getUniProtId());	
	}

	
	public void teest() throws ServiceException{

		ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
		UniProtService uniprotService = serviceFactoryInstance.getUniProtQueryService();
		uniprotService.start();
		
		Set<String> set = new HashSet<>();
		set.add("P32467");

		Query query = UniProtQueryBuilder.accessions(set);

		//System.out.println("query "+query);

		QueryResult<UniProtEntry> entries = uniprotService.getEntries(query);
		while (entries.hasNext()) {
			
			UniProtEntry entry = entries.next(); 
			System.out.println(entry.getUniProtId());
		}
		
		uniprotService.stop();
	}

	//@Test
	public void getUniprotData() throws Exception{

		String taxID = "";
		
		HomologuesData h = new HomologuesData();
		h.setRefSeqGI("15645788");

		List<Pair<String, String>> set = new ArrayList<>();
		Pair<String,String> p = new Pair<String,String>("15645788", "15645788");
		set.add(0, p);
		NcbiAPI.getNcbiData(h, set, 0, new AtomicBoolean(false), true, taxID);
		
		System.out.println(NcbiAPI.getNcbiGI("NP_207965.1"));
	}

	public void s() {

		EntryData entryData = UniProtAPI.getEntryData("HVO_1284");
		System.out.println(entryData.getLocusTag());
		System.out.println(entryData);
	}

	//@Test
	public void t() throws ServiceException{

		Set<String> uniprotIDs = new HashSet<String>();
		uniprotIDs.add("D4GW84");
		uniprotIDs.add("G2MM24");
		uniprotIDs.add("U1MRT0");
		uniprotIDs.add("B0R502");
		uniprotIDs.add("V4XDJ0");

		ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
		UniProtService uniprotService = serviceFactoryInstance.getUniProtQueryService();
		uniprotService.start();

		Query query = UniProtQueryBuilder.accessions(uniprotIDs);

		QueryResult<UniProtEntry> entries = uniprotService.getEntries(query);
		while (entries.hasNext()) {

			UniProtEntry entry = entries.next();

			String primary_accession = entry.getPrimaryUniProtAccession().getValue();
			System.out.println(primary_accession);	
		}
	}


	/**
	 * @throws ServiceException 
	 * 
	 */
	//@Test
	public void getEntry() throws ServiceException {

		Set<String> uniprotIDs = new HashSet<String>();
		uniprotIDs.add("AGI21893.1");
		uniprotIDs.add("AGI21889");
		uniprotIDs.add("AGI21891");
		uniprotIDs.add("T2E8P7");
		uniprotIDs.add("Q9I0B1");
		uniprotIDs.add("T2ER45");
		uniprotIDs.add("M2ADX4");

		ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
		UniProtService uniprotService = serviceFactoryInstance.getUniProtQueryService();
		uniprotService.start();

		Query query = UniProtQueryBuilder.accessions(uniprotIDs);

		QueryResult<UniProtEntry> entries = uniprotService.getEntries(query);
		while (entries.hasNext()) {

			UniProtEntry entry = entries.next();
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

	@Test
	public void  getEntryTest() {

		String query = "WP_002885585.1";

		EntryData entryData = UniProtAPI.getEntryData(query);
		System.out.println(entryData);
//		System.out.println(entryData.getEcNumbers());
//		System.out.println(entryData.getUniprotReviewStatus());
//		System.out.println(entryData.getEntryID());
//		System.out.println(entryData.getLocusTag());
	}


	public void getTaxa() throws Exception{

		String t = "1544797";
		EntrezTaxonomy ncsa = new EntrezTaxonomy();

		Map<String,String[]> ncbi_ids = ncsa.getTaxonList(t);

		for (String k : ncbi_ids.keySet()) {

			System.out.println(k);

			for (String s : ncbi_ids.get(k))
				System.out.println("\t"+s);
		}
	}

		//@Test
	public void testUniprot() throws Exception {
			
			List <String> query = FileUtils.readLines("C:/Users/Oscar/Desktop/refSeqs.txt");
			System.out.println(UniProtAPI.getUniprotEntriesFromRefSeq(query, new AtomicBoolean(false),0));
		}

}
