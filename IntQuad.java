import java.util.ArrayList;
import java.util.List;

public class IntQuad {
	int offset;
	int byteCount;
	int termFreq;
	int docFreq;
	boolean compressed;
	
	public void setOffset(int offset){
		this.offset = offset;
	}
	
	public void setbyteCount(int byteCount){
		this.byteCount = byteCount;
	}
	
	public void setTermFreq(int termFreq){
		this.termFreq = termFreq;
	}
	
	public void setDocFreq(int docFreq){
		this.docFreq = docFreq;
	}
	
	public void setCompressed(boolean compressed){
		this.compressed = compressed;
	}
	
	public int getOffset(){
		return offset;
	}
	
	public int getbyteCount(){
		return byteCount;
	}
	
	public int getTermFreq(){
		return termFreq;
	}
	
	public int getDocFreq(){
		return docFreq;
	}
	
	public boolean getCompressed(){
		return compressed;
	}
	
	public static List<Integer> toString(IntQuad quad)
	{
		List<Integer>  termInfo = new ArrayList<>();
		termInfo.add(quad.getOffset());
		termInfo.add(quad.getbyteCount());
		termInfo.add(quad.getTermFreq());
		termInfo.add(quad.getDocFreq());
		
		return termInfo;
	}
}
