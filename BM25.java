import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

public class BM25 {
	
	ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
	Retriever Retriever = new Retriever();
	
	HashMap<String,List<Integer>> wordsInfo = dsManager.readPlayIdorByteInfo("byteInfo.txt");
	HashMap<Integer,Integer> docInfo = dsManager.readDocInfo("docId.txt");
	HashMap<Integer,Integer> docLen = dsManager.readDocInfo("docLen.txt");
	HashMap<Integer,String> sceneId = dsManager.readSceneId("sceneId.txt");
	List<String> vocabulary = new ArrayList<String>(wordsInfo.keySet());
	Set<Integer> documents = docInfo.keySet();
	TreeMap<Integer, List<Double>> docToWordCount = new TreeMap<>(); 
	List<List<Double>> vectorizedQueries= new ArrayList<>();
	int docSize = documents.size();
	Comparator<Scores> comparator = new Scores();
	
	public List<PriorityQueue<Scores>> computeScores(List<String> queries){
		
		System.out.println("Calculating scores using BM25 as a scoring function");
		int queryNum = 1;
		
		List<PriorityQueue<Scores>> allScores = new ArrayList<>();
		int totalLen = 0;
		for(int key : docLen.keySet()){
			totalLen += docLen.get(key);
		}
		int avgLen = totalLen/docSize;
		for(String query : queries){
			String[] queryTerms = query.split(" ");
			HashMap<String, Integer> queryTermCount = new HashMap<>();
			
			for(String term : queryTerms){
				if(!queryTermCount.containsKey(term)){
					queryTermCount.put(term, 1);
				}
				
				else{
					queryTermCount.put(term, queryTermCount.get(term)+1);
				}
			}
			PriorityQueue<Scores> totalScores = new PriorityQueue<Scores>(comparator);
			for(int docId : documents){
				HashMap<String,List<Integer>> wordPostings = new HashMap<>();
				double score = 0.0;
				for(String term : queryTerms){
					int fi = 0;
					int df = 0;
					List<Integer> posting = new ArrayList<>();
					if(!wordPostings.containsKey(term)){
						posting = Retriever.retrieve(term);
						wordPostings.put(term, posting);
					}
					else{
						posting = wordPostings.get(term);
					}
					for(int i = 0;i<posting.size()-1;i++){
						
						int documentId = posting.get(i);
						int count = posting.get(++i);
						if(docId == documentId){
							fi = count;
							df = wordsInfo.get(term).get(3);
							break;
						}
						i += count;
					}
					int qfi = queryTermCount.get(term);
					//Assuming K1 = 1.2, K2 = 100; b = 0.75
					double K = 1.2*((1-0.75)+(0.75*docLen.get(docId)/avgLen));
							
					double term1 = 1/((df+0.5)/(docSize-df+0.5));
					double term2 = ((1.2+1)*fi)/(K+fi);
					double term3 = ((100+1)*qfi)/(100+qfi);
					
					score += Math.log10(term1) * term2 * term3;
			}
				Scores docScore = new Scores();
				if(score!=0.0){
					docScore.setScore(score);
					docScore.setDocId(docId);
					docScore.setQueryNum(queryNum);
					docScore.setSceneId(sceneId.get(docId));
					docScore.setMethod("rmmody-BM25-1.2-100");
					totalScores.add(docScore);
				}
			}
			
			queryNum++;
			allScores.add(totalScores);
			
		}
		return allScores;
	}
	

}
