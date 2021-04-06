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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
            List<String> methodCalls = null;
            lInclude = new ArrayList<String>(Arrays.asList(args[1].split(",")));
            lExclude = new ArrayList<String>(Arrays.asList(args[2].split(",")));
            File f = new File(args[0]);
            if (!f.exists()) {
                System.err.println("Jar file " + args[0] + " does not exist");
            }
            try (JarFile jar = new JarFile(f)) {
                Stream<JarEntry> entries = enumerationAsStream(jar.entries()); // All files from jar
                methodCalls = entries.flatMap(e -> {
                    if (e.isDirectory() || !e.getName().endsWith(".class"))
                        return (new ArrayList<String>()).stream();
                    if (isPackage(e.getName())) {
                        ClassParser cp = new ClassParser(args[0], e.getName());
                        return getClassVisitor.apply(cp).start().methodCalls().stream();
                    } else {
                        return null;
                    }
                }).collect(Collectors.toList());
                BufferedWriter log = new BufferedWriter(new OutputStreamWriter(System.out));
                log.write(methodCalls.toString());
                log.close();
            }
            createCSV(methodCalls);

            // csvPrinter.flush();
        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
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
        
        if(lInclude.isEmpty() && lExclude.isEmpty()) {
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

    private static boolean isExactSubsecuence(String source, String subItem) {
        String pattern = "\\b" + subItem + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }
}
