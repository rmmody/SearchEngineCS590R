import java.util.*;

public class UnOrderedWindow {
	List<Integer> docPosting = new ArrayList<>();

	public HashMap<Integer, WindowDetails> unOrderedWindow(List<String> terms, int windowSize, boolean bAflag) {

		List<HashMap<Integer, WindowDetails>> windowsPerDoc = new ArrayList<>();
		HashMap<Integer, WindowDetails> windowPerDoc = new HashMap<>();
		ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
		Retriever Retriever = new Retriever();

		HashMap<String, List<Integer>> wordsInfo = dsManager.readPlayIdorByteInfo("byteInfo.txt");
		HashMap<Integer, Integer> docInfo = dsManager.readDocInfo("docId.txt");
		HashMap<Integer, Integer> docLen = dsManager.readDocInfo("docLen.txt");
		Set<Integer> documents = docInfo.keySet();
		HashMap<String, List<Integer>> docTermMap = new HashMap<>();
		List<Integer> visited = new ArrayList<>();

		HashMap<String, HashMap<Integer, List<Integer>>> termPositionsPerDoc = new HashMap<>();
		for (String term : terms) {
			List<Integer> posting = Retriever.retrieve(term);
			HashMap<Integer, List<Integer>> termMap = new HashMap<>();
			for (int i = 0; i < posting.size() - 1; i++) {

				int documentId = posting.get(i);
				int count = posting.get(++i);
				List<Integer> positions = new ArrayList<>();
				for (int j = i + 1; j <= i + count; j++) {
					positions.add(posting.get(j));
				}
				termMap.put(documentId, positions);
				i += count;

			}
			termPositionsPerDoc.put(term, termMap);
		}

		for (Integer docId : documents) {
			WindowDetails windowDetails = new WindowDetails();
			List<List<Integer>> windowPositions = new ArrayList<>();
			boolean notPresent = false;
			int windowCount = 0;
			if (bAflag) {
				windowSize = docLen.get(docId);
			}
			for (String term : terms) {
				if (!termPositionsPerDoc.get(term).containsKey(docId)) {
					notPresent = true;
					break;

				} else {
					docTermMap.put(term, termPositionsPerDoc.get(term).get(docId));
				}
			}
			if (!notPresent) {
				for (int i = 0; i < docLen.get(docId); i++) {
					boolean termsLeft = true;
					int start = i;
					int end = i + windowSize - 1;
					while (termsLeft) {
						for (String term : terms) {
							for (int pos : docTermMap.get(term)) {
								if (pos >= start && pos <= end && !visited.contains(pos)) {
									visited.add(pos);
									break;
								}
							}
						}
						if (visited.size() == windowSize) {
							windowCount++;
							windowPositions.add(new ArrayList<>(visited));
							removeVisited(docTermMap, visited);

						} else {
							termsLeft = false;
						}
						visited.clear();
					}
				}
				windowDetails.setWindowCount(windowCount);
				windowDetails.setWindowPositions(windowPositions);

			}
			docTermMap.clear();
			// if(windowCount!=0){
			windowPerDoc.put(docId, windowDetails);
			// }
		}

		return windowPerDoc;
	}

	public void removeVisited(HashMap<String, List<Integer>> docTermMap, List<Integer> visited) {
		for (int i : visited) {
			for (String key : docTermMap.keySet()) {
				docPosting = docTermMap.get(key);
				if (docPosting.contains(i)) {
					docPosting.remove(new Integer(i));
				}
			}
		}
		// docPosting.clear();
	}

	public void printUnOrderedWindows(HashMap<Integer, WindowDetails> windowPerDoc) {

		WindowDetails windowDetails = new WindowDetails();

		for (int key : windowPerDoc.keySet()) {
			windowDetails = windowPerDoc.get(key);

			System.out.print(key + "=");
			System.out.print(windowDetails.getWindowCount() + ",");
			System.out.print(windowDetails.getWindowPositions() + ";");

		}
		System.out.print("\n");
	}

}
