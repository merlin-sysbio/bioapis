package pt.uminho.sysbio.common.bioapis.externalAPI;

import org.junit.Test;

import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.EntrezService;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.EntrezServiceFactory;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.NcbiDatabases;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ELinkResult;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ESearchResult;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.TaxaSet;

public class NCBI_API {

	@Test
	public void test() {
		
		EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("http://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
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

}
