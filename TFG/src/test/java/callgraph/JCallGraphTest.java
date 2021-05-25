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
        String[] s = new String[3];
        s[0] = "indaba_report.jar";
        s[1] = "config,js,tools,ws,configuration,models,utils,view,providers,exporters,factories";
        s[2] = "sonarqube,apache,google,okhttp3,sonar";
        JCallGraph.main(s);
    }

    @Test 
    void testMain3() {
        String[] s = new String[3];
        s[0] = "indaba_report.jar";
        s[1] = "configuration";
        s[2] = "sonarqube,apache,google,okhttp3";
        JCallGraph.main(s);
    }

    @Test 
    void testMain4() {
        String[] s = new String[3];
        s[0] = "ProyectoAnalizarExtendido.jar";
        s[1] = "paquete";
        s[2] = "*,";
        JCallGraph.main(s);
    }

    @Test 
    void testMain5() {
        String[] s = new String[3];
        s[0] = "TFG-0.1-static.jar";
        s[1] = "callgraph";
        s[2] = "apache";
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
        String[] s = new String[3];
        s[0] = "arthas-boot.jar";
        s[1] = "";
        s[2] = "";
        JCallGraph.main(s);
    }

    @Test 
    void testMain9() {
        String[] s = new String[3];
        s[0] = "indaba_report.jar";
        s[1] = "";
        s[2] = "";
        JCallGraph.main(s);
    }
}
