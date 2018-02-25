import java.util.List;

public class QueryPhrases extends Expression{
	public List<String> inputTerms;

	public List<String> getInputTerms() {
		return inputTerms;
	}

	public void setInputTerms(List<String> inputTerms) {
		this.inputTerms = inputTerms;
	}
}
