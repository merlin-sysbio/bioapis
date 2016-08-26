package pt.uminho.sysbio.common.bioapis.externalAPI;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.common.bioapis.externalAPI.ebi.EbiAPI;
import pt.uminho.sysbio.common.bioapis.externalAPI.ebi.EbiRestful;
import pt.uminho.sysbio.common.bioapis.externalAPI.ebi.interpro.InterProMain;
import pt.uminho.sysbio.common.bioapis.externalAPI.ebi.interpro.InterProResultsList;
import pt.uminho.sysbio.common.bioapis.externalAPI.ebi.phobius.PhobiusParser;

/**
 * @author Oscar Dias
 *
 */
public class EbiTests {

	final static Logger logger = LoggerFactory.getLogger(EbiTests.class);
	final static String path = "C:/Users/Oscar Dias/Desktop/xde/genome.faa";
	
	/**
	 * 
	 */
	@Test
	public void testInterPro() throws ClientProtocolException, IOException, InterruptedException, CompoundNotFoundException {

		File file = new File(path);

		Map<String,ProteinSequence> genome = FastaReaderHelper.readFastaProteinSequence(file);
		
		long startTime = GregorianCalendar.getInstance().getTimeInMillis();
		long waitingPeriod = 180000;

		Map<String, InterProResultsList> interPro = EbiAPI.getInterProAnnotations(genome, waitingPeriod);
		
		for(String key: interPro.keySet()) {
			
			InterProResultsList interProResultsList = interPro.get(key); 
			logger.info("{} ec is {}.",key,interProResultsList.getMostLikelyEC());
			logger.info("{} localization  {}.",key,interProResultsList.getMostLikelyLocalization());
			logger.info("{} name is {}.", key,interProResultsList.getName());
			logger.info("{} query is {}.", key,interProResultsList.getQuery());
			System.out.println();
		}
		
		long endTime = GregorianCalendar.getInstance().getTimeInMillis();
		long time = endTime-startTime;
		
		int seconds = (int) (time / 1000) % 60 ;
		int minutes = (int) ((time / (1000*60)) % 60);
		int hours   = (int) ((time / (1000*60*60)) % 24);
		
		logger.warn("hours {} minutes {} seconds {}", hours, minutes, seconds);;
	}
	
	@Test
	public void testPhobius() throws ClientProtocolException, IOException, InterruptedException, CompoundNotFoundException {

		File file = new File(path);

		Map<String,ProteinSequence> genome = FastaReaderHelper.readFastaProteinSequence(file);
		
		long startTime = GregorianCalendar.getInstance().getTimeInMillis();
		long waitingPeriod = 180000;

		Map<String, Integer> phobius = EbiAPI.getHelicesFromPhobius(genome, waitingPeriod);
		
		for(String key: phobius.keySet())			
			logger.info("{} has {} helices.",key,phobius.get(key));
		
		long endTime = GregorianCalendar.getInstance().getTimeInMillis();
		long time = endTime-startTime;
		
		int seconds = (int) (time / 1000) % 60 ;
		int minutes = (int) ((time / (1000*60)) % 60);
		int hours   = (int) ((time / (1000*60*60)) % 24);
		
		logger.warn("hours {} minutes {} seconds {}", hours, minutes, seconds);;
	}
	
	
	/**
	 * 
	 */
	public void simpeTest() throws ClientProtocolException, IOException, InterruptedException, CompoundNotFoundException {

		//		ProteinSequence sequence = new ProteinSequence("madrrnrcnqilllayqsfglvfgdlsisplyvykctfygglrhhqtedtifgafslifwtitllslikymvfvlsaddngeggifalyallcrharfsllpnqqaadeeistyygpgdasrnlpssafksliernkrsktallvlvlvgtsmvitigvltpaisvsssidglvaktslkhstvvmiacallvglfvlqhrgtnkvaflfapimilwlliiatagvynivtwnpsvykalspyyiyvffrdtgidgwlslggillcitgteaifaelgqftatsirfafccvvypclvlqymgqaaflsknfsalpssfyssipdpffwpvlmmamlaamvasqavifatfsivkqcyalgcfprvkivhkprwvlgqiyipeinwvvmiltlavticfrdtrhiafafglacmtlafvttwlmpliinfvwnrnivfsvlfilffgtielifvasalvkipkggwitlllslfftfityvwhygsrkkylcdqhnkvpmksilslgpslgiikvpgmgliytelasgvpatfkhfltnlpafyqvvvfvccktvpipyvpqkeryligrigpktyrmyrciiragykdvnkdgddfedelvmsiaefiqlesegyggsntdrsidgrlavvkasnkfgtrlsrsiseaniagssrsqttvtnskspallklraeyeqelprlsmrrmfqfrpmdtkfrqpqvkeelfdlvnakdaevayivghghvkakrnsvfvkqlvvnvaysflrkncrspgvmlniphiclikvgmnyyl");

		//		List<Object>optargs = new ArrayList<Object>();
		//		optargs.add("protein");
		//		optargs.add("short");

		String email = "maildosequeira@gmail.com";

		String path = "D:/Dropbox/Public/merlin_releases/griffon.faa";
		
		File file = new File(path);
		Map<String,ProteinSequence> map = FastaReaderHelper.readFastaProteinSequence(file);
		long waitingPeriod = 30000;

		for( String key : map.keySet()) {
			
			ProteinSequence p = map.get(key);
			
			String phobius = EbiWebServices.runPhobius(email, key, p);
			List<String> result = EbiRestful.getXml(phobius, "phobius", waitingPeriod);
			int h = PhobiusParser.getNumberOfHelices(result);
			
			System.out.println(key+" "+h);
		}

		//		String interpro = EbiWebServices.runInterProScan(email, title, sequence, (String) args.get(0), (String) args.get(1), (List<String>) args.get(2));
		//		List<String> result1 = EbiRestful.getXml(interpro,"iprscan5", outputformat);
	}

