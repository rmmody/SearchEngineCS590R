import java.util.HashMap;
import java.util.List;

public class statsCalculator {
	
	public void calculateStats(){
		ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
		
		List<Integer> lengths = dsManager.readFromFile("docLen.txt");
		HashMap<Integer,Integer> sceneLengths = new HashMap<>();
		HashMap<String,List<Integer>> playScenes = new HashMap<>();
		HashMap<String,Integer> playLength = new HashMap<>();
		sceneLengths = dsManager.readDocInfo("docLen.txt");
		playScenes = dsManager.readPlayIdorByteInfo("pId.txt");
		
		for(String key : playScenes.keySet()){
			List<Integer> scenes = playScenes.get(key);
			int playLen = 0;
			for(int i : scenes){
				playLen += sceneLengths.get(i);
			}
			playLength.put(key, playLen);
		}
		int sum = 0;
		int min = Integer.MAX_VALUE;
		for(int i:lengths){
			if(min>i){
				min = i;
			}
			sum += i;
		}
		int longest = Integer.MIN_VALUE;
		String longestPlay = "";
		int shortest = Integer.MAX_VALUE;
		String shortestPlay = "";
		for(String key : playLength.keySet()){
			if(playLength.get(key)>longest){
				longest = playLength.get(key);
				longestPlay = key;
			}
			else if(playLength.get(key)<shortest){
				shortest = playLength.get(key);
				shortestPlay = key;
			}
		}
		
		int shortestScene = min;
		double avgLenScene = sum*1.0/lengths.size();
		
		System.out.println("Shortest scene is: "+shortestScene);
		System.out.println("Avg scene length is: "+avgLenScene);
		System.out.println("Longest play is: "+longestPlay);
		System.out.println("Shortst play is: "+shortestPlay);
	}
}
