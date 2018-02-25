import java.util.ArrayList;
import java.util.List;

public class FilterReject {


	
		Expression expression = new Expression();
		QueryNode queryNode = new QueryNode();
		
		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = expression;
		}

		public QueryNode getQueryNode() {
			return queryNode;
		}

		public void setQueryNode(QueryNode queryNode) {
			this.queryNode = queryNode;
		}
		
		QueryLikelihood dirichelet = new QueryLikelihood();
		QueryNodeEvaluator queryNodeEvaluator = new QueryNodeEvaluator();

		
	public List<Scores> filterReject(){
			
			List<Scores> queryNodeEvaluation = queryNodeEvaluator.queryNodeEvaluator(this.queryNode);
					
			List<Scores> diricheletEvaluation = dirichelet.computeScores(this.expression);
			List<Scores> finalResult = new ArrayList<>();
			List<Integer> docIds = new ArrayList<>();
			for(Scores dirDocScore : diricheletEvaluation){
				docIds.add(dirDocScore.getDocId());
			}
			for(Scores docScore : queryNodeEvaluation){
				
				if(!docIds.contains((docScore.getDocId()))){
					finalResult.add(docScore);
				}
			}
			
			return finalResult;	
		}
	
}
