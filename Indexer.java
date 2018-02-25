import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Indexer {
	static HashMap<Integer,Integer> docId = new HashMap<>();
	static HashMap<String, List<Integer>> pId = new HashMap<>();
	static HashMap<Integer,Integer> docLen = new HashMap<>();
	static HashMap<String, Integer> playLen = new HashMap<>();
	static HashMap<Integer, String> sceneId = new HashMap<>();
	static HashMap<String, List<List<Integer>>> invIndex = new HashMap<>();
	static HashMap<Integer, List<String>> docWordList = new HashMap<>();
	static HashMap<Integer, Double> randomPriorScores = new HashMap<>();
	static HashMap<Integer, Double> uniformPriorScores = new HashMap<>();
	static TreeMap<Integer, Long> priorByteOffset = new TreeMap<>();
	static int sceneNum = 0;
	static String playId = "";
	static int did = 0;
	static int docLength = 0;
	
	public static void indexer(List<Tokens> tokenList, boolean compressed){
		HashMap<String,Integer> deltaForDoc = new HashMap<>();
		for(int i = 0;i<tokenList.size();i++){
			sceneNum = tokenList.get(i).getSceneNum();
			playId = tokenList.get(i).getPlayId();
			String[] docWords = tokenList.get(i).getWords();
			HashMap<String, List<Integer>> docWordPos = new HashMap<>();
			docLength = docWords.length;
			did = sceneNum;
			sceneId.put(did, tokenList.get(i).getSceneId());
			docId.put(sceneNum, did);
			uniformPriorScores.put(did, Math.log10(1.0/tokenList.size()));
			randomPriorScores.put(did, Math.log10(Math.random()/tokenList.size()));
			priorByteOffset.put(did, (long)did*8);
			if(!pId.containsKey(playId)){
				List<Integer> docs = new ArrayList<Integer>();
				pId.put(playId,docs);
			}
			pId.get(playId).add(did);
			docLen.put(did, docLength);
			
			if(playLen.containsKey(playId)){
				playLen.put(playId, playLen.get(playId)+1);
			}
			else{
				playLen.put(playId, 1);
			}
			
			
			HashMap<String,Integer> deltaForWord = new HashMap<>();
			for(int j = 0;j<docWords.length;j++){
				
				if(!docWordList.containsKey(did)){
					List<String> words = new ArrayList<>();
					docWordList.put(did,words);
				}
				docWordList.get(did).add(docWords[j]);
			
				
				if(docWords[j].isEmpty()) {
					continue;
				}
				if(compressed){
					if(!deltaForWord.containsKey(docWords[j])){
						deltaForWord.put(docWords[j], 0);
					}
					if(!deltaForDoc.containsKey(docWords[j])){
						deltaForDoc.put(docWords[j], 0);
					}
				}
				
				if(!docWordPos.containsKey(docWords[j])){
					List<Integer> positions = new ArrayList<>();
					docWordPos.put(docWords[j], positions);
					if(compressed){
						docWordPos.get(docWords[j]).add(did-deltaForDoc.get(docWords[j]));
					}
					else{
						docWordPos.get(docWords[j]).add(did);
					}
					docWordPos.get(docWords[j]).add(0);
					deltaForDoc.put(docWords[j], did);
					
					
				}
				if(!compressed){
					docWordPos.get(docWords[j]).add(j);
					int count = docWordPos.get(docWords[j]).get(1);
					docWordPos.get(docWords[j]).set(1, ++count);
				}
				
				else{
					int sumOfPos = deltaForWord.get(docWords[j]);
					docWordPos.get(docWords[j]).add(j-sumOfPos);
					deltaForWord.put(docWords[j], j);
					
					int count = docWordPos.get(docWords[j]).get(1);
					docWordPos.get(docWords[j]).set(1, ++count);
					
				}
				
			}
			
				
				
			for(String word : docWordPos.keySet()){
				
				if(!invIndex.containsKey(word)){
					List<List<Integer>> wordPos = new ArrayList<>();
					invIndex.put(word, wordPos);	
				}
				invIndex.get(word).add(docWordPos.get(word));
				
			}
			
			
			
			
		}
		

	    try{
		    
	
		    RandomAccessFile write = new RandomAccessFile("invertedIndex", "rw");
		    Iterator<Entry<String, List<List<Integer>>>> invIndexIter = invIndex.entrySet().iterator();
		    int offset = 0;
		    HashMap<String, IntQuad> byteInfo = new HashMap<>();
		    while (invIndexIter.hasNext()) {
		    	int intCount = 0;
		    	int docCount = 0;
		    	int byteCount = 0;
		    	IntQuad pair = new IntQuad();
		    	Map.Entry<String, List<List<Integer>>> pairs = invIndexIter.next();
		    	
		        if(!compressed){
		        	for(List<Integer> list:pairs.getValue()){
			        	docCount++;
			        	for(int i : list){
			        		write.writeInt(i);
			        		intCount++;
			        	}
		        	}
		        	
		        	byteCount = intCount*Integer.SIZE/8;
		        }
		        
		        else{
		        	int totalLen = 0;
		        	for(List<Integer> list:pairs.getValue()){
			        	docCount++;
			        	List<Byte> output = new ArrayList<>();
			        	for(int i : list){
			        		byteEncode(i,output);
			        		intCount++;
			        	}
			        	intCount -= 2;
			        	totalLen += output.size();
			        	
			        	for(Byte elem : output)
			        	{
			        		write.writeByte(elem);
			        	}			        	
		        	}
		        	
		        	byteCount = totalLen;
		        }
		        pair.setbyteCount(byteCount);
		        pair.setOffset(offset);
		        pair.setTermFreq(intCount); 
		        pair.setDocFreq(docCount);
		        pair.setCompressed(compressed);
		        
		        offset += byteCount;
		        byteInfo.put(pairs.getKey(),pair);
		    }
		    ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
		    RandomAccessFile writeRandomPrior = new RandomAccessFile("randomPrior", "rw");
		    RandomAccessFile writeUniformPrior = new RandomAccessFile("uniformPrior", "rw");
		    dsManager.writeToBinaryFile(randomPriorScores, writeRandomPrior);
		    dsManager.writeToBinaryFile(uniformPriorScores, writeUniformPrior);
		    dsManager.writeToFile(uniformPriorScores, "uniform.prior");
		    dsManager.writeToFile(randomPriorScores, "random.prior");
		    dsManager.writeToFile(priorByteOffset, "priorScoresLookup.txt");
		    Retriever retriever = new Retriever();
		    retriever.setByteInfo(byteInfo);
		    
		    
			dsManager.writeToFile(docId,"docId.txt");
			dsManager.writeToFile(docLen, "docLen.txt");
			dsManager.writeToFile(playLen,"playLen.txt");
			dsManager.writeToFile(pId, "pId.txt");
			dsManager.writeToFile(byteInfo, "byteInfo.txt");
			dsManager.writeToFile(docWordList, "docWordList.txt");
			dsManager.writeToFile(sceneId, "sceneId.txt");
		    
	    }
		catch(IOException e){
			e.printStackTrace();
		}
	    
	    
		
	}
	
	
	public static void byteEncode(int i,List<Byte> output){
		while(i>=128){
			output.add((byte)(i & 0x7F));
			i= i >> 7;
		}
		output.add((byte)(i | 0x80));
	}
	
	
}
