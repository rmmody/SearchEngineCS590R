import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExtractRandomWords{
	
	public void extractAndStore(List<String> vocabulary){
		ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
		StringBuffer buffer = new StringBuffer();
		for(int i = 0;i<100;i++){
			List<String> selectedWords = dsManager.randomWordSelecter(vocabulary);
			for(String s : selectedWords){
				buffer.append(s);
				buffer.append(" ");
			}
			buffer.append("\n");
		}
		try{
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("randomWords.txt")));
			bw.write(buffer.toString());
			bw.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
