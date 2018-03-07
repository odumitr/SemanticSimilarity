import java.io.File;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Input {

	private RandomAccessFile dataFile;	
	private File owlFile;
	private String gene1;
	private String gene2;
	
	public Input(RandomAccessFile dataFile, File owlFile, String gene1, String gene2) {
		
		this.dataFile = dataFile;
		this.owlFile = owlFile;
		this.gene1 = gene1;
		this.gene2 = gene2;
	}
	
	public Input() {}
	
	public RandomAccessFile getDataFile() {
		return dataFile;
	}

	public File getOwlFile() {
		return owlFile;
	}

	public String getGene1() {
		return gene1;
	}

	public void setGene1(String gene1) {
		this.gene1 = gene1;
	}

	public String getGene2() {
		return gene2;
	}

	public void setGene2(String gene2) {
		this.gene2 = gene2;
	}
	
	public void  getInput() {
		
		this.getInputFiles();
//		this.gene1 = getGene();
	//	this.gene2 = getGene();
	}
	
	private void getInputFiles() {
		
		File f = new File("Assignment3-Corpus.txt");
		File owl = new File("./go.owl");
		Scanner reader = new Scanner(System.in);  						
		if(f.exists() == false) {
			System.err.println("Input file "+f.getAbsolutePath()+" does not exist\n"+
							" Please provide the path for the file that contains the input data");
			String inputFilePath = reader.next();
			f = new File(inputFilePath);			 
		}						
		if(f == null) {
			throw new RuntimeException("The input is not valid");			
		}
		
		RandomAccessFile acc = null;		
		try {
			acc = new RandomAccessFile(f, "r");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		this.dataFile = acc;
		
		if(owl.exists() == false) {
			System.err.println("Input file "+owl.getAbsolutePath()+" does not exist\n"+
							" Please provide the path for the owl file");
			String inputFilePath = reader.next();
			owl = new File(inputFilePath);
			reader.close(); 
		}
		if(owl == null) {
			throw new RuntimeException("The input is not valid");
		}
		this.owlFile = owl;		
	}
	
	private String getGene() {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Please provide the gene for computing semantic similarity");
		String gene = sc.nextLine();
		return gene;
	}
}
