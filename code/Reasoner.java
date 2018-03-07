import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

public class Reasoner {

	private int noLines = 0;
	private ArrayList<String> gene1GOids;
	private ArrayList<String> gene2GOids;
	
	public Reasoner() {
		
		gene1GOids = new ArrayList<String>();
		gene2GOids = new ArrayList<String>();
	}
	
	HashMap<String, GOTerm> reasonOnData(RandomAccessFile f, File owlFile, String gene1, String gene2) 
			throws IOException, OWLOntologyCreationException{							
		
		System.out.println("I am uploading the ontology "
				+ "and finding the superclasses for each GO term in the data set");
		HashMap<String, GOTerm> goTerms = new HashMap<String, GOTerm>();		
		//take each line from the input file and for each GO term, find its super concepts
		
		String DOCUMENT_IRI = "file:./go.owl&format=RDF/XML";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		IRI docIRI = IRI.create(owlFile);
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(docIRI);
				
		String line = null;					
		int count = 0;
		while(f != null && (line = f.readLine()) != null) {
			noLines++;			
			String []resultParseLine = line.split(",");
			//build a hashmap that gives the info if for a certain gene, a certain GO term 
			//was already taken into consideration or not			
			HashMap<String, Boolean> addedGOTerms= new HashMap<String, Boolean>();
									
			//while working with the data file, look for the genes of your interest
			//when you find it, put each GO term associated with it in an array for later use
			boolean isGeneLine = false;
			if(resultParseLine[0].equals(gene1) || resultParseLine[0].equals(gene2)) {
				isGeneLine = true;
				count++;
			}			
			for(int i=1; i<resultParseLine.length; i++) {
				String goId = resultParseLine[i];				
				//if this a gene line, then save the GO id in the array for gene 1 or 2
				//verify to which one of the 2 genes you have to annotate this GO term
				if(isGeneLine == true && count == 1) {						
					gene1GOids.add(goId);					
				}
				else if(isGeneLine == true && count == 2) {					
					gene2GOids.add(goId);
				}
															
				HashMap<String, GOTerm> superClasses = createGOSuperClasses(ont, manager, goId);
				if(superClasses == null) {
					continue;
				}
				//see if the current go term was already added to the hash table
				GOTerm g = goTerms.get(goId);
				if(g == null) {
					goTerms.put(goId, new GOTerm(goId, superClasses));
					addedGOTerms.put(goId, new Boolean(true));
				}
				else {
					//maybe g was found previously for gene x and already added once					
					//maybe g was found prev for gene y and you just have to increment its count
					//if g is added, but does not contain its superclasses, then just add them					
					if(addedGOTerms.get(goId) == null) {
						g.incCount();
						addedGOTerms.put(goId, new Boolean(true));
					}					
					if(g.getSuperClasses() == null) {
						g.setSuperClasses(superClasses);
					}
				}
				//for each superclass, add it to the hashtable or increment its count if it is already present
				Set<String> goIds = superClasses.keySet();
				for (String key : goIds) {					
					GOTerm g1 = goTerms.get(key);
					//if g1 is not added in the map, then add g1 
					//if g1 is already added for gene x, then do nothing
					//if g1 is added for another gene, but not for gene x, then increment its count
					if(g1 == null) {
						goTerms.put(key, superClasses.get(key));
						addedGOTerms.put(key, new Boolean(true));
					}
					else {
						if(addedGOTerms.get(key) == null) {
							g1.incCount();
							addedGOTerms.put(key, new Boolean(true));
						}						
					}
				}
			}
		}
		return goTerms;
	}
	
	private HashMap<String, GOTerm> createGOSuperClasses(OWLOntology ont, OWLOntologyManager manager, 
														String GOid) throws OWLOntologyCreationException{
		
		HashMap<String, GOTerm> map = new HashMap<String, GOTerm>();
		
		NodeSet<OWLClass> superClasses = findSuperClasses(ont, manager, GOid);
		if(superClasses == null) {
			return null;
		}
		Iterator<Node<OWLClass>> it = superClasses.iterator();
		while(it.hasNext()) {
			Node<OWLClass> n = it.next();
			OWLClass o = n.getRepresentativeElement();
			String superClassId = o.getIRI().getRemainder().get();			
			GOTerm t = new GOTerm(superClassId, null);
			map.put(superClassId, t);
		}
		//also put the GO term itself in its super classes 
		GOTerm t = new GOTerm(GOid, null);
		map.put(GOid, t);
		return map;
	}
	
	private NodeSet<OWLClass> findSuperClasses(OWLOntology ont, OWLOntologyManager manager, 
			String GOid) throws OWLOntologyCreationException {        		        

       OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();        
       OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ont);
       OWLDataFactory fac = manager.getOWLDataFactory();
       IRI i = IRI.create("http://purl.obolibrary.org/obo/"+GOid);
       //verify that the GO term that will be processed is present in the ontology
       if (i.getRemainder().isPresent() == false) {        	
       	return null;
       }
       OWLClass goTerm1 = fac.getOWLClass(i);        
       NodeSet<OWLClass> superCls = reasoner.getSuperClasses(goTerm1, false);
       return superCls;
	}
	
	public double semSimilarity(String methodType, HashMap<String, GOTerm> goTerms) {
						
		ArrayList<Double> allPairesResults = new ArrayList<Double>();
		//generate each pair of go terms and for each compute jaccard ss
		for (String goId1 : gene1GOids) {
			for (String goId2 : gene2GOids) {				
				double value = 0;
				if(methodType == "Jaccard") {
					value = jaccardPairGOTerms(goTerms.get(goId1), goTerms.get(goId2));
				}
				else if(methodType == "Resnik") {
					value = resnikPairGOTerms(goTerms.get(goId1), goTerms.get(goId2), goTerms);
				}
				if(value != -1) {
					allPairesResults.add(value);
				}				
			}			
		}
		//return the median of all values in the array
		Collections.sort(allPairesResults);		
		double result = 0;
		if(allPairesResults.size() % 2 == 0) {
			result = allPairesResults.get(allPairesResults.size()/2) + 
										allPairesResults.get((allPairesResults.size()/2)+1);
			result = result/2;
		}
		else {
			result = allPairesResults.get((int) Math.ceil(allPairesResults.size()/2));
		}
		return result;
	}
	
	private double jaccardPairGOTerms(GOTerm g1, GOTerm g2) {
		
		if(g1 == null || g2 == null) {
			return -1;
		}
		int commonSuperClasses = 0;
		int unionSuperClasses = 0;
		HashMap<String, GOTerm> superclassesG1 = g1.getSuperClasses();
		HashMap<String, GOTerm> superclassesG2 = g2.getSuperClasses();
		Set<String> goIDSuperclassesG1 = superclassesG1.keySet();
		for (String goId : goIDSuperclassesG1) {
			if(superclassesG2.get(goId) != null) {
				commonSuperClasses++;
			}
		}
		unionSuperClasses = superclassesG1.size() - commonSuperClasses;
		unionSuperClasses = unionSuperClasses + superclassesG2.size();
		double jaccardValue = commonSuperClasses/(double) unionSuperClasses;
//		System.out.println("for go terms "+g1.getValue()+", "+g2.getValue()+
//						" no common superclasses is "+commonSuperClasses+
//						" no union superclasses is "+unionSuperClasses);
		return jaccardValue;	
	}		
	
	private double resnikPairGOTerms(GOTerm g1, GOTerm g2, HashMap<String, GOTerm> goTerms) {
		
		if(g1 == null || g2 == null) {
			return -1;
		}
		double resnikValue = 0; 
		HashMap<String, GOTerm> superclassesG1 = g1.getSuperClasses();
		HashMap<String, GOTerm> superclassesG2 = g2.getSuperClasses();
		Set<String> goIDSuperclassesG1 = superclassesG1.keySet();
		for (String goId : goIDSuperclassesG1) {
			if(superclassesG2.get(goId) != null) {
				if(goTerms.get(goId).getInfoCont() > resnikValue) {
					resnikValue = goTerms.get(goId).getInfoCont();
				}
			}
		}				
		//System.out.println("for go terms "+g1.getValue()+", "+g2.getValue()+" resnik value is "+resnikValue);
		return resnikValue;
	}
	
	public int getNoLines() {
		return noLines;
	}	
}
