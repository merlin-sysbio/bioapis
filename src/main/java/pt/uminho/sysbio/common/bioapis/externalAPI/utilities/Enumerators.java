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
			RNA_FROM_GENOMIC("rna_from_genomic.fna");
			//R_RNA_FROM_GENOMIC ("rrna_from_genomic.fna"),
			//T_RNA_FROM_GENOMIC ("trna_from_genomic.fna");
		
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
}
