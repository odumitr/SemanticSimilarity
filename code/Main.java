//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.util.HashMap;
//import java.util.Set;
//
//import org.semanticweb.owlapi.model.OWLOntologyCreationException;
//
//
//public class Main {
//	
//	private static final String OUTPUT_FILE_NAME = "SemSimilarityOutput.txt";
//	
//	public static void main(String []args) throws OWLOntologyCreationException, IOException {						
//		
//		//The input class will gather all the necessary input for the computations 
//		//Reasoner class gets each line of the Corpus file, and for each GO term gets its superclasses
//		//All GO terms and their superclasses are added to a hash table
//		//TAble was used as a data str for faster searches that are needed for incrementing the value
//		//that represents how many times a GO term is present in the data set
//		//When the same GO term is encountered, if it has not been already taken into consideration for Gene X
//		//its count will be incremented
//		//Each GO Term instance has its superclasses, which are put in a hash table
//		//The table was chosen as data str for constant findings when computing Jaccard/Resnik similarities
//		
//		Reasoner r = new Reasoner();
//		Input i = new Input();
//		i.getInput();
//		i.setGene1("CREB3L1");
//		i.setGene2("RPS11");		
//		HashMap<String, GOTerm> goTerms = r.reasonOnData(i.getDataFile(), i.getOwlFile(),
//														i.getGene1(), i.getGene2());
//		computeInfoCont(goTerms, r.getNoLines());
//		System.out.println();
//		System.out.println("Jaccard value for genes "+i.getGene1()+", "+i.getGene2()+
//							" is "+r.semSimilarity("Jaccard", goTerms));
//		System.out.println("Resnik value for genes "+i.getGene1()+", "+i.getGene2()+
//				" is "+r.semSimilarity("Resnik", goTerms));	
//		System.out.println();
//		System.out.println("To find more detailes for Jaccard similarity-no of common superclasses & "
//				+ "no of union superclasses for each pair of GO Terms,"
//				+ " please decommnent lines ?-? from function jaccardPairGOTerms in class Reasoner");
//		System.out.println("To find more detailes for Resnik similarity-Resnik value for each pair"
//				+ " of GO Terms, please decomment lines ?? from function resnikPairGOTerms"
//				+ " in class Reasoner " );
//		writeToFile(goTerms);		
//	}
//	
//	private static void writeToFile(HashMap<String, GOTerm> goTerms) throws IOException {
//		
//		RandomAccessFile f = new RandomAccessFile(OUTPUT_FILE_NAME, "rw");
//		Set<String> set = goTerms.keySet();
//		for (String goValue : set) {
//			String s1 = goTerms.get(goValue).getValue();
//			double s2 = goTerms.get(goValue).getInfoCont();			
//			String s = s1 + " " + s2;
//			f.writeBytes(s+"\n");
//		}				
//	}
//	
//	private static void computeInfoCont(HashMap<String, GOTerm> goTerms, int noLines){
//		
//		Set<String> s = goTerms.keySet();
//		for (String goValue : s) {
//			(goTerms.get(goValue)).computeInfoCont(noLines);
//		}		
//	}	
//	
//}