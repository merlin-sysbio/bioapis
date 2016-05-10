package pt.uminho.sysbio.common.bioapis.externalAPI.ncbi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.axis2.AxisFault;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.pair.Pair;
import pt.uminho.sysbio.common.bioapis.externalAPI.datatypes.EntryData;
import pt.uminho.sysbio.common.bioapis.externalAPI.datatypes.HomologuesData;
import pt.uminho.sysbio.common.bioapis.externalAPI.uniprot.MyNcbiTaxon;
import pt.uminho.sysbio.common.bioapis.externalAPI.uniprot.TaxonomyContainer;
import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;

/**
 * @author Oscar
 *
 */
public class NcbiAPI {


	/**
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public static Map<String, ProteinSequence> getNCBILocusTags(Map<String, ProteinSequence> genome) throws Exception {
		
		Map<String, ProteinSequence> newGenome = new HashMap<String, ProteinSequence>();

		Map<String, String> idLocus = NcbiAPI.getNCBILocusTags(genome.keySet());

		for (String id : idLocus.keySet()) {
			
			ProteinSequence pSequence = genome.get(id);
			pSequence.setOriginalHeader(idLocus.get(id));
			newGenome.put(idLocus.get(id), genome.get(id));
		}

		return newGenome;
	}
	
	/**
	 * @param genome
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public static Map<String, ProteinSequence> getNCBILocusTags(Map<String, ProteinSequence> genome, int size) throws Exception {

		Map<String, ProteinSequence> newGenome = new HashMap<String, ProteinSequence>();

		Map<String, String> idLocus = NcbiAPI.getNCBILocusTags(genome.keySet(), size);

		for (String id : idLocus.keySet()) {
			
			ProteinSequence pSequence = genome.get(id);
			pSequence.setOriginalHeader(idLocus.get(id));
			newGenome.put(idLocus.get(id), genome.get(id));
		}

		return newGenome;
	}

	/**
	 * @param keys
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getNCBILocusTags(Set<String> keys) throws Exception {

		EntrezFetch entrezFetch = new EntrezFetch();
		Map<String, String> idLocus = entrezFetch.getLocusFromID(keys,500);

		return idLocus;
	}
	
	/**
	 * @param keys
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getNCBILocusTags(Set<String> keys, int size) throws Exception {

		EntrezFetch entrezFetch = new EntrezFetch();

		Map<String, String> idLocus = entrezFetch.getLocusFromID(keys,size);

		return idLocus;
	}


	/**
	 * @param path
	 * @return
	 */
	public static List<String> get_data_file(String path, boolean trim) {
		File aFile = new File(path);
		List<String> contents = new ArrayList<String>();

		try 
		{
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			String line = null;
			while (( line = input.readLine()) != null)
			{
				if(trim)
				{
					contents.add(line.trim());
				}
				else
				{
					contents.add(line);
				}
				//contents.add(System.getProperty("line.separator"));
			}
			input.close();
		}
		catch (IOException ex){ex.printStackTrace();}

		return contents;
	}

