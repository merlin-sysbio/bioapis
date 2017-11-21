package pt.uminho.sysbio.common.bioapis.externalAPI.ncbi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.template.AbstractSequence;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.pair.Pair;
import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;
import pt.uminho.sysbio.common.bioapis.externalAPI.ebi.uniprot.UniProtAPI;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.DocumentSummary;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.DocumentSummarySet;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ESearchResult;
import pt.uminho.sysbio.common.bioapis.externalAPI.ncbi.containers.ESummaryResult;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

public class CreateGenomeFile {

	public static String tempPath = FileUtils.getCurrentTempDirectory();
	private static String today = CreateGenomeFile.setToday();
	public static String fastaName = "codingSequences";
	private static String aaFastaExtension = "_protein.faa";
	private static String naFastaExtension = "_genomica.fna";
	
	

//	/**
//	 * @param genomeID
//	 * @param numberOfDaysOld
//	 * @param sourceDB
//	 * @throws IOException
//	 * @throws ParseException
//	 */
//	public static CreateGenomeFile(String databaseName, int numberOfDaysOld, String sourceDB, String extension) throws Exception {
//		//databaseName
//		this.createTempFile(numberOfDaysOld, sourceDB, extension);
//	}
//

	/**
	 * @param genomeID
	 * @param extension
	 * @return
	 * @throws Exception
	 */
	public static Map<String, AbstractSequence<?>> getGenomeFromID(String databaseName, String extension) throws Exception {
		
		try {

//			String nameFastaFile = "coding_sequences";
			
			if (!CreateGenomeFile.currentTemporaryDataIsNOTRecent(0,tempPath, databaseName, CreateGenomeFile.setToday(), extension)) {

				Map<String, AbstractSequence<?>> ret = new HashMap<>();
				Map<String,ProteinSequence> aas = FastaReaderHelper.readFastaProteinSequence(new File(tempPath+databaseName+fastaName+extension));
				ret.putAll(aas);
				
				return ret;
			}

		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}
		return null;
	}

//	/**
//	 * @param genomeID
//	 * @param path
//	 * @param extension
//	 * @return
//	 * @throws Exception
//	 */
//	public static Map<String, ProteinSequence> getGenomeFromID(String genomeID, String path, String extension) throws Exception {
//
//		if(!CreateGenomeFile.currentTemporaryDataIsNOTRecent(0,path, genomeID, CreateGenomeFile.setToday(), extension)) {
//
//			return FastaReaderHelper.readFastaProteinSequence(new File(path+genomeID+extension));
//		}
//		return null;
//	}
	
	
	/** Retrieves assembly information for a given taxonomyID
	 * @param taxonomyID
	 * @return eSummaryReport (GenBank and RefSeq accessions, GenBank and RefSeq ftps url,...)
	 */
	public static DocumentSummarySet getESummaryFromNCBI(String taxonomyID) {
		
		EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("https://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
        EntrezService entrezService = entrezServiceFactory.build();
        
        ESearchResult eSearchResult = entrezService.eSearch(NcbiDatabases.assembly, taxonomyID +"[Taxonomy ID]", "xml", "100");
        
        List<String> idList = eSearchResult.idList;
        String uids = idList.get(0);
        idList.remove(0);
        for(String i:eSearchResult.idList)
        	uids += ","+i;
        
        ESummaryResult eSummaryResult = entrezService.eSummary(NcbiDatabases.assembly, uids);
        DocumentSummarySet docSummarySet = eSummaryResult.documentSummarySet.get(0);
        
        return docSummarySet;
	}
	
	
	
	/** Given an assembly UID and a DocumentSummarySet, retrives the desired ftp url
	 * @param uid
	 * @param docSumSet
	 * @param isGenBankFtp
	 * @return (GenBank/RefSeq) ftp URL
	 */
	public static ArrayList<String> getFtpURLFromAssemblyUID(String uid, DocumentSummarySet docSumSet, boolean isGenBankFtp){
		
		DocumentSummary document = null;

		for (int i=0; i<docSumSet.documentSummary.size(); i++) {
			DocumentSummary doc = docSumSet.documentSummary.get(i);
			if (doc.uid == uid)
				document = doc;
		}
		ArrayList<String> ftpURLinfo = new ArrayList<>();
		
		if(isGenBankFtp) {
			ftpURLinfo.add(document.ftpGenBank);
			ftpURLinfo.add(document.accessionGenBank);
			ftpURLinfo.add(document.assemblyName);
		}
		else {
			ftpURLinfo.add(document.ftpRefSeq);
			ftpURLinfo.add(document.accessionRefSeq);
			ftpURLinfo.add(document.assemblyName);
		}
		
		return ftpURLinfo;		
	}
	
	
	
	/**
	 * @param ftpURLinfo
	 * @param isFaa
	 * @param databaseName
	 * @throws IOException
	 */
	public static void getFastaFromFtpURL(ArrayList<String> ftpURLinfo , boolean isFaa, String databaseName) throws IOException {
		
		// para que serve buffer_size?
		int BUFFER_SIZE = 4096;  
		
		String ftpUrl = ftpURLinfo.get(0);
		String filePath = null;
		String extension = null;
		
		if(isFaa) {
			filePath = ftpURLinfo.get(1) + "_" + ftpURLinfo.get(2) + aaFastaExtension + ".gz";
			extension = ".faa";
		}
		else {
			filePath = ftpURLinfo.get(1) + "_" + ftpURLinfo.get(2) + naFastaExtension + ".gz";
			extension = ".fna";
		}
		
		String savePath = tempPath + databaseName + "/" + fastaName + extension;		
		String ftpUrlFile = String.format(ftpUrl, filePath);
//        System.out.println("URL: " + ftpUrlFile);
 
        try {
            URL url = new URL(ftpUrlFile);
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();
 
            FileOutputStream outputStream = new FileOutputStream(savePath);
 
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
//            System.out.println("File downloaded");
        } 
        
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
		
	
	
	/**
	 * verify if a database directory exists, if not creats it
	 * @param databaseName
	 * @return
	 */
	public static boolean createFolder(String databaseName) {
		
		File newPath = new File(tempPath+databaseName+"/");
		
		if (newPath.exists())
			return false;
		else 
			newPath.mkdirs();
		
		return true;
	}
	
	/**
	 * @param fastaFiles
	 * @throws Exception 
	 */
	public static void createGenomeFileFromFasta(String databaseName, File fastaFile, String extension) throws Exception {
		
		CreateGenomeFile.createFolder(databaseName);
		
		if(CreateGenomeFile.currentTemporaryDataIsNOTRecent(-1,tempPath,databaseName, today,extension)) {

			Map<String, AbstractSequence<?>> sequences= new HashMap<String, AbstractSequence<?>>();

			//for(File fastFile : fastaFiles)				
			sequences.putAll(FastaReaderHelper.readFastaProteinSequence(fastaFile));

			CreateGenomeFile.buildFastFile(databaseName, null, sequences, extension);
			CreateGenomeFile.createLogFile(databaseName, extension);
		}
	}

	/**
	 * @return
	 */
	private static String setToday() {

		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MMM/dd");
		return formatter.format(currentDate.getTime());
		//formatter = new SimpleDateFormat("HH:mm:ss");
		//String now = formatter.format(currentDate.getTime());
	}

	/**
	 * @param numberOfDaysOld
	 * @return
	 * @throws Exception
	 */
	public Map<String, ProteinSequence> getGenome(String databaseName, String extension) throws Exception {

		return FastaReaderHelper.readFastaProteinSequence(new File(tempPath+"/"+databaseName+"/"+fastaName+extension));
	}


//	/**
//	 * @param numberOfDaysOld
//	 * @param sourceDB
//	 * @throws IOException
//	 * @throws ParseException
//	 */
//	private static void createTempFile(String databaseName, int numberOfDaysOld,String sourceDB, String extension) throws Exception{
//
//		new File(tempPath).mkdir();
//		if(CreateGenomeFile.currentTemporaryDataIsNOTRecent(numberOfDaysOld,tempPath, databaseName, today, extension))
//		{
//			long startTime = System.currentTimeMillis();
//			Pair<Map<String,String>, Map<String,AbstractSequence<?>>> pair = CreateGenomeFile.getFastaAAGenomeFromEntrezProtein(sourceDB);
//			Map<String, AbstractSequence<?>> sequences = pair.getPairValue();
//			Map<String, String> locusTag = pair.getValue();
//			long endTime = System.currentTimeMillis();
//			System.out.println("Total elapsed time in execution of method is :"+ String.format("%d min, %d sec", 
//					TimeUnit.MILLISECONDS.toMinutes(endTime-startTime),TimeUnit.MILLISECONDS.toSeconds(endTime-startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime-startTime))));
//
//			CreateGenomeFile.buildFastFile(databaseName, locusTag, sequences, extension);
//			CreateGenomeFile.createLogFile(databaseName, extension);
//			//return pair;
//		}
//	}

	/**
	 * @param locusTag
	 * @param sequences
	 * @throws IOException
	 */
	private static void buildFastFile(String databaseName, Map<String, String> locusTag, Map<String, AbstractSequence<?>> sequences, String extension) throws IOException{

		File myFile = new File(tempPath+"/"+databaseName+"/"+fastaName+extension);

		FileWriter fstream = new FileWriter(myFile);  
		BufferedWriter out = new BufferedWriter(fstream); 

		for(String key:sequences.keySet()) {

			String fileKey = key;
			//Temp solution for new NCBI FASTA Headers
			fileKey = fileKey.split("\\s")[0];

			if(locusTag!=null && locusTag.containsKey(key) && locusTag.get(key)!=null)
				out.write(">"+fileKey+"|"+locusTag.get(key)+"\n");
			else
				out.write(">"+fileKey+"\n");

			out.write(sequences.get(key).getSequenceAsString()+"\n");

		}
		out.close();
	}

	/**
	 * @throws IOException
	 */
	private static void createLogFile(String databaseName, String extension) throws IOException{
		
		String pathtemp = tempPath;
		StringBuffer buffer = new StringBuffer();
		if(new File(pathtemp+"genomes.log").exists()) {

			FileInputStream finstream = new FileInputStream(tempPath+"genomes.log");
			DataInputStream in = new DataInputStream(finstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			while ((strLine = br.readLine()) != null) {

				buffer.append(strLine+"\n");
			}
			br.close();
			in.close();
			finstream.close();
		}
		else
		{
			buffer.append("organismID\tdate\n");
		}

		new File(tempPath+"genomes.log");
		FileWriter fstream = new FileWriter(tempPath+"genomes.log");  
		BufferedWriter out = new BufferedWriter(fstream);
		out.append(buffer);
		out.write("genome_"+databaseName+extension+"\t"+today);
		out.close();
	}

	/**
	 * @param numberOfDaysOld
	 * @param tempPath
	 * @param genomeID
	 * @param today
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	private static boolean currentTemporaryDataIsNOTRecent(int numberOfDaysOld, String tempPath, String databasename, String today, String extension) throws ParseException, IOException{

		if(numberOfDaysOld<0) {

			return true;
		}

		if(new File(tempPath+"genomes.log").exists()) {

			FileInputStream fstream = new FileInputStream(tempPath+"genomes.log");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String logFileDate = null;
			StringBuffer buffer = new StringBuffer();

			String strLine;
			while ((strLine = br.readLine()) != null) {

				String[] data = strLine.split("\t"); 

				if(data[0].equalsIgnoreCase(databasename+extension)) {

					logFileDate = data[1];
				}
				else {

					buffer.append(strLine+"\n");
				}
			}
			br.close();
			in.close();
			fstream.close();

			if(logFileDate!=null) {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd");
				Date date1 = sdf.parse(today);
				Date date2 = sdf.parse(logFileDate.replace("\"", ""));

				if(numberOfDaysOld==0) {

					date2 = sdf.parse(today);
				}

				if(date1.after(date2) || date1.equals(date2)) {

					Calendar cal1 = Calendar.getInstance();
					Calendar cal2 = Calendar.getInstance();
					cal1.setTime(date1);
					cal2.setTime(date2);

					if(cal1.get(Calendar.YEAR)==cal2.get(Calendar.YEAR)) {

						int old = cal1.get(Calendar.DAY_OF_YEAR)-cal2.get(Calendar.DAY_OF_YEAR);

						if(old<=numberOfDaysOld || numberOfDaysOld<=0) {

							return false;
						}
						else  {

							System.out.println("File is\t"+old+"\tdays old.");
						}
					}
				}
				new File(tempPath+"genomes.log");
				FileWriter fWriterStream = new FileWriter(tempPath+"genomes.log");  
				BufferedWriter out = new BufferedWriter(fWriterStream);
				out.append(buffer);
				out.write(databasename+extension+"\t"+logFileDate);
				out.close();
			}
		}
		else {

			FileWriter fstream = new FileWriter(tempPath+"genomes.log");  
			BufferedWriter out = new BufferedWriter(fstream);  
			new File(tempPath+"genomes.log");
			out.write("organismID\tdate\n");
			out.close();
		}
		return true;
	}

//	/**
//	 * @param genomeID
//	 * @return 
//	 * @throws AxisFault 
//	 */
//	private Pair<Map<String, String>,Map<String, AbstractSequence<?>>> getFastaAAGenomeFromEntrezProtein(String sourceDB) throws Exception{
//
//		List<String> ids_list = new ArrayList<String>();
//		ids_list.add(this.genomeID);
//
//		EntrezLink entrezLink = new EntrezLink();
//		List<List<String>> links_list = entrezLink.getLinksList(ids_list, NcbiDatabases.taxonomy, NcbiDatabases.protein,1000);
//
//		EntrezFetch entrezFetch;
//		entrezFetch = new EntrezFetch();
//
//		Pair<Map<String, String>,Map<String, AbstractSequence<?>>> pair = entrezFetch.getLocusAndSequencePairFromID(links_list,500,sourceDB);
//
//		pair = this.checkForDuplicates(pair);
//
//		return pair;
//	}

	/**
	 * @param pair
	 * @return
	 */
	private Pair<Map<String, String>,Map<String, AbstractSequence<?>>> checkForDuplicates(Pair<Map<String, String>,Map<String, AbstractSequence<?>>> pair) {

		Map<String, String> locustags = pair.getValue();
		Map<String, AbstractSequence<?>> sequences =  pair.getPairValue();
		Set<String> locus_to_be_checked = new HashSet<String>();

		List<String> duplicate_keys = new ArrayList<String>();

		for(String key : locustags.keySet()) {

			if(!duplicate_keys.contains(key)) {

				String locus = locustags.get(key);
				for(String test_key : locustags.keySet()) {

					if(locustags.get(test_key).equals(locus)) {

						duplicate_keys.add(test_key);
						if(!test_key.equals(key)) {

							locus_to_be_checked.add(key);
						}
					}
				}
				duplicate_keys.remove(key);
			}
		}

		for(String key:duplicate_keys) {

			locustags.remove(key);
			sequences.remove(key);
		}

		duplicate_keys = new ArrayList<String>();
		for(String key : sequences.keySet()) {

			if(!duplicate_keys.contains(key)) {

				AbstractSequence<?> sequence = sequences.get(key);
				for(String test_key : sequences.keySet()) {

					if(sequences.get(test_key).equals(sequence)) {

						duplicate_keys.add(test_key);
						if(!test_key.equals(key)) {

							locus_to_be_checked.add(key);
						}
					}
				}
				duplicate_keys.remove(key);

			}
		}

		for(String key:duplicate_keys) {

			locustags.remove(key);
			sequences.remove(key);
		}

		for(String acc : locus_to_be_checked) {

			String locus = locustags.get(acc);
			UniProtEntry uniProtEntry = UniProtAPI.getUniProtEntryFromXRef(acc,0);
			if(uniProtEntry!=null && UniProtAPI.getLocusTags(uniProtEntry)!=null && UniProtAPI.getLocusTags(uniProtEntry).size()>0) {

				locus = UniProtAPI.getLocusTags(uniProtEntry).get(0);//.getValue();
			}
			else {

				uniProtEntry = UniProtAPI.getUniProtEntryFromXRef(locustags.get(acc),0);
				if(uniProtEntry!=null && UniProtAPI.getLocusTags(uniProtEntry)!=null && UniProtAPI.getLocusTags(uniProtEntry).size()>0) {

					locus = UniProtAPI.getLocusTags(uniProtEntry).get(0);//.getValue();
				}
			}

			if(!locus.equalsIgnoreCase(locustags.get(acc))) {

				locustags.put(acc, locus);
			}
		}
		pair.setValue(locustags);
		pair.setPairValue(sequences);
		return pair;
	}


//	/**
//	 * @return the genomeID
//	 */
//	public String getGenomeID() {
//		return genomeID;
//	}
//
//	/**
//	 * @param genomeID the genomeID to set
//	 */
//	public void setGenomeID(String genomeID) {
//		this.genomeID = genomeID;
//	}

	//	public static void main(String [] args) throws Exception{
	//		//new CreateGenomeFile("83333",5,"",".faa"); // ecoli k12
	//		
	//		NcbiEFetchSequenceStub_API fetchStub = new NcbiEFetchSequenceStub_API(50);
	//		Set<String> querySet = new HashSet<String>();
	//		querySet.add("NP_207514.1");
	//		System.out.println(fetchStub.getLocusFromID(querySet,10).get("NP_207514.1"));
	//	}

}
