package pt.uminho.sysbio.common.bioapis.externalAPI.ncbi;

import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ELinkResult;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ESearchResult;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.GBSet;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.TaxaSet;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * @author Oscar
 *
 */
public interface EntrezService {

	//http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=5&retmode=xml
	//http://endpoint/cmd/{var}/path0/{var2}?q1=a&q2=b
	// TAXONOMY DOES NOT RETURN GBSet
	@GET("/efetch.fcgi")
	public GBSet eFetch(@Query("db") NcbiDatabases database, @Query("id") String id, @Query("retmode") String retmode);

	//http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=5&retmode=xml
	//http://endpoint/cmd/{var}/path0/{var2}?q1=a&q2=b
	// TAXONOMY DOES NOT RETURN GBSet
	@GET("/efetch.fcgi")
	public TaxaSet eFetchTaxonomy(@Query("db") String taxonomy, @Query("id") String id, @Query("retmode") String retmode);
	
	//http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=protein&term=70000:90000%5bmolecular+weight
	//http://endpoint/cmd/{var}/path0/{var2}?q1=a&q2=b
	@GET("/esearch.fcgi")
	public ESearchResult eSearch(@Query("db") NcbiDatabases database, @Query("term") String term, @Query("retmode") String retmode, @Query("retmax") String retmax);
	
	
	//http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=protein&term=70000:90000%5bmolecular+weight
	//http://endpoint/cmd/{var}/path0/{var2}?q1=a&q2=b
	@GET("/elink.fcgi")
	public ELinkResult eLink(@Query("db") NcbiDatabases database, @Query("dbfrom") NcbiDatabases databasefrom, @Query("id") String query, @Query("retmode") String retmode);
	
	//https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=assembly&id=38841
	//http://endpoint/cmd/{var}/path0/{var2}?q1=a&q2=b
	//@GET("/esummary.fcgi")
	//public ESummaryReport eSummary(@Query("db") NcbiDatabases database, @Query("id") String[] uids)
	
}
