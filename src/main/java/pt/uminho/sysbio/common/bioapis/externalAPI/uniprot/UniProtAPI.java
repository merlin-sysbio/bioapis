package pt.uminho.sysbio.common.bioapis.externalAPI.uniprot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.AccessionID;
import org.biojava.nbio.core.sequence.DataSource;
import org.biojava.nbio.core.sequence.ProteinSequence;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.list.ListUtilities;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.pair.Pair;
import pt.uminho.sysbio.common.bioapis.externalAPI.datatypes.HomologuesData;
import pt.uminho.sysbio.common.bioapis.externalAPI.utilities.MySleep;
import uk.ac.ebi.kraken.interfaces.common.Sequence;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Gene;
import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxonomyId;
import uk.ac.ebi.kraken.interfaces.uniprot.Organelle;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntryType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.GeneNameSynonym;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.ORFName;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.OrderedLocusName;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

/**
 * @author oscar
 *
 */
///*
// * Client Class has a couple of static methods to create a ServiceFactory instance.
// * From ServiceFactory, you can fetch the JAPI Services.
// */
//ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
//
//// UniProtService
//UniProtService uniprotService = serviceFactoryInstance.getUniProtQueryService();
//
//// UniParcService
//UniParcService uniparcService = serviceFactoryInstance.getUniParcQueryService();
//
//// UniRefService
//UniRefService unirefService = serviceFactoryInstance.getUniRefQueryService();
//
//// Blast Service
//BlastService blastService = serviceFactoryInstance.getBlastService();
//
//// UniProt Blast Service
//UniProtBlastService uniProtBlastService = serviceFactoryInstance.getUniProtBlastService();
//
//// Clustal Omega Service
//ClustalOService clustalOService = serviceFactoryInstance.getClustalOService();
//
//// UniProt Clustal Omega Service
//UniProtClustalOService uniProtClustalOService = serviceFactoryInstance.getUniProtClustalOService();
public class UniProtAPI {

	public static ServiceFactory serviceFactoryInstance = null;
	public static UniProtService uniProtService = null;
	public static UniProtAPI uniProtApi = null;

	/**
	 * 
	 */
	public UniProtAPI() {

		serviceFactoryInstance = Client.getServiceFactoryInstance();
		uniProtService = serviceFactoryInstance.getUniProtQueryService();
	}



	/**
	 * @param uniprot_id
	 * @param errorCount
	 * @return
	 */
	public static UniProtEntry getEntryFromUniProtID(String uniprot_id, int errorCount){

		UniProtAPI.getInstance();

		try {

			UniProtEntry entry = uniProtService.getEntry(uniprot_id);

			return entry;
		} 
		catch(Exception e) {

			if(errorCount<20) {

				errorCount+=1;
				MySleep.myWait(1000);
				System.out.println(UniProtAPI.class+"\tEntry From UniProt ID trial\t"+errorCount);
				return (UniProtEntry) getEntryFromUniProtID(uniprot_id, errorCount);
			}
			else {

				System.out.println(UniProtAPI.class+"\tcould not retrieve single entry. Returning null.");
				//printStackTrace();
				return null;
			}
		}
	}

	/**
	 * @param uniprot_id
	 * @param errorCount
	 * @return
	 */
	public static List<UniProtEntry> getEntriesFromUniProtIDs(Set<String> uniprotIDs, int errorCount){

		UniProtAPI.getInstance();

		try {

			List<UniProtEntry> uniprotEntries = new ArrayList<>();

			List<List<String>> uniprotIDs_subsets = ListUtilities.split(new ArrayList<>(uniprotIDs), 100);

			for(List<String> uniprotIDs_subset : uniprotIDs_subsets) {

				Query query = UniProtQueryBuilder.accessions(new HashSet<String> (uniprotIDs_subset));

				QueryResult<UniProtEntry> entries = uniProtService.getEntries(query);
				while (entries.hasNext()) {

					UniProtEntry entry = entries.next();
					uniprotEntries.add(entry);
				}
			}
			return uniprotEntries;
		} 
		catch(Exception e) {

			if(errorCount<20) {

				MySleep.myWait(1000);
				errorCount+=1;
				System.out.println(UniProtAPI.class+"\tEntries From UniProt IDs trial\t"+errorCount);
				return UniProtAPI.getEntriesFromUniProtIDs(uniprotIDs, errorCount);
			}
			else {

				//e.printStackTrace();
				System.out.println(UniProtAPI.class+"\tcould not retrieve entries list. Returning null.");
				return null;
			}
		}
	}

