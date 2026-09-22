package enjine.GeoMath;

import java.awt.*;
import java.util.function.Consumer;

import enjine.util.*;

public class Circle extends Geometry implements Comparable<Circle> {
    public Coord og;

    public double rad;

    public Circle() { og = new Coord(0,0); rad = 0; }
    public Circle(Coord og, double rad) { this.og = og; this.rad = rad; }
    public Circle(double x, double y, double rad) { og = new Coord(x,y); this.rad = rad; }
    public Circle(Circle copy) { og = copy.og; rad = copy.rad; }

    public int x() { return (int) og.x; }
    public int y() { return (int) og.y; }
    public int rad() { return (int) rad; }

    @Override
    public boolean equals(Object o) {
        //System.out.println("test");
        if (o != null) {
            if (o instanceof Circle temp) {
                return temp.og == og && temp.rad == rad;
            } } return false; }

    public int compareTo(Circle c) {
        if (rad > c.rad) return 1;
        if (rad < c.rad) return -1;
        return og.compareTo(c.og);
    }

    public double distanceTo(Circle c) {
        return Math.sqrt((c.og.x - og.x) * (c.og.x - og.x) + (c.og.y - og.y) * (c.og.y - og.y)) - rad - c.rad;
    }

    public void copy(Circle c) {
        og.x = c.og.x;
        og.y = c.og.y;
        rad = c.rad;
    }

    public void transform(Coord d) { og.transform(d); }
    public void transform(double dx, double dy) { og.transform(dx, dy); }
    public void turn(double a) { og.turn(a); }
    public void turnAround(double a, Coord c) { og.turnAround(a, c); }

    public Consumer<Graphics> debugRender() {
        return (Graphics g) -> {g.setColor(Color.black); g.fillOval((int) (og.x-rad),(int) (og.y-rad),(int) (rad*2),(int) (rad*2));};
    }

    public MultiConsumer debugRender2() {
        return (new MultiConsumer(new Class[]{Graphics.class, Color.class, Coord.class, Coord.class, Coord.class}, (Object[] o) -> {
            Graphics g = (Graphics) o[0]; Color col = (Color) o[1]; Coord offset = (Coord) o[2]; Coord posScale = (Coord) o[3]; Coord dimScale = (Coord) o[4];
            g.setColor(col);
            g.fillOval((int) ((og.x * posScale.x + offset.x) - rad * dimScale.x), (int) ((og.y * posScale.y + offset.y) - rad * dimScale.y), (int) (rad * 2 * dimScale.x), (int) (rad * 2 * dimScale.y));
        }));
    }

    public String toString() {
        return "(" + og.x + ", " + og.y + ") " + rad + "'";
    }
}