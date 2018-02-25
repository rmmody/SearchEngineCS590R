import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DocScore implements Comparator<DocScore> {
	int docId=0;
	int score=0;
	
	public void setDocId(int docId){
		this.docId = docId;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public int getScore(){
		return score;
	}
	public int getDocId(){
		return docId;
	}
	
	
	public static String toString(DocScore score)
	{
		List<Integer>  docScore = new ArrayList<>();
		docScore.add(score.getDocId());
		docScore.add(score.getScore());
		
		String s = score.getDocId() + "," + score.getScore();
		return s;
	}

	

	@Override
	public int compare(DocScore o1, DocScore o2) {
		// TODO Auto-generated method stub
		return o2.score-o1.score;
	}

	
}
