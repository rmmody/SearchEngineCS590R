import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.Random;

public class ManageAuxDataStruc {
	
	public  void writeToFile(Map<?,?> map,String name){
	    FileWriter fstream;
	    BufferedWriter out;

	   try{
		    fstream = new FileWriter(name);
		    out = new BufferedWriter(fstream);
		    
		    Iterator<?> it = map.entrySet().iterator();
	
	
		    while (it.hasNext()) {
	
	
		        Map.Entry<?, ?> pairs = (Entry<?, ?>) it.next();
		        
		        if(pairs.getValue() instanceof IntQuad)
		        {
		        	out.write(pairs.getKey() +" : "+ IntQuad.toString((IntQuad)pairs.getValue()) + "\n");	
		        }
		        else
		        {
		        out.write(pairs.getKey() +" : "+ pairs.getValue() + "\n");
		        }
		    }
	
		    out.close();
	   }
	   
	   catch(IOException e){
		   e.printStackTrace();
	   }
	}
	
	public void writeToBinaryFile(HashMap<Integer, Double> priorScore, RandomAccessFile write){
		Iterator<Entry<Integer, Double>> priorScoreIter = priorScore.entrySet().iterator();
	    while (priorScoreIter.hasNext()) {
		    try{
		    	Map.Entry<Integer, Double> pairs = priorScoreIter.next();
		    	Double priorProb = pairs.getValue();
		    	write.writeDouble(priorProb);
		    }
		    catch(IOException e){
				   e.printStackTrace();
			   }
	    }
	}
	
	public  void writeToFile(List<List<DocScore>> scores,String name){
	    FileWriter fstream;
	    BufferedWriter out;

	   try{
		    fstream = new FileWriter(name+".txt");
		    out = new BufferedWriter(fstream);
		    
		    
		    for(List<DocScore> score : scores){
		    	out.write("[");
		    	for(DocScore ds : score){
		    		out.write("[");
			    	out.write(DocScore.toString((DocScore)ds));	
			    	out.write("]");
			    }
		    	out.write("]\n");
		    }
		    
	
		    out.close();
	   }
	   
	   catch(IOException e){
		   e.printStackTrace();
	   }
	}
	
	
	
