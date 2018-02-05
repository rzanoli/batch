# batch

BATCH (fBk keyphrAses sofT Clustering algoritHm) is an open source library for clustering keyphrases (expressions which help understand and summarize the content of documents) in text documents. It uses an algorithm based on graph connectivity for Cluster analysis, by first representing the similarity among keyphrases in a similarity graph, and then finding all the connected subgraphs (groups of keyphrases that are connected to one another, but that have no connections to keyphrases outside the group) as clusters. The algorithm does not make any prior assumptions on the number of the clusters.

BATCH has been designed to meet the following requirements:

- Method: 
  - Soft agglomerative clustering: a keyphrase can belong to more than one cluster.
  - Constrained clustering: use must-link constraints (i.e., keyphrases compatibility relations) to specify that two Keyphrases in a must-link relation have to be associated with the same cluster.
  - Incremental clustering: new keyphrases added to the data collection without having to perform a full re-clustering
- Efficiency: parallelized to exploit multi-core processors. 
- Portability: written in Java to be portable across different platforms.
- Simplicity:  implemented as a maven project to make it easy to install, configure and use. A Command Line Interface (CLI) is provided for convenience of experiments.

keyphrases compatibility relations are used to specify that two keyphrases has to be assigned into one cluster. The current implemented relations are:
- Abbreviation: We consider an abbreviation any token which finishes with a dot and we check if it is a substring of some token in another keyphrase; kj and ki must have the same number of tokens in the same order, and the number of token must be more than 1, one token in kj can be the abbreviation of one token in ki
- Acronym: kj and ki are variants if kj consists of one token of n>1 letters and ki consists of n tokens; the initials of the n tokens of ki are the letters composing the single token of kj in the same order.
- Entailment: check if two variants have the same semantic head and one has just one token less.
- Equality: kj and kl are equal (i.e. same tokens in the same order). This is a special case, as occurrences of the same keyphrase are not considered as different variants, rather we collapse them into a single keyphrase type.
- Modifier Swap: check if all tokens are the same in different order and the head is the same (no permutation of the head)
e.g. elezioni francesi 2017 for elezioni 2017 francesi
- Singular/Plural: check if two variants have the same lemma (based on the comp-morpho analysis of TextPro)
e.g. fondazione for fondazioni (and viceversa)


## Getting started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

- Java 1.8 or later
- Apache Maven (http://maven.apache.org) (optional)

### How to get the code

The jar file containing all the Java code can be download from this address: 

https://github.com/rzanoli/batch/releases/download/v0.1-alpha/batch-0.0.1-SNAPSHOT-jar-with-dependencies.jar


### Dataset format

Files in input contain the keyphrases produced by KD (https://dh.fbk.eu/technologies/kd) in IOB2 format, e.g.,
```
Sulla     ES  su/det    O
salute    SS  salute    B-KEY
del       ES  di/det    I-KEY
pianeta   SS  pianeta   I-KEY
stiamo    VI  stare     O
perdendo  VG  perdere   O
la        RS  det       O
battaglia SS  battaglia B-KEY
.         XPS full_stop O
```

The output consists of xml files containing the produced clusters, e.g.,
```
<KEC_graph id="88766" node_count="2">
     <node id="88766" root="true">
          <text>kit di scrittura gratuito</text>
          <ids>9767800.txt.txp.iob_10</ids>
     </node>
     <node id="140151" root="false">
          <text>kit gratuito di scrittura</text>
          <ids>9891493.txt.txp.iob_14 9917114.txt.txp.iob_10</ids>
     </node>
     <edge relation_role="7" source="88766" target="140151"/>
</KEC_graph>
```

### Installation

Save the BATCH jar file that you have just downaloded into your working directory.

### Running the code

From your working directory, run the following command:

```>java -cp batch-0.0.1-SNAPSHOT-jar-with-dependencies.jar eu.fbk.hlt.nlp.cluster.Runner filesIn clustersOut graphDir```

Where: 
- filesIn the directory containing the keyphrases produced by KD.
- clustersOut the directory containing the produced clusters.
- graphDir the directory containing the produced graph and the keyphrases list in input that can be used in a second phase of incremental clustering.

## Authors

- Alberto Lavelli
- Bernardo Magnini
- Manuela Speranza
- Roberto Zanoli

## License

