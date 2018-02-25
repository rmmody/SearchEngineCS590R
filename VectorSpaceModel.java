import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

public class VectorSpaceModel {
	ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
	Retriever Retriever = new Retriever();

	HashMap<String, List<Integer>> wordsInfo = dsManager.readPlayIdorByteInfo("byteInfo.txt");
	HashMap<Integer, Integer> docInfo = dsManager.readDocInfo("docId.txt");
	HashMap<Integer, String> sceneId = dsManager.readSceneId("sceneId.txt");
	List<String> vocabulary = new ArrayList<String>(wordsInfo.keySet());
	Set<Integer> documents = docInfo.keySet();
	TreeMap<Integer, List<Double>> docToWordCount = new TreeMap<>();
	List<List<Double>> vectorizedQueries = new ArrayList<>();

	public void mapTFToDoc() {
		System.out.println("Calculating scores using Vector Space Model as a scoring function");
		Collections.sort(vocabulary);
		HashMap<String, List<Integer>> wordPostings = new HashMap<>();
		for (int docId : documents) {
			List<Double> tf = new ArrayList<>(Collections.nCopies(vocabulary.size(), 0.0));
			int wordIndex = 0;
			double normFactor = 0;

			for (String word : vocabulary) {
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
							value = 1 + Math.log10(count);
						}
						int docFreq = wordsInfo.get(word).get(3);
						value *= Math.log10((documents.size() * 1.0) / docFreq);
						normFactor += (value * value);
						tf.add(wordIndex, (double) value);

						break;
					}
					i += count;
				}
				wordIndex++;

			}
			normFactor = Math.sqrt(normFactor);

			for (int i = 0; i < tf.size(); i++) {
				tf.set(i, tf.get(i) / (normFactor * 1.0));
			}
			docToWordCount.put(docId, tf);
		}

	}

	public void queryToWordCount(List<String> queries) {

		for (String query : queries) {
			String[] queryTerms = query.split(" ");
			List<Double> queryTermFreq = new ArrayList<>(Collections.nCopies(vocabulary.size(), 0.0));
			double normFactor = 0.0;

			for (int index = 0; index < vocabulary.size(); index++) {
				String vocabTerm = vocabulary.get(index);
				double occurences = 0.0;
				double value = 0.0;
				for (String queryTerm : queryTerms) {

					if (queryTerm.equals(vocabTerm)) {
						occurences += 1.0;
					}

				}
				if (occurences > 0.0) {
					value = Math.log10(occurences) + 1;
					int docFreq = wordsInfo.get(vocabTerm).get(3);
					value *= Math.log10((documents.size() * 1.0) / docFreq);
				}
				queryTermFreq.add(index, value);
				normFactor += (value * value);
			}

			normFactor = Math.sqrt(normFactor);
			for (int i = 0; i < queryTermFreq.size(); i++) {
				queryTermFreq.set(i, queryTermFreq.get(i) / (normFactor * 1.0));
			}
			vectorizedQueries.add(queryTermFreq);

		}

	}

	public List<PriorityQueue<Scores>> computeCosineScores() {
		List<PriorityQueue<Scores>> allScores = new ArrayList<>();
		int querynum = 1;
		Comparator<Scores> comparator = new Scores();
		for (List<Double> vectorizedQuery : vectorizedQueries) {
			PriorityQueue<Scores> totalScores = new PriorityQueue<Scores>(comparator);

			for (int i = 0; i < docToWordCount.size(); i++) {
				double score = 0.0;
				List<Double> vectorSpace = docToWordCount.get(i);

				for (int j = 0; j < vectorSpace.size(); j++) {
					score += (vectorSpace.get(j) * vectorizedQuery.get(j));
				}
				if (score != 0.0) {
					Scores docScore = new Scores();
					docScore.setScore(score);
					docScore.setDocId(i);
					docScore.setQueryNum(querynum);
					docScore.setSceneId(sceneId.get(i));
					docScore.setMethod("rmmody-Cosine");
					totalScores.add(docScore);
				}

			}
			allScores.add(totalScores);
			querynum++;
			
		}
		return allScores;
	}


}
