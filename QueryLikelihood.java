import java.util.*;

public class QueryLikelihood {

	ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
	Retriever Retriever = new Retriever();

	HashMap<String, List<Integer>> wordsInfo = dsManager.readPlayIdorByteInfo("byteInfo.txt");
	HashMap<Integer, Integer> docInfo = dsManager.readDocInfo("docId.txt");
	HashMap<Integer, Integer> docLen = dsManager.readDocInfo("docLen.txt");
	HashMap<Integer, String> sceneId = dsManager.readSceneId("sceneId.txt");
	List<String> vocabulary = new ArrayList<String>(wordsInfo.keySet());
	Set<Integer> documents = docInfo.keySet();
	TreeMap<Integer, List<Double>> docToWordCount = new TreeMap<>();
	List<List<Double>> vectorizedQueries = new ArrayList<>();
	int docSize = documents.size();
	Comparator<Scores> comparator = new Scores();

	public List<Scores> computeScores(Expression e) {
		int C = 0;
		for (int key : docLen.keySet()) {
			C += docLen.get(key);

		}

		List<Scores> allScores = new ArrayList<>();
		if (e instanceof Term) {

			String[] terms = new String[] { ((Term) e).getTerms() };
			
			for (int docId : documents) {
				boolean smoothing = false;
				HashMap<String, List<Integer>> wordPostings = new HashMap<>();
				double score = 0.0;

				int cqi = 0;

				int fqi = 0;
				for (String term : terms) {
					cqi = wordsInfo.get(term).get(2);
					List<Integer> posting = new ArrayList<>();
					if (!wordPostings.containsKey(term)) {
						posting = Retriever.retrieve(term);
						wordPostings.put(term, posting);
					} else {
						posting = wordPostings.get(term);
					}
					for (int i = 0; i < posting.size() - 1; i++) {

						int documentId = posting.get(i);
						int count = posting.get(++i);
						if (docId == documentId) {
							fqi = count;

							break;
						}
						i += count;
					}

					int D = docLen.get(docId);
					double mu = 2000;
					double alphaD = mu / (D + mu);

					double term1 = ((1 - alphaD) * fqi * 1.0) / D;
					double term2 = (alphaD * cqi * 1.0) / C;

					score += term1 + term2;
					if(term1==0){
						smoothing = true;
					}
					
				}
				Scores docScore = new Scores();
				if (score != 0.0) {
					score = Math.log10(score);
					docScore.setScore(score);
					docScore.setDocId(docId);
					docScore.setSmoothing(smoothing);
					// docScore.setQueryNum(queryNum);
					docScore.setSceneId(sceneId.get(docId));
					// docScore.setMethod(methodName);
					allScores.add(docScore);
				}

			}

		}

		else if (e instanceof UWEvaluator) {
			UnOrderedWindow unOrderWin = new UnOrderedWindow();
			boolean bAflag = false;
			List<String> terms = ((UWEvaluator) e).getInputTerms();
			int windowSize = ((UWEvaluator) e).getWindowSize();

			HashMap<Integer, WindowDetails> windowPerDoc = unOrderWin.unOrderedWindow(terms, windowSize, bAflag);
			int cwc = 0;
			int wc = 0;
			for (int docId : documents) {
				cwc += windowPerDoc.get(docId).getWindowCount();
			}

			for (int docId : documents) {
				double score = 0.0;
				wc = windowPerDoc.get(docId).getWindowCount();
				int D = docLen.get(docId);
				double mu = 2000.0;
				double alphaD = mu / (D + mu);

				double term1 = ((1 - alphaD) * wc * 1.0) / D;
				double term2 = (alphaD * cwc * 1.0) / C;
				score = term1 + term2;
				Scores docScore = new Scores();
				if (wc == 0) {
					docScore.setSmoothing(true);
				} else {
					docScore.setSmoothing(false);
				}
				if (score != 0.0) {
					score = Math.log10(score);
					docScore.setScore(score);
					docScore.setDocId(docId);
					docScore.setSceneId(sceneId.get(docId));
					allScores.add(docScore);
				}
			}

		}

		else if (e instanceof OWEvaluator) {
			OrderedWindow orderWin = new OrderedWindow();
			List<String> terms = ((OWEvaluator) e).getInputTerms();
			int separationSize = ((OWEvaluator) e).getSeparationSize();

			HashMap<Integer, WindowDetails> windowPerDoc = orderWin.orderedWindow(terms, separationSize);
			int cwc = 0;
			int wc = 0;
			for (int docId : documents) {
				cwc += windowPerDoc.get(docId).getWindowCount();
			}

			for (int docId : documents) {
				double score = 0.0;
				wc = windowPerDoc.get(docId).getWindowCount();
				int D = docLen.get(docId);
				double mu = 2000;
				double alphaD = mu / (D + mu);

				double term1 = ((1 - alphaD) * wc * 1.0) / D;
				double term2 = (alphaD * cwc * 1.0) / C;

				score = term1 + term2;

				Scores docScore = new Scores();
				if (wc == 0) {
					docScore.setSmoothing(true);
				} else {
					docScore.setSmoothing(false);
				}
				if (score != 0.0) {
					score = Math.log10(score);
					docScore.setScore(score);
					docScore.setDocId(docId);
					docScore.setSceneId(sceneId.get(docId));
					allScores.add(docScore);
				}
			}
		}

		else if (e instanceof BAndEvaluator) {
			BooleanAnd band = new BooleanAnd();
			boolean bAflag = true;
			List<String> terms = ((BAndEvaluator) e).getInputTerms();
			HashMap<Integer, WindowDetails> windowPerDoc = band.booleanAnd(terms);
			int cwc = 0;
			int wc = 0;
			for (int docId : documents) {
				cwc += windowPerDoc.get(docId).getWindowCount();
			}

			for (int docId : documents) {
				double score = 0.0;
				wc = windowPerDoc.get(docId).getWindowCount();
				int D = docLen.get(docId);
				double mu = 2000;
				double alphaD = mu / (D + mu);

				double term1 = ((1 - alphaD) * wc * 1.0) / D;
				double term2 = (alphaD * cwc * 1.0) / C;

				score = term1 + term2;

				Scores docScore = new Scores();
				if (score != 0.0) {
					score = Math.log10(score);
					docScore.setScore(score);
					docScore.setDocId(docId);
					// docScore.setQueryNum(queryNum);
					docScore.setSceneId(sceneId.get(docId));
					// docScore.setMethod(methodName);
					allScores.add(docScore);
				}
			}
		}
		return allScores;

	}

}
