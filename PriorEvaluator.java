import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class PriorEvaluator {

	public void evaluation(String query, Prior typeOfPrior) {


		Comparator<Scores> comparator = new Scores();
		int queryNum = 1;

		PriorityQueue<Scores> queryResult = new PriorityQueue<Scores>(comparator);
		List<String> terms = Arrays.asList(query.split(" "));
		List<Expression> expression = new ArrayList<>();
		for (String term : terms) {
			Term t = new Term();
			t.setTerms(term);
			expression.add(t);
		}
		List<QueryNode> childQN = new ArrayList<>();

		QueryNode priorNode = new QueryNode();
		priorNode.setOperatorParams(new ArrayList<QueryNode>());
		priorNode.setExpression(new ArrayList<Expression>());
		priorNode.setOperator(typeOfPrior);
		childQN.add(priorNode);
		

		QueryNode combine = new QueryNode();
		And and = new And();
		and.setName("AND");
		and.setWeighted(false);
		combine.setOperator(and);
		combine.setExpression(expression);
		combine.setOperatorParams(childQN);
		
		System.out.println("Evaluating given query");
		QueryNodeEvaluator qNE = new QueryNodeEvaluator();
		List<Scores> results = qNE.queryNodeEvaluator(combine);

		for (Scores score : results) {
			if (!score.getSmoothing()) {
				score.setQueryNum(queryNum);
				score.setMethod("rmmody-" + typeOfPrior.getType());
				queryResult.add(score);
			}
		}
		queryNum++;

		ManageAuxDataStruc dsManager = new ManageAuxDataStruc();
		dsManager.writeScoresToFile(queryResult, typeOfPrior.getType());
	}
}