	/**
	 * @param locusTag
	 * @param errorCount
	 * @return
	 * @throws ServiceException 
	 */
	public static UniProtEntry getEntry(String locusTag, int errorCount) throws ServiceException {

		UniProtAPI.getInstance();

		try {

			String originalLocusTag = locusTag;
			Query query = UniProtQueryBuilder.gene(locusTag);

			QueryResult<UniProtEntry> entries = uniProtService.getEntries(query);

			while(entries.hasNext()) {

				UniProtEntry uniProtEntry = entries.next();

				if(locusTag.contains(".*")) {

					locusTag = locusTag.replace(".*","");
				}

				List<Gene> genes = uniProtEntry.getGenes();

				for(int i=0;i<genes.size();i++) {

					List<OrderedLocusName> locusTags = genes.get(i).getOrderedLocusNames();

					for(int j=0;j<locusTags.size();j++) {

						if(locusTags.get(j).getValue().equalsIgnoreCase(locusTag)) {

							i=genes.size();
							j=locusTags.size();

							return uniProtEntry;
						}
					}

					if(genes.get(i).getGeneName().getValue().equalsIgnoreCase(locusTag)) {


						return uniProtEntry;
					}

					List<ORFName> orfNames = genes.get(i).getORFNames();

					for(int j=0;j<orfNames.size();j++) {

						if(orfNames.get(j).getValue().equalsIgnoreCase(locusTag)) {

							i=genes.size();
							j=orfNames.size();

							return uniProtEntry;
						}
					}
					List<GeneNameSynonym> geneNameSynonyms = genes.get(i).getGeneNameSynonyms();

					for(int j=0;j<geneNameSynonyms.size();j++) {

						if(geneNameSynonyms.get(j).getValue().equalsIgnoreCase(locusTag)) {

							i=genes.size();
							j=geneNameSynonyms.size();

							return uniProtEntry;
						}
					}
				}
				//search for locus in kegg also
				//				for(DatabaseCrossReference dbcr : uniProtEntry.getDatabaseCrossReferences(DatabaseType.KEGG))
				//				{
				//					if(dbcr.toString().split(":")[1].equalsIgnoreCase(locusTag))
				//					{
				//						return uniProtEntry;
				//					}
				//				}
				for(DatabaseCrossReference dbcr : uniProtEntry.getDatabaseCrossReferences()) {

					String[] xRefs = dbcr.toString().split(":");

					for(String xRef:xRefs) {

						if(xRef.contains(locusTag)) {

							return uniProtEntry;
						}
					}
				}

				//				for(DatabaseCrossReference dbcr : uniProtEntry.getDatabaseCrossReferences())
				//				{
				//					String[] xRefs = dbcr.toString().split(":");
				//					for(String xRef:xRefs){
				//						if(xRef.contains(".")){
				//							xRef=xRef.split("\\.")[0];
				//							System.out.println(xRef);
				//							}
				//						if(xRef.equalsIgnoreCase(locusTag)){
				//						return uniProtEntry;
				//						}
				//					}
				//				}
			}
			if(!originalLocusTag.contains(".")) {

				originalLocusTag=originalLocusTag.concat(".*");

				return getEntry(originalLocusTag, 0);
			}

			return null;
		} 
		catch(ServiceException e) {

			if(errorCount<10) {

				MySleep.myWait(1000);
				return getEntry(locusTag, errorCount+1);
			}
			else {

				throw e;
			}
		}
		catch(OutOfMemoryError err) {

			if(errorCount<10) {

				MySleep.myWait(1000);
				return getEntry(locusTag, errorCount+1);
			}
			else {

				throw err;
			}
		}
	}

