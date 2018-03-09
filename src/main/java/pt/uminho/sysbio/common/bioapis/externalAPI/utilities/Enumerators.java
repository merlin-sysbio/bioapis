package pt.uminho.sysbio.common.bioapis.externalAPI.utilities;

public class Enumerators {

	public enum FileExtensions{

		//			ASSEMBLY_REPORT("assembly_report.txt"),
		//			ASSEMBLY_STATS("assembly_stats.txt"),
		CDS_FROM_GENOMIC("cds_from_genomic.fna"),
		//			FEATURE_TABLE("feature_table.txt"),
		//			GENOMIC_FNA("genomic.fna"),
		//			GENOMIC_GBFF("genomic.gbff"),
		//			GENOMIC_GFF("genomic.gff"),
		PROTEIN_FAA("protein.faa"), 
		//			PROTEIN_GPFF("protein.gpff"),
		RNA_FROM_GENOMIC("rna_from_genomic.fna"),
		//R_RNA_FROM_GENOMIC ("rrna_from_genomic.fna"),
		//T_RNA_FROM_GENOMIC ("trna_from_genomic.fna"),
		CUSTOM_GENBANK_FILE("customGenBankFile");


		private String extension;

		FileExtensions(String extension){
			this.extension = extension;
		}

		public String getExtension(){
			return extension;
		}

	}

	public enum TypeOfExport{

		PROTEIN_FAA("protein.faa"), 
		ALL_FILES(".mer");

		private String type;

		private TypeOfExport(String type){
			this.type = type;
		}

		public String extension(){
			return this.type;
		}

	}

	public enum GenBankFiles{

		//		PROTEIN_GPFF("protein.gpff"), 
		GENOMIC_GBFF("genomic.gbff"),
		CUSTOM_FILE("customGenBankFile");

		private String file;

		private GenBankFiles(String file){
			this.file = file;
		}

		public String extension(){
			return this.file;
		}

	}

//	public enum ExpectedValues{
//
//		_1E_minus_200 (1e-200),
//		_1E_minus_100 (1e-100),
//		_1E_minus_50 (1e-50),
//		_1E_minus_10 (1e-10),
//		_1E_minus_5 (1e-5),
//		_1E_minus_4 (1e-4),
//		_1E_minus_3 (1e-3),
//		_1E_minus_2 (1e-2),
//		_1E_minus_1 (1e-1),
//		_1 (1.0),
//		_100 (100),
//		_1000 (1000);
//
//		private final double index;   
//
//		ExpectedValues(double index) {
//			this.index = index;
//		}
//
//		public double index() { 
//			return index; 
//		}
//
//	}
}
