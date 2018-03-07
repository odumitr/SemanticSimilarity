import java.util.HashMap;

public class GOTerm {

	private String value;
	private int count;
	private HashMap<String, GOTerm> superClasses;
	private double infoCont;

	public GOTerm(String value, HashMap<String, GOTerm> map) {
		
		this.value = value;
		this.count = 1;
		superClasses = map;
	}	
	
	public HashMap<String, GOTerm> getSuperClasses() {
		return superClasses;
	}

	public void setSuperClasses(HashMap<String, GOTerm> superClasses) {
		this.superClasses = superClasses;
	}
	
	public String getValue() {
		return value;
	}
	
	public int getCount() {
		return count;
	}
	
	public void incCount() {
		count++;
	}
	
	public void setCount(int count) {
		this.count = count;
	}	
	
	public double getInfoCont() {
		return infoCont;
	}
	
	public void computeInfoCont(int maxAppearances) {
		
		this.infoCont = -(Math.log(count/(double) maxAppearances));
		if(infoCont == -0.0) {
			infoCont = 0.0;
		}
	}
	
	public String toString() {
		
		return "GO term value is "+value+", info content is "+infoCont+"\n";
	}
}
