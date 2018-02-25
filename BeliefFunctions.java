import java.util.*;

public class BeliefFunctions {

	double beliefValue = 0.0;
	ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
	HashMap<Integer, Integer> docInfo = dsManager.readDocInfo("docId.txt");
	HashMap<Integer, String> sceneId = dsManager.readSceneId("sceneId.txt");
	Set<Integer> documents = docInfo.keySet();

	public List<Scores> beliefNot(List<List<Scores>> results) {

		List<Scores> beliefScores = new ArrayList<>();
		for (int docId : documents) {
			double prod = 0.0;
			boolean smoothing = true;
			for (Scores eachScore : results.get(0)) {
				if (eachScore.getDocId() == docId) {
					smoothing = (smoothing && eachScore.getSmoothing());
					prod = 1 - Math.pow(10, eachScore.getScore());
				}

			}
			beliefValue = prod;
			Scores currDocScore = new Scores();
			currDocScore.setScore(Math.log10(beliefValue));
			currDocScore.setDocId(docId);
			currDocScore.setSmoothing(smoothing);
			currDocScore.setSceneId(sceneId.get(docId));
			beliefScores.add(currDocScore);
		}
		return beliefScores;

	}

	public List<Scores> beliefOr(List<List<Scores>> results) {

		
		List<Scores> beliefScores = new ArrayList<>();
		for (int docId : documents) {
			int sum = 0;
			boolean smoothing = true;
			for (List<Scores> list : results) {
				for (Scores eachScore : list) {
					if (eachScore.getDocId() == docId) {
						smoothing = (smoothing && eachScore.getSmoothing());
						sum += Math.log10(1 - Math.pow(10, eachScore.getScore()));
					}
				}
			}
			beliefValue =Math.log10(1-Math.pow(10,sum));
			Scores currDocScore = new Scores();
			currDocScore.setScore(beliefValue);
			currDocScore.setDocId(docId);
			currDocScore.setSmoothing(smoothing);
			currDocScore.setSceneId(sceneId.get(docId));
			beliefScores.add(currDocScore);
		}
		return beliefScores;

	}

	public List<Scores> beliefAnd(List<List<Scores>> results) {
		List<Scores> beliefScores = new ArrayList<>();
		if (results.isEmpty() || (results.size() == 1 && results.get(0).isEmpty())) {
			return beliefScores;
		}
		for (int docId : documents) {
			double sum = 0.0;
			boolean smoothing = true;
			for (List<Scores> list : results) {
				for (Scores eachScore : list) {
					if (eachScore.getDocId() == docId) {
						smoothing = (smoothing && eachScore.getSmoothing());
						sum += eachScore.getScore();
					}
				}
			}
			beliefValue = sum;

			Scores currDocScore = new Scores();
			currDocScore.setScore(beliefValue);
			currDocScore.setDocId(docId);
			currDocScore.setSceneId(sceneId.get(docId));
			currDocScore.setSmoothing(smoothing);
			beliefScores.add(currDocScore);

		}
		return beliefScores;
	}

	public List<Scores> beliefWAnd(List<List<Scores>> results, List<Double> weights) {

		List<Scores> beliefScores = new ArrayList<>();
		for (int docId : documents) {
			int sum = 0;
			boolean smoothing = true;
			for (List<Scores> list : results) {
				for (Scores eachScore : list) {
					if (eachScore.getDocId() == docId) {
						smoothing = (smoothing && eachScore.getSmoothing());
						sum += eachScore.getScore() * weights.get(docId);
					}
				}
			}
			beliefValue = sum;
			Scores currDocScore = new Scores();
			currDocScore.setScore(beliefValue);
			currDocScore.setDocId(docId);
			currDocScore.setSmoothing(smoothing);
			currDocScore.setSceneId(sceneId.get(docId));
			beliefScores.add(currDocScore);
		}
		return beliefScores;

	}

	public List<Scores> beliefMax(List<List<Scores>> results) {
		

		List<Scores> beliefScores = new ArrayList<>();
		for (int docId : documents) {
			double max = Integer.MIN_VALUE;
			boolean smoothing = true;
			for (List<Scores> list : results) {
				for (Scores eachScore : list) {
					if (eachScore.getDocId() == docId) {
						smoothing = (smoothing && eachScore.getSmoothing());
						if (max < list.get(docId).getScore()) {
							max = Math.pow(10, eachScore.getScore());
						}
					}
				}
			}
			beliefValue = max;
			Scores currDocScore = new Scores();
			currDocScore.setScore(Math.log10(beliefValue));
			currDocScore.setDocId(docId);
			currDocScore.setSmoothing(smoothing);
			currDocScore.setSceneId(sceneId.get(docId));
			beliefScores.add(currDocScore);
		}
		return beliefScores;
	}

	public List<Scores> beliefSum(List<List<Scores>> results) {
		

		List<Scores> beliefScores = new ArrayList<>();
		for (int docId : documents) {
			double sum = 0.0;
			boolean smoothing = true;
			for (List<Scores> list : results) {
				for (Scores eachScore : list) {
					if (eachScore.getDocId() == docId) {
						smoothing = (smoothing && eachScore.getSmoothing());
						sum += Math.pow(10, eachScore.getScore());
					}
				}
			}
			beliefValue = sum / documents.size();
			Scores currDocScore = new Scores();
			currDocScore.setScore(Math.log10(beliefValue));
			currDocScore.setDocId(docId);
			currDocScore.setSmoothing(smoothing);
			currDocScore.setSceneId(sceneId.get(docId));
			beliefScores.add(currDocScore);
		}
		return beliefScores;

	}

	public List<Scores> beliefWSum(List<List<Scores>> results, List<Double> weights) {
		

		List<Scores> beliefScores = new ArrayList<>();
		for (int docId : documents) {
			double sum = 0.0;
			double weightSum = 0.0;
			boolean smoothing = true;
			for (List<Scores> list : results) {
				for (Scores eachScore : list) {
					if (eachScore.getDocId() == docId) {
						smoothing = (smoothing && eachScore.getSmoothing());
						sum += (Math.pow(10, eachScore.getScore()) * weights.get(docId));
						weightSum += weights.get(docId);
					}
				}
			}
			beliefValue = sum / weightSum;
			Scores currDocScore = new Scores();
			currDocScore.setScore(Math.log10(beliefValue));
			currDocScore.setDocId(docId);
			currDocScore.setSmoothing(smoothing);
			currDocScore.setSceneId(sceneId.get(docId));
			beliefScores.add(currDocScore);
		}
		return beliefScores;

	}
	
	public List<Scores> beliefPrior(Prior priorNode){
		Retriever retriever = new Retriever();
		List<Scores> priorScores = new ArrayList<>();
		String filename = priorNode.getType() + "Prior";
		for(int docId : documents){
			double priorProb = retriever.retrievePriorScore(docId, filename);
			Scores currDocScore = new Scores();
			currDocScore.setScore(priorProb);
			currDocScore.setDocId(docId);
			currDocScore.setSmoothing(true);
			currDocScore.setSceneId(sceneId.get(docId));
			priorScores.add(currDocScore);
			
		}
		return priorScores;
	}
}
