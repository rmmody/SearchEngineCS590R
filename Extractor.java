import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Extractor {
	
	public static List<Tokens> extractor(){
		JSONParser parser = new JSONParser();
		List<Tokens> tokenList = new ArrayList<Tokens>();
		try{
			Object obj = parser.parse(new FileReader("shakespeare-scenes.json"));
			JSONObject jsonobj = (JSONObject) obj;
			
			JSONArray jsonarr = (JSONArray) jsonobj.get("corpus");
			
			for(int i = 0;i<jsonarr.size();i++){
				String playId = (String)((JSONObject) jsonarr.get(i)).get("playId");
				String sceneId = (String)((JSONObject) jsonarr.get(i)).get("sceneId");
				Number sceneNum = (Number)((JSONObject) jsonarr.get(i)).get("sceneNum");
				String text = (String)((JSONObject) jsonarr.get(i)).get("text");
				
				String[] words = text.split(" ");
				
				Tokens tokens = new Tokens();
				tokens.setPlayId(playId);
				tokens.setSceneId(sceneId);
				tokens.setSceneNum(sceneNum.intValue());
				tokens.setWordArray(words);
				
				tokenList.add(tokens);
				
				
			}

			
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
            e.printStackTrace();
        }catch (ParseException e) {
            e.printStackTrace();
        }
		
		
		return tokenList;
	
	}
}
	

