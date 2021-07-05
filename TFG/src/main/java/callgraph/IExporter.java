package callgraph;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IExporter {
    void createCSV() throws IOException;

    void printChildren(MethodReport method, Map<MethodReport, List<MethodReport>> map, int level) throws IOException;
}
