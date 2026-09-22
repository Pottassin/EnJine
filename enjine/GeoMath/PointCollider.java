package enjine.GeoMath;

import java.util.ArrayList;

public class PointCollider extends Collider {

    public Coord og;

    public PointCollider(Coord c) {
        og = c;
    }

    public boolean isColliding(PointCollider c) {
        return og == c.og;
    }

    public boolean isColliding(LineCollider c) {
        if (c.l.dir() == 7 || c.l.dir() == 3) return (og.x == c.l.s.x && c.l.onRange(og.y));
        else if (c.l.dir() != 0) {
            if (!c.l.onDomain(og.x)) return false;
        } else return (og.x == c.l.s.x && og.y == c.l.s.y);
        return (og.x - c.l.s.x) * c.l.slope() - og.y == c.l.s.y;
    }

    public boolean isColliding(CircleCollider c) {
        return Math.sqrt((og.x - c.og.x) * (og.x - c.og.x) + (og.y - c.og.y) * (og.y - c.og.y)) <= c.rad;
    }

    public boolean isColliding(RectCollider c) {
        double transformedX = (-Math.cos(c.length.a) * (c.og.x - og.x) + Math.sin(c.length.a) * (-c.og.y + og.y));
        double transformedY = (-Math.sin(c.length.a) * (c.og.x - og.x) - Math.cos(c.length.a) * (-c.og.y + og.y));
        if (c.length.d - transformedX < 0) {
            return false;
        }
        if (c.width - transformedY < 0) {
            return false;
        }
        if (transformedX < 0) {
            return false;
        }
        if (c.width + transformedY < 0) {
            return false;
        }
        return true;
    }

    public ArrayList<Geometry> getEdgeContacts(PointCollider c) {
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        if (og == c.og) ret.add(og);
        return ret;
    }

    public ArrayList<Geometry> getEdgeContacts(LineCollider c) {
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        if (isColliding(c)) ret.add(og);
        return ret;
    }

    public ArrayList<Geometry> getEdgeContacts(RectCollider c) {
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        System.out.println("'getEdgeContacts(RectCollider c)' Not yet implemented");
        return ret;
    }

    public ArrayList<Geometry> getEdgeContacts(CircleCollider c) {
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        if (c.og.distanceTo(og) == c.rad) {
            ret.add(og);
        }
        return ret;
    }
}
