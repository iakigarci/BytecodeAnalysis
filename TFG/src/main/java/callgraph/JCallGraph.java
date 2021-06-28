/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package callgraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKMethodResult;
import com.github.mauricioaniche.ck.Runner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.time.StopWatch;
import org.codehaus.plexus.util.FileUtils;

import org.apache.bcel.classfile.ClassParser;

/**
 * Constructs a callgraph out of a JAR archive. Can combine multiple archives
 * into a single call graph.
 *
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class JCallGraph {

    private static List<String> lInclude;
    private static List<String> lExclude = null;
    private static List<HashMap<MethodReport, List<MethodReport>>> methodCalls;
    private static List<MethodReport> visitedMethods = new ArrayList<MethodReport>();
    private static CSVPrinter csvPrinter = null;
    private static CalledFromList cfl;
    private static String calledFrom = "";
    private static JCallGraph jCallGraph = null;

    public static void main(String[] args) {
        System.out.println("FICHERO" + Arrays.toString(args));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Function<ClassParser, ClassVisitor> getClassVisitor = (ClassParser cp) -> {
            try {
                return new ClassVisitor(cp.parse());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };

        try {
            cfl = CalledFromList.getCalledfromlist();
            methodCalls = new ArrayList<HashMap<MethodReport, List<MethodReport>>>();
            if (args[1].length() != 0) {
                lInclude = new ArrayList<String>(Arrays.asList(args[1].split(",")));
            } else {
                lInclude = new ArrayList<>();
            }
            if (args[2].length() != 0) {
                lExclude = new ArrayList<String>(Arrays.asList(args[2].split(",")));
            } else {
                lExclude = new ArrayList<>();
            }
            File f = new File(args[0]);
            if (!f.exists()) {
                System.err.println("Jar file " + args[0] + " does not exist");
            }
            try (JarFile jar = new JarFile(f)) {
                Stream<JarEntry> entries = enumerationAsStream(jar.entries()); // All files from jar
                entries.forEach(e -> {
                    if (!e.isDirectory() && e.getName().endsWith(".class")) {
                        if (isPackage(e.getName())) {
                            System.out.println("[CALLGRAPH]: " + e.getName());
                            ClassParser cp = new ClassParser(args[0], e.getName());
                            HashMap<MethodReport, List<MethodReport>> map = getClassVisitor.apply(cp).start()
                                    .methodCalls();
                            if (map != null) {
                                methodCalls.add(map);
                            }
                        }
                    }
                });
                // BufferedWriter log = new BufferedWriter(new OutputStreamWriter(System.out));
                // log.write(methodCalls.toString());
                // log.close();
            }
            System.out.println("[CALLGRAPH]: Tiempo transcurrido -> " + stopWatch + "\n");
            String[] runArgs = { args[3] };
            try {
                Runner.main(runArgs);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("[METRICAS]: Tiempo transcurrido -> " + stopWatch + "\n");
            createCSV();
            System.out.println("[FINAL]: Tiempo transcurrido -> " + stopWatch + "\n");
            stopWatch.stop();
        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static JCallGraph getJCallGraph() {
        if (jCallGraph == null) {
            jCallGraph = new JCallGraph();
        }
        return jCallGraph;
    }

    public static void createCSV() throws IOException {
        File dir = null;
        String dirName = System.getProperty("user.dir") + "/csv/";
        Path path = Paths.get(dirName);
        dir = new File(dirName);
        if (Files.exists(path)) {
            FileUtils.deleteDirectory(dir);
        }
        dir.mkdir();
        BufferedWriter writer = null;
        FileWriter fileWriter = null;
        try {
            for (HashMap<MethodReport, List<MethodReport>> map : methodCalls) { // Recorrer .class
                Set<Entry<MethodReport, List<MethodReport>>> entrySet = map.entrySet();
                if (!entrySet.isEmpty()) {
                    String name = "/" + entrySet.iterator().next().getKey().getPaquete() + ".csv";
                    fileWriter = new FileWriter(dir + name);
                    System.out.println("[IMPRIMIR]: " + dir + name);
                    writer = new BufferedWriter(fileWriter);
                    csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Nombre", "Nivel", "LOC", "WMC",
                            "Resultado", "Linea en clase", "Llamado por"));
                    for (Map.Entry<MethodReport, List<MethodReport>> entry : map.entrySet()) {
                        if (isZeroLelel(map, entry.getKey())) {
                            if (cfl.getCalledMap()
                                    .containsKey(entry.getKey().getPaquete() + entry.getKey().getNombre())) {
                                calledFrom = cfl.getCalledMap()
                                        .get(entry.getKey().getPaquete() + entry.getKey().getNombre()).toString();
                            }
                            csvPrinter.printRecord(entry.getKey().getNombre(), 0, entry.getKey().getLOC(),
                                    entry.getKey().getWmc(), entry.getKey().getResultado(),
                                    entry.getKey().getLineaClase(), calledFrom);

                            calledFrom = "";
                            // Recorrer hijos
                            for (MethodReport method : entry.getValue()) {
                                if (map.get(method) != null) {
                                    visitedMethods.clear();
                                    printChildren(method, new HashMap<>(map), 1);
                                }
                            }
                        }
                    }
                    csvPrinter.close();
                    writer.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void printChildren(MethodReport method, HashMap<MethodReport, List<MethodReport>> map, int level)
            throws IOException {
        if (!visitedMethods.contains(method)) {
            visitedMethods.add(method);
            if (cfl.getCalledMap().containsKey(method.getPaquete() + method.getNombre())) {
                calledFrom = cfl.getCalledMap().get(method.getPaquete() + method.getNombre()).toString();
            }
            MethodReport methodMetric = getKey(method, map);
            if (methodMetric != null) {
                csvPrinter.printRecord(methodMetric.getNombre(), level, methodMetric.getLOC(), methodMetric.getWmc(),
                        methodMetric.getResultado(), methodMetric.getLineaClase(), calledFrom);
            } else {
                csvPrinter.printRecord(method.getNombre(), level, method.getLOC(), method.getWmc(),
                        method.getResultado(), method.getLineaClase(), calledFrom);
            }

            calledFrom = "";
            if (method != null && map != null && !map.isEmpty()) {
                for (MethodReport aux : map.get(method)) {
                    if (aux != null) {
                        printChildren(aux, map, level + 1);
                    }
                }
            }
        }
    }

    public static void addCKMetrics(CKClassResult result) {
        String className = result.getClassName();
        for (HashMap<MethodReport, List<MethodReport>> map : methodCalls) { // Iterar .class
            Set<Entry<MethodReport, List<MethodReport>>> entrySet = map.entrySet();
            if (!entrySet.isEmpty() && entrySet.iterator().next().getKey().getPaquete().contains(className)) {
                for (Map.Entry<MethodReport, List<MethodReport>> entry : map.entrySet()) { // Iterar keys
                    for (CKMethodResult method : result.getMethods()) {
                        if (method.getMethodName().contains(entry.getKey().getNombre())) {
                            // Meter metricas
                            entry.getKey().setLOC(method.getLoc());
                            entry.getKey().setLineaClase(method.getStartLine());
                            entry.getKey().setWmc(method.getWmc());
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    public static MethodReport getKey(MethodReport method, HashMap<MethodReport, List<MethodReport>> map) {
        for (Map.Entry<MethodReport, List<MethodReport>> entry : map.entrySet()) {
            if (entry.getKey().equals(method)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
            public T next() {
                return e.nextElement();
            }

            public boolean hasNext() {
                return e.hasMoreElements();
            }
        }, Spliterator.ORDERED), false);
    }

    public static List<String> getlInclude() {
        return lInclude;
    }

    public static boolean isPackage(String name) {
        if (name.contains("$")) {
            return false;
        } else if (lInclude.isEmpty() && lExclude.isEmpty()) {
            return true;
        } else if (lExclude.get(0).equals("*")) {
            return isExactSubsecuence(name, lInclude.get(0));
        } else {
            for (String include : lInclude) {
                if (isExactSubsecuence(name, include)) {
                    boolean exit = false;
                    for (String exclude : lExclude) {
                        if (isExactSubsecuence(name, exclude)) {
                            exit = true;
                            break;
                        }
                    }
                    if (!exit) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static boolean isExactSubsecuence(String source, String subItem) {
        String pattern = "\\b" + subItem + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }

    private static boolean isZeroLelel(HashMap<MethodReport, List<MethodReport>> map, MethodReport m) {
        if (m.getNombre().contains("init")) {
            return true;
        }
        for (Map.Entry<MethodReport, List<MethodReport>> entry : map.entrySet()) {
            if (entry.getValue().contains(m)) {
                return false;
            }
        }
        return true;
    }
}
