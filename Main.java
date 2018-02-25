import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Tokens> tokenList = new ArrayList<Tokens>();
		tokenList = Extractor.extractor();

		boolean compression = false;
		if (args.length == 1 && args[0].equals("-c")) {
			compression = true;
		}
		System.out.println("Argument Chosen : Compression Enabled : " + compression);

		ManageAuxDataStruc dsManager = new ManageAuxDataStruc();

		// List<String> queries = dsManager.readQuery("query.txt");
		Indexer.indexer(tokenList, compression);

		PriorEvaluator priorEvaluator = new PriorEvaluator();
		Prior uniformPrior = new Prior();
		uniformPrior.setName("prior");
		uniformPrior.setType("uniform");
		Prior randomPrior = new Prior();
		randomPrior.setName("prior");
		randomPrior.setType("random");

		priorEvaluator.evaluation("the king queen royalty", uniformPrior);
		priorEvaluator.evaluation("the king queen royalty", randomPrior);

	}

}
