# SemanticSimilarity
Computing Semantic Similarity between 2 genes

The project is used to compute  the semantic similarity using Jaccard and Resnik similarities between the following CREB3L1 and RPS11 genes.
It uses Corpus.txt as the annotation dataset for the genes and go.owl as the ontology. 
To get both the Jaccard and Resnik values, I uses the All Pairs method to compute pairwise similarity between the sets of GO terms associated with the two genes. 
To get the final score, I obtained the median of the pairwise similarities.


