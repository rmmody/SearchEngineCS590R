import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DiceCoeff {
	public void diceCoeff() {
		DocAtATime d = new DocAtATime();
		ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
		Retriever Retriever = new Retriever();
		try {

			BufferedReader in = new BufferedReader(new FileReader("randomWords.txt"));
			String line = "";
			List<String[]> queries = new ArrayList<>();
			while ((line = in.readLine()) != null) {
				String[] termQueries = line.split(" ");
				queries.add(termQueries);
			}
			in.close();

			HashMap<String, Integer> ctf = new HashMap<>();
			BufferedReader input = new BufferedReader(new FileReader("byteInfo.txt"));
			String firstline = "";
			List<String> words = new ArrayList<>();
			while ((firstline = input.readLine()) != null) {
				String[] wordInfo = firstline.split(":");
				words.add(wordInfo[0]);
				String[] info = wordInfo[1].split(", ");
				ctf.put(wordInfo[0], Integer.parseInt(info[2]));
			}
			input.close();
			HashMap<Integer, List<String>> docWordList = new HashMap<>();
			docWordList = dsManager.getWordsinDoc("docWordList.txt");

			HashMap<String, List<List<Integer>>> fileinvIndex = new HashMap<>();
			for (String word : words) {

				List<List<Integer>> invIndex = new ArrayList<>();
				List<Integer> postings = Retriever.retrieve(word);
				invIndex = d.convToInvIndex(postings);
				fileinvIndex.put(word, invIndex);

			}

			HashMap<String, String> diceCo = new HashMap<>();
			for (String[] query : queries) {
				for (String term : query) {
					List<String> seenWords = new ArrayList<>();
					
					//term = "unsure";
					int n_a = 0;
					List<List<Integer>> termInfo = fileinvIndex.get(term);
					n_a = ctf.get(term) * 2;
					double diceCoef = 0.0;
					double maxdiceCoef = 0.0;
					for (List<Integer> termPerDocInfo : termInfo) {
						int docId = termPerDocInfo.get(0);
						List<String> wordsInDoc = docWordList.get(docId);

						for (String word : wordsInDoc) {
							if (word.isEmpty()) {
								continue;
							}
							if(word.equals(term) || seenWords.contains(word)){
								continue;
							}
							seenWords.add(word);
							int n_b = 0;
							int n_ab = 0;
							List<List<Integer>> wordInfo = fileinvIndex.get(word);
							
							n_b = ctf.get(word) * 2;
							for (List<Integer> wordPerDocInfo : wordInfo) {
								List<Integer> dummywordPerDocInfo = new ArrayList<>();
								if (wordPerDocInfo.get(0).intValue() == termPerDocInfo.get(0).intValue()) {
									for (int i = 2; i < wordPerDocInfo.size(); i++) {
										dummywordPerDocInfo.add(wordPerDocInfo.get(i) + 1);
									}
									dummywordPerDocInfo.retainAll(termPerDocInfo.subList(2, termPerDocInfo.size()));
									n_ab += dummywordPerDocInfo.size();
								}
								
								List<Integer> dummywordPerDocInfoNeg = new ArrayList<>();
								if (wordPerDocInfo.get(0).intValue() == termPerDocInfo.get(0).intValue()) {
									for (int i = 2; i < wordPerDocInfo.size(); i++) {
										dummywordPerDocInfoNeg.add(wordPerDocInfo.get(i) - 1);
									}
									dummywordPerDocInfoNeg.retainAll(termPerDocInfo.subList(2, termPerDocInfo.size()));
									n_ab += dummywordPerDocInfoNeg.size();
								}

							}

							diceCoef = (n_ab * 1.0) / (n_a + n_b);
							if (maxdiceCoef < diceCoef) {
								maxdiceCoef = diceCoef;
								diceCo.put(term, word);
							}

						}
					}

					
				}
				
				
			}
			
			List<List<String>> phraseQueries = new ArrayList<>();
			for(String[] sa: queries){
				List<String> pQa = new ArrayList<>();
				for(int i = 0;i<sa.length;i++){
					pQa.add(sa[i]);
					pQa.add(diceCo.get(sa[i]));
				}
				phraseQueries.add(pQa);
			}
			
			StringBuffer buffer = new StringBuffer();
			for(List<String> pQa : phraseQueries){
				for(String s: pQa){
					buffer.append(s+" ");
				}
				buffer.append("\n");
			}
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("twoWordPhrases.txt")));
			bw.write(buffer.toString());
			bw.close();
			
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		
		

	}

}
