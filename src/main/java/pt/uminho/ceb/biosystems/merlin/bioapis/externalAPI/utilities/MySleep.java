package pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.utilities;

public class MySleep {

	/**
	 * @param xMilliseconds
	 * @return 
	 */
	public static void myWait(long xMilliseconds) {
		
		try {

			//System.out.println("Waiting "+xMilliseconds+" milliseconds");
			Thread.sleep(xMilliseconds);
		}	
		catch(InterruptedException e) {

			e.printStackTrace();
		}
	}
}
