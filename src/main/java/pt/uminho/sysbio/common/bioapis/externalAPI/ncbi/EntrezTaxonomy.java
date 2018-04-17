package pt.uminho.sysbio.common.bioapis.externalAPI.ncbi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.TaxaSet;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.Taxon;

/**
 * @author odias
 *
 */
public class EntrezTaxonomy {

	private EntrezService entrezService;
	private static final Logger logger = LoggerFactory.getLogger(EntrezTaxonomy.class);
	
	/**
	 * @param numConnections
	 * @throws AxisFault
	 */
	public EntrezTaxonomy() {

		EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("https://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
		this.entrezService = entrezServiceFactory.build();
	}


	/**
	 * @param taxonomy_ids_list
	 * @param trialCounter
	 * @throws Exception 
	 */
	public Map<String,String[]> getTaxID_and_Superkingdom(List<List<String>> taxonomy_ids_list) throws Exception {

		Map<String,String[]> result = new HashMap<String, String[]>();

		try {

			for (int index_=0;index_<taxonomy_ids_list.size();index_++) {

				//req_taxon.setId("9685,522328");
				String query = new String(taxonomy_ids_list.get(index_).toString().replace("[", "").replace("]", "").replace(" ", "").getBytes(),"UTF-8");
				TaxaSet taxaSet = this.entrezService.eFetchTaxonomy("taxonomy", query, "xml");

				for (int i = 0; i < taxaSet.taxon.size(); i++) {

					Taxon taxon = taxaSet.taxon.get(i);

					if(!result.containsKey(taxon.scientificName)) {

						String[] array = new String[2];
						array[0] = taxon.taxId;
						//array[1] = res_taxon.getTaxaSet().getTaxon()[i].getLineage();

						if(taxon.lineageEx!=null) {

							//j = 0 cellular organism, thus start with j = 1
							for (int j = 1; j < taxon.lineageEx.size(); j++)
								if(taxon.lineageEx.get(j).rank.equalsIgnoreCase("superkingdom"))
									array[1] = taxon.lineageEx.get(j).scientificName;
						}
						result.put(taxon.scientificName, array);
					}
				}
			}
			return result;
		}
		catch(Exception e) {

			throw e;
		}
	}

	/**
	 * @param taxonomy_ids
	 * @param trialCounter
	 * @throws Exception 
	 */
	public Map<String,String[]> getTaxonList(String taxonomy_ids) throws IllegalArgumentException {

		Map<String,String[]> result = null;
		
		try {

			result = this.getTaxonListMethod(taxonomy_ids);
		}
		catch (Error e) {

			throw e;
		}
		catch(IllegalArgumentException e) {

				throw e;
		}
		return result;
	}

	/**
	 * @param taxonomy_ids
	 * @throws Exception 
	 */
	private Map<String,String[]> getTaxonListMethod(String taxonomy_ids) throws IllegalArgumentException {

		Map<String,String[]> result = new HashMap<String, String[]>();
		String[] array = new String[2];

		try {
			
			System.out.println("TAXONOMY IDS---->"+taxonomy_ids);
			
			String query = new String(taxonomy_ids.toString().replace("[", "").replace("]", "").replace(" ", "").getBytes(),"UTF-8");
			
			
			System.out.println("TAX IDS QUERY----->"+query);
			
			
			TaxaSet taxaSet = this.entrezService.eFetchTaxonomy("taxonomy", query, "xml");


			for (int i = 0; i < taxaSet.taxon.size(); i++) {

				Taxon taxon = taxaSet.taxon.get(i);

				array[0] = taxon.scientificName;
				array[1] = "";

				if(taxon.lineageEx!=null) {

					//j = 0 cellular organism, thus start with j = 1

					for (int j = 1; j < taxon.lineageEx.size(); j++) {

						array[1]+= taxon.lineageEx.get(j).scientificName;

						if(j<taxon.lineageEx.size()-1)
							array[1]+= "; ";
					}
				}

				if(taxon.taxId != null)
					result.put(taxon.taxId, array);

				array = new String[2];
			}
			return result;
		}
		catch (Error e) {

			//e.printStackTrace();
			throw new Error("Service unavailable!");
		}
		catch(Exception e) {
			
			//e.printStackTrace();
			logger.error("taxonomy id {}",taxonomy_ids);
			throw new IllegalArgumentException("Error retrieving taxonomy information.\n"+e.getMessage()); 
		}
//		array = new String[2];
//		array[0] = "";
//		array[1] = "";
//		result.put(taxonomy_ids, array);
//		return result;
	}
	
}
