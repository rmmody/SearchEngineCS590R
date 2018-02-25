import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BooleanAnd {
	
	public HashMap<Integer,WindowDetails> booleanAnd(List<String> terms){
		UnOrderedWindow unOrderWind = new UnOrderedWindow();
		
		return unOrderWind.unOrderedWindow(terms, 0,true);
		
	}
	
	
}