	/**
	 * @param crossReference
	 * @param errorCount
	 * @return
	 */
	public static UniProtEntry getUniProtEntryFromXRef(String crossReference, int errorCount) {

		UniProtAPI.getInstance();

		try {

			Query query = UniProtQueryBuilder.xref(crossReference);
			QueryResult<UniProtEntry> entries = uniProtService.getEntries(query);

			if(entries.getNumberOfHits()<1) {
				query = UniProtQueryBuilder.xref(crossReference.split("\\.")[0]);
				entries = uniProtService.getEntries(query);
			}

			while(entries.hasNext()) {

				UniProtEntry uniProtEntry = entries.next();

				//System.out.println("Primary Acc = " +uniProtEntry.getPrimaryUniProtAccession().getValue());
				Iterator<DatabaseCrossReference> it = uniProtEntry.getDatabaseCrossReferences().iterator();
				while(it.hasNext()) {

					DatabaseCrossReference dbcr = it.next();

					//DatabaseType database = dbcr.getDatabase();
					//if(database.equals(DatabaseType.REFSEQ)) {	

					String x_ref = dbcr.getPrimaryId().getValue();

					for(String id:x_ref.split(":")) {

						if(id.trim().equalsIgnoreCase(crossReference.trim())) {


							return uniProtEntry;
						}

						id = id.trim().split("\\.")[0];
						String cross = crossReference.split("\\.")[0];

						if(id.equalsIgnoreCase(cross)) {


							return uniProtEntry;
						}
					}

				}
			}
			return null;
		}
		catch(Exception e) {

			if(errorCount<20) {

				MySleep.myWait(1000);
				errorCount+=1;
				System.out.println(UniProtAPI.class+"\t xRef trial\t"+errorCount);
				return getUniProtEntryFromXRef(crossReference, errorCount);
			}
			else {

				System.out.println(UniProtAPI.class+"\tcould not retrieve single entry from xRef. Returning null. "+crossReference);
				return null;
			}
		}
	}

	/**
	 * @param crossReferences
	 * @return
	 */
	public static Map<String, String> getUniProtLocus(Collection<String> crossReferences) {

		Map<String, String> out = new TreeMap<>();

		for(String crossReference : crossReferences) {

			String locus = UniProtAPI.getLocusTag(UniProtAPI.getUniProtEntryFromXRef(crossReference, 0));
			out.put(crossReference, locus);
		}

		return out;
	}

	/**
	 * @param taxID
	 * @param errorCount
	 * @return
	 */
	public static TaxonomyContainer getTaxonomyFromNCBITaxnomyID(long taxID, int errorCount) {

		UniProtAPI.getInstance();

		TaxonomyContainer result = new TaxonomyContainer();

		try {

			Query query = UniProtQueryBuilder.gene(""+taxID);

			QueryResult<UniProtEntry> entries = uniProtService.getEntries(query);

			while(entries.hasNext()) {

				UniProtEntry uniProtEntry = entries.next();
				List<NcbiTaxonomyId> taxa = uniProtEntry.getNcbiTaxonomyIds();

				for(NcbiTaxonomyId taxon : taxa) {

					if(taxon.getValue().equalsIgnoreCase(""+taxID)) {

						result.setSpeciesName(uniProtEntry.getOrganism().getScientificName().getValue());
						result.setTaxonomy(uniProtEntry.getTaxonomy());


						return result;
					}
				}
			}
			return null;
		}
		catch(ServiceException e) {

			if(errorCount<10) {

				MySleep.myWait(1000);
				errorCount = errorCount+1;
				return getTaxonomyFromNCBITaxnomyID(taxID, errorCount+1);
			}
			else {

				e.printStackTrace();

				return null;
			}
		}

	}


	/**
	 * @param entry
	 * @return
	 */
	public static String getProteinExistence(UniProtEntry entry){

		if(entry!=null) {

			return entry.getProteinExistence().getValue();
		}
		return null;
	}

	//	/**
	//	 * @param entry
	//	 * @return
	//	 */
	//	public static List<OrderedLocusName> getLocusTag(UniProtEntry entry) {
	//
	//		if(entry!=null) {
	//
	//			if(entry.getGenes().size()>0) {
	//				
	//				entry.getGenes().get(0).getORFNames();	
	//				return entry.getGenes().get(0).getOrderedLocusNames();	
	//			}
	//		}
	//		return null;
	//	}
	//	
	/**
	 * @param entry
	 * @return
	 */
	public static List<String> getLocusTags(UniProtEntry entry) {

		List<String> out = null;

		if(entry!=null) {

			out = new ArrayList<String>();

			if(entry.getGenes().size()>0) {

				if(entry.getGenes().get(0).getOrderedLocusNames()!=null) {

					for(OrderedLocusName oln : entry.getGenes().get(0).getOrderedLocusNames())
						out.add(oln.getValue());
				}
				else if(entry.getGenes().get(0).getORFNames()!=null) {

					for(ORFName oln : entry.getGenes().get(0).getORFNames())
						out.add(oln.getValue());
				}
			}
		}
		return out;
	}

	/**
	 * @param query
	 * @return
	 */
	public static String getFirstLocusTag(String query) {

		UniProtEntry uniProtEntry = UniProtAPI.getUniProtEntryFromXRef(query,0);

		return UniProtAPI.getLocusTags(uniProtEntry).get(0);//.getValue();
	}

