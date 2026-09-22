package enjine.GeoMath;

import java.awt.*;
import java.util.function.Consumer;

import enjine.util.*;

public class Line extends Geometry implements Comparable<Line> {
    public Coord s;
    public Coord e;
    public Line() {
        s = new Coord();
        e = new Coord();
    }
    public Line(Coord start, Coord end) {
        s = start;
        e = end;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            if (o instanceof Line temp) {
                return temp.s.x == s.x && temp.s.y == s.y && temp.e.x == e.x && temp.e.y == e.y;
            } } return false; }

    public int compareTo(Line c) {
        if (slope() > c.slope()) return 1;
        if (slope() < c.slope()) return -1;
        return 0;
    }
    public double slope() {
        return dy() / dx();
    }

    public double angle() {
        return Math.atan2(e.y - s.y, e.x - s.x);
    }

    public int dir() {
        // none: 0 right - down right: 1 - 8 (ccw)
        if (s.x == e.x) {
            if (s.y == e.y) return 0;
            if (s.y > e.y) return 7;
            return 3;
        }
        else if (s.x < e.x) {
            if (s.y == e.y) return 1;
            if (s.y > e.y) return 8;
            return 2;
        }
        if (s.y == e.y) return 5;
        if (s.y > e.y) return 6;
        return 4;
    }
    public double yInt() {
        return s.y-s.x*slope();
    }
    public double xInt() {
        return s.x-s.y/slope();
    }
    public double dy() {
        return e.y - s.y;
    }
    public double dx() {
        return e.x - s.x;
    }
    public Coord midpoint() { return new Coord((s.x + e.x) / 2, (s.y + e.y) / 2); }

    public double length() { return Math.hypot(dx(),dy()); }

    public boolean onDomain(double x) {
        if (e.x >= s.x)
            return x >= s.x && x <= e.x;
        return x <= s.x && x >= e.x;
    }

    public boolean onRange(double y) {
        if (e.y >= s.y)
            return y >= s.y && y <= e.y;
        return y <= s.y && y >= e.y;
    }

    public Coord leftP() { return s.x < e.x ? s : e; }
    public Coord rightP() { return s.x > e.x ? s : e; }
    public Coord downP() { return s.y < e.y ? s : e; }
    public Coord upP() { return s.y > e.y ? s : e; }

    //public Line project(Line o) {}

    public Coord pointProjectedTo(Coord p, Line l) {
        Coord ret = new Coord();
        double a = Math.atan2(p.y-s.y,p.x-s.x) - angle() + l.angle();
        double d = Math.hypot(p.x-s.x,p.y-s.y)*l.length()/length();
        ret.x = (d * Math.cos(a)) + l.s.x;
        ret.y = (d * Math.sin(a)) + l.s.y;
        return ret;
    }

    public Line lineProjectedTo(Line p, Line l) {
        Line ret = new Line();
        double a = Math.atan2(p.s.y-s.y,p.s.x-s.x) - angle() + l.angle();
        double d = Math.hypot(p.s.x-s.x,p.s.y-s.y)*l.length()/length();
        ret.s.x = (d * Math.cos(a)) + l.s.x;
        ret.s.y = (d * Math.sin(a)) + l.s.y;
        a = Math.atan2(p.e.y-s.y,p.e.x-s.x) - angle() + l.angle();
        d = Math.hypot(p.e.x-s.x,p.e.y-s.y)*l.length()/length();
        ret.e.x = (d * Math.cos(a)) + l.s.x;
        ret.e.y = (d * Math.sin(a)) + l.s.y;
        return ret;
    }

    public void transform(double dx, double dy) {
        s.transform(dx, dy);
        e.transform(dx, dy);
    }

    public void transform(Coord d) {
        s.transform(d.x, d.y);
        e.transform(d.x, d.y);
    }

    public void scale(double dx, double dy) {
        s.scale(dx, dy);
        e.scale(dx, dy);
    }

    public void scale(Coord d) {
        s.scale(d.x, d.y);
        e.scale(d.x, d.y);
    }

    public void turn(double a) {
        s.turn(a);
        e.turn(a);
    }

    public void turnAround(double a, Coord c) {
        s.turnAround(a, c);
        e.turnAround(a, c);
    }

    public Consumer<Graphics> debugRender() {
        return (Graphics g) -> {/*g.setColor(Color.black);*/ g.drawLine(s.x(),s.y(),e.x(),e.y());};
    }

    public MultiConsumer debugRender2() {
        return (new MultiConsumer(new Class[]{Graphics.class, Color.class, Coord.class, Coord.class, Coord.class}, (Object[] o) -> {
            Graphics g = (Graphics) o[0]; Color col = (Color) o[1]; Coord offset = (Coord) o[2]; Coord posScale = (Coord) o[3]; Coord dimScale = (Coord) o[4];
            g.setColor(col);
            g.drawLine((int) ((s.x * dimScale.x + offset.x) * posScale.x), (int) ((s.y * dimScale.y + offset.y) * posScale.y), (int) ((e.x * dimScale.x + offset.x) * posScale.x), (int) ((e.y * dimScale.y + offset.y) * posScale.y));
        }));
    }


    public String toString() {
        return "y + "+(-s.y)+" = "+slope()+" * ( x + "+(-s.x)+" ) [ "+s.x+", "+e.x+" ]";
    }

    public String altToString() {
        return "("+s.x+", "+s.y+") -> ("+e.x+", "+e.y+")";
    }
}