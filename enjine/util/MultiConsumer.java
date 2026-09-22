package enjine.util;

import java.util.function.Consumer;

public class MultiConsumer {
    public Class[] types;
    public Consumer<Object[]> method;

    public MultiConsumer(Class[] t, Consumer<Object[]> m) {
        types = t;
        method = m;
    }

    public boolean isValid(Object[] obs) {
        if (types.length != obs.length) { System.out.println("differing lengths"); return false; }
        for (int x = 0; x < types.length; x++) {
            if (obs[x].getClass() != types[x]) {
                System.out.println("incompatible type " + obs[x].getClass() + " (" + obs[x] + "), expected " + types[x]);
                return false;
            }
        }
        return true;
    }

    public void run(Object[] obs) {
        /*if (isValid(obs)) */method.accept(obs);
        //else System.out.println("Invalid pass");
    }
}
