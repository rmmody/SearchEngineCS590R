import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scores implements Comparator<Scores>{
	int docId;
	double score;
	String sceneId;
	int queryNum;
	String method;
	int rank;
	boolean smoothing;
	
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public boolean getSmoothing() {
		return smoothing;
	}
	public void setSmoothing(boolean smoothing) {
		this.smoothing = smoothing;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getSceneId() {
		return sceneId;
	}
	public void setSceneId(String sceneId) {
		this.sceneId = sceneId;
	}
	public int getQueryNum() {
		return queryNum;
	}
	public void setQueryNum(int queryNum) {
		this.queryNum = queryNum;
	}
	
	public String toString()
	{
		String s = "Q"+this.getQueryNum() + " " +"skip"+" "+ this.getSceneId() + "      " +this.getRank() + " " + this.getScore() + " " + this.getMethod();
		return s;
	}
	//@Override
//	public int compareTo(Scores o) {
//		
//		return (int) (o.score - this.score);
//	}
	@Override
	public int compare(Scores o1, Scores o2) {
		// TODO Auto-generated method stub
		return ((Double)o2.getScore()).compareTo((Double)o1.getScore());
	}
	
}
