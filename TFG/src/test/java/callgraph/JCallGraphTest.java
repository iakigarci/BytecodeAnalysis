package callgraph;

import org.junit.jupiter.api.Test;

public class JCallGraphTest {
    @Test
    void testMain() {
        String[] s = new String[2];
        s[0] = "ProyectoAnalizar.jar";
        s[1] = "java.util";
        JCallGraph.main(s);
    }
}
