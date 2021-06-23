package callgraph;

import org.junit.jupiter.api.Test;

public class JCallGraphTest {
    @Test
    void testMain() {
        String[] s = new String[3];
        s[0] = "ProyectoAnalizar.jar"; // JAR
        s[1] = "paquete";              // Include list
        s[2] = "*,";                    // Exclude list
        JCallGraph.main(s);
    }

    @Test
    void testMain2() {
        String[] s = new String[4];
        s[0] = "indaba_report.jar";
        s[1] = "config,js,tools,ws,configuration,models,utils,view,providers,exporters,factories";
        s[2] = "sonarqube,apache,google,okhttp3,sonar";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\SonarReport-master\\SonarReportMaven\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain3() {
        String[] s = new String[4];
        s[0] = "indaba_report.jar";
        s[1] = "utils";
        s[2] = "sonarqube,apache,google,okhttp3";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\SonarReport-master\\SonarReportMaven\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain4() {
        String[] s = new String[4];
        s[0] = "ProyectoAnalizarExtendido.jar";
        s[1] = "paquete";
        s[2] = "*,";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\ProyectoAnalizarExtendido-master\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain5() {
        String[] s = new String[4];
        s[0] = "TFG-0.1-static.jar";
        s[1] = "callgraph";
        s[2] = "apache";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\TFG\\TFG\\src";
        JCallGraph.main(s);
    }

    @Test
    void testMain6() {
        String[] s = new String[3];
        s[0] = "TFG-0.1.jar";
        s[1] = "callgraph,util";
        JCallGraph.main(s);
    }

    @Test 
    void testMain7() {
        String[] s = new String[3];
        s[0] = "git_java.jar";
        s[1] = "github";
        s[2] = "*,";
        JCallGraph.main(s);
    }

    @Test 
    void testMain8() {
        String[] s = new String[4];
        s[0] = "arthas-boot.jar";
        s[1] = "";
        s[2] = "";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\arthas-master";
        JCallGraph.main(s);
    }

    @Test 
    void testMain9() {
        String[] s = new String[4];
        s[0] = "indaba_report.jar";
        s[1] = "";
        s[2] = "";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\SonarReport-master\\SonarReportMaven\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain10() {
        String[] s = new String[4];
        s[0] = "jooq-3.15.0-SNAPSHOT.jar";
        s[1] = "jooq";
        s[2] = "*,";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\jOOQ-main\\jOOQ\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain11() {
        String[] s = new String[4];
        s[0] = "Prueba.jar";
        s[1] = "generated";
        s[2] = "*,";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\java-bullshifier-master\\salida\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain12() {
        String[] s = new String[4];
        s[0] = "claseGrande.jar";
        s[1] = "generated";
        s[2] = "*,";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\java-bullshifier-master\\claseGrande\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain13() {
        String[] s = new String[4];
        s[0] = "mediano.jar";
        s[1] = "generated";
        s[2] = "*,";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\java-bullshifier-master\\mediano\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain14() {
        String[] s = new String[4];
        s[0] = "proyectoGrande.jar";
        s[1] = "generated";
        s[2] = "*,";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\java-bullshifier-master\\proyectoGrande\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain15() {
        String[] s = new String[4];
        s[0] = "proyectoGrande2.jar";
        s[1] = "generated";
        s[2] = "*,";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\java-bullshifier-master\\proyectoGrande2\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain16() {
        String[] s = new String[4];
        s[0] = "proyectoExtenso.jar";
        s[1] = "generated";
        s[2] = "*,";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\java-bullshifier-master\\proyectoExtenso\\src";
        JCallGraph.main(s);
    }
}
