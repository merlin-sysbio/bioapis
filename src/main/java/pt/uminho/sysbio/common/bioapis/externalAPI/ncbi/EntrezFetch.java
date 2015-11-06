package pt.uminho.sysbio.common.bioapis.externalAPI.ncbi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.biojava3.core.sequence.ProteinSequence;

import pt.uminho.sysbio.common.bioapis.externalAPI.datatypes.HomologuesData;
import pt.uminho.sysbio.common.bioapis.externalAPI.datatypes.NcbiData;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.GBFeature;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.GBQualifier;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.GBSeq;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.GBSet;
import pt.uminho.sysbio.common.bioapis.externalAPI.uniprot.UniProtAPI;
import pt.uminho.sysbio.common.bioapis.externalAPI.utilities.MySleep;
import pt.uminho.sysbio.common.utilities.datastructures.list.ListUtilities;
import pt.uminho.sysbio.common.utilities.datastructures.pair.Pair;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;


/**
 * @author ODias
 *
 */
public class EntrezFetch {

	private EntrezService entrezService;

	/**
	 * @param numConnections
	 * @throws Exception
	 */
	public EntrezFetch() throws Exception {

		EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("http://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
		this.entrezService = entrezServiceFactory.build();
	}


	/**
	 * @param GBSeq
	 * @param featureKey
	 * @return
	 */
	private static Map<String,String> getFeatures(GBSeq GBSeq, String featureKey) {

		Map<String, String> ret = new HashMap<>();

		List<GBFeature> gbFeatures =  GBSeq.features;

		for(int j=0;j<gbFeatures.size();j++) {

			if(featureKey.equalsIgnoreCase(gbFeatures.get(j).featureKey)) {

				List<GBQualifier> gbQualifiers = gbFeatures.get(j).qualifiers;

				for(int k=0;k<gbQualifiers.size();k++) {

					String name = gbQualifiers.get(k).name;
					String value = gbQualifiers.get(k).value;

					ret.put(name, value);
				}
			}
		}
		return ret;
	}

	/**
	 * @param genes
	 * @param queryResponseConcatenationSize
	 * @return
	 */
	public Map<String,String> getLocusFromID(Set<String> genes, int queryResponseConcatenationSize) {

		List<String> temp = new ArrayList<String>();
		Map<String,String> result = new HashMap<String,String>();
		List<String> ids_list = new ArrayList<String>(genes);

		try {	

			List<String> queryList = new ArrayList<String>();
			String links="";
			int index=0;

			for(int i=0; i<ids_list.size();i++) {

				if(index>0) {

					links=links.concat(",");
				}
				links=links.concat(ids_list.get(i).replace("\"", ""));
				index++;

				if(index>queryResponseConcatenationSize) {

					queryList.add(new String(links));
					links="";
					index=0;
				}
			}
			queryList.add(links);

			for(String query:queryList) {

				query = new String(query.getBytes(),"UTF-8");
				GBSet gbSet = this.entrezService.eFetch(NcbiDatabases.protein, query, "xml");

				List<GBSeq> gbSeqs = gbSet.gBSeq;

				for (int i = 0; i < gbSeqs.size(); i++) {

					GBSeq gbSeq = gbSeqs.get(i);
					String primary_accession = gbSeq.accessionVersion;

					Map<String, String> features = getFeatures(gbSeq, "CDS");

					if(features.containsKey("locus_tag")) {

						result.put(primary_accession, features.get("locus_tag"));
						temp.add(primary_accession);
					}

					if(!temp.contains(primary_accession)) {

						UniProtEntry uni = UniProtAPI.getUniProtEntryFromXRef(primary_accession,0);

						List<String> g = UniProtAPI.getLocusTags(uni); 
						if(g!= null && g.size()>0) {

							String locus = g.get(0);
							result.put(primary_accession, locus);
						}
					}
				}
			}
			return result;
		}
		catch (Exception e) {

			//e.printStackTrace();

			if(queryResponseConcatenationSize>0) {

				int ind = (queryResponseConcatenationSize/2);

				if(ind<2) {

					queryResponseConcatenationSize = 0;
				}
				else {

					queryResponseConcatenationSize=queryResponseConcatenationSize/2;
				}
				System.out.println("new initalNumberOfRequests = "+queryResponseConcatenationSize);
				return getLocusFromID(genes, queryResponseConcatenationSize);
			}
			else {

				System.err.println("Get locus error!");
				return null;
			}
		}
	}

	/**
	 * @param geneID
	 * @return
	 * @throws RemoteException 
	 */
	public String getTaxonomy(String geneID) throws Exception {

		String taxID = null;

		GBSet gbSet = this.entrezService.eFetch(NcbiDatabases.protein, geneID, "xml");

		List<GBSeq> gbSeqs = gbSet.gBSeq;

		for (int i = 0; i < gbSeqs.size(); i++) {

			GBSeq gbSeq = gbSeqs.get(i);

			Map<String, String> features = getFeatures(gbSeq, "source");

			if(features.containsKey("db_xref")) {

				if(features.get("db_xref").startsWith("taxon")) {

					taxID = features.get("db_xref").replace("taxon:", "");
				}
			}
		}
		return taxID;	
	}


	/**
	 * @param ids_list
	 * @param queryResponseConcatenationSize
	 * @return
	 */
	public Map<String, ProteinSequence> createSequencesMap(List<String> ids_list, int queryResponseConcatenationSize){
		Map<String,ProteinSequence> sequences = new HashMap<String,ProteinSequence>();
		Set<String> added= new TreeSet<String>();

		try {

			List<String> queryList = new ArrayList<String>();
			String links="";
			int index=0;
			for(int i=0; i<ids_list.size();i++) {

				if(index>0) {

					links=links.concat(",");
				}
				links=links.concat(ids_list.get(i));
				index++;
				if(index>queryResponseConcatenationSize) {

					queryList.add(links);
					links="";
					index=0;
				}
			}
			queryList.add(links);

			for(String query:queryList) {

				GBSet gbSet = this.entrezService.eFetch(NcbiDatabases.protein, query, "xml");

				List<GBSeq> gbSeqs = gbSet.gBSeq;

				for (int i = 0; i < gbSeqs.size(); i++) {

					GBSeq gbSeq = gbSeqs.get(i);
					String primary_accession = gbSeq.accessionVersion;
					
					Map<String, String> features = getFeatures(gbSeq, "CDS");

					if(features.containsKey("locus_tag")) {

						sequences.put(features.get("locus_tag"), new ProteinSequence(gbSeq.sequence.toUpperCase()));
						added.add(primary_accession);
					}

					String acc = primary_accession;
					if(acc!=null) {

						if(!added.contains(primary_accession)) {

							UniProtEntry uni = UniProtAPI.getUniProtEntryFromXRef(acc,0); 
							if(uni!=null) {

								String locus = UniProtAPI.getLocusTags(uni).get(0);//.getValue();
								sequences.put(locus, 
										new ProteinSequence(gbSeq.sequence.toUpperCase()));
								added.add(primary_accession);
							}
						}
					}
				}
			}
			return sequences;
		}
		catch (Exception e)  {

			if(queryResponseConcatenationSize>5) {

				//System.out.println("Trial EFetchResult "+trialCounter);
				//trialCounter++;
				queryResponseConcatenationSize=queryResponseConcatenationSize/2;
				return this.createSequencesMap(ids_list, queryResponseConcatenationSize);
			}
		}
		return null;
	}

	/**
	 * @param ids_list
	 * @param queryResponseConcatenationSize
	 * @return
	 */
	public Pair<Map<String, String>,Map<String, ProteinSequence>> getLocusAndSequencePairFromID(List<List<String>> ids_list, int queryResponseConcatenationSize, String sourceDB){
		try
		{	
			List<List<String>> queryList = new ArrayList<List<String>>();
			List<String> links= new ArrayList<String>();
			int index=0;
			for(int i=0; i<ids_list.size();i++)
			{
				for(int j=0; j<ids_list.get(i).size();j++)
				{
					links.add(ids_list.get(i).get(j).trim());
				}
				index++;
				if(index>=queryResponseConcatenationSize)
				{
					if(!links.isEmpty())
					{
						queryList.add(links);
					}
					links=new ArrayList<String>();
					index=0;
				}
			}
			if(!links.isEmpty())
			{
				queryList.add(links);
			}

			List<Thread> threads = new ArrayList<Thread>();
			ConcurrentLinkedQueue<List<String>> queryArray = new ConcurrentLinkedQueue<List<String>>(queryList);

			int numberOfCores = Runtime.getRuntime().availableProcessors()*2;
			if(queryArray.size()<numberOfCores){numberOfCores=queryArray.size();}
			System.out.println("number Of threads: "+numberOfCores);

			ConcurrentHashMap<String,ProteinSequence> sequences = new ConcurrentHashMap<String,ProteinSequence>();
			ConcurrentHashMap<String,String> locus_Tag = new ConcurrentHashMap<String,String>();

			for(int i=0; i<numberOfCores; i++) {

				Runnable lc	= new Runnable_Sequences_retriever(queryArray,locus_Tag,sequences,sourceDB);
				Thread thread = new Thread(lc);
				threads.add(thread);
				System.out.println("Start "+i);
				thread.start();
			}

			for(Thread thread :threads)
				thread.join();


			Map<String,ProteinSequence> sequences_Pair = new HashMap<String,ProteinSequence>(sequences);
			Map<String,String> locus_Tag_Pair = new HashMap<String,String>(locus_Tag);

			return new Pair<Map<String, String>,Map<String, ProteinSequence>>(locus_Tag_Pair,sequences_Pair);
		}
		catch (Exception e) {

			if(queryResponseConcatenationSize>5) {

				System.out.println("Reducing concatenation size to:\t"+queryResponseConcatenationSize);
				//trialCounter++;
				queryResponseConcatenationSize=queryResponseConcatenationSize/2;
				return this.getLocusAndSequencePairFromID(ids_list, queryResponseConcatenationSize,sourceDB);
			}
		}
		return null;
	}

	/**
	 * @param query
	 * @param sourceDB
	 * @param ncbiData
	 * @return
	 * @throws Exception
	 */
	public NcbiData getSequences(List<String> query, String sourceDB, NcbiData ncbiData) throws Exception {

		List<String> added = new ArrayList<String>();

		String queryString = new String(query.toString().replace("[", "").replace("]", "").replace(" ", "").getBytes(),"UTF-8");

		GBSet gbSet = this.entrezService.eFetch(NcbiDatabases.protein, queryString, "xml");

		List<GBSeq> gbSeqs = gbSet.gBSeq;

		for (int i = 0; i < gbSeqs.size(); i++) {

			GBSeq gbSeq = gbSeqs.get(i);

			String primary_accession = gbSeq.accessionVersion;
			
			Map<String, String> features = getFeatures(gbSeq, "CDS");

			if(features.containsKey("locus_tag")) {

				ncbiData.addLocusTag(primary_accession, features.get("locus_tag"));
				ncbiData.addSequence(primary_accession, new ProteinSequence(gbSeq.sequence.toUpperCase()));
				added.add(primary_accession);
			}

			String acc = gbSeq.accessionVersion;

			if(acc!=null &&!added.contains(acc)) {

				UniProtEntry uniProtEntry = UniProtAPI.getUniProtEntryFromXRef(acc,0);
				if(uniProtEntry!=null) {

					if(UniProtAPI.getLocusTags(uniProtEntry)!=null && UniProtAPI.getLocusTags(uniProtEntry).size()>0) {

						String locus = UniProtAPI.getLocusTags(uniProtEntry).get(0);//.getValue();
						ncbiData.addLocusTag(acc, locus);
						ncbiData.addSequence(acc, new ProteinSequence(gbSeq.sequence.toUpperCase()));
						added.add(acc);
					}
					else {

						//System.err.println(UniprotAPI.getUniProtEntryID(acc).getUniProtId());
					}
				}
			}
		}

		return ncbiData;
	}

	/**
	 * @param ncbiData
	 * @param isNCBIGenome
	 * @param cancel
	 * @param resultsPairs
	 * @param taxonomyID
	 * @param uniprotStatus
	 * @return
	 * @throws Exception
	 */
	public HomologuesData getNcbiData(HomologuesData ncbiData, boolean isNCBIGenome, AtomicBoolean cancel, List<Pair<String, String>> resultsPairs, 
			Map<String, String> taxonomyID, boolean uniprotStatus) throws Exception {

		List<String> resultList = new ArrayList<>(), accessionNumbers = new ArrayList<>();

		for(int i = 0; i< resultsPairs.size(); i++)
			resultList.add(resultsPairs.get(i).getB());

		List<List<String>> resultsListsList = ListUtilities.split(resultList, 99);

		List<GBSeq> gbSeqs = new ArrayList<>();

		for(List<String> result : resultsListsList) {

			if(!result.isEmpty()) {

				GBSet gbSet = this.getEntries(result, 0);
				gbSeqs.addAll(gbSeqs.size(), gbSet.gBSeq);
			}
		}

		for (int i = 0; i < gbSeqs.size(); i++) {

			GBSeq gbSeq = gbSeqs.get(i);

			if(cancel.get()) {

				i=gbSeqs.size();
			}
			else {

				String primary_accession = gbSeq.accessionVersion;
				ncbiData.addDefinition(primary_accession, gbSeq.definition);

				if(i==0 && isNCBIGenome) {

					//	ncbiData.addEValue(primary_accession,0.0);
					//	ncbiData.addBits(primary_accession,-1);
					ncbiData.addLocusID(primary_accession, i);
				}
				else {

					if(ncbiData.getBits(primary_accession)>=0) {

						ncbiData.addEValue(primary_accession,ncbiData.getEvalue(primary_accession));
						ncbiData.addBits(primary_accession,ncbiData.getBits(primary_accession));
						ncbiData.addLocusID(primary_accession, i);
					}

				}

				if(i==0) {

					if(isNCBIGenome) {

						ncbiData.setOrganismID(gbSeq.organism);
					}
					else {

						ncbiData.setOrganismID(ncbiData.getTaxonomyID()[0]);
					}
				}

				Map<String, String> features = getFeatures(gbSeq, "source");
				
				if(primary_accession!=null && !accessionNumbers.contains(primary_accession))
					accessionNumbers.add(primary_accession);

				for(int j=0;j<features.size();j++) {

					if((isNCBIGenome && j==0) || ncbiData.getBits(primary_accession)>=0) {

						LinkedList<String> ecn = new LinkedList<String>();

						if(features.containsKey("db_xref") && features.get("db_xref").startsWith("taxon"))									
							taxonomyID.put(primary_accession, features.get("db_xref").replace("taxon:", ""));

						if(features.containsKey("chromosome")) {

							if(isNCBIGenome) {

								if(i==0)
									ncbiData.setChromosome(features.get("chromosome"));								

								if(i==1 && ncbiData.getChromosome()==null)
									ncbiData.setChromosome(features.get("chromosome"));
							}
						}

						if(features.containsKey("organelle")) {

							if(i==0) {

								if(isNCBIGenome)
									ncbiData.setOrganelle(features.get("organelle"));
							}
							else {

								ncbiData.addOganelles(primary_accession, features.get("organelle"));
							}
						}

						if(features.containsKey("EC_number"))
							ecn.add(features.get("EC_number"));

						///////////////////////////////////////////////////////////////////////
						features = getFeatures(gbSeq, "Protein");

						if(features.containsKey("product"))
							ncbiData.addProduct(primary_accession, features.get("product"));

						if(features.containsKey("calculated_mol_wt"))
							ncbiData.addCalculated_mol_wt(primary_accession,features.get("calculated_mol_wt"));

						if(features.containsKey("EC_number"))
							ecn.add(features.get("EC_number"));

						if(features.containsKey("note"))
							ncbiData.setLocus_protein_note(features.get("note"));

						/////////////////////////////////////////////////////////////////////
						features = getFeatures(gbSeq, "CDS");

						if(features.containsKey("locus_tag")) {

							if(i==0) {

								if(isNCBIGenome)
									ncbiData.setLocusTag(features.get("locus_tag"));
							}
							else {

								ncbiData.addBlastLocusTags(primary_accession, features.get("locus_tag"));
							}
						}

						if(features.containsKey("coded_by")) {

							if(i==0) {

								if(isNCBIGenome) {

									ncbiData.setSequence_code(features.get("coded_by").split(":")[1]);
								}
							}
							else {

								if(ncbiData.getLocusTag()==null && ncbiData.getSequence_code().equalsIgnoreCase(features.get("coded_by").split(":")[1])) {

									ncbiData.setLocusTag(ncbiData.getBlast_locus_tag().get(primary_accession));
								}
							}
						}

						if(features.containsKey("note") && i==0 && !features.get("note").contains(""))
							ncbiData.setLocus_gene_note(features.get("note") );

						if(i==1 && ncbiData.getLocusTag()==null && isNCBIGenome){ //&& locus_gene_note==null && locus_protein_note==null) {

							ncbiData.setLocusTag(ncbiData.getBlast_locus_tag().get(primary_accession));
						}

						if(features.containsKey("gene")) {

							if(i==0 && isNCBIGenome) {

								String tempGene= features.get("gene") ;

								if(tempGene!=null)
									ncbiData.setGene(tempGene);
								else
									ncbiData.setGene("");
							}
							else {

								ncbiData.addGenes(primary_accession, features.get("gene") );
							}
						}

						if(i==1 && (ncbiData.getGene()==null || ncbiData.getGene().isEmpty()) && isNCBIGenome) {

							String tempGene=ncbiData.getGenes().get(primary_accession);

							if(tempGene!=null)
								ncbiData.setGene(tempGene);
							else
								ncbiData.setGene("");
						}

						if(features.containsKey("db_xref")) {

							if(features.get("db_xref").startsWith("UniProtKB/Swiss-Prot")) {

								//uniProtReference = value.replace("UniProtKB/Swiss-Prot:", "");
								ncbiData.addUniprotStatus(primary_accession,true);
								accessionNumbers.remove(primary_accession);
							}

							if(features.get("db_xref").startsWith("UniProtKB/TrEMBL")) {

								//uniProtReference = value.replace("UniProtKB/TrEMBL:", "");
								ncbiData.addUniprotStatus(primary_accession,false);
								accessionNumbers.remove(primary_accession);
							}	
						}
						if(!ecn.isEmpty()) {

							String[] ecnb = new String[ecn.size()];
							for(int e=0;e<ecn.size();e++){ecnb[e]=ecn.get(e);}
							ncbiData.addECnumbers(primary_accession, ecnb);
						}
					}
					//System.out.println("------------------------------------------");

					if(i==0 && ncbiData.getLocusTag() == null) { 

						//					if(uniProtReference == null) {
						//
						//						UniProtEntry uniProtEntry = UniProtAPI.getEntry(primary_accession, 0);
						//
						//						if(uniProtEntry!= null && uniProtEntry.getGenes().size()>0 && uniProtEntry.getGenes().get(0).getOrderedLocusNames().size()>0) {
						//
						//							ncbiData.setLocus_tag(uniProtEntry.getGenes().get(0).getOrderedLocusNames().get(0).getValue());
						//							ncbiData.addUniprotStatus(primary_accession, Boolean.valueOf(UniProtAPI.isStarred(uniProtEntry)));
						//						}					
						//					}
						if(ncbiData.getUniprotLocusTag()!=null)
							ncbiData.setLocusTag(ncbiData.getUniprotLocusTag());
					}
				}
			}
		}

		if(uniprotStatus && accessionNumbers.size()>0 && !cancel.get()) {

			Map<String, List<UniProtEntry>> uniProtEntries = UniProtAPI.getUniprotEntriesFromRefSeq(accessionNumbers, cancel,0);

			for(String accessionsNumber:uniProtEntries.keySet()) {

				if(!cancel.get()) {

					for(UniProtEntry uniProtEntry : uniProtEntries.get(accessionsNumber)) {

						String primary_accession = accessionsNumber;

						if(ncbiData.getUniprotStatus().containsKey(primary_accession) && ncbiData.getUniprotStatus().get(primary_accession)) {

							//System.out.println("Exists entry for "+primary_accession+". UniProtEntries size "+uniProtEntries.size());
						}
						else
							ncbiData.addUniprotStatus(primary_accession, UniProtAPI.isStarred(uniProtEntry));
					} }
			}
		}
		return ncbiData;
	}

	/**
	 * @param result
	 * @param errorCount
	 * @return
	 * @throws Exception 
	 */
	public GBSet getEntries(List<String> result, int errorCount) throws Exception{

		try {

			GBSet gbSet = this.entrezService.eFetch(NcbiDatabases.protein, result.toString().substring(1, result.toString().length()-2), "xml");
			return gbSet;
		}
		catch (Exception e)  {

			if(errorCount<20) {

				MySleep.myWait(1000);
				errorCount+=1;
				return this.getEntries(result, errorCount);
			}
			else
				throw new Exception();
		}
	}
}