	public void writeScoresToFile(PriorityQueue<Scores> allScores,String name) {

		FileWriter fstream;
	    BufferedWriter out;

	   try{
		    fstream = new FileWriter(name+".trecrun");
		    out = new BufferedWriter(fstream);
		    

			int i = 1;
			while (!allScores.isEmpty()) {
				Scores scores = allScores.poll();
				scores.setRank(i);
				out.write(scores.toString()+"\n");
				i++;
			}
			
		    System.out.println("Results have been stored in file "+name+".trecrun" );
		    out.close();
	   }
	   
	   catch(IOException e){
		   e.printStackTrace();
	   }
		
	}

	
	public HashMap<Integer,Integer> readDocInfo(String name){
		HashMap<Integer,Integer> map = new HashMap<>();
        try{
			BufferedReader in = new BufferedReader(new FileReader(name));
	        String line = "";
	        while ((line = in.readLine()) != null) {
	            String parts[] = line.split(" : ");
	            map.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	        }
	        
	        in.close();
        }
        
        catch(IOException e){
        	e.printStackTrace();
        }
        return map;
	}
	public HashMap<String,Integer> readFromPlayLen(String name){
		HashMap<String,Integer> map = new HashMap<>();
        try{
			BufferedReader in = new BufferedReader(new FileReader(name));
	        String line = "";
	        while ((line = in.readLine()) != null) {
	            String parts[] = line.split(":");
	            map.put(parts[0], Integer.parseInt(parts[1].trim()));
	        }
	        
	        in.close();
        }
        
        catch(IOException e){
        	e.printStackTrace();
        }
        return map;
	}
	
public double readFromBinaryFile(String priorFileName, int offset){
	double priorProb = 0;
	try{
		RandomAccessFile priorScores = new RandomAccessFile(priorFileName, "r");
		priorScores.seek((long) offset);
		priorProb = priorScores.readDouble();
		priorScores.close();
	}
	catch(IOException e){
		e.printStackTrace();
	}
	
	return priorProb;
}
	
public HashMap<String,List<Integer>> readPlayIdorByteInfo(String name){
		HashMap<String,List<Integer>> map = new HashMap<>();
        try{
			BufferedReader in = new BufferedReader(new FileReader(name));
	        String line = "";
	        while ((line = in.readLine()) != null) {
	            String parts[] = line.split(" : ");
	            String list = parts[1].substring(1, parts[1].length()-1);

	            String docs[] = list.split(", ");
	                
	            List<Integer> docIds = new ArrayList<Integer>();
	            
	            for(String s : docs){
	            	docIds.add(Integer.parseInt(s));
	            }
	            map.put(parts[0], docIds);
	        }

	        
	        in.close();
        }
        
        catch(IOException e){
        	e.printStackTrace();
        }
        return map;
        
	}

public List<String> readFile(String filename){
	List<String> words = new ArrayList<>();
	List<Integer> termFreq = new ArrayList<>();
	List<Integer> docFreq = new ArrayList<>();
	try{
		BufferedReader in = new BufferedReader(new FileReader(filename));
        String line = "";
        while ((line = in.readLine()) != null) {
            String parts[] = line.split(":");
            words.add(parts[0]);
            String[] freq = parts[1].substring(1, parts[1].length()-1).split(", ");
            termFreq.add(Integer.parseInt((freq[0])));
            docFreq.add(Integer.parseInt((freq[1])));
        }
        
        in.close();
    }
    
    catch(IOException e){
    	e.printStackTrace();
    }
	return words;
}


public HashMap<Integer,List<String>> getWordsinDoc(String name){
	HashMap<Integer,List<String>> map = new HashMap<>();
    try{
		BufferedReader in = new BufferedReader(new FileReader(name));
        String line = "";
        while ((line = in.readLine()) != null) {
            String parts[] = line.split(":");
            String list = parts[1].substring(1, parts[1].length()-1);

            String docs[] = list.split(", ");
                
            List<String> words = new ArrayList<String>();
            
            for(String s : docs){
            	words.add(s);
            }
            map.put(Integer.parseInt(parts[0]), words);
        }

        
        in.close();
    }
    
    catch(IOException e){
    	e.printStackTrace();
    }
    return map;
    
}

public List<Integer> readFromFile(String filename){
	
	List<Integer> length = new ArrayList<>();
	try{
		BufferedReader in = new BufferedReader(new FileReader(filename));
        String line = "";
        while ((line = in.readLine()) != null) {
            String parts[] = line.split(":");
            length.add(Integer.parseInt((parts[1])));
        }
        
        in.close();
    }
    
    catch(IOException e){
    	e.printStackTrace();
    }
	return length;
}




public List<String> randomWordSelecter(List<String> words){
	

	List<Integer> indices = new ArrayList<>();
	List<String> selected = new ArrayList<>();
	Random R = new Random();
	for(int i = 0;i<7;i++){
		indices.add(R.nextInt(words.size()));
	}
	for(int i : indices){
		selected.add(words.get(i));
	}
	
	return selected;
}

public HashMap<Integer, String> readSceneId(String name) {
	HashMap<Integer, String> map = new HashMap<>();
    try{
		BufferedReader in = new BufferedReader(new FileReader(name));
        String line = "";
        while ((line = in.readLine()) != null) {
            String parts[] = line.split(" : ");
            map.put(Integer.parseInt(parts[0]),parts[1]);
        }
        
        in.close();
    }
    
    catch(IOException e){
    	e.printStackTrace();
    }
    return map;
}

public List<String> readQuery(String filename){
	List<String> queries = new ArrayList<>();

	try{
		BufferedReader in = new BufferedReader(new FileReader(filename));
        String line = "";
        while ((line = in.readLine()) != null) {
            
            queries.add(line);
            
        }
        
        in.close();
    }
    
    catch(IOException e){
    	e.printStackTrace();
    }
	return queries;
}


}
