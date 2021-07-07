package callgraph;

import org.junit.jupiter.api.Test;

public class JCallGraphTest {

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
        s[0] = "TFG-0.3-static.jar";
        s[1] = "callgraph,com";
        s[2] = "apache";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\TFG\\TFG\\src";
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

    @Test 
    void testMain17() {
        String[] s = new String[4];
        s[0] = "TFG-0.3-launcher.jar";
        s[1] = "callgraph,com";
        s[2] = "*,";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\TFG\\TFG\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain18() {
        String[] s = new String[9];
        s[0] = "-jar";
        s[1] = "ProyectoAnalizarExtendido.jar";
        s[2] = "-i";
        s[3] = "paquete";
        s[4] = "a b ";
        s[5] = "-e";
        s[6] = "a ";
        s[7] = "-s";
        s[8] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\ProyectoAnalizarExtendido-master\\src";
        JCallGraph.main(s);
    }

    @Test 
    void testMain19() {
        String[] s = new String[2];
        s[0] = "-jar";
        s[1] = " a";
        JCallGraph.main(s);
    }

    @Test 
    void testMain20() {
        String[] s = new String[4];
        s[0] = "Avanzado.jar";
        s[1] = "generated";
        s[2] = "*,";
        s[3] = "D:\\UNIVERSIDAD\\TFG\\Repositorio\\java-bullshifier-master\\Avanzado\\ProjFbfxomckzun\\src";
        JCallGraph.main(s);
    }
    
}
