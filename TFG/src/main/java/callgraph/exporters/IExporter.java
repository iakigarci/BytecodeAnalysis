package callgraph.exporters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import callgraph.MethodReport;

public interface IExporter {
    void create() throws IOException;

    void printChildren(MethodReport method, Map<MethodReport, List<MethodReport>> map, int level) throws IOException;
}
