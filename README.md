# SemanticSimilarity
Computing Semantic Similarity between 2 genes

The project is used to compute  the semantic similarity using Jaccard and Resnik similarities between the following CREB3L1 and RPS11 genes.
It uses Corpus.txt as the annotation dataset for the genes and go.owl as the ontology. There are several dependencies that need to be available for the OWL API to work, attached in the resource zip. Please find owl file, zip, txt file in the input directory. Make sure you have included all the necessary jars for the code to run.
To get both the Jaccard and Resnik values, I used the All Pairs method to compute pairwise similarity between the sets of GO terms associated with the two genes. 
To get the final score, I obtained the median of the pairwise similarities.
The expected output: Jaccard value for genes CREB3L1, RPS11 is 0.125, Resnik value for genes CREB3L1, RPS11 is 0.0.


