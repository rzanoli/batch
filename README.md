# batch

BATCH (fBk keyphrAses sofT Clustering algoritHm) is an open source library for clustering keyphrases (expressions which help understand and summarize the content of documents) in text documents. It uses an algorithm based on graph connectivity for Cluster analysis, by first representing the similarity among keyphrases in a similarity graph, and afterwards finding all the connected subgraphs (groups of keyphrases that are connected to one onother, but that have no connections to keyphrases outside the group) as clusters. The algorithm does not make any prior assumptions on the number of the clusters.

BATCH has been designed to meet the following requirements:

- Method: 
  - soft agglomerative clustering.
-- constrained clustering
Use must-link constraints (i.e., keyphrases compatibility relations) to specify that two Keyphrases in a must-link relation have to be associated with the same cluster
-- Incremental Clustering New keyphrases added to the data collection without having to perform a full re-clustering
- Efficiency: parallelized to exploit multi-core processors. 
- Portability: written in Java to be portable across different platforms.
- Simplicity:  implemented as a maven project to make it easy to install, configure and use. A Command Line Interface (CLI) is provided for convenience of experiments.



## Getting started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

- Java 1.8 or later
- Apache Maven (http://maven.apache.org) (optional)

### How to get the code

The jar file containing all the Java code can be download from this address: 

...


### Dataset format
to do

### Installation

Save the BATCH jar file that you have just downaloded into your working directory.

### Running the code

From your working directory, run the following command:

```> ...```

Where: 
- dirIn
- dirOut


## Authors

- Alberto Lavelli
- Bernardo Magnini
- Manuela Speranza
- Roberto Zanoli

## License