	public void test() throws IOException, InterruptedException, URISyntaxException {

		//    	InterProParser parser = new InterProParser(sequence);
		//    	for (HashMap<String,Object>domain:parser.xml_information){System.out.println(domain.get("Family"));}
		//		long startTime = System.currentTimeMillis();

		//		String file = "GCF_000410535.2_ASM41053v2_protein";
		String path = "D:/Dropbox/Public/merlin_releases/griffon.faa";

		File file = new File(path);

		Map<String,ProteinSequence> map = FastaReaderHelper.readFastaProteinSequence(file);

		Map<String,InterProResultsList> interpro = InterProMain.processGenome("mikemike@gmail.com", "title", "true", "true",new ArrayList<String>(), map);

		System.out.println(interpro);
		
		//		long endTime = System.currentTimeMillis();

		//		System.out.println("That took " + (endTime - startTime) + " milliseconds");

		//    	for(String key:readFasta(readFile()).keySet()){
		//    		System.out.println(key+"\n"+readFasta(readFile()).get(key));
		//    	}

	}
	
	public void testInterProParser(){

		//	String sequence = "MITIDGNGAVASVAFRTSEVIAIYPITPSSTMAEQADAWAGNGLKNVWGDTPRVVEMQSEAGAIATVHGALQTGALSTSFTSSQGLLLMIPTLYKLAGELTPFVLHVAARTVATHALSIFGDHSDVMAVRQTGCAMLCAANVQEAQDFALISQIATLKSRVPFIHFFDGFRTSHEINKIVPLADDTILDLMPQVEIDAHRARALNPEHPVIRGTSANPDTYFQSREATNPWYNAVYDHVEQAMNDFSAATGRQYQPFEYYGHPQAERVIILMGSAIGTCEEVVDELLTRGEKVGVLKVRLYRPFSAKHLLQALPGSVRSVAVLDRTKEPGAQAEPLYLDVMTALAEAFNNGERETLPRVIGGRYGLSSKEFGPDCVLAVFAELNAAKPKARFTVGIYDDVTNLSLPLPENTLPNSAKLEALFYGLGSDGSVSATKNNIKIIGNSTPWYAQGYFVYDSKKAGGLTVSHLRVSEQPIRSAYLISQADFVGCHQLQFIDKYQMAERLKPGGIFLLNTPYSADEVWSRLPQEVQAVLNQKKARFYVINAAKIARECGLAARINTVMQMAFFHLTQILPGDSALAELQGAIAKSYSSKGQDLVERNWQALALARESVEEVPLQPVNPHSANRPPVVSDAAPDFVKTVTAAMLAGLGDALPVSALPPDGTWPMGTTRWEKRNIAEEIPIWKEELCTQCNHCVAACPHSAIRAKVVPPEAMENAPASLHSLDVKSRDMRGQKYVLQVAPEDCTGCNLCVEVCPAKDRQNPEIKAINMMSRLEHVEEEKINYDFFLNLPEIDRSKLERIDIRTSQLITPLFEYSGACSGCGETPYIKLLTQLYGDRMLIANATGCSSIYGGNLPSTPYTTDANGRGPAWANSLFEDNAEFGLGFRLTVDQHRVRVLRLLDQFADKIPAELLTALKSDATPEVRREQVAALRQQLNDVAEAHELLRDADALVEKSIWLIGGDGWAYDIGFGGLDHVLSLTENVNILVLDTQCYSNTGGQASKATPLGAVTKFGEHGKRKARKDLGVSMMMYGHVYVAQISLGAQLNQTVKAIQEAEAYPGPSLIIAYSPCEEHGYDLALSHDQMRQLTATGFWPLYRFDPRRADEGKLPLALDSRPPSEAPEETLLHEQRFRRLNSQQPEVAEQLWKDAAADLQKRYDFLAQMAGKAEKSNTD";


		//	    	Scanner s = new Scanner(new File("C:\\Users\\João Sequeira\\Documents\\Escola\\Projeto Bioinformática BSIstemas\\interproscan API\\iprscan5-R20160424-232342-0215-12126963-pg.xml.xml"));
		//			ArrayList<String> list = new ArrayList<String>();
		//			while (s.hasNextLine()){
		//			    list.add(s.nextLine());
		//			}
		//			s.close();
		//			
		//	    	InterProResultsList list = xml_information(list);
		//	    	
		//	    	for (InterProResult result: list.getResults()) {
		//				System.out.println(result.getAccession());
		//				System.out.println(result.getEC());
		//				System.out.println(result.geteValue());
		//				System.out.println(result.getFamily());
		//				System.out.println(result.getGOName());
		//				System.out.println(result.getLocalization());
		//				System.out.println(result.getName());
		//				System.out.println(result.getScore());
		//				System.out.println(result.getTool());
		//				System.out.println(result.getClass());
		//				System.out.println(result.getDatabase());
		//				System.out.println(result.getEntry());
		//				System.out.println(result.getLocation());
		//				System.out.println(result.getModels());
		//	    	}

		//		InterProParser parser = new InterProParser(sequence);
		//
		//		for(InterProResult result: parser.getInterProResultsList().getResults()) {
		//
		//			System.out.println(result.getGOName());
		//			System.out.println(result.getEC());
		//		}
		//		parser.getInterProResultsList().setName();
		//		parser.getInterProResultsList().setMostLikelyEC();
		//		System.out.println(parser.getInterProResultsList().getName());
		//		System.out.println(parser.getInterProResultsList().getMostLikelyEC());
	}

}
