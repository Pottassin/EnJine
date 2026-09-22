package enjine.GeoMath;

import java.awt.*;
import java.util.function.Consumer;

import enjine.util.*;

public class Coord extends Geometry implements Comparable<Coord> {

    public double x;
    public double y;

    public Coord() { x = 0; y = 0; }
    public Coord(double x, double y) { this.x = x; this.y = y; }
    public Coord(Coord copy) { x = copy.x; y = copy.y; }
    public Coord(Point copy) { x = 0; if (copy != null) x = copy.x; y = 0; if (copy != null) y = copy.y; }

    public int x() { return (int) x; }

    public int y() { return (int) y; }

    @Override
    public boolean equals(Object o) {
        //System.out.println("test");
        if (o != null) {
            if (o instanceof Coord temp) {
                return temp.x == x && temp.y == y;
            } } return false; }

    public int compareTo(Coord c) {
        if (x > c.x) return 1;
        if (x < c.x) return -1;
        if (y > c.y) return 1;
        if (y < c.y) return -1;
        return 0;
    }

    public void reduce() {
        double m = mag();
        if (m == 0) { return; }
        x = x / m;
        y = y / m;
    }

    public Coord add(Coord c) { return new Coord(x+c.x, y+c.y); }
    public Coord add(double n) { return new Coord(x+n, y+n); }
    public Coord subtract(Coord c) { return new Coord(x-c.x, y-c.y); }
    public Coord subtract(double n) { return new Coord(x-n, y-n); }
    public Coord multiply(Coord c) { return new Coord(x*c.x, y*c.y); }
    public Coord multiply(double n) { return new Coord(x*n, y*n); }
    public Coord divide(Coord c) { return new Coord(x/c.x, y/c.y); }
    public Coord divide(double n) { return new Coord(x/n, y/n); }
    public Coord pow(Coord c) { return new Coord(Math.pow(x,c.x), Math.pow(y,c.y)); }
    public Coord pow(double n) { return new Coord(Math.pow(x,n), Math.pow(y,n)); }
    public Coord mod(Coord c) { return new Coord(x%c.x, y%c.y); }
    public Coord mod(double n) { return new Coord(x%n, y%n); }
    public double dotProduct(Coord c) { return x*c.x + y*c.y; }

    public double distanceTo(Coord c) {
        return Math.sqrt((c.x - x) * (c.x - x) + (c.y - y) * (c.y - y));
    }

    public double distanceTo(Coord c, Coord o) {
        return Math.sqrt((c.x + o.x - x) * (c.x + o.x - x) + (c.y + o.y -y) * (c.y + o.y - y));
    }

    public double mag() { return Math.sqrt(x * x + y * y); }
    public double angle() { return Math.atan2(y, x); }

    public static double angleBetween(Coord c1, Coord c2) {
        return Math.atan2(c1.y - c2.y, c1.x - c2.x);
    }

    public void transform(double dx, double dy) {
        x -= dx;
        y -= dy;
    }

    public void transform(Coord d) {
        x -= d.x;
        y -= d.y;
    }

    public void scale(double dx, double dy) {
        x *= dx;
        y *= dy;
    }

    public void scale(Coord d) {
        x *= d.x;
        y *= d.y;
    }

    public void turn(double a) {
        x = (mag() * Math.cos(angle() + a));
        y = (mag() * Math.sin(angle() + a));
    }

    public void turn90(int rs) {
        rs = rs%4;
        if (rs == 1) { double temp = x; x = -y; y = temp; }
        else if (rs == 2) { x = -x; y = -y; }
        else if (rs == 3) { double temp = x; x = -y; y = -temp; }
    }

    public void turnAround(double a, Coord c) {
        c.x=-c.x;
        c.y=-c.y;
        transform(c);
        x = (mag() * Math.cos(angle() + a));
        y = (mag() * Math.sin(angle() + a));
        c.x=-c.x;
        c.y=-c.y;
        transform(c);
    }

    public void setAngle(double a) {
        x = (mag() * Math.cos(a));
        y = (mag() * Math.sin(a));
    }

    public void copy(Coord c) {
        x = c.x;
        y = c.y;
    }

    public PCoord getPCoord() { return new PCoord(angle(), mag()); }

    public Consumer<Graphics> debugRender() {
        return (Graphics g) -> {/*g.setColor(Color.black);*/ g.fillOval(x()-1,y()-1,2,2);};
    }

    public MultiConsumer debugRender2() {
        return (new MultiConsumer(new Class[]{Graphics.class, Color.class, Coord.class, Coord.class, Coord.class}, (Object[] o) -> {
            Graphics g = (Graphics) o[0]; Color col = (Color) o[1]; Coord offset = (Coord) o[2]; Coord posScale = (Coord) o[3]; Coord dimScale = (Coord) o[4];
            g.setColor(col);
            g.fillOval((int) ((x * posScale.x + offset.x) - .5f * dimScale.x), (int) ((y * posScale.y + offset.y) - .5f * dimScale.y), dimScale.x(), dimScale.y());
        }));
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}