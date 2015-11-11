/**
 * 
 */
package pt.uminho.sysbio.common.bioapis.externalAPI.ncbi;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.EntrezLink.KINGDOM;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ESearchResult;
import pt.uminho.sysbio.common.utilities.datastructures.pair.Pair;

/**
 * @author oscar
 *
 */
public class EntrezSearch {


	private EntrezService entrezService;

	/**
	 * @param numConnections
	 * @throws Exception
	 */
	public EntrezSearch() throws Exception {

		EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("http://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
		this.entrezService = entrezServiceFactory.build();
	}

	/**
	 * @param trialCounter
	 * @return
	 */
	public List<String> getGenomesIDs(KINGDOM kingdom){

		try 
		{
			String term = null;
			//req.setTerm("\"genome taxonomy\"[Filter]");
			//			Bacteria	10081
			//			Archaea	341
			//			Viruses	2951
			//			Viroids	41
			//			Eukaryota	1642

			switch (kingdom){
			case All:
			{
				term = "\"genome taxonomy\"[Filter]";
				break;
			}
			default: 
			{
				term = "\"genome taxonomy\"[Filter] AND "+kingdom+"[Organism]";
			}
			}

			ESearchResult eSearchResult = entrezService.eSearch(NcbiDatabases.genome, term, "xml", "100000");

			int n = eSearchResult.count;

			List<String> list_of_ids = new ArrayList<String>();

			for (int i = 0; i < n; i++) {

				String genome=eSearchResult.idList.get(i);
				list_of_ids.add(genome);
			}
			return list_of_ids;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @param database
	 * @param ids
	 * @param queryResponseConcatenationSize
	 * @return
	 * @throws Exception 
	 */
	public List<Pair<String, String>> getDatabaseIDs(NcbiDatabases database, List<String> ids, int queryResponseConcatenationSize) throws Exception {

		try {

			List<Pair<String, String>> result = new ArrayList<>();

			List<String> queryList = new ArrayList<String>();
			String results="";
			int index = 0;

			for(int i=0; i<ids.size();i++) {

				if(index>0)
					results=results.concat(",");

				String id=ids.get(i);
				if(id.contains("|")) {

					String[] idString = id.split("\\|");
					id=idString[1];
					if(id.isEmpty()) {

						if(idString[2].isEmpty()) {

							id = ids.get(i);
							System.err.println(EntrezSearch.class+" weird id "+id);
						}
						else {

							id = idString[2];
						}
					}
					else if(idString[0].equalsIgnoreCase("pdb")) {

						id=id.concat("_").concat(idString[2]);
					}
				}

				results=results.concat(id);
				index++;
				if(index>=queryResponseConcatenationSize) {

					queryList.add(results);
					results="";
					index=0;
				}
			}

			if(!results.isEmpty())
				queryList.add(results);

			int counter = 0;
			for(String query : queryList) {

				String term = new String(query.toString().replace("[", "").replace("]", "").getBytes(), "UTF-8");
				ESearchResult eSearchResult = entrezService.eSearch(database, term, "xml",  ids.size()+"");

				if(eSearchResult.idList!=null){
					// results output
					for (int i = 0; i < eSearchResult.count; i++) {

						if(counter<ids.size()) {

							result.add(counter, new Pair<String,String>(ids.get(counter),eSearchResult.idList.get(i)));
							counter++;
						}
					}
				}
			}

			return result;
		} 
		catch (Exception e) {

			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @param xMilliseconds
	 */
	public static void myWait(long xMilliseconds) {

		try {

			//System.out.println("Thread.sleep(xMilliseconds);
			Thread.sleep(xMilliseconds);
		}
		catch(InterruptedException e) {

			e.printStackTrace();
		}
	}
}
