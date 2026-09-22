package enjine.GeoMath;
import java.awt.*;
import java.util.function.Consumer;

import enjine.util.*;

public abstract class Geometry {
    public Consumer<Graphics> debugRender() { return (Graphics g) -> {}; }
    public MultiConsumer debugRender2() { return new MultiConsumer(new Class[]{}, (Object[] o) -> {}); }

    public void turn(double a) {}
    public void turnAround(double a, Coord c) {}
    public void transform(double dx, double dy) {}
    public void transform(Coord d) {}
    public void scale(double dx, double dy) {}
    public void scale(Coord d) {}
}

/*public class Arc extends Geometry implements Comparable<Arc> {

    public Coord og;
    public double rad;
    public double a1;
    public double a2;

    public Arc(double x, double y, double rad, double a1, double a2) {
        og = new Coord(x, y);
        this.rad = rad;
        this.a1 = a1;
        this.a2 = a2;
    }
    public Arc(Coord og, double rad, double a1, double a2) {
        this.og = og;
        this.rad = rad;
        this.a1 = a1;
        this.a2 = a2;
    }
    public Arc(Coord og, PCoord ray, double a) {
        this.og = og;
        rad = ray.d;
        a1 = ray.a;
        a2 = a;
    }
    public Arc(Circle c, double a1, double a2) {
        og = c.og;
        rad = c.rad;
        this.a1 = a1;
        this.a2 = a2;
    }


}*/

/*public class Rectangle extends Geometry implements Comparable<Rectangle> {
    public Coord og;
    public PCoord length;
    public double width;


}*/