	/**
	 * @param entry
	 * @return
	 */
	public static Set<String> get_ecnumbers(UniProtEntry entry) {

		Set<String> out = new HashSet<>();

		if(entry!=null) {

			for(String ec : entry.getProteinDescription().getEcNumbers())
				out.add(ec.trim());

			return out;
		}
		return null;
	}

	/**
	 * @param entry
	 * @return
	 */
	public static List<String> getECnumbers(UniProtEntry entry){
		if(entry!=null)
		{
			if(entry.getProteinDescription()!=null)
			{
				if(entry.getProteinDescription().getEcNumbers()!=null)
				{
					List<String> ecs = entry.getProteinDescription().getEcNumbers();
					if(!ecs.isEmpty())
					{
						return ecs;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param entry
	 * @return
	 */
	public static String getOrganism(UniProtEntry entry){

		if(entry!=null)
		{
			String organism = entry.getOrganism().getScientificName().getValue();
			if(organism!=null)
			{
				return organism;
			}
		}
		return null;
	}

	/**
	 * @param entryRetrievalService
	 * @param uniprotID
	 * @param errorCount
	 * @return
	 */
	public static UniProtEntry getUniprotEntry(String uniprotID, int errorCount){

		UniProtAPI.getInstance();

		UniProtEntry entry = null;
		try
		{
			entry = (UniProtEntry) uniProtService.getEntry(uniprotID);
			return entry;
		}
		catch(ServiceException e){

			if(errorCount<10){

				MySleep.myWait(1000);
				entry=getUniprotEntry(uniprotID, (errorCount+1)); 
			}
		}

		return entry;

	}


	/**
	 * @param locusTag
	 * @param errorCount
	 * @return
	 */
	public static TaxonomyContainer setOriginOrganism(String locusTag, int errorCount) {

		TaxonomyContainer result= new TaxonomyContainer();

		UniProtAPI.getInstance();

		try {

			Query query = UniProtQueryBuilder.gene(locusTag);

			QueryResult<UniProtEntry> entries = uniProtService.getEntries(query);

			while(entries.hasNext()) {

				UniProtEntry uniProtEntry = entries.next();

				if (uniProtEntry != null) {

					result.setSpeciesName(uniProtEntry.getOrganism().getScientificName().getValue());
					result.setTaxonomy(uniProtEntry.getTaxonomy());

					return result;
				}
			}
		}
		catch(ServiceException e) { 

			if(errorCount<10) {

				errorCount = errorCount+1;
				MySleep.myWait(1000);
				result=UniProtAPI.setOriginOrganism(locusTag, errorCount);
			}
		}

		return result;
	}

	/**
	 * @param entry
	 * @return
	 */
	public static TaxonomyContainer get_uniprot_entry_organism(String entry){

		TaxonomyContainer result = new TaxonomyContainer();

		UniProtEntry uniProtEntry = UniProtAPI.getEntryFromUniProtID(entry,0);
		if(uniProtEntry==null) {

			result.setSpeciesName("deleted");
			System.err.println("deleted "+entry);
		}
		else {

			result.setSpeciesName(uniProtEntry.getOrganism().getScientificName().getValue());
			result.setTaxonomy(uniProtEntry.getTaxonomy());
		}
		return result;

	}


	/**
	 * @param entry
	 * @return
	 */
	public static String[] getOrganismTaxa(UniProtEntry entry) {
		String [] taxon = new String[2];
		if(entry!=null)
		{
			String organism = entry.getOrganism().getScientificName().getValue();
			if(organism!=null)
			{
				taxon[0] = organism;
			}
			String tax = entry.getTaxonomy().toString();
			if(tax!=null)
			{
				taxon[1] = tax;
			}
			return taxon;
		}
		return null;
	}


	/**
	 * @param locusTag
	 * @return
	 * @throws ServiceException 
	 */
	public static boolean isStarred(String locusTag) throws ServiceException {

		UniProtEntry entry = UniProtAPI.getEntry(locusTag,0);

		return UniProtAPI.isStarred(entry);
	}

	/**
	 * @param entry
	 * @return
	 */
	public static boolean isStarred(UniProtEntry entry) {

		if(entry==null) {

			throw new NullPointerException("UniProt Entry Does not exist!");
		}

		if(entry.getType().getValue().equals( UniProtEntryType.SWISSPROT.getValue())) {

			return true;
		}
		return false;
	}

	//	/**
	//	 * @param ecNumber
	//	 * @param reviewed
	//	 * @return
	//	 */
	//	public static EntryIterator<UniProtEntry> getEntriesByECNumber(String ecNumber, boolean reviewed) {
	//		Query query = UniProtQueryBuilder.buildECNumberQuery(ecNumber);
	//		if(reviewed)
	//			query = UniProtQueryBuilder.setReviewedEntries(query);
	//
	//		return uniProtQueryService.getEntryIterator(query);
	//
	//	};

	/**
	 * @param entry
	 * @return
	 * @throws CompoundNotFoundException 
	 */
	public static ProteinSequence getProteinSequenceFromEntry(UniProtEntry entry) throws CompoundNotFoundException {
		Sequence sequence = entry.getSequence();
		ProteinSequence proteinSequence = new ProteinSequence(sequence.getValue());
		AccessionID accession = 
				new AccessionID(entry.getUniProtId().toString(), DataSource.UNIPROT);
		proteinSequence.setAccession(accession);
		return proteinSequence;
	}

	/**
	 * @param query
	 * @return
	 */
	public static String getUniprotStatus(String query) {

		String star = null;
		try {

			UniProtEntry uniProtEntry = UniProtAPI.getUniProtEntryFromXRef(query,0);

			star = UniProtAPI.isStarred(uniProtEntry)+"";
		}
		catch(Exception e) {

			star = null;
			//e.printStackTrace();
		}
		return star;
	}

	/**
	 * @param query
	 * @return
	 */
	public static String getUniprotECnumbers(String query) {

		String uniprot_ecnumber = null;

		try {

			UniProtEntry uniProtEntry = UniProtAPI.getUniProtEntryFromXRef(query,0);

			Set<String> ecnumbers = UniProtAPI.get_ecnumbers(uniProtEntry);
			if(ecnumbers!= null) {

				uniprot_ecnumber = "";

				for(String ecnumber : ecnumbers) {

					uniprot_ecnumber += ecnumber+", ";
				}
				if(uniprot_ecnumber.contains(", ")) {

					uniprot_ecnumber = uniprot_ecnumber.substring(0, uniprot_ecnumber.lastIndexOf(", "));
				}
			}
		}
		catch(Exception e) {

			e.printStackTrace();
		}
		return uniprot_ecnumber;
	}

	/**
	 * @param uniprotID
	 * @return
	 */
	public static String getRefSeq(String uniprotID) {

		try {

			UniProtEntry uniProtEntry = UniProtAPI.getEntryFromUniProtID(uniprotID, 0);

			if(uniProtEntry!=null && uniProtEntry.getDatabaseCrossReferences(DatabaseType.REFSEQ)!=null) {

				for(DatabaseCrossReference dbcr : uniProtEntry.getDatabaseCrossReferences(DatabaseType.REFSEQ)) {

					for(String ref : dbcr.toString().split(":")) {

						//XM_, XR_, and XP_
						//NM_, NR_, and NP_
						//Nucleotide
						//ref.contains("XM")
						//ref.contains("NM")
						//ref.contains("XR")
						if(ref.contains("NR"))
							System.out.println(UniProtAPI.class+" "+ref);

						if(ref.contains("XM") || ref.contains("XR") ||ref.contains("XP") || ref.contains("NM") ||ref.contains("NR") || ref.contains("NP"))
							return  ref.replace(";", "");
					}
				}
			}
			else {

				System.out.println(UniProtAPI.class+" "+uniProtEntry.getDatabaseCrossReferences()+" "+uniProtEntry.getDatabaseCrossReferences()!= null);
			}
			return null;
		}
		catch (Exception e) {

			System.out.println(uniprotID);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param uniprotID
	 * @return
	 */
	public static Map<String, String> getRefSeq(Set<String> uniprotIDs) {

		try {

			Map<String, String> ret = new HashMap<>();
			List<UniProtEntry> uniProtEntries = UniProtAPI.getEntriesFromUniProtIDs(uniprotIDs,0);

			for(UniProtEntry uniProtEntry: uniProtEntries) {

				if(uniProtEntry!=null && uniProtEntry.getDatabaseCrossReferences(DatabaseType.REFSEQ)!=null) {

					for(DatabaseCrossReference dbcr : uniProtEntry.getDatabaseCrossReferences(DatabaseType.REFSEQ)) {

						for(String ref : dbcr.toString().split(":")) {

							//XM_, XR_, and XP_
							//NM_, NR_, and NP_
							//Nucleotide
							//ref.contains("XM")
							//ref.contains("NM")
							//ref.contains("XR")
							if(ref.contains("NR"))
								System.out.println("NR "+ref);

							if( ref.contains("XP") || ref.contains("NR") || ref.contains("NP"))
								ret.put(uniProtEntry.getPrimaryUniProtAccession().getValue(), ref.replace(";", ""));
						}
					}
				}
				else {

					System.out.print(UniProtAPI.class+" "+uniProtEntry.getDatabaseCrossReferences());
					System.out.println(" "+uniProtEntry.getDatabaseCrossReferences()!= null);
				}
			}
			return ret;
		}
		catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param refSeqIDs
	 * @param cancel
	 * @param errorCount
	 * @return
	 * @throws Exception 
	 */
	public static Map<String, List<UniProtEntry>> getUniprotEntriesFromRefSeq(List<String> refSeqIDs, AtomicBoolean cancel, int errorCount) throws Exception {

		try {

			Map<String, List<UniProtEntry>> ret = new HashMap<>();

			List<String> ids = new ArrayList<>(refSeqIDs);

			int counter = 0;
			while(ids.size()>0 && counter<20) {

				ids = UniProtAPI.processXRefsData(ids, ret, cancel);
				counter++;
			}
			return ret;
		}
		catch (Exception e) {

			errorCount+=1;

			if(errorCount<20) {

				System.out.println("getUniprotEntriesFromRefSeq trial "+errorCount);
				MySleep.myWait(1000);

				return UniProtAPI.getUniprotEntriesFromRefSeq(refSeqIDs, cancel, errorCount);
			}
			else{

				e.printStackTrace();
				System.out.println(refSeqIDs);
				throw new Exception();
			}
		}
	}

	/**
	 * @param refSeqIDs
	 * @param uniprotEntries
	 * @param cancel
	 * @return
	 * @throws ServiceException
	 */
	private static List<String> processXRefsData(List<String> refSeqIDs, Map<String, List<UniProtEntry>> uniprotEntries, AtomicBoolean cancel) throws ServiceException {

		List<String> ret = new ArrayList<>();

		UniProtAPI.getInstance();

		for(String refSeqID: refSeqIDs) {

			List<UniProtEntry> temp = new ArrayList<>();
			Query query = UniProtQueryBuilder.or(UniProtQueryBuilder.xref(refSeqID), 
					UniProtQueryBuilder.gene(refSeqID),
					UniProtQueryBuilder.id(refSeqID),
					UniProtQueryBuilder.keyword(refSeqID),
					UniProtQueryBuilder.proteinName(refSeqID),
					UniProtQueryBuilder.accession(refSeqID));
			QueryResult<UniProtEntry> entries = uniProtService.getEntries(query);

			try {

				if(entries.getNumberOfHits()<1) {

					String parsedRefSeqID = refSeqID.split("\\.")[0];
					query =  UniProtQueryBuilder.or(UniProtQueryBuilder.xref(parsedRefSeqID), 
							UniProtQueryBuilder.gene(parsedRefSeqID),
							UniProtQueryBuilder.id(parsedRefSeqID),
							UniProtQueryBuilder.keyword(parsedRefSeqID),
							UniProtQueryBuilder.proteinName(parsedRefSeqID),
							UniProtQueryBuilder.accession(parsedRefSeqID));
					entries = uniProtService.getEntries(query);
				}

				while(entries.hasNext() && !cancel.get()) {

					UniProtEntry uniProtEntry = entries.next();
					temp.add(uniProtEntry);
				}
			} 
			catch (Exception e) {

				ret.add(refSeqID);
			}
			uniprotEntries.put(refSeqID,temp);
		}		

		return ret;
	}

	/**
	 * @param homologuesData
	 * @param homologuesList
	 * @param cancel
	 * @param uniprotStatus
	 * @param isNCBIGenome
	 * @return
	 * @throws Exception
	 */
	public static HomologuesData getUniprotData(HomologuesData homologuesData, List<Pair<String, String>> homologuesList, 
			AtomicBoolean cancel, boolean uniprotStatus, boolean isNCBIGenome) throws Exception {

		List<String> genesList = new ArrayList<>();

		UniProtEntry geneEntry = UniProtAPI.getEntryFromUniProtID(homologuesData.getUniProtEntryID(),0); 

		if(geneEntry!=null)
			genesList.add(0,geneEntry.getPrimaryUniProtAccession().getValue());

		for(int i = 0; i<homologuesList.size();i++)
			genesList.add(i,homologuesList.get(i).getA());

		int dummy = 0;

		while(dummy<(genesList.size())) {

			homologuesData.addLocusID("",dummy);
			dummy++;
		}

		List<UniProtEntry> entriesList = UniProtAPI.getEntriesFromUniProtIDs(new HashSet<>(genesList),0);

		for(int i=0 ; i<entriesList.size() ; i++) {

			UniProtEntry entry = entriesList.get(i);

			String primary_accession = entry.getPrimaryUniProtAccession().getValue();

			//System.out.println("primary_accession "+primary_accession);
			//System.out.println("homologuesData.getUniProtEntryID() "+homologuesData.getUniProtEntryID());

			String locus = UniProtAPI.getLocusTag(entry);

			if(primary_accession.equalsIgnoreCase(homologuesData.getUniProtEntryID())) {

				homologuesData.addEValue(primary_accession,0.0);
				homologuesData.addBits(primary_accession,-1);
				homologuesData.setLocusTag(locus);

				if(isNCBIGenome) {

					homologuesData.setOrganismID(entry.getOrganism().getScientificName().getValue());
				}
				else {

					homologuesData.setOrganismID(homologuesData.getTaxonomyID()[0]);
				}
				homologuesData.setFastaSequence(entry.getSequence().getValue());

				try {
					homologuesData.setGene(entry.getGenes().get(0).getGeneName().getValue());
				}
				catch (Exception e) {e.printStackTrace();}
			}

			homologuesData.addLocusID(primary_accession, genesList.indexOf(primary_accession));
			homologuesData.addLocusTag(primary_accession, locus);
			homologuesData.addBlastLocusTags(primary_accession, locus);

			String name = null;

			try {

				name = entry.getProteinDescription().getRecommendedName().getFieldsByType(FieldType.FULL).get(0).getValue();
			}
			catch (Exception e) {name = null;}

			if(name == null) {

				try {

					name = entry.getProteinDescription().getSubNames().get(0).getFieldsByType(FieldType.FULL).get(0).getValue();
				} catch (Exception e) {name=null;}
			}

			homologuesData.addDefinition(primary_accession, name);
			homologuesData.addProduct(primary_accession, name);		
			homologuesData.addOrganism(primary_accession, entry.getOrganism().getScientificName().getValue());
			homologuesData.addTaxonomy(primary_accession, entry.getTaxonomy().toString().replace(",", ";").replace("[", "").replace("]", ""));
			homologuesData.addUniprotStatus(locus, entry.getType().getValue().equals(UniProtEntryType.SWISSPROT.getValue()));

			try {

				List<String> le = entry.getProteinDescription().getEcNumbers();
				if(le.size()>0) {

					String[] ec = new String[le.size()]; 
					homologuesData.addECnumbers(primary_accession, le.toArray(ec));
				}
			} catch (Exception e) {e.printStackTrace();}

			try {
				List<Organelle> organelles = entry.getOrganelles();
				if(organelles.size()>0)
					homologuesData.addOganelles(primary_accession, organelles.toString());

			} catch (Exception e) {e.printStackTrace();}


		}

		return homologuesData;
	}

	/**
	 * @param query
	 * @return
	 */
	public static EntryData getEntryData(String query) {

		return UniProtAPI.getEntryData(query,0);
	}

	/**
	 * @param entry
	 * @return
	 */
	public static String getLocusTag(UniProtEntry entry) {

		String out = null;

		try {

			out = entry.getGenes().get(0).getOrderedLocusNames().get(0).getValue();
		}
		catch(Exception e) {
			out=null;
		}

		if(out==null) {

			try {

				out = entry.getGenes().get(0).getORFNames().get(0).getValue();
			}
			catch(Exception e) {

				out = entry.getPrimaryUniProtAccession().getValue();
			}
		}
		return out;
	}

	/**
	 * @param query
	 * @param errorCount
	 * @return
	 */
	private static EntryData getEntryData(String query, int errorCount) {

		EntryData entry;

		UniProtEntry uniProtEntry;

		uniProtEntry = UniProtAPI.getUniProtEntryFromXRef(query,0);

		try {



			if(uniProtEntry==null)
				uniProtEntry = UniProtAPI.getUniprotEntry(query, errorCount);

			entry = new EntryData(uniProtEntry.getPrimaryUniProtAccession().getValue());

			String uniprot_ecnumber = null;
			Set<String> ecnumbers = UniProtAPI.get_ecnumbers(uniProtEntry);
			if(ecnumbers!= null) {

				uniprot_ecnumber = "";

				for(String ecnumber : ecnumbers) {

					uniprot_ecnumber += ecnumber+", ";
				}
				if(uniprot_ecnumber.contains(", ")) {

					uniprot_ecnumber = uniprot_ecnumber.substring(0, uniprot_ecnumber.lastIndexOf(", "));
				}
			}
			entry.setEcnumber(uniprot_ecnumber);
			entry.setUniprotReviewStatus(UniProtAPI.isStarred(uniProtEntry)+"");

			String locus = null;

			try {

				locus = uniProtEntry.getGenes().get(0).getOrderedLocusNames().get(0).getValue();
			}
			catch(Exception e) {locus=null;}

			if(locus==null) {

				try {

					locus = uniProtEntry.getGenes().get(0).getORFNames().get(0).getValue();
				}
				catch(Exception e) {}
			}

			if(locus==null) {

				try {

					locus = uniProtEntry.getGenes().get(0).getGeneName().getValue();
				}
				catch(Exception e) {locus = uniProtEntry.getPrimaryUniProtAccession().getValue();}
			}

			entry.setLocusTag(locus);

		}
		catch (Exception e) {

			errorCount+=1;
			//e. printStackTrace();

			if(errorCount<3) {

				MySleep.myWait(1000);
				return UniProtAPI.getEntryData(query, errorCount);
			}
			else {

				if(errorCount<5) {

					MySleep.myWait(1000);
					if(query.contains(".")) {

						query = query.split("\\.")[0];
					}
					return UniProtAPI.getEntryData(query+".*", errorCount);
				}

				else {

					entry = new EntryData(query.replace(".*", ""));
					entry.setUniprotReviewStatus(null);
					entry.setEcnumber(null);
					entry.setLocusTag(query.replace(".*", ""));
				}
			}
		}

		return entry;
	}

	/**
	 * @param orgID
	 * @param i
	 * @return
	 */
	public static String[] newTaxID(String organismmName, int i) {

		UniProtAPI.getInstance();

		try {
			String[] result = new String[2];

			Query query = UniProtQueryBuilder.organismName(organismmName);
			QueryResult<UniProtEntry> entries = uniProtService.getEntries(query);

			while (entries.hasNext()) {

				UniProtEntry entry = entries.next();
				result[0] = entry.getOrganism().getScientificName().getValue();
				result[1] = entry.getTaxonomy().toString().replace(",", ";").replace("[", "").replace("]", "");

				return result;
			}

		} catch (ServiceException e) {

			i++;
			if(i<10)
				return newTaxID(organismmName, i);
		}

		return null;
	} 


	/**
	 * @param accessionNumber
	 * @param organism
	 * @return
	 */
	public static String retrieveLocusTagIfOrganism(String accessionNumber, String organism) {

		UniProtAPI.getInstance();
		UniProtAPI.getInstance();

		//Retrieve UniProt entry by its accession number
		UniProtEntry entry = UniProtAPI.getUniprotEntry(accessionNumber, 0);

		//If entry with a given accession number is not found, entry will be equal null
		if (entry != null) {

			if(entry.getOrganism().getScientificName().getValue().contains(organism)) {

				//System.out.println("entry = " + entry.getUniProtId().getValue());
				if(entry.getGenes().size()>0 && entry.getGenes().get(0).getOrderedLocusNames().size()>0) {

					String locusTag = entry.getGenes().get(0).getOrderedLocusNames().get(0).getValue();
					if(locusTag!=null) {

						return locusTag;
					}
				}
			}
		}

		return "";

		//Retrieve UniRef entry by its ID
		//	    UniRefEntry uniRefEntry = entryRetrievalService.getUniRefEntry("UniRef90_Q12979-2");
		//
		//	    if (uniRefEntry != null) {
		//	      System.out.println("Representative Member Organism = " +
		//	       uniRefEntry.getRepresentativeMember().getSourceOrganism().getValue());
		//	    }
	}

	/**
	 * @return
	 */
	public static UniProtAPI getInstance() {

		if(uniProtApi == null)
			UniProtAPI.uniProtApi = new UniProtAPI();

		if(!uniProtService.isStarted())
			uniProtService.start();

		return uniProtApi;
	}

	/**
	 * 
	 */
	public static void stopUniProtService(){

		UniProtAPI.getInstance();
		uniProtService.stop();
	}
}
