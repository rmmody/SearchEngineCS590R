
public class Tokens {
	
	String playId;
	String sceneId;
	int sceneNum;
	String[] words;
	
	public void setPlayId(String playId){
		this.playId = playId;
	}
	
	public void setSceneId(String sceneId){
		this.sceneId = sceneId;
	}
	
	public void setSceneNum(int sceneNum){
		this.sceneNum = sceneNum;
	}
	
	public void setWordArray(String[] words){
		this.words = words;
	}
	
	public String getPlayId(){
		return playId;
	}
	
	public String getSceneId(){
		return sceneId;
	}
	
	public int getSceneNum(){
		return sceneNum;
	}
	
	public String[] getWords(){
		return words;
	}
}
