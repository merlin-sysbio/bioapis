package pt.uminho.sysbio.common.bioapis.externalAPI;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.EntrezService;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.EntrezServiceFactory;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.EntrezTaxonomy;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.NcbiDatabases;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ELinkResult;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ESearchResult;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.GBSet;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.TaxaSet;
import retrofit.RetrofitError;

public class NCBI_API {

	final static Logger logger = LoggerFactory.getLogger(NCBI_API.class);

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

	//@Test
	public void test() {

		EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("https://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
		EntrezService entrezService = entrezServiceFactory.build();

		//		GBSet gbSet = entrezService.eFetch(NcbiDatabases.nucleotide, "5,10", "xml");
		//		System.out.println(gbSet.GBSeq);

		TaxaSet taxaSet = entrezService.eFetchTaxonomy("taxonomy", "9685,522328", "xml");
		System.out.println(taxaSet.taxon.get(0).scientificName);

		ESearchResult eSearchResult = entrezService.eSearch(NcbiDatabases.protein, "70000:90000[molecular weight", "xml", "100");
		System.out.println(eSearchResult.count);
		System.out.println(eSearchResult.idList);

		ELinkResult eLinkResult = entrezService.eLink(NcbiDatabases.protein, NcbiDatabases.gene, "15718680,157427902", "xml");
		System.out.println(eLinkResult.linkSet);

	}

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
	
	@Test
	public void getTaxTest() {

		EntrezTaxonomy t = new EntrezTaxonomy();
		System.out.println(t.getTaxonList("5421"));
	}

}
