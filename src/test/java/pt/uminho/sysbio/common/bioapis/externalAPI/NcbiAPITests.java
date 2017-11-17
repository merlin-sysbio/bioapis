package pt.uminho.sysbio.common.bioapis.externalAPI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.EntrezFetch;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.EntrezService;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.EntrezServiceFactory;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.EntrezTaxonomy;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.NcbiAPI;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.NcbiDatabases;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ELinkResult;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ESearchResult;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.GBSet;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.LinkSet;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.TaxaSet;
import retrofit.RetrofitError;

public class NcbiAPITests {

	final static Logger logger = LoggerFactory.getLogger(NcbiAPITests.class);

	
	
	//@Test
	public void getEntriesTest() {

		List<String> result = new ArrayList<>();
		result.add("654142261");
		result.add("654142262");
		result.add("311216979");

		EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("https://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
		EntrezService entrezService = entrezServiceFactory.build();
		String query = result.toString().substring(1, result.toString().length()-2);
		System.out.println(query);
		GBSet gbSet;
		try {
			gbSet = entrezService.eFetch(NcbiDatabases.protein, query, "xml");
			System.out.println(gbSet);
		} catch (RetrofitError e) {
			System.out.println(e.getResponse().getStatus());
			e.printStackTrace();
		}

	}
	
	@Test
    public void testLink() {
        
        EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("https://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
        EntrezService entrezService = entrezServiceFactory.build();
        
        ESearchResult eSearchResult = entrezService.eSearch(NcbiDatabases.assembly, "243276[Taxonomy ID]", "xml", "100");
        System.out.println(eSearchResult.count);
        System.out.println(eSearchResult.idList);

    }
	
	
	@Test
	public void taxonomyList() {
	
		String s = "861557, 930089, 665912, 665912, 930090, 336722, 383855, 717646, 1287680, 1287680, 656061, "
				+ "214684, 283643, 367775, 578456, 561896, 717944, 732165, 650164, 721885, 747525, 741275, "
				+ "717982, 717982, 694068, 694068, 670483, 486041, 554373, 1381753, 240176, 578458, 597362, "
				+ "936046, 741705, 578457, 237631, 1277687, 418459, 747676, 431895, 946362, 352472, 352472, "
				+ "5786, 261658, 261658, 312017, 412030, 412030, 1280413, 556484, 296543, 403677, 67593, "
				+ "695850, 905079, 185431, 353153, 347515, 435258, 5661, 929439, 420245";
		
		EntrezTaxonomy e = new EntrezTaxonomy();
		Map<String,String[]> m = e.getTaxonList(s);
		
		
		for(String key : m.keySet())
			System.out.println(key+"\t"+m.get(key)[0]);
	}
	
	
	@Test
	public void taxonomy() {

		EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("https://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
		EntrezService entrezService = entrezServiceFactory.build();

		//		GBSet gbSet = entrezService.eFetch(NcbiDatabases.nucleotide, "5,10", "xml");
		//		System.out.println(gbSet.GBSeq);

		TaxaSet taxaSet = entrezService.eFetchTaxonomy("taxonomy", "393595", "xml");
		System.out.println(taxaSet.taxon.get(0).scientificName);

		ESearchResult eSearchResult = entrezService.eSearch(NcbiDatabases.protein, "70000:90000[molecular weight", "xml", "100");
		System.out.println(eSearchResult.count);
		System.out.println(eSearchResult.idList);

		ELinkResult eLinkResult = entrezService.eLink(NcbiDatabases.protein, NcbiDatabases.gene, "15718680,157427902", "xml");
		System.out.println(eLinkResult.linkSet);
		
		GBSet gbSet = entrezService.eFetch(NcbiDatabases.protein, "CDZ96150.1", "xml");
		System.out.println(gbSet.gBSeq);

	}

	//@Test
	public void testAPI() throws Exception {

		EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("https://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
		EntrezService entrezService = entrezServiceFactory.build();

		GBSet gbSet = entrezService.eFetch(NcbiDatabases.protein, "CDZ96150.1", "xml");
		System.out.println(gbSet.gBSeq);

		//		System.out.println(NcbiAPI.getNcbiGI("LmxM.28.2120,LmxM.19.1240,LmxM.07.0770,LmxM.14.1310,LmxM.04.0210,LmxM.30.1490,LmxM.31.1200,LmxM.33.0010,LmxM.20.0220,LmxM.17.1440"));
		//		Set<String> s = new HashSet<>();
		//		s.add("LmxM.28.2120");
		//		s.add("LmxM.19.1240");
		//		s.add("LmxM.07.0770");
		//		System.out.println(NcbiAPI.getNCBILocusTags(s));
	}
	
	//@Test
	public void getLocusTest() throws Exception {
		
		Set<String> s = new HashSet<>();
		s.add("Q9XGY7.1");
		s.add("Q6F7B8");
		s.add("Q6F7B8");
		s.add("ABG93414.1");
		s.add("CAL18190.1");
		s.add("YP_002782672.1");
 		EntrezFetch e = new EntrezFetch();
		System.out.println(e.getLocusFromID(s,100));
//		
//		System.out.println("Get Locus Tag "+NcbiAPI.getLocusTag("AFN51404.1"));
		
//		System.out.println(NcbiAPI.getProductAndTaxonomy("WP_003407734.1").getValue());
//		System.out.println(NcbiAPI.getProductAndTaxonomy("WP_003407734.1").getPairValue());
	}
	
	//@Test
	public void getTaxTest() {

		EntrezTaxonomy t = new EntrezTaxonomy();
		System.out.println(t.getTaxonList("5421"));
	}

}
