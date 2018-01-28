# batch

BATCH (fBk keyphrAses sofT Clustering algoritHm) is an open source library for clustering keyphrases (expressions which help understand and summarize the content of documents) in text documents. It uses an algorithm based on graph connectivity for Cluster analysis, by first representing the similarity among keyphrases in a similarity graph, and afterwards finding all the connected subgraphs (groups of keyphrases that are connected to one another, but that have no connections to keyphrases outside the group) as clusters. The algorithm does not make any prior assumptions on the number of the clusters.

BATCH has been designed to meet the following requirements:

- Method: 
  - Soft agglomerative clustering: a keyphrase can belong to more than one cluster.
  - Constrained clustering: use must-link constraints (i.e., keyphrases compatibility relations) to specify that two Keyphrases in a must-link relation have to be associated with the same cluster.
  - Incremental clustering: new keyphrases added to the data collection without having to perform a full re-clustering
- Efficiency: parallelized to exploit multi-core processors. 
- Portability: written in Java to be portable across different platforms.
- Simplicity:  implemented as a maven project to make it easy to install, configure and use. A Command Line Interface (CLI) is provided for convenience of experiments.

keyphrases compatibility relations are used to specify that two keyphrases should be assigned into one cluster. The current implemented relations are:
- Abbreviation: We consider an abbreviation any token which finishes with a dot and we check if it is a substring of some 
  token in another keyphrase; kj and ki must have the same number of tokens in the same order, one or more tokens 
  in kj can be the abbreviation of one or more tokens in ki.
- Acronym: kj and ki are variants if kj consists of one token of n>1 letters and ki consists of n tokens; the initials of the n tokens of ki are the letters composing the single token of kj in the same order.
- Entailment: check if two variants have the same semantic head and one has just one token less.
- Equality: kj and kl are equal (i.e. same tokens in the same order). This is a special case, as occurrences of the same keyphrase are not considered as different variants, rather we collapse them into a single keyphrase type.

## Getting started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

- Java 1.8 or later
- Apache Maven (http://maven.apache.org) (optional)

### How to get the code

The jar file containing all the Java code can be download from this address: 

https://github.com/rzanoli/batch/releases/download/v0.1-alpha/batch-0.0.1-SNAPSHOT-jar-with-dependencies.jar


### Dataset format

```
rank    keyword synonyms        score   frequency
1       ricorso abusivo al credito              23.702113582721168      2
2       fascicolo               23.591343915003407      2
3       chiarezza sui prospetti         19.96721317927301       1
4       crac della banca                17.766248619607136      1
5       fascicolo sulle obbligazioni            17.10372433919623       1
6       grane giudiziarie               16.2332507011343        1
7       ex consiglieri          14.139538077863026      1
8      sindaci revisori                13.82010208421649       1
```

### Installation

Save the BATCH jar file that you have just downaloded into your working directory.

### Running the code

From your working directory, run the following command:

```>java -cp batch-0.0.1-SNAPSHOT-jar-with-dependencies.jar eu.fbk.hlt.nlp.cluster.Runner dirIn dirOut modelDir incremental```

Where: 
- dirIn is the directory containing the keyphrases produced by KD (https://dh.fbk.eu/technologies/kd) tool.
- dirOut is the directory containing the produced clusters.
- modelDir is the directory containing the produced graph and the keyphrases list in input that can be used in a second phase of incremental clustering.
- incremental true for incremental clustering; false otherwise.

## Authors

- Alberto Lavelli
- Bernardo Magnini
- Manuela Speranza
- Roberto Zanoli

## License

