import java.util.*;

public class WindowDetails {
	
	List<List<Integer>> windowPositions;
	int windowCount;
	
	public WindowDetails()
	{
		windowPositions = new ArrayList<>();
	}
	
	public List<List<Integer>> getWindowPositions() {
		return windowPositions;
	}
	public void setWindowPositions(List<List<Integer>> windowPositions) {
		this.windowPositions = windowPositions;
	}
	public int getWindowCount() {
		return windowCount;
	}
	public void setWindowCount(int windowCount) {
		this.windowCount = windowCount;
	}
	
}
