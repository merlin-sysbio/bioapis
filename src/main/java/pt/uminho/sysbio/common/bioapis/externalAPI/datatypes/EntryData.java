package pt.uminho.sysbio.common.bioapis.externalAPI.datatypes;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Oscar Dias
 *
 */
public class EntryData {

	private Set<String> ecNumbers;
	private String uniprotReviewStatus;
	private String locusTag;
	private String entryID;
	private String function;
	private String codedBy;
	private long taxonomyID;
	
	/**
	 * @param entryID
	 */
	public EntryData(String entryID) {
		super();
		this.entryID = entryID;
		this.ecNumbers = new HashSet<>();
	}
	
	public Set<String> getEcNumbers() {
		return ecNumbers;
	}
	
	public void setEcNumbers(Set<String> ecNumbers) {
		this.ecNumbers = ecNumbers;
	}
	
	public void addEcNumber(String ecNumber) {
		this.ecNumbers.add(ecNumber);
	}
	
	public String getUniprotReviewStatus() {
		return uniprotReviewStatus;
	}
	
	public void setUniprotReviewStatus(String uniprotReviewStatus) {
		this.uniprotReviewStatus = uniprotReviewStatus;
	}
	
	public String getLocusTag() {
		return locusTag;
	}
	public void setLocusTag(String locusTag) {
		this.locusTag = locusTag;
	}
	
	public String getEntryID() {
		return entryID;
	}
	
	public void setEntryID(String entryID) {
		this.entryID = entryID;
	}

	/**
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * @param function the function to set
	 */
	public void setFunction(String function) {
		this.function = function;
	}

	/**
	 * @return the codedBy
	 */
	public String getCodedBy() {
		return codedBy;
	}

	/**
	 * @param codedBy the codedBy to set
	 */
	public void setCodedBy(String codedBy) {
		this.codedBy = codedBy;
	}

	/**
	 * @return the taxonomyID
	 */
	public long getTaxonomyID() {
		return taxonomyID;
	}

	/**
	 * @param taxonomyID the taxonomyID to set
	 */
	public void setTaxonomyID(long taxonomyID) {
		this.taxonomyID = taxonomyID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EntryData [ecNumbers=" + ecNumbers + ", uniprotReviewStatus=" + uniprotReviewStatus + ", locusTag="
				+ locusTag + ", entryID=" + entryID + ", function=" + function + ", codedBy=" + codedBy
				+ ", taxonomyID=" + taxonomyID + "]";
	}

	
}
