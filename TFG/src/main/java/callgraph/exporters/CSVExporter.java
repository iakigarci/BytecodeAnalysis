package callgraph.exporters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.codehaus.plexus.util.FileUtils;

import callgraph.IExporter;
import callgraph.JCallGraph;
import callgraph.MethodReport;

public class CSVExporter implements IExporter {

    private String projectName;

    public CSVExporter(String projectName) {
        this.projectName = projectName;
    }
    @Override
    public void createCSV() throws IOException {
        File dir = null;
        String dirName = System.getProperty("user.dir") + "/csv/" + projectName;
        Path path = Paths.get(dirName);
        dir = new File(dirName);
        if (Files.exists(path)) {
            FileUtils.deleteDirectory(dir);
        }
        dir.mkdir();
        BufferedWriter writer = null;
        FileWriter fileWriter = null;
        try {
            for (HashMap<MethodReport, List<MethodReport>> map : JCallGraph.methodCallsList.getMethodCalls()) { // Recorrer .class
                Set<Entry<MethodReport, List<MethodReport>>> entrySet = map.entrySet();
                if (!entrySet.isEmpty()) {
                    String name = "/" + entrySet.iterator().next().getKey().getPaquete() + ".csv";
                    fileWriter = new FileWriter(dir + name);
                    System.out.println("[IMPRIMIR]: " + dir + name);
                    writer = new BufferedWriter(fileWriter);
                    JCallGraph.csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("LOC", "Nombre", "Nivel",  "WMC",
                            "Resultado", "Linea en clase", "Llamado por"));
                    for (Map.Entry<MethodReport, List<MethodReport>> entry : map.entrySet()) {
                        if (JCallGraph.isZeroLelel(map, entry.getKey())) {
                            if (JCallGraph.cfl.getCalledMap()
                                    .containsKey(entry.getKey().getPaquete() + entry.getKey().getNombre())) {
                                JCallGraph.calledFrom = JCallGraph.cfl.getCalledMap()
                                        .get(entry.getKey().getPaquete() + entry.getKey().getNombre()).toString();
                            }
                            JCallGraph.csvPrinter.printRecord(entry.getKey().getLOC(), entry.getKey().getNombre(), 0, 
                                    entry.getKey().getWmc(), entry.getKey().getResultado(),
                                    entry.getKey().getLineaClase(), JCallGraph.calledFrom);
    
                            JCallGraph.calledFrom = "";
                            // Recorrer hijos
                            for (MethodReport method : entry.getValue()) {
                                if (map.get(method) != null) {
                                    JCallGraph.visitedMethods.clear();
                                    this.printChildren(method, new HashMap<>(map), 1);
                                }
                            }
                        }
                    }
                    JCallGraph.csvPrinter.close();
                    writer.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void printChildren(MethodReport method, Map<MethodReport, List<MethodReport>> map, int level)
            throws IOException {
        if (!JCallGraph.visitedMethods.contains(method)) {
            JCallGraph.visitedMethods.add(method);
            if (JCallGraph.cfl.getCalledMap().containsKey(method.getPaquete() + method.getNombre())) {
                JCallGraph.calledFrom = JCallGraph.cfl.getCalledMap().get(method.getPaquete() + method.getNombre()).toString();
            }
            MethodReport methodMetric = JCallGraph.methodCallsList.getKey(method, (HashMap<MethodReport, List<MethodReport>>) map);
            if (methodMetric != null) {
                JCallGraph.csvPrinter.printRecord(methodMetric.getLOC(), methodMetric.getNombre(), level,  methodMetric.getWmc(),
                        methodMetric.getResultado(), methodMetric.getLineaClase(), JCallGraph.calledFrom);
            } else { 
                JCallGraph.csvPrinter.printRecord(method.getLOC(), method.getNombre(), level,  method.getWmc(),
                        method.getResultado(), method.getLineaClase(), JCallGraph.calledFrom);
            }
    
            JCallGraph.calledFrom = "";
            if (method != null && map != null && !map.isEmpty() && map.containsKey(method)) {
                for (MethodReport aux : map.get(method)) {
                    if (aux != null) {
                        printChildren(aux, map, level + 1);
                    }
                }
            }
        }
    }
    
}
