import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DocAtATime {
	public void docScoring(String filename){
		Retriever Retriever = new Retriever();
		try{
			int numOfDocs = 0;
			BufferedReader input = new BufferedReader(new FileReader("docId.txt"));
	        String start = "";
	     
	        while ((start = input.readLine()) != null) {
	            numOfDocs++;
	        }
			
	        input.close();
			BufferedReader in = new BufferedReader(new FileReader(filename));
	        String line = "";
	        List<String[]> queries = new ArrayList<>();
	        while ((line = in.readLine()) != null) {
	            String[] termQueries = line.split(" ");
	            queries.add(termQueries);
	        }
	        in.close();
	        long StartTime = System.currentTimeMillis();
	        List<List<DocScore>> allScores = new ArrayList<>();
	        for(String[] query : queries){
	        	HashMap<String,List<List<Integer>>> queryMap = new HashMap<>();
	        	for(String term: query){
	        		List<List<Integer>> invIndex = new ArrayList<>();
	        		List<Integer> postings = Retriever.retrieve(term);
	        		invIndex = convToInvIndex(postings);
	        		queryMap.put(term, invIndex);
	        	}
	        	
	        	List<DocScore> scores = new ArrayList<>();
	        	
	        	
	        	for(int i = 0;i<=numOfDocs;i++){
	        		DocScore docScore = new DocScore();
	        		int score = 0;
	        		
	        		for(String term:queryMap.keySet()){
		        		for(List<Integer> wordDetails : queryMap.get(term)){
		        			if(wordDetails.get(0)==i){
		        				score += wordDetails.get(1);
		        			}
		        		}
	        		}
	        		docScore.setDocId(i);
	        		docScore.setScore(score);
	        		Collections.sort(scores,new DocScore());
	        		if(scores.size()>3){
	        			scores.remove(3);
	        		}
	        		if(scores.size()<3){
	        			scores.add(docScore);
	        		}
	        		
	        		if(scores.size()==3 && scores.get(2).getScore()<score){
	        			scores.add(docScore);
	        		}
	        		
	        	}
	        	allScores.add(scores);
	        	
	        }
	        long EndTime = System.currentTimeMillis();
	        double totalTime = (EndTime-StartTime)/1000.0;
	        System.out.println("Time taken for retrieval in sec:" +totalTime);
	        ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
        	dsManager.writeToFile(allScores, "docScores"+filename);
        }
        
        catch(IOException e){
        	e.printStackTrace();
        }
        
	}
	
	public List<List<Integer>> convToInvIndex(List<Integer> postings){
		List<List<Integer>> invIndex = new ArrayList<>();
		int i = -1;
		int j = 0;
		
		while(i<postings.size()-1){
			List<Integer> docPosting = new ArrayList<>();
			docPosting.add(postings.get(++i));
			int remTerms = postings.get(++i);
			docPosting.add(remTerms);
			for(j =1;j<=remTerms;j++){
				docPosting.add(postings.get(i+j));
			}
			invIndex.add(docPosting);
			i += j-1;
		}
		
		
		return invIndex;
	}
}
