import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class Clustering {
	List<Double> thresholds = Arrays.asList(0.05,0.10,0.15,0.20,0.25,0.30,0.35,0.40,0.45,0.50,0.55,0.60,0.65,0.70,0.75,0.80,0.85,0.90,0.95);
	
	List<String> linkageChoices = Arrays.asList("mean", "average", "min", "max");
	SparseVSM vsm = new SparseVSM();
	ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
	TreeMap<Integer, HashMap<String, Double>> docVector = vsm.computeDocVector();
	HashMap<Integer, HashMap<String, Double>> runningMean = new HashMap<>();
	HashMap<Integer, List<Integer>> clusters = new HashMap<>();

	public void clusterAssigner(String linkageChoice) {
		List<Integer> docs = new ArrayList<>(docVector.keySet());
		Collections.sort(docs);
		System.out.println("Assigning documents to clusters using "+ linkageChoice + " linking");
		for (double threshold : thresholds) {
			for (int docId : docs) {
				int bestCluster = 0;
				boolean newCluster = false;
				if (clusters.isEmpty()) {
					List<Integer> memberDocs = new ArrayList<>();
					memberDocs.add(docId);
					clusters.put(1, memberDocs);
					runningMean.put(1, docVector.get(docId));
				}

				else {
					double tempBestScore = Double.MAX_VALUE;
					List<Integer> custerIds = new ArrayList<>(clusters.keySet());
					Collections.sort(custerIds);
					for (int cluster : custerIds) {

						double clusterScore = computeDist(docId, cluster, linkageChoice);
						if (clusterScore < tempBestScore) {
							tempBestScore = clusterScore;
							bestCluster = cluster;
						}
					}
					if (tempBestScore < threshold) {
						newCluster = false;
					} else {
						newCluster = true;

					}
					if (!newCluster) {
						List<Integer> existingDocsInCluster = clusters.get(bestCluster);
						existingDocsInCluster.add(docId);
						if (linkageChoice == "mean") {
							runningMean.put(bestCluster, computeRunningMean(bestCluster));
						}
					} else {
						bestCluster = clusters.size() + 1;
						List<Integer> existingDocsInCluster = new ArrayList<>();
						existingDocsInCluster.add(docId);
						clusters.put(bestCluster, existingDocsInCluster);
						if (linkageChoice == "mean") {
							runningMean.put(bestCluster, new HashMap<>(docVector.get(docId)));
						}
					}
				}

			}
			dsManager.writeToFile(clusters, "cluster-" + threshold+".out");
			System.out.println(clusters.size() + " clusters formed for threshold : "+ threshold );
			clusters.clear();
			runningMean.clear();
			
		}

	}

	public double computeDist(int docId, int cluster, String linkageChoice) {
		double clusterScore = Double.MAX_VALUE;
		List<Integer> docsInCluster = clusters.get(cluster);
		boolean mean = false;
		if (linkageChoice == "mean") {
			mean = true;
			clusterScore = computeCosineDistScores(docId, cluster, mean);
			return clusterScore;
		} else if (linkageChoice == "min") {
			for (int doc : docsInCluster) {
				double score = computeCosineDistScores(docId, doc, mean);
				if (score < clusterScore) {
					clusterScore = score;
				}
			}
			return clusterScore;
		} else if (linkageChoice == "max") {
			clusterScore = Double.MIN_VALUE;
			for (int doc : docsInCluster) {
				double score = computeCosineDistScores(docId, doc, mean);
				if (score > clusterScore) {
					clusterScore = score;
				}
			}
			return clusterScore;
		} else if (linkageChoice == "average") {
			clusterScore = 0.0;
			for (int doc : docsInCluster) {
				clusterScore += computeCosineDistScores(docId, doc, mean);
			}
			return clusterScore / docsInCluster.size();
		}

		return Double.MIN_VALUE;
	}

	public double computeCosineDistScores(int docId, int doc, boolean meanFlag) {

		double score = 0.0;
		HashMap<String, Double> tf1 = docVector.get(docId);
		HashMap<String, Double> tf2 = new HashMap<>();
		if (meanFlag) {
			tf2 = runningMean.get(doc);
		} else {
			tf2 = docVector.get(doc);
		}
		for (String word : tf1.keySet()) {
			if (tf2.containsKey(word)) {
				score += tf1.get(word) * tf2.get(word);
			}
		}

		return (1.0 - score);
	}

	public HashMap<String, Double> computeRunningMean(int clusterId) {
		HashMap<String, Double> runningMean = new HashMap<>();
		List<Integer> docsInCluster = clusters.get(clusterId);

		Set<String> allKeys = new HashSet<>();
		for (int docId : docsInCluster) {
			HashMap<String, Double> currDocVector = docVector.get(docId);
			allKeys.addAll(currDocVector.keySet());
		}

		for (String term : allKeys) {
			double currValue = 0.0;
			for (int docId : docsInCluster) {
				if (docVector.get(docId).containsKey(term)) {
					currValue += docVector.get(docId).get(term);
				}
			}
			runningMean.put(term, (currValue * 1.0) / docsInCluster.size());
		}
		return runningMean;
	}
}
