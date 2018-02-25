import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Retriever {
	static HashMap<String, IntQuad> byteInfo = new HashMap<>();
	
	
	public void setByteInfo(HashMap<String, IntQuad> byteInfo){
		this.byteInfo = byteInfo;
	}
	ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
	public List<Integer> retrieve(String word){
		
		HashMap<Integer,Integer> docId = new HashMap<>();
		HashMap<String, List<Integer>> pId = new HashMap<>();
		HashMap<Integer,Integer> docLen = new HashMap<>();
		HashMap<String, Integer> playLen = new HashMap<>();
		
		
		docId = dsManager.readDocInfo("docId.txt");
		docLen = dsManager.readDocInfo("docLen.txt");
		playLen = dsManager.readFromPlayLen("playLen.txt");
		pId = dsManager.readPlayIdorByteInfo("pId.txt");
		
		
		IntQuad pair = Retriever.byteInfo.get(word);
		List<Integer> wordPosting = new ArrayList<>();
		
		try{
			RandomAccessFile byteWords = new RandomAccessFile("invertedIndex", "r");
			
			
			int offset = pair.getOffset();
			int byteCount = pair.getbyteCount();
			int termFreq = pair.getTermFreq();
			int docFreq = pair.getDocFreq();
			boolean compressed = pair.getCompressed();
			byteWords.seek(offset);
			
			
			if(!compressed){
				while(byteCount>0){
					wordPosting.add(byteWords.readInt());
					byteCount -= 4;
				}
			}
				
			else{
				
				byte[] bytes = new byte[byteCount];
				byteWords.seek(offset);
				byteWords.readFully(bytes, 0, byteCount);
				for ( int i = 0; i < bytes.length; i++ ) {
					int position = 0;
					int result = ((int) bytes[i] & 0x7F);
					while ( (bytes[i] & 0x80) == 0 ) {
						i += 1;
						position += 1;
						int unsignedByte = ((int) bytes[i] & 0x7F);
						result |= (unsignedByte << (7 * position));
					}
					wordPosting.add(result);
					
				}
				int i = -1;
				int prevDoc = 0;
				while(i<wordPosting.size()-1){
					i++;
					int temp = wordPosting.get(i) + prevDoc; 
					wordPosting.set(i, temp);
					prevDoc = temp;
					i++;
					i += wordPosting.get(i);
			
					
				}
				i = -1;
				int[] tempList = new int[wordPosting.size()];
				Arrays.fill(tempList, 0);
				int j = 0;
				while(i<wordPosting.size()-1){
					i+=2;
					
					for(j = 1;j<=wordPosting.get(i);j++){
						
						tempList[i+j] = wordPosting.get(i+j)+tempList[i+j-1];
					
					}
					i += j-1;
					
				}
				
				for(i = 0;i<wordPosting.size();i++){
					if(tempList[i]!=0){
						wordPosting.set(i, tempList[i]);
					}
				}
			}
			
			//System.out.println(wordPosting);
			byteWords.close();
		
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return wordPosting;
		
	}
	
	
	public Double retrievePriorScore(int docId, String priorFileName){
		HashMap<Integer, Integer> priorScoresLookup = dsManager.readDocInfo("priorScoresLookup.txt");
		int offset = priorScoresLookup.get(docId);
		double priorProb = dsManager.readFromBinaryFile(priorFileName, offset);
		return priorProb;
	}
}
