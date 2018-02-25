import java.util.*;

//enum operator{
//	Not, Or, And, WAnd, Max, Sum, WSum;  
//}

public class QueryNode {
	
	List<Expression> expression;
	List<QueryNode> operatorParams;
	List<Double> weights;
	BeliefOperator operator;
	
	public List<Expression> getExpression() {
		return expression;
	}
	public void setExpression(List<Expression> expression) {
		this.expression = expression;
	}
	public List<QueryNode> getOperatorParams() {
		return operatorParams;
	}
	public void setOperatorParams(List<QueryNode> operatorParams) {
		this.operatorParams = operatorParams;
	}
	public List<Double> getWeights() {
		return weights;
	}
	public void setWeights(List<Double> weights) {
		this.weights = weights;
	}
	public BeliefOperator getOperator() {
		return operator;
	}
	public void setOperator(BeliefOperator operator) {
		this.operator = operator;
	}
	
	
}
