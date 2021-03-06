package callgraph;

import org.junit.jupiter.api.Test;

public class JCallGraphTest {
    @Test
    void testMain() {
        String[] s = new String[2];
        s[0] = "ProyectoAnalizar.jar";
        s[1] = "paquete";
        JCallGraph.main(s);
    }

    @Test
    void testMain2() {
        String[] s = new String[3];
        s[0] = "indaba_report.jar";
        s[1] = "config,js,tools,ws,configuration,models,utils,view,providers,exporters,factories";
        s[2] = "sonarqube,apache,google,okhttp3";
        JCallGraph.main(s);
    }

    @Test 

    void testMain3() {
        String[] s = new String[3];
        s[0] = "indaba_report.jar";
        s[1] = "ws";
        s[2] = "sonarqube,apache,google,okhttp3";
        JCallGraph.main(s);
    }
}
