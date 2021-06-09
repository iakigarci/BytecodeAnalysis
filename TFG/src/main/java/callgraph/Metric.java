package callgraph;

public class Metric {
    
    private String methodName;
    private int LOC;
    private int cyclomatiComplexity;
    private int sourceLine;

    public Metric(String methodName, int lOC, int cyclomatiComplexity, int sourceLine) {
        this.methodName = methodName;
        LOC = lOC;
        this.cyclomatiComplexity = cyclomatiComplexity;
        this.sourceLine = sourceLine;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public int getLOC() {
        return LOC;
    }
    public void setLOC(int lOC) {
        LOC = lOC;
    }
    public int getCyclomatiComplexity() {
        return cyclomatiComplexity;
    }
    public void setCyclomatiComplexity(int cyclomatiComplexity) {
        this.cyclomatiComplexity = cyclomatiComplexity;
    }
    public int getSourceLine() {
        return sourceLine;
    }
    public void setSourceLine(int sourceLine) {
        this.sourceLine = sourceLine;
    }

    
}
