package callgraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaFile {

    private InputStream stream;
    private String fileName;
    private BufferedReader br;
    private List<Metric> metricList;
    private int lineNumber;
    private int cyclomatiComplexity;
    private int fileLineCount;
    private int fileLineCountAux;
    private int sourceLine;

    public JavaFile(InputStream stream, String fileName) {
        this.stream = stream;
        this.fileName = fileName;
        metricList = new ArrayList<>();
    }

    public List<Metric> calculateMetrics() throws IOException {
        br = new BufferedReader(new InputStreamReader(stream));
        String line;
        String method;
        while ((line = br.readLine()) != null) {
            fileLineCount++;
            if (isMethod(line)) {
                method = getMethodName(line.trim());
                calculateLineNumber();
                metricList.add(getMetric(method));
            }
        }
        return metricList;
    }

    private Metric getMetric(String methodName) {
        return new Metric(methodName, lineNumber, cyclomatiComplexity, sourceLine);
    }

    private boolean isMethod(String line) {
        if ((line.trim().startsWith("public") || line.trim().startsWith("private")) && !line.contains("class") && !line.contains("final")
                && !line.contains("interface") && !line.contains("enum") && !line.contains(";")) {
            return true;
        }
        return false;
    }

    private String getMethodName(String line) {
        sourceLine = fileLineCount;
        line = line.trim();
        String methodName = "";
        int end = line.indexOf("(");
        if (end != -1) {
            methodName = line.substring(0, end);
        }
        ArrayList<String> subStrings = new ArrayList<String>(Arrays.asList(methodName.split(" ")));
        if (subStrings.size() <= 2) {
            return "<init>";
        }
        return subStrings.get(subStrings.size() - 1);
    }

    private void calculateLineNumber() throws IOException {
        lineNumber = 0;
        fileLineCountAux = 0;
        boolean commentBegan = false;
        String line = null;

        while ((line = br.readLine()) != null) {
            fileLineCount++;
            if (isMethod(line)) {
                br.reset();
                break;
            }
            line = line.trim();
            if ("".equals(line) || line.startsWith("//")) {
                continue;
            }
            if (commentBegan) {
                if (commentEnded(line)) {
                    line = line.substring(line.indexOf("*/") + 2).trim();
                    commentBegan = false;
                    if ("".equals(line) || line.startsWith("//")) {
                        continue;
                    }
                } else
                    continue;
            }
            if (isSourceCodeLine(line)) {
                lineNumber++;
            }
            if (commentBegan(line)) {
                commentBegan = true;
            }
            if (line.contains("}")) {
                br.mark(100);
                fileLineCountAux = fileLineCount;
            }
        }
        // if ((line = br.readLine()) == null) {
        //     lineNumber--;
        // }
    }

    private boolean commentBegan(String line) {
        // If line = /* */, this method will return false
        // If line = /* */ /*, this method will return true
        int index = line.indexOf("/*");
        if (index < 0) {
            return false;
        }
        int quoteStartIndex = line.indexOf("\"");
        if (quoteStartIndex != -1 && quoteStartIndex < index) {
            while (quoteStartIndex > -1) {
                line = line.substring(quoteStartIndex + 1);
                int quoteEndIndex = line.indexOf("\"");
                line = line.substring(quoteEndIndex + 1);
                quoteStartIndex = line.indexOf("\"");
            }
            return commentBegan(line);
        }
        return !commentEnded(line.substring(index + 2));
    }

    /**
     *
     * @param line
     * @return This method checks if in the given line a comment has ended and no
     *         new comment has not begun
     */
    private boolean commentEnded(String line) {
        // If line = */ /* , this method will return false
        // If line = */ /* */, this method will return true
        int index = line.indexOf("*/");
        if (index < 0) {
            return false;
        } else {
            String subString = line.substring(index + 2).trim();
            if ("".equals(subString) || subString.startsWith("//")) {
                return true;
            }
            if (commentBegan(subString)) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     *
     * @param line
     * @return This method returns true if there is any valid source code in the
     *         given input line. It does not worry if comment has begun or not. This
     *         method will work only if we are sure that comment has not already
     *         begun previously. Hence, this method should be called only after
     *         {@link #commentBegan(String)} is called
     */
    private boolean isSourceCodeLine(String line) {
        boolean isSourceCodeLine = false;
        line = line.trim();
        if ("".equals(line) || line.startsWith("//")) {
            return isSourceCodeLine;
        }
        if (line.length() == 1) {
            return true;
        }
        int index = line.indexOf("/*");
        if (index != 0) {
            return true;
        } else {
            while (line.length() > 0) {
                line = line.substring(index + 2);
                int endCommentPosition = line.indexOf("*/");
                if (endCommentPosition < 0) {
                    return false;
                }
                if (endCommentPosition == line.length() - 2) {
                    return false;
                } else {
                    String subString = line.substring(endCommentPosition + 2).trim();
                    if ("".equals(subString) || subString.indexOf("//") == 0) {
                        return false;
                    } else {
                        if (subString.startsWith("/*")) {
                            line = subString;
                            continue;
                        }
                        return true;
                    }
                }

            }
        }
        return isSourceCodeLine;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Metric> getMetricList() {
        return metricList;
    }

    public void setMetricList(List<Metric> metricList) {
        this.metricList = metricList;
    }

}