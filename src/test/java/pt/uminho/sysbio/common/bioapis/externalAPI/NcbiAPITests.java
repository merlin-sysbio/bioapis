package pt.uminho.sysbio.common.bioapis.externalAPI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.sound.midi.Synthesizer;

import org.apache.avro.SchemaBuilder.ArrayBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.request.CollectionAdminRequest.Create;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.CreateGenomeFile;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.EntrezFetch;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.EntrezService;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.EntrezServiceFactory;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.EntrezTaxonomy;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.NcbiDatabases;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.containers.DocumentSummary;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.containers.DocumentSummarySet;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.containers.ELinkResult;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.containers.ESearchResult;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.containers.ESummaryResult;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.containers.GBSet;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.ncbi.containers.TaxaSet;
import pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.utilities.Enumerators.FileExtensions;
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

	//	@Test
	public void testGetFasta() throws Exception {

		EntrezServiceFactory entrezServiceFactory = new EntrezServiceFactory("https://eutils.ncbi.nlm.nih.gov/entrez/eutils", false);
		EntrezService entrezService = entrezServiceFactory.build();

		ESearchResult eSearchResult = entrezService.eSearch(NcbiDatabases.assembly, "322[Taxonomy ID]", "xml", "100");
		//        System.out.println(eSearchResult.count);
		//        System.out.println(eSearchResult.idList.toString());
		List<String> idList = eSearchResult.idList;
		String uids = idList.get(0);
		idList.remove(0);
		for(String i:eSearchResult.idList)
			uids += ","+i;
		System.out.println(uids);
		//        System.out.println(eSearchResult.idList);
		//        GBSet res = entrezService.eFetch(NcbiDatabases.assembly, "38841[UID]", "xml"); 
		//        System.out.println(res.gBSeq);
		ESummaryResult res = entrezService.eSummary(NcbiDatabases.assembly, uids);
		System.out.println(res.documentSummarySet.get(0).documentSummary);
		//        System.out.println(res.assemblyAccession);
		//        System.out.println(res.accessionGenBank);
		//        System.out.println(res.accessionRefSeq);
		//        System.out.println(res.ftpGeneBank);
		//        System.out.println(res.ftpRefSeq);
		//        DocumentSummary docSum = res.documentSummarySet.get(0).documentSummary.get(0);

		//        String uid = docSum.uid;
		//		String speciesName = docSum.speciesName;
		//		String assemblyAccession = docSum.assemblyAccession;
		//		String lastupdateDate = docSum.lastupdateDate.substring(0, 10);
		//		String accessionGenBank = docSum.accessionGenBank;
		//		String genBankStatus = docSum.propertyList.get(3);
		//		String accessionRefSeq = docSum.accessionRefSeq;
		//		String refSeqStatus = docSum.propertyList.get(4);
		//		
		//		System.out.println("uid=" + uid + "\n" + ", assemblyAccession=" + assemblyAccession + "\n" + ", accessionGeneBank="
		//				+ accessionGenBank + "\n" + ", accessionRefSeq=" + accessionRefSeq );
		//		System.out.println(",speciesName=" + speciesName + "\n" +  ", lastUpdateDate=" + lastupdateDate + "\n" 
		//				+ ", genbankStatus=" + genBankStatus + "\n" + ", refSeqStatus=" + refSeqStatus);
		//        

	}


	//    @Test
	public void testeEnum(){    	

		//    	FileExtensions fe = FileExtensions.valueOf("assembly_report".toUpperCase());
		//    	System.out.println(fe.extension());
		//    	for(FileExtensions extension: FileExtensions.values())
		//    		System.out.println(extension.extension());

		System.out.println(FileExtensions.valueOf("assembly_report".toUpperCase()).getExtension());
	}

	//@Test
	public void unzipFiles() throws Exception{

		byte[] buffer = new byte[1024];

		try {

			FileInputStream fileIn = new FileInputStream("D:/projects/merlin/merlin-core/temp/tvol");

			GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);

			FileOutputStream fileOutputStream = new FileOutputStream("D:/projects/merlin/merlin-core/temp/tvol/codingSequences.faa");

			int bytes_read;

			while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {

				fileOutputStream.write(buffer, 0, bytes_read);
			}

			gZIPInputStream.close();
			fileOutputStream.close();

			System.out.println("The file was decompressed successfully!");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}	

	//		@Test
	public void buildFasta() throws IOException{


		File fasta = new File("D:/projects/merlin/merlin-core/temp/tvol/codingSequences.faa");

		Map<String, AbstractSequence<?>> sequences= new HashMap<String, AbstractSequence<?>>();

		//for(File fastFile : fastaFiles)			

		sequences.putAll(FastaReaderHelper.readFastaProteinSequence(fasta));

		Map<String, String> locusTag = null;

		FileWriter fstream = new FileWriter(fasta);  
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


	@Test
	public void testReadConfExtensions() throws IOException{

		File extensionFile = new File("D:/projects/merlin/merlin-core/temp/../conf/ftpfiles_extensions.conf");

		HashMap<String, String> extensions = new HashMap<String,String>();

		BufferedReader bufferedReader = new BufferedReader(new FileReader(extensionFile));

		String text, type, extension;
		while ((text = bufferedReader.readLine()) != null) {
			if(text.toUpperCase().matches("^[A-Z].*$")) {
				type = text.toUpperCase().split("\t")[0];
				extension = text.split("\t")[1];

				extensions.put(type,extension);
			}
		}
		bufferedReader.close();
	}


	//	@Test
	public void testDownloadFromFTP() {

		int BUFFER_SIZE = 4096;  

		//		String ftpUrl = ftpURLinfo.get(0);
		String filePath = "ftp://ftp.ncbi.nlm.nih.gov/genomes/all/GCA/000/410/535/GCA_000410535.2_ASM41053v2/GCA_000410535.2_ASM41053v2_protein.faa.gz";
		//		String extension = null;

		//		if(isFaa) {
		//			filePath = ftpURLinfo.get(1) + "_" + ftpURLinfo.get(2)+"_protein.faa.gz";
		//			extension = ".faa";
		//		}
		//		else {
		//			filePath = ftpURLinfo.get(1) + "_" + ftpURLinfo.get(2)+"_genomic.fna.gz";
		//			extension = ".fna";
		//		}

		String savePath = "C:/Users/adias/Desktop/Teste/GCA_000410535.2_ASM41053v2_protein.faa.gz";		
		//		String ftpUrlFile = String.format(ftpUrl, filePath);
		//        System.out.println("URL: " + ftpUrlFile);

		try {
			URL url = new URL(filePath);
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
			System.out.println("File downloaded");
		} 

		catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	//	@Test
	public void writeTxtFile() throws FileNotFoundException, UnsupportedEncodingException {

		DocumentSummarySet docSumSet = CreateGenomeFile.getESummaryFromNCBI("243276");
		DocumentSummary docSum = docSumSet.documentSummary.get(1);

		PrintWriter writer;
		String uid = docSum.uid;
		String speciesName = docSum.speciesName;
		String assemblyAccession = docSum.assemblyAccession;
		String lastupdateDate = docSum.lastupdateDate.substring(0, 10);
		String accessionGenBank = docSum.accessionGenBank;
		String genBankStatus = docSum.propertyList.get(3);
		String accessionRefSeq = docSum.accessionRefSeq;
		String refSeqStatus = docSum.propertyList.get(4);

		writer = new PrintWriter("C:/Users/Amaro/Desktop/Teste/escreverTeste.txt", "UTF-8");
		writer.println("UID: " + uid + System.getProperty("line.separator") + "Assembly Accession: " + assemblyAccession + System.getProperty("line.separator")
		+ "Accession GeneBank: " + accessionGenBank + System.getProperty("line.separator") + "Accession RefSeq: " + accessionRefSeq);
		writer.println("Species Name: " + speciesName + System.getProperty("line.separator") +  "Last Update Date: " + lastupdateDate + System.getProperty("line.separator") 
		+ "GenBank Status: " + genBankStatus + System.getProperty("line.separator") + "RefSeq Status: " + refSeqStatus);
		writer.close();
		System.out.println("Ficheiro escrito com sucesso!");
	}

	//	@Test
	public void readTxtFile() {

		List<String> lista = null;
		File file = new File("C:/Users/Amaro/Desktop/Teste/escreverTeste.txt");

		try {
			lista = FileUtils.readLines(file, "UTF-8");
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		for (String t : lista)
			System.out.println(t);

	}

	//	@Test
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


	//	@Test
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

	//	@Test
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

	//	@Test
	//	public void getDocSummarySet() {
	//		
	//		DocumentSummarySet doc = CreateGenomeFile.getESummaryFromNCBI("309800");
	//		System.out.println(doc.documentSummary);
	//	}

	@Test
	public void testURL() throws Exception{


		DocumentSummary documentSummary = CreateGenomeFile.getESummaryFromNCBI("243276").documentSummary.get(1);
		ArrayList<String> ftpURLinfo = CreateGenomeFile.getFtpURLFromAssemblyUID(documentSummary, true);
		
		String ftpUrl = ftpURLinfo.get(0);
		String filePath = null;
		
		File extensionFile = new File("D:/projects/merlin/merlin-core/conf/ftpfiles_extensions.conf");
		
		HashMap<String, String> fileExtensions = new HashMap<String,String>();
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(extensionFile));

		String text, type, extension;
		while ((text = bufferedReader.readLine()) != null) {
			if(text.toUpperCase().matches("^[A-Z].*$")) {
				type = text.split("\t")[0];
				extension = text.split("\t")[1];
				
				fileExtensions.put(type,extension);
			}
		}
		bufferedReader.close();
		
		System.out.println("Not Accepted: " + HttpURLConnection.HTTP_NOT_FOUND);
		System.out.println("OK: " + HttpURLConnection.HTTP_OK);
		
		for(String types : fileExtensions.keySet()){

			filePath = ftpURLinfo.get(1) + "_" + ftpURLinfo.get(2) + "_" + fileExtensions.get(types) + ".gz";
			
			String ftpUrlFile = ftpUrl + "/" + filePath;
			String httpUrl = ftpUrlFile.replace("ftp:", "https:");

			URL u = new URL (httpUrl);
			HttpURLConnection huc =  ( HttpURLConnection )  u.openConnection (); 
			huc.setRequestMethod ("HEAD"); 
			HttpURLConnection.setFollowRedirects(false);
			huc.connect () ; 
			int code = huc.getResponseCode();

			System.out.println(types + "---->" + (code == HttpURLConnection.HTTP_OK));
			System.out.println(code);

		}
	}
}
