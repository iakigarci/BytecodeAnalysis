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
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
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
import java.util.Random;
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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.codehaus.plexus.util.FileUtils;
import org.apache.bcel.classfile.ClassParser;

/**
 * Constructs a callgraph out of a JAR archive. Can combine multiple archives
 * into a single call graph.
 *
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class JCallGraph {

    private static List<String> lInclude = null;
    private static List<String> lExclude = null;
    private GenericTree<MethodReport> tree = new GenericTree<>();
    private static List<HashMap<MethodReport,List<MethodReport>>> methodCalls;
    private static List<MethodReport> visitedMethods = new ArrayList<MethodReport>();
    private static CSVPrinter csvPrinter = null;


    public static void main(String[] args) {
        System.out.println("FICHERO" + Arrays.toString(args));
        Function<ClassParser, ClassVisitor> getClassVisitor = (ClassParser cp) -> {
            try {
                return new ClassVisitor(cp.parse());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };

        try {
            methodCalls = new ArrayList<HashMap<MethodReport,List<MethodReport>>>();
            lInclude = new ArrayList<String>(Arrays.asList(args[1].split(",")));
            lExclude = new ArrayList<String>(Arrays.asList(args[2].split(",")));
            File f = new File(args[0]);
            if (!f.exists()) {
                System.err.println("Jar file " + args[0] + " does not exist");
            }
            try (JarFile jar = new JarFile(f)) {
                Stream<JarEntry> entries = enumerationAsStream(jar.entries()); // All files from jar
                entries.forEach(e -> { 
                    if (!e.isDirectory() && e.getName().endsWith(".class")) {
                        if (isPackage(e.getName())) { 
                            ClassParser cp = new ClassParser(args[0], e.getName());
                            HashMap<MethodReport,List<MethodReport>> map = getClassVisitor.apply(cp).start().methodCalls();
                            if(map!=null){
                                methodCalls.add(map);
                            }
                        }
                    }
                });
                CalledFromList cfl = CalledFromList.getCalledfromlist();
                BufferedWriter log = new BufferedWriter(new OutputStreamWriter(System.out));
                log.write(methodCalls.toString());
                log.close();
            }
            createCSV2();
        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createTree() {
        GenericTree<MethodReport> tree = new GenericTree<>();
        GenericTreeNode<MethodReport> root = new GenericTreeNode<>(new MethodReport());
        tree.setRoot(root);
        boolean exit = false;
        for(HashMap<MethodReport,List<MethodReport>> map : methodCalls) {
            // Se recorren todas las clases analizadas
            Entry<MethodReport, List<MethodReport>> init = map.entrySet().iterator().next();
            GenericTreeNode<MethodReport> child = new GenericTreeNode<>(init.getKey());
            for(MethodReport m : init.getValue()) { // hijos del init
                GenericTreeNode<MethodReport> childAux = new GenericTreeNode<>(m);
                while(!exit) {
                    if (map.get(m)==null) {
                        exit=true;
                    } else {
                        for(MethodReport mAux : map.get(m)) {

                        } 
                    }
                }
                exit = false;
                child.addChild(childAux);
            }
            root.addChild(child); // El arbol tiene ya el init, colgando de root.

        }
    }

    public static void createCSV2() throws IOException {
        File dir = null;
        String dirName = System.getProperty("user.dir")+"/csv/";
        Path path = Paths.get(dirName);
        if (Files.exists(path)) {
            Files.delete(path);
        }
        dir = new File(dirName);
        dir.mkdir();
        BufferedWriter writer = null;
        FileWriter fileWriter = null;
        try {
            for(HashMap<MethodReport,List<MethodReport>> map : methodCalls) { // Recorrer .class
                Set<Entry<MethodReport, List<MethodReport>>> entrySet = map.entrySet();
                if (!entrySet.isEmpty()) {
                    // MethodReport f = (MethodReport) map.keySet().toArray()[0];
                    String name = "/"+ entrySet.iterator().next().getKey().getPaquete() + ".csv";
                    fileWriter = new FileWriter(dir+name);
                    writer = new BufferedWriter(fileWriter);
                    csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Nombre", "Nivel", "LOC", "Resultado", "Linea en clase", "Llamado por"));
                    for(Map.Entry<MethodReport, List<MethodReport>> entry : map.entrySet()) {
                        if (isZeroLelel(map, entry.getKey())) {
                            csvPrinter.printRecord(entry.getKey().getNombre(),0, entry.getKey().getLOC(), entry.getKey().getResultado(), entry.getKey().getLineaClase(),"");
                            // Recorrer hijos
                            for(MethodReport method : entry.getValue()) {
                                if (map.get(method) != null) {
                                    visitedMethods.clear();
                                    printHijos(method, new HashMap<>(map),1);
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

    public static void printHijos(MethodReport method, HashMap<MethodReport, List<MethodReport>> map, int level) throws IOException {
        CalledFromList cfl = CalledFromList.getCalledfromlist();
        String calledFrom = "";
        if (!visitedMethods.contains(method)) {
            visitedMethods.add(method);
            if (cfl.getCalledMap().containsKey(method)) {
                calledFrom = cfl.getCalledMap().get(method).toString();
            }
            csvPrinter.printRecord(method.getNombre(),level, method.getLOC(), method.getResultado(), method.getLineaClase(),calledFrom);
            for(MethodReport aux : map.get(method)) {
                printHijos(aux,map,level+1);
            }
        }
    }

    public static void createCSV(List<String> methodCalls) throws IOException {
        File dir = null;
        dir = new File(System.getProperty("user.dir"));
        dir.mkdir();
        BufferedWriter writer = null;
        CSVPrinter csvPrinter = null;
        try {
            writer = Files.newBufferedWriter(Paths.get("prueba.csv"));
            csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Nombre", "Paquete", "LOC", "Resultado", "Linea en clase", "Destino"));
            for(String s : methodCalls) {
                String[] str = s.split(" ");
                String[] method = str[0].split("/");
                csvPrinter.printRecord(method[0], method[1], method[2], method[3], method[4],str[1]);
            }
            

        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            csvPrinter.close();
            writer.close();
        }

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
        if (lExclude.get(0).equals("*")) {
            return isExactSubsecuence(name, lInclude.get(0));
        } else if(lInclude.isEmpty() && lExclude.isEmpty()) {
            return true;
        }else{
            for (String include : lInclude) {
                if (isExactSubsecuence(name, include) ) {
                    boolean exit = false;
                    for(String exclude : lExclude) {
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
        for(Map.Entry<MethodReport, List<MethodReport>> entry : map.entrySet()) {
            if (entry.getValue().contains(m)) {
                return false;
            }
        }
        return true;
    }
}
