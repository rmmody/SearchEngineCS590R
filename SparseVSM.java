import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class SparseVSM {
	ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
	Retriever Retriever = new Retriever();

	HashMap<String, List<Integer>> wordsInfo = dsManager.readPlayIdorByteInfo("byteInfo.txt");
	HashMap<Integer, Integer> docInfo = dsManager.readDocInfo("docId.txt");
	HashMap<Integer, String> sceneId = dsManager.readSceneId("sceneId.txt");
	Set<Integer> documents = docInfo.keySet();
	TreeMap<Integer, HashMap<String, Double>> docVector = new TreeMap<>();
	HashMap<Integer, List<String>> wordsInDoc = dsManager.getWordsinDoc("docWordList.txt");

	public TreeMap<Integer, HashMap<String, Double>> computeDocVector() {
		System.out.println("Computing document vectors using Vector Space Model as a scoring function");
		HashMap<String, List<Integer>> wordPostings = new HashMap<>();
		for (int docId : documents) {
			HashMap<String, Double> tf = new HashMap<>();

			double normFactor = 0.0;
			Set<String> docVocabulary = new HashSet<>(wordsInDoc.get(docId));

			for (String word : docVocabulary) {
				if (word.equals("") || word.equals(" ")) {
					continue;
				}
				List<Integer> posting = new ArrayList<>();

				if (!wordPostings.containsKey(word)) {
					posting = Retriever.retrieve(word);
					wordPostings.put(word, posting);
				} else {
					posting = wordPostings.get(word);
				}
				for (int i = 0; i < posting.size() - 1; i++) {
					double value = 0.0;
					int documentId = posting.get(i);
					int count = posting.get(++i);
					if (docId == documentId) {
						if (count > 0) {
							value = 1 + Math.log10(count*1.0);
						}
						int docFreq = wordsInfo.get(word).get(3);
						value *= Math.log10((documents.size() * 1.0) / docFreq);
						normFactor += (value * value);
						tf.put(word, (double) value);

						break;
					}
					i += count;
				}

			}
			normFactor = Math.sqrt(normFactor);

			for (String word : tf.keySet()) {
				tf.put(word, tf.get(word) / (normFactor * 1.0));
			}
			docVector.put(docId, tf);
		}
		return docVector;
	}

}
