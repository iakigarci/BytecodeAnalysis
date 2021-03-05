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
        String[] s = new String[2];
        s[0] = "indaba_report.jar";
        s[1] = "providers";
        JCallGraph.main(s);
    }
}