	/**
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public Map<String, ProteinSequence> set_Sequences(String filePath) throws Exception{
		File seqFile = new File(filePath);
		FastaReader<ProteinSequence,AminoAcidCompound> fastaReader = new FastaReader<ProteinSequence,AminoAcidCompound>(
				seqFile,
				new GenericFastaHeaderParser<ProteinSequence,AminoAcidCompound>(), 
				new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
		Map<String, ProteinSequence> queryMap  =  fastaReader.process();
		return queryMap;
	}

	/**
	 * @param queryInpuStream
	 * @return
	 * @throws Exception
	 */
	public static Map<String, ProteinSequence> set_Sequences(InputStream queryInpuStream) throws Exception{
		FastaReader<ProteinSequence,AminoAcidCompound> fastaReader = new FastaReader<ProteinSequence,AminoAcidCompound>(
				queryInpuStream,
				new GenericFastaHeaderParser<ProteinSequence,AminoAcidCompound>(), 
				new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
		Map<String, ProteinSequence> queryMap  =  fastaReader.process();
		return queryMap;
	}

	/**
	 * select and align candidate genes only
	 * 
	 * @param queryMap
	 */
	public static Map<String, ProteinSequence> filterCandidatesGenBank(Set<String> candidates, Map<String, ProteinSequence> queryMap){
		if(candidates!=null)
		{
			for(String geneID: new TreeSet<String>(queryMap.keySet()))
			{
				if(!candidates.contains(geneID))
				{
					queryMap.remove(geneID);
				}
			}
		}
		return queryMap;
	}

	/**
	 * select and align candidate genes only
	 * 
	 * @param queryMap
	 */
	public static  Map<String, ProteinSequence> filterCandidatesGenolevures(Set<String> candidates, Map<String, ProteinSequence> queryMap){
		if(candidates!=null)
		{
			for(String query: new TreeSet<String>(queryMap.keySet()))
			{
				String geneID = query;
				if(query.contains(" "))
				{
					StringTokenizer stq = new StringTokenizer(query," ");
					geneID = stq.nextToken();
				}
				if(!candidates.contains(geneID))
				{
					queryMap.remove(query);
				}
			}
		}
		return queryMap;
	}



	/**
	 * read TMHMM file 
	 * 
	 * @param file
	 * @return set with transport protein candidates
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static Map<String,Integer> readTMHMMGenolevures(File file, int minimum_number_of_helices) throws NumberFormatException, IOException {

		BufferedReader reader = null;
		//Set<String> ids = new TreeSet<String>();
		Map<String,Integer> tmhmmScore=(new TreeMap<String, Integer>());
		reader = new BufferedReader(new FileReader(file));
		String text = null;

		while ((text = reader.readLine()) != null) {
			
			if(text.startsWith("#")) {
				
				text=text.replace("# ", "");
				StringTokenizer st = new StringTokenizer(text," ");
				String id = st.nextToken();
				if(st.nextToken().equals("Number"))
				{
					st = new StringTokenizer(text,":");
					st.nextToken();
					int number_of_helices = Integer.parseInt(st.nextToken().trim());
					if(number_of_helices>=minimum_number_of_helices)
					{
						//	este parser n�o resulta, os identificadores com _ perdem-se. usar alguma api para fazer parsing, por exemplo se come�ar por gi usar entrez protein se come�ar por GNV usar este parser...api
						// verificar para que sao os metodos no construtor e descreve-los bem...
						st = new StringTokenizer(id,"_");
						String locusTag = null;
						while(st.hasMoreTokens()){locusTag= st.nextToken();}
						//ids.add(locusTag.replace("||","_"));
						tmhmmScore.put(locusTag.replace("||","_"),number_of_helices);
					}
				}
			}
		}
		reader.close();
		return tmhmmScore;
	}

	/**
	 * read TMHMM file 
	 * 
	 * @param file
	 * @return set with transport protein candidates
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static Map<String,Integer> readTMHMMGenbank(File file, int minimum_number_of_helices, Map<String, String> ids_locus) throws NumberFormatException, IOException{
		BufferedReader reader = null;
		//Set<String> ids = new TreeSet<String>();
		Map<String,Integer> tmhmmScore=new TreeMap<String, Integer>();
		reader = new BufferedReader(new FileReader(file));
		String text = null;

		while ((text = reader.readLine()) != null) 
		{
			if(text.startsWith("#"))
			{
				text=text.replace("# ", "");
				StringTokenizer st = new StringTokenizer(text," ");
				String id = st.nextToken();
				if(st.nextToken().equals("Number"))
				{
					st = new StringTokenizer(text,":");
					st.nextToken();
					int number_of_helices = Integer.parseInt(st.nextToken().trim());
					if(number_of_helices>=minimum_number_of_helices)
					{
						//	este parser n�o resulta, os identificadores com _ perdem-se. usar alguma api para fazer parsing, por exemplo se come�ar por gi usar entrez protein se come�ar por GNV usar este parser...api
						// verificar para que sao os metodos no construtor e descreve-los bem...

						///String locus = ids_locus.get(id.split("ref_")[1].replace(".","\t").split("\t")[0]);
						String temp=id.split("ref_")[1];
						String locus = ids_locus.get(temp.substring(0, temp.length()-1));
						//ids.add(locus);
						tmhmmScore.put(locus,number_of_helices);
					}
				}
			}
		}
		reader.close();
		return tmhmmScore;
	}


	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean isTMHMMFile(File file) throws IOException {
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file));
		String text = null;

		while ((text = reader.readLine()) != null) {

			if(text.trim().equalsIgnoreCase("TMHMM result")) {

				reader.close();
				return true;
			}

			if(text.trim().equalsIgnoreCase("Number of predicted TMHs")) {

				reader.close();
				return true;
			}
		}
		reader.close();
		return false;
	}

	/**
	 * @param file
	 * @param minimum_number_of_helices
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static Map<String,Integer> readTMHMMGenbankNCBI(File file, int minimum_number_of_helices) throws NumberFormatException, IOException{
		BufferedReader reader = null;
		Map<String,Integer> tmhmmScore=new TreeMap<String, Integer>();
		reader = new BufferedReader(new FileReader(file));
		String text = null;

		while ((text = reader.readLine()) != null) {

			if(text.startsWith("#")) {

				text=text.replace("# ", "");
				StringTokenizer st = new StringTokenizer(text," ");
				String id = st.nextToken();

				if(st.nextToken().equals("Number")) {

					st = new StringTokenizer(text,":");
					st.nextToken();
					int number_of_helices = Integer.parseInt(st.nextToken().trim());

					if(number_of_helices>=minimum_number_of_helices) {

						String separator = "\\|";

						if(id.contains("_") && !id.contains("|"))
							separator = "_";

						String temp;
						if(id.contains("ref")) {

							temp=id.split("ref"+separator)[1];
						} 
						else if(id.contains("emb")) {

							temp=id.split("emb"+separator)[1];
						}
						else if(id.contains("gb")) {

							temp=id.split("gb"+separator)[1];
						}
						else {

							System.out.println("NEW identifier on "+id);
							temp=null;
						}
						tmhmmScore.put(temp.substring(0, temp.length()-1),number_of_helices);
					}
				}
			}
		}
		reader.close();
		return tmhmmScore;
	}

	/**
	 * @param file
	 * @param minimum_number_of_helices
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static Map<String,Integer> readTMHMMGenbank(File file, int minimum_number_of_helices) throws NumberFormatException, NoSuchElementException, IOException{
		BufferedReader reader = null;
		Map<String,Integer> tmhmmScore=new TreeMap<String, Integer>();
		reader = new BufferedReader(new FileReader(file));
		String text = null;

		while ((text = reader.readLine()) != null) {

			if(text.startsWith("#")) {

				text=text.replace("# ", "");
				StringTokenizer st = new StringTokenizer(text," ");
				String id = st.nextToken();

				if(st.nextToken().equals("Number")) {

					st = new StringTokenizer(text,":");
					st.nextToken();
					int number_of_helices = Integer.parseInt(st.nextToken().trim());
					if(number_of_helices>=minimum_number_of_helices) {

						tmhmmScore.put(id,number_of_helices);
					}
				}
			}
		}
		reader.close();
		return tmhmmScore;
	}


	/**
	 * @param ids
	 * @param ids_locus
	 * @return
	 */
	public static Map<String, ProteinSequence> parse_locus(Map<String, ProteinSequence> ids){
		Map<String, ProteinSequence> locus_protein = new HashMap<String, ProteinSequence>();

		for(String key:ids.keySet())
		{
			locus_protein.put(key.split(" ")[0].replace("gnl|GLV|", ""),ids.get(key));
		}
		return locus_protein;
	}

	//		/**
	//		 * @param ids
	//		 * @return
	//		 */
	//		public static Map<String,String> get_ids_locus(Set<String> ids){
	//	
	//			List<String> locus = new ArrayList<String> (ids);
	//			List<String> locus_parsed = new ArrayList<String> ();
	//			
	//			for(int i=0; i<locus.size();i++)
	//			{
	//				String temp_string = locus.get(i).replace(".","\t").split("\t")[0];
	//				locus_parsed.set(i,temp_string);
	//			}
	//			return UtilsMethods.get_locus_from_id(locus);
	//		}



	/**
	 * @param ids
	 * @param ids_locus
	 * @return
	 */
	public static Map<String, ProteinSequence> replace_ids_with_locus(Map<String, ProteinSequence> ids, Map<String,String> ids_locus){
		Map<String, ProteinSequence> locus_protein = new HashMap<String, ProteinSequence>();
		//		Map<String, ProteinSequence> temp = new HashMap<String, ProteinSequence>();

		//		for(int i=0; i<locus.size();i++)
		//		{
		//			String temp_string = locus.get(i).replace(".","\t").split("\t")[0];
		//			temp.put(temp_string, ids.get(locus.get(i)));
		//		}
		//		ids_locus = UtilsMethods.get_locus_from_id(new ArrayList<String>(ids.keySet()));

		for(String key:ids.keySet())
		{
			String surrogate_key = key;
			//			if(key.contains("."))
			//			{
			//				surrogate_key=key.split("\\.")[0];
			//			}
			locus_protein.put(ids_locus.get(surrogate_key),ids.get(key));
		}
		//temp=null;
		return locus_protein;
	}

	/**
	 * @param ids
	 * @param ids_locus
	 * @return
	 */
	public static Map<String, Integer> replace_tmhmm_ids_with_locus(Map<String, Integer> ids, Map<String,String> ids_locus){
		Map<String, Integer> locus_tmhmm = new HashMap<String, Integer>();
		//		Map<String, ProteinSequence> temp = new HashMap<String, ProteinSequence>();

		//		for(int i=0; i<locus.size();i++)
		//		{
		//			String temp_string = locus.get(i).replace(".","\t").split("\t")[0];
		//			temp.put(temp_string, ids.get(locus.get(i)));
		//		}
		//		ids_locus = UtilsMethods.get_locus_from_id(new ArrayList<String>(ids.keySet()));

		for(String key:ids.keySet())
		{
			String surrogate_key = key;
			//			if(key.contains("."))
			//			{
			//				surrogate_key=key.split("\\.")[0];
			//			}
			locus_tmhmm.put(ids_locus.get(surrogate_key),ids.get(key));
		}
		//temp=null;
		return locus_tmhmm;
	}

	/**
	 * @param locus_tag_list
	 * @param initalNumberOfRequests
	 * @return
	 */
	public static Map<String, ProteinSequence> createSequencesMap(List<String> locus_tag_list, int initalNumberOfRequests ) throws Exception{
		
		try {
			
			boolean go=true;
			int ids_counter = 0;
			
			EntrezFetch entrezFetch = new EntrezFetch();
			Map<String, ProteinSequence> temp = new HashMap<>();
			
			while(go) {
				
				String query=locus_tag_list.get(ids_counter);
				int request_counter = 0;
				//while (ids_counter<locus_tag_list.size() && query.length()<8050 && request_counter<250)
				while (request_counter<initalNumberOfRequests) {
					
					//System.out.println(ids.get(ids_counter));
					query=query.concat(","+locus_tag_list.get(ids_counter));
					ids_counter++;
					request_counter++;
				}

				temp.putAll(entrezFetch.createSequencesMap(locus_tag_list, initalNumberOfRequests));
				if(ids_counter==locus_tag_list.size())
					go=false;
				
			}
			return temp;
		}
		catch (Exception e) {
			
				e.printStackTrace();
				System.err.println("get locus error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				throw e;
		}
	}

	/**
	 * @param homologuesData
	 * @param resultsList
	 * @param trialNumber
	 * @param isNCBIGenome
	 * @param cancel
	 * @param uniprotStatus 
	 * @return
	 * @throws Exception
	 */
	public static HomologuesData getNcbiData(HomologuesData homologuesData, List<Pair<String, String>> resultsList, 
			int trialNumber, boolean isNCBIGenome, AtomicBoolean cancel, boolean uniprotStatus) throws Exception {

		Map<String, String> taxonomyID = new HashMap<>();

		try {

			if(isNCBIGenome && !resultsList.get(0).getB().equalsIgnoreCase(homologuesData.getRefSeqGI()))
				resultsList.add(0, new Pair<String,String>(homologuesData.getQuery(), homologuesData.getRefSeqGI()));
			
			EntrezFetch entrezFetch = new EntrezFetch();
			homologuesData = entrezFetch.getNcbiData(homologuesData, isNCBIGenome, cancel, resultsList,  taxonomyID, uniprotStatus);
			homologuesData = NcbiAPI.processOrganismsTaxonomy(homologuesData, taxonomyID, taxonomyID.size(), 0, cancel);
		}
		catch (Exception e) {

			//e.printStackTrace();

			int newTrial = trialNumber+1;
			if(newTrial<20 && !cancel.get()) {

				try {

					Thread.sleep(2000); //miliseconds
				}
				catch (InterruptedException e1) {

					if(!cancel.get()) {
						
						throw e1;
					}
				}
				NcbiAPI.getNcbiData(homologuesData, resultsList, newTrial, isNCBIGenome, cancel, uniprotStatus);
			}
			else {
					homologuesData.setDataRetrieved(false);
					throw new Exception();
			}

		}
		return homologuesData;
	}

	/**
	 * @param ncbiData
	 * @param taxonomyID
	 * @param queryResponseConcatenationSize
	 * @param trialNumber
	 * @param cancel
	 * @return
	 * @throws InterruptedException
	 */
	public static HomologuesData processOrganismsTaxonomy(HomologuesData ncbiData, Map<String, String> taxonomyID,
			int queryResponseConcatenationSize, int trialNumber, AtomicBoolean cancel) throws InterruptedException {

		List<String> query = new ArrayList<String>();
		List<String> queryList = new ArrayList<String>();

		for(String taxID : taxonomyID.values()) {

			if(!ncbiData.getTaxonomyMap().containsKey(taxID)) {

				query.add(taxID);

				if(query.size() >= queryResponseConcatenationSize) {

					queryList.add(query.toString());
					query = new ArrayList<String>();
				}
			}
		}

		if(query.size() > 0) {

			queryList.add(query.toString());
		}

		try {

			EntrezTaxonomy entrezTaxon = new EntrezTaxonomy();

			for(String q : queryList)
				ncbiData.getTaxonomyMap().putAll(entrezTaxon.getTaxonList(q));

			for(String gene : taxonomyID.keySet()) {

				ncbiData.addOrganism(gene, ncbiData.getTaxonomyMap().get(taxonomyID.get(gene))[0]);
				ncbiData.addTaxonomy(gene, ncbiData.getTaxonomyMap().get(taxonomyID.get(gene))[1]);
			}
			return ncbiData;
		}
		catch (Exception e) {

			e.printStackTrace();

			int newTrial = trialNumber+1;

			if(newTrial<5 && !cancel.get()) {

				try {

					Thread.sleep(500); //miliseconds
				}
				catch (InterruptedException e1) {

					if(!cancel.get()) {

						throw e1;
					}
				}
				if(queryResponseConcatenationSize>1) {

					queryResponseConcatenationSize = queryResponseConcatenationSize/2;
				}
				processOrganismsTaxonomy(ncbiData, taxonomyID,queryResponseConcatenationSize,newTrial, cancel);
			}
			else {

				if(!cancel.get()) {

					ncbiData.setDataRetrieved(false);
				}
			}
		}
		return ncbiData;

	}

	/**
	 * @param orgID
	 * @param trialNumber
	 * @return
	 * @throws Exception
	 */
	public static List<Pair<String, String>> newNcbiRefSeqID(String orgID, int trialNumber) throws Exception {

		List<Pair<String, String>> results = null;
		List<String> query = new ArrayList<String>();
		query.add(orgID);

		try {

			EntrezSearch entrezSearch = new EntrezSearch();
			results = entrezSearch.getDatabaseIDs(NcbiDatabases.protein, query, 100);

			return results;
		}
		catch (Exception e) {

			if(trialNumber<15) {

				trialNumber=trialNumber+1;
				return NcbiAPI.newNcbiRefSeqID(orgID, trialNumber);
			}
			else {

				e.printStackTrace();
				System.out.println(NcbiAPI.class.toString()+" newOrgID error "+e.getMessage()+" trial "+trialNumber+"\nQuery\t"+query);
				throw e;
			}
		}
	}
	
	/**
	 * Get parsed UniProt entry data from NCBI accession.
	 * 
	 * @param query
	 * @return
	 */
	public static EntryData getEntryDataFromAccession(String accession) {

		try {
			return NcbiAPI.getEntryDataFromAccession(accession,0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param accession
	 * @param errorCount
	 * @return
	 * @throws Exception 
	 * 
	 */
	private static EntryData getEntryDataFromAccession(String accession, int errorCount) throws Exception {

		EntrezFetch entrezFetch = new EntrezFetch();
		return entrezFetch.getEntryDataFromAccession(accession, errorCount);
	}

	/**
	 * @param query
	 * @return
	 * @throws Exception 
	 */
	public static String getLocusTag(String query) throws Exception {

		EntrezFetch entrezFetch = new EntrezFetch();
		Set<String> querySet = new HashSet<String>();
		querySet.add(query);
		return entrezFetch.getLocusFromID(querySet,10).get(query);
	}

	/**
	 * @param resultsList 
	 * @param trialNumber
	 * @return 
	 * @throws Exception 
	 */
	public static List<Pair<String, String>> getProteinDatabaseIDS(List<String> resultsList, int trialNumber, int queryResponseConcatenationSize) throws Exception {

		List<Pair<String, String>> results = null;
		try {

			EntrezSearch entrezSearch =  new EntrezSearch();
			results = entrezSearch.getDatabaseIDs(NcbiDatabases.protein, resultsList, queryResponseConcatenationSize);
			return results;
		}
		catch (Exception e) {

			if(trialNumber<5) {

				trialNumber++;
				if(queryResponseConcatenationSize>1) {

					queryResponseConcatenationSize = queryResponseConcatenationSize/2;
				}
				return NcbiAPI.getProteinDatabaseIDS(resultsList, trialNumber, queryResponseConcatenationSize);
			}
			else {

				System.out.println(NcbiAPI.class+" homology data getProteinDatabaseIDS error "+e.getMessage()+" trial "+trialNumber);
				System.out.println(NcbiAPI.class+" "+resultsList);
				throw e;
			}
		}
	} 
	/**
	 * @param orgID
	 * @param trialNumber
	 * @return
	 * @throws Exception 
	 */
	public static String getNcbiGI(String orgID) throws Exception {

		List<Pair<String, String>> results = NcbiAPI.newNcbiRefSeqID(orgID, 0);

		String ret = orgID;
		if(results.size()>0)
			ret = results.get(0).getB();
		else
			System.out.println(NcbiAPI.class+" New locus for "+orgID+" currently unavailable.");

		return ret;
	}

	/**
	 * @param orgID
	 * @param trialNumber
	 * @return
	 * @throws Exception 
	 */
	public static String[] newTaxID(String orgID, int trialNumber) throws Exception {


		try {

			EntrezTaxonomy entrezTaxon = new EntrezTaxonomy();

			return entrezTaxon.getTaxonList(orgID).get(orgID);
		}
		catch (Error e) {

			throw new Error("Service unavailable");
		}
		catch (Exception e) {

			if(trialNumber<10) {

				trialNumber=trialNumber+10;
				return newTaxID(orgID, trialNumber);
			}
			else {

				System.out.println(NcbiAPI.class+" "+e.getMessage()+" trial "+trialNumber);
				throw e;
			}
		}
	}
	
	/**
	 * @param orgID
	 * @return
	 * @throws Exception 
	 */
	public static String[] ncbiNewTaxID(long orgID) throws Exception {

		try {

			return NcbiAPI.newTaxID(orgID+"", 0);
		}
		catch (Error e) {

			throw new Error("Service unavailable");
		}
		catch (Exception e) {

			throw e;
		}
	}
	
	/**
	 * @param taxID
	 * @param errorCount
	 * @return
	 * @throws AxisFault 
	 */
	public static TaxonomyContainer getTaxonomyFromNCBI(long taxID, int errorCount) throws Exception {

		TaxonomyContainer result = new TaxonomyContainer();
		Map<String,String[]> taxData = null;

		try {

			EntrezTaxonomy entrezTaxonomy = new EntrezTaxonomy();

			taxData = entrezTaxonomy.getTaxonList(taxID+"");

		}
		catch(Exception e) {

			if(errorCount<10) {

				errorCount = errorCount+1;
				return getTaxonomyFromNCBI(taxID, errorCount+1);
			}
			else {

				e.printStackTrace();
				return null;
			}
		}

		String[] myTax = taxData.get(taxID+"");
		List<NcbiTaxon> list_taxon = new ArrayList<NcbiTaxon>();

		int i = 0;
		for(String taxon : myTax[1].split("; ")) {

			list_taxon.add(i,new MyNcbiTaxon(taxon));
			i++;
		}

		result.setSpeciesName(myTax[0]);
		result.setTaxonomy(list_taxon);

		return result;
	}
	
	/**
	 * @param genome
	 * @return
	 * @throws Exception 
	 */
	public static String getGenomeID(Map<String, ProteinSequence> genome) throws Exception {

		if(genome.keySet().size()>0) {

			List<String> list = new ArrayList<>(genome.keySet());
			String geneID = list.get(0);

			EntrezFetch entrezFetch = new EntrezFetch();
			String res = entrezFetch.getTaxonomy(geneID);

			return res;
		}
		return null;

	}
	
	/**
	 * @param kingdom
	 * @throws RemoteException
	 */
	public void getOrganismList(EntrezLink.KINGDOM kingdom) throws Exception {
			
			EntrezLink entrezLink = new EntrezLink();
			EntrezSearch entrezSearch = new EntrezSearch();
			EntrezTaxonomy entrezTaxonomy = new EntrezTaxonomy();
			long startTime = System.currentTimeMillis();

			List<String> list_of_ids = entrezSearch.getGenomesIDs(kingdom);
			List<List<String>> linkList = entrezLink.getLinksList(list_of_ids, NcbiDatabases.genome,NcbiDatabases.taxonomy, 999);
			Map<String,String[]> result = entrezTaxonomy.getTaxID_and_Superkingdom(linkList, 0);

			List<String> ordered_name = new ArrayList<String>(result.keySet());
			java.util.Collections.sort(ordered_name);
			for(String name : ordered_name) {
				
				System.out.println(name +"\t ncbi id: "+result.get(name)[0] );
				System.out.println();
			}

			long endTime = System.currentTimeMillis();
			System.out.println("Total elapsed time in execution of query is :"+ String.format("%d min, %d sec", 
					TimeUnit.MILLISECONDS.toMinutes(endTime-startTime),TimeUnit.MILLISECONDS.toSeconds(endTime-startTime) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime-startTime))));

	}

	/**
	 * @param tax_id
	 * @return
	 * @throws Exception 
	 */
	public static List<String> getReferenceTaxonomy(long tax_id_long) throws Exception {

		String tax_id = tax_id_long+"";
		List<String> referenceTaxonomy = new ArrayList<>();
		Map<String, String[]> ncbi_ids;

		try {
			
			EntrezTaxonomy entrezTaxonomy = new EntrezTaxonomy();
			ncbi_ids = entrezTaxonomy.getTaxonList(tax_id);

			String[] taxonomy = ncbi_ids.get(tax_id)[1].split(";");

			for(String t : taxonomy)
				referenceTaxonomy.add(t.trim());

			referenceTaxonomy.add(ncbi_ids.get(tax_id)[0].trim());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return referenceTaxonomy;
	}
	
	/**
	 * @param tax_ids
	 * @return
	 * @throws Exception 
	 */
	public static Map<String,String[]> getTaxonList(String tax_ids) throws Exception {

		EntrezTaxonomy entrezTaxonomy = new EntrezTaxonomy();
	
		return entrezTaxonomy.getTaxonList(tax_ids);
	}
	
}
