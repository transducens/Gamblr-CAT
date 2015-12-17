# Gamler-CAT

This project is aimed at developing software for estimating the quality in computer aided translation (CAT) based on translation memories (TM) at the word level, a task also known as word-keeping recommendation. The name of the project refers to a song by Kenny Rogers called "The Gambler"; one of the verses says: "Every gambler knows that the secret to survivin' is knowin' what to throw away and knowin' what to keep". 

## Description of the code

The code provided is divided in three packages:
* **Gamblr-CAT-alignments** uses word alignments for word-level quality estimation (QE) in TM-based CAT. It takes as input the source language (SL) and target language (TL) segments in a TM, together with the alignment between their words in the format used by [Moses](http://www.statmt.org/moses/?n=FactoredTraining.AlignWords) and the collection of SL segments to be translated. For each segment to be translated, the tool outputs the collection of matching translation units for a given FMS threshold and the word-keeping recommendations for each of them. It is possible to provide the reference translations for the collection of SL segments to be translated; in this case, the tool performs an evaluation of the word-keeping recommendations produced by checking which words in the translation suggestion remain in the reference translations. This is done by computing a monotonous alignment between the translation suggestions and the references based on the edit distance.
* **Gamblr-CAT-SBI** uses the library **Gamblr-CAT-SBI-lib** (see below) to perform word-level QE in TM-based CAT by using sources of bilingual information (SBI). This tool takes as an input the SL and TL segments of a TM together with a collection of sub-segment pairs, which are the result of splitting the segments in the TM and using SBI to translate them in both translation directions, and the collection of SL segments to be translated. As in **Gamblr-CAT-alignments**, the tool outputs the collection of matching translation units corresponding to each segment to be translated for a given FMS threshold and the word-keeping recommendations for each of them. As in **Gamblr-CAT-alignments** the reference translations can be provided for the collection of SL segments to be translated to evaluate the word-keeping recommendation performance.
* **Gamblr-CAT-SBI-lib** is a library, mainly used by the tools **Gamblr-CAT-SBI** and **Gamblr-CAT-alignments** described above. It implements methods to match sub-segments between two segments in different languages and to extract different collections of features for word-level QE in TM-based CAT. The objective of implementing this as a library is to ease its integration in different tools.
 
## Using the tools

Only two of the packages can be used as a stand-alone tool: **Gamblr-CAT-alignments** and **Gamblr-CAT-SBI**. Once compiled, this is how they are used:

### Gamblr-CAT-alignments

A standard call to the tool can be made by using the command:
```bash
java -cp Gamblr-CAT-alignments.jar es.ua.dlsi.gizawkrecommend.GWKRecommend --s-tmsentences 
<SL.TM>.gz --t-tmsentences <TL.TM>.gz -a <ALIGNMENTS> --ssentences <SL.TEST>.gz -c <THRESHOLD> --decision <DECISION>
```
The parameters of the call correspond to the following options:
* `<SL.TM>.gz` and `<TL.TM>.gz`: Source language and target language segments in the translation memory to be used for translation. The files must contain one segment per line and the segments at the same line must be mutual translations (parallel segments). Both files must be gzipped.
* `<ALIGNMENTS>`: File containing the symmetrised word alignments between every pair of segments in the translation memory. This format is described at the Moses website: [http://www.statmt.org/moses/?n=FactoredTraining.AlignWords](http://www.statmt.org/moses/?n=FactoredTraining.AlignWords).
* `<SL.TEST>.gz`: Source language segments to be translated. Again, there must be one segment per line and the file must be gzipped.
* `<THRESHOLD>`: Fuzzy-match score threshold to choose the translation proposals.
* `<DECISION>`: Decision criterion for estimating the quality of the words. There are two decision criterions possiblee: `majority` or `unanimity`.

### Gamblr-CAT-SBI

In this case, two steps are required for word-level quality estimation. In the first setp, the translation memory is pre-processed to extract information from each translation unit using sub-segment translations obtained by means of SBI. This is done by running the follwing command:

```bash
java -cp Gamblr-CAT-SBI.jar es.ua.dlsi.translationmemory.TranslationMemory -a g --tm-source <SL.TM>.gz --tm-target <TL.TM>.gz --tm-path <TM.OBJECT> -m <MAXLEN> --seg-source <SL.SUBSEGS> --seg-target <TL.SUBSEGS>
```
The parameters of the call correspond to the following options:
* `<SL.TM>.gz` and `<TL.TM>.gz`: Source language and target language segments in the translation memory to be used for translation. The files must contain one segment per line and the segments at the same line must be mutual translations (parallel segments). Both files must be gzipped.
* `<TM.OBJECT>`: Output path for the file (java object) that will be generated containing the processed translation memory.
* `<MAXLEN>`: Maximum length (number of words) of the sub-segments used.
* `<SL.SUBSEGS> and <TL.SUBSEGS>`: Sub-segment pairs obtained by splitting the segments in `<SL.TM>.gz` and `<TL.TM>.gz` in sub-segments of length from 1 word to `<MAXLEN>` and translating them into TL and SL, respectively.

Once the TM object has been created, it is possible to estimate the quality of the words in the translation proposals by using an heurisitc method, that does not need to be trained, or a multilayer perceptron classifier. In the first case, it is only necessary to run the command:
```bash
java Gamblr-CAT-SBI.jar es.ua.dlsi.tests.geometric.TestGeometricClassifier --test-source <SL.TEST>.gz -t <THRESHOLD>  --tm-path <TM.OBJECT> --max-segment-len <MAXLEN>
```

To use the multilayer perceptron classifier, it is necessary to train a classifier by using the machine learning toolkit [weka](http://www.cs.waikato.ac.nz/ml/weka/). To do so, it is necessary to obtain a collection of features:
```bash
java -cp Gamblr-CAT-SBI.jar es.ua.dlsi.translationmemory.TranslationMemory -a f --feat-type source-target --provide-fms-editdistance --discard-words-noevidence --tm-path <TM.OBJECT> -t <THRESHOLD> -m <MAXLEN> -o <FEATURES>
```
Where `<FEATURES>` is the path to the file where the features will be stored. Before training the classifier, it is necessary to convert the features to the ARFF format used in weka (see the [documentation](http://www.cs.waikato.ac.nz/ml/weka/arff.html)). Once this is done, the following command can be run using the weka JAR file inclued in the directory `lib` of the package:
```bash
java lib/weka.jar weka.classifiers.functions.MultilayerPerceptron -L 0.4 -M 0.1 -N 0 -V 10 -S 0 -E 10 -H i -C -I -D -t <FEATURES>.arff -d <CLASSIFIER.OBJECT>
```
where the `<CLASSIFIER.OBJECT>` is the java object generated that contains the classifier trained. Once the classifier is trained, it is possible to compute quality estimation by running the command:
```bash
ava -cp Gamblr-CAT-SBI.jar es.ua.dlsi.tests.weka.general.normalised.TestPerceptronClassifierGeometricStyleWekaPerceptronOnlyPosWithQuantitativeInfo --test-source <SL.TEST> -t <THRESHOLD> --model <CLASSIFIER.OBJECT> --tm-path <TM.OBJECT> --max-segment-len <MAXLEN>
```

## Citation

If you use this software in a scientific work, please reference the following paper:

Miquel Esplà-Gomis, Felipe Sánchez-Martínez, and Mikel L. Forcada. (2015). **Using Machine Translation to Provide Target-Language Edit Hints in Computer Aided Translation Based on Translation Memories**. In *Journal of Artificial Intelligence Research*, volume 53, p. 169–222.
