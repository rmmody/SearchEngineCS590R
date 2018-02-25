import java.util.*;

public class QueryNodeEvaluator {
	QueryLikelihood dirichelet = new QueryLikelihood();
	
	public List<Scores> queryNodeEvaluator(QueryNode queryNode){
		BeliefFunctions beliefFunction = new BeliefFunctions();
		List<Expression> expressions= queryNode.getExpression();
		BeliefOperator operator = queryNode.getOperator();
		List<QueryNode> operatorParams = queryNode.getOperatorParams();
		List<Double> weights = queryNode.getWeights();
		List<List<Scores>> results = new ArrayList<List<Scores>>();
		List<Scores> finalScores = new ArrayList<>();
		Scores score = new Scores();
		
		if(operatorParams.size()!=0){
			for(QueryNode queryNodes : operatorParams){
				results.add(queryNodeEvaluator(queryNodes));
			}
		}
		
		for(Expression e: expressions){
			results.add(dirichelet.computeScores(e));
		}
		if(operator instanceof Prior ){
			finalScores.addAll(beliefFunction.beliefPrior((Prior)operator));
		}
		
		if(operator instanceof And && !((And) operator).isWeighted() ){
			finalScores.addAll(beliefFunction.beliefAnd(results));
		}
		
		else if(operator instanceof And && ((And) operator).isWeighted() ){
			finalScores.addAll(beliefFunction.beliefWAnd(results, weights));
		}
		
		else if(operator instanceof Or){
			finalScores.addAll(beliefFunction.beliefOr(results));
		}
		
		else if(operator instanceof Not){
			finalScores.addAll(beliefFunction.beliefNot(results));
		} 

		else if(operator instanceof Max){
			finalScores.addAll(beliefFunction.beliefMax(results));
		} 
		
		if(operator instanceof Sum && !((Sum) operator).isWeighted() ){
			finalScores.addAll(beliefFunction.beliefSum(results));
		}
		
		else if(operator instanceof Sum && ((Sum) operator).isWeighted() ){
			finalScores.addAll(beliefFunction.beliefWSum(results, weights));
		}
		
		return finalScores;
		
	}
}
