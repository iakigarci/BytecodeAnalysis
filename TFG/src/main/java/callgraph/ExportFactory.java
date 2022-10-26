package callgraph;

import callgraph.exporters.CSVExporter;
import callgraph.exporters.IExporter;

public class ExportFactory {
    
    public IExporter createFiles(String exportType, String projectName) {
        if (exportType == null) {
            return null;
        }

        switch (exportType) {
            case "CSVExporter":
                return new CSVExporter(projectName);        
            default:
                return null;
        }
    }
}
