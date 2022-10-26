package callgraph;

import org.apache.commons.cli.*;  
  
import java.io.OutputStream;

public class CliManager {

    public static void main(String[] args) { 
        /** Options for JAR path */
        String jar = null;
        /** Options for source code path */
        String sourceCodePath = null;
        /** Options for included packages */ 
        String[] lInclude = null;
        /** Options for excluded packages */ 
        String[] lExclude = null;

        OutputStream output  = null;  
        CommandLineParser parser  = null;  
        CommandLine cmdLine = null; 

        Options options = new Options();
        options.addOption("jar", true, "Direccion absoluta del Jar");
        options.addOption("s", true, "Dirección absoluta del codigo fuente");
        options.addOption("h", "help", false, "Imprime el mensaje de ayuda");   

        // Opciones que pueden contener muchas cosas
        Option optionInclude = new Option("i", true, "Paquete(s) que se quiere(n) incluir");
        Option optionExclude = new Option("e", true, "Paquete(s) que se quiere(n) excluir");
        optionInclude.setArgs(Option.UNLIMITED_VALUES);
        optionExclude.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(optionInclude);
        options.addOption(optionExclude);

        OptionGroup group =  new OptionGroup();  
        group.addOption(new Option("err",     "Salida estándar de errores"));  
        group.addOption(new Option("console", "Salida estándar"));  
        options.addOptionGroup(group);  

        try {
            parser  = new BasicParser();  
            cmdLine = parser.parse(options, args);  

            if (cmdLine.hasOption("h")){    // No hace falta preguntar por el parámetro "help". Ambos son sinónimos  
                new HelpFormatter().printHelp(CliManager.class.getCanonicalName(), options );  
                return;  
            }
            
            jar = cmdLine.getOptionValue("jar");
            if (jar == null) {
                throw new org.apache.commons.cli.ParseException("La direccion absoluta del JAR es requerida");  
            }
            sourceCodePath = cmdLine.getOptionValue("s");
            if (sourceCodePath == null) {
                throw new org.apache.commons.cli.ParseException("La direccion absoluta del codigo fuente es requerida");  
            }
            lInclude = cmdLine.getOptionValues("i");
            if (lInclude == null) {
                throw new org.apache.commons.cli.ParseException("Paquetes a incluir requeridos");  
            }
            lExclude = cmdLine.getOptionValues("e");
            if (lExclude == null) {
                throw new org.apache.commons.cli.ParseException("Paquetes a excluir requeridos");  
            }

            if (cmdLine.hasOption("console")){  
                output = System.out;  
            } else if (cmdLine.hasOption("err")){  
                output = System.err;  
            } else {  
                output = null;  
            }  
        } catch (org.apache.commons.cli.ParseException ex){  
            System.out.println(ex.getMessage());  
            new HelpFormatter().printHelp(CliManager.class.getCanonicalName(), options );    // Error, imprimimos la ayuda  
        } catch (java.lang.NumberFormatException ex){  
            new HelpFormatter().printHelp(CliManager.class.getCanonicalName(), options );    // Error, imprimimos la ayuda  
        } 
    }
}
