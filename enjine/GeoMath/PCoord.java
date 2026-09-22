package enjine.GeoMath;

import java.awt.*;
import java.util.function.Consumer;

import enjine.util.*;

public class PCoord extends Geometry implements Comparable<PCoord> {

    public double a;
    public double d;

    public PCoord() { a = 0; d = 0; }
    public PCoord(double a, double d) { this.a = a; this.d = d; }

    @Override
    public boolean equals(Object o) {
        System.out.println("test");
        if (o != null) {
            if (o instanceof PCoord temp) {
                return temp.a == a && temp.d == d;
            } } return false; }

    public int compareTo(PCoord c) {
        //return double.compare(d, c.d);
        if (d > c.d) return 1;
        if (d < c.d) return -1;
        if (a > c.a) return 1;
        if (a < c.a) return -1;
        return 0;
    }

    public int x() { return (int) (Math.cos(a) * d); }
    public int y() { return (int) (Math.sin(a) * d); }

    public Coord getCoord() { return new Coord(x(), y()); }

    public Consumer<Graphics> debugRender() {
        return (Graphics g) -> {/*g.setColor(Color.black);*/ g.drawLine(0,0,x(),y());};
    }

    public MultiConsumer debugRender2() {
        return (new MultiConsumer(new Class[]{Graphics.class, Color.class, Coord.class, Coord.class, Coord.class}, (Object[] o) -> {
            Graphics g = (Graphics) o[0]; Color col = (Color) o[1]; Coord offset = (Coord) o[2]; Coord posScale = (Coord) o[3]; Coord dimScale = (Coord) o[4];
            g.setColor(col);
            g.drawLine((int) (offset.x*posScale.x), (int) (offset.y()*posScale.y), (int) ((Math.cos(a) * d * dimScale.x + offset.x) * posScale.x), (int) ((Math.sin(a) * d * dimScale.y + offset.y) * posScale.y));
        }));
    }

    public String toString() {
        return "(" + d +" @ " + a + ")";
    }
}