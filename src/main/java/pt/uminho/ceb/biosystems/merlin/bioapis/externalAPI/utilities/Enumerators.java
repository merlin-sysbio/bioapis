package pt.uminho.ceb.biosystems.merlin.bioapis.externalAPI.utilities;

public class Enumerators {

	public enum FileExtensions{

		//			ASSEMBLY_REPORT("assembly_report.txt"),
		//			ASSEMBLY_STATS("assembly_stats.txt"),
		CDS_FROM_GENOMIC("cds_from_genomic.fna"){
			@Override
			public String toString(){
				return "cds file (cds_from_genomic.fna)";
			}
		},
		//			FEATURE_TABLE("feature_table.txt"),
		//			GENOMIC_FNA("genomic.fna"),
//		GENOMIC_GBFF("genomic.gbff"){
//			@Override
//			public String toString(){
//				return "genbank file ('.gbff')";
//			}
//		},
		//			GENOMIC_GFF("genomic.gff"),
		PROTEIN_FAA("protein.faa"), 
		//			PROTEIN_GPFF("protein.gpff"),
		RNA_FROM_GENOMIC("rna_from_genomic.fna"){
			@Override
			public String toString(){
				return "rna file (rna_from_genomic.fna)";
			}
		},
		//R_RNA_FROM_GENOMIC ("rrna_from_genomic.fna"),
		//T_RNA_FROM_GENOMIC ("trna_from_genomic.fna"),
		CUSTOM_GENBANK_FILE("customGenBankFile"){
			@Override
			public String toString(){
				return "custom genbank file ('.gbff'/'.gpff')";
			}
		};

		private String extension;

		FileExtensions(String extension){
			this.extension = extension;
		}

		public String getExtension(){
			return extension;
		}
		
		@Override
		public String toString(){
			return "fasta file (protein.faa)";
		}

	}

	public enum TypeOfExport{

		PROTEIN_FAA("protein.faa"){
			@Override
			public String toString(){
				return "fasta file (protein.faa)";
			}
		}, 
		ALL_FILES(".mer");

		private String type;

		private TypeOfExport(String type){
			this.type = type;
		}

		public String extension(){
			return this.type;
		}
		
		@Override
		public String toString(){
			return "all files";
		}

	}

	public enum GenBankFiles{

		//		PROTEIN_GPFF("protein.gpff"), 
		GENOMIC_GBFF("genomic.gbff"),
		CUSTOM_FILE("customGenBankFile"){
			@Override
			public String toString(){
				return "custom genbank file ('.gbff'/'.gpff')";
			}
		};

		private String file;

		private GenBankFiles(String file){
			this.file = file;
		}

		public String extension(){
			return this.file;
		}
		
		@Override
		public String toString(){
			return "gb file downloaded by merlin ('genomic.gbff')";
		}

	}
	
}
