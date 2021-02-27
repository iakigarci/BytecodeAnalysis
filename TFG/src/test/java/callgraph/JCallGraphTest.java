package callgraph;

import org.junit.jupiter.api.Test;

public class JCallGraphTest {
    @Test
    public void testMain() {
        String[] s = new String[1];
        s[0] = "ProyectoAnalizar.jar";
        JCallGraph.main(s);
    }
}
