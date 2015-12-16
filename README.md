# Gamler-CAT

This project is aimed at developing software for estimating the quality in computer aided translation (CAT) based on translation memories (TM) at the word level, a task also known as word-keeping recommendation. The name of the project refers to a song by Kenny Rogers called "The Gambler"; one of the verses says: "Every gambler knows that the secret to survivin' is knowin' what to throw away and knowin' what to keep". 

## Description of the code

The code provided is divided in three packages:
* **Gamblr-CAT-alignments** uses word alignments for word-level quality estimation (QE) in TM-based CAT. It takes as input the source language (SL) and target language (TL) segments in a TM, together with the alignment between their words in the format used by [Moses](http://www.statmt.org/moses/?n=FactoredTraining.AlignWords) and the collection of SL segments to be translated. For each segment to be translated, the tool outputs the collection of matching translation units for a given FMS threshold and the word-keeping recommendations for each of them. It is possible to provide the reference translations for the collection of SL segments to be translated; in this case, the tool performs an evaluation of the word-keeping recommendations produced by checking which words in the translation suggestion remain in the reference translations. This is done by computing a monotonous alignment between the translation suggestions and the references based on the edit distance.
* **Gamblr-CAT-SBI-lib** is a library, mainly used by the tool **Gamblr-CAT-SBI** and the pluggin **Gamblr-CAT-alignments** described above. It implements methods to match sub-segments between two segments in different languages and to extract different collections of features for word-level QE in TM-based CAT. The objective of implementing this as a library is to ease its integration in different tools.
* **Gamblr-CAT-SBI** uses the library **Gamblr-CAT-SBI-lib** to perform word-level QE in TM-based CAT. This tool takes as an input the SL and TL segments of a TM together with a collection of sub-segment pairs, which are the result of splitting the segments in the TM and using SBI to translate them in both translation directions, and the collection of SL segments to be translated. As in **Gamblr-CAT-alignments**, the tool outputs the collection of matching translation units corresponding to each segment to be translated for a given FMS threshold and the word-keeping recommendations for each of them. As in **Gamblr-CAT-alignments** the reference translations can be provided for the collection of SL segments to be translated to evaluate the word-keeping recommendation performance.

## Citation

If you use this software in a scientific work, please reference the following paper:

Miquel Esplà-Gomis, Felipe Sánchez-Martínez, and Mikel L. Forcada. (2015). Using Machine Translation to Provide Target-Language Edit Hints in Computer Aided Translation Based on Translation Memories. In Journal of Artificial Intelligence Research, volume 53, p. 169–222.
