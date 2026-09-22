package enjine.GeoMath;

import java.util.ArrayList;

public class LineCollider extends Collider {
    public Line l;

    public LineCollider(Coord s, Coord e) {
        l = new Line(s, e);
    }

    public LineCollider(Line src) {
        l = src;
    }

    public boolean isColliding(PointCollider c) {
        if (l.dir() == 7 || l.dir() == 3) return (c.og.x == l.s.x && l.onRange(c.og.y));
        else if (l.dir() != 0) {
            if (!l.onDomain(c.og.x)) return false;
        } else return (c.og.x == l.s.x && c.og.y == l.s.y);
        return (c.og.x - l.s.x) * l.dy() == (l.s.y + c.og.y) * l.dx();
    }

    public boolean isColliding(LineCollider c) {
        if (l.s.x == l.e.x && c.l.s.x == c.l.e.x) {
            if (c.l.s.x == l.s.x)
                return (c.l.upP().y >= l.downP().y && c.l.downP().y <= l.upP().y);
            return false;
        }
        if (l.slope() == c.l.slope()) {
            if (l.yInt() == c.l.yInt())
                return (c.l.rightP().x >= l.leftP().x && c.l.leftP().x <= l.rightP().x);
            return false;
        }
        double xInt = 0;
        try {
            xInt = ((l.dx() * (c.l.dx() * (l.s.y - c.l.s.y) + c.l.s.x * c.l.dy()) - l.s.x * c.l.dx() * l.dy())) / ((c.l.dy() * l.dx() - c.l.dx() * l.dy()));
        } catch (Exception e) {
            System.out.println("no intercept");
            return false;
        }
        //System.out.println(((xInt - l.s.x) * l.slope() + l.s.y) +", " + ((xInt - c.l.s.x) * c.l.slope() + c.l.s.y));
        if (l.onDomain(xInt) && c.l.onDomain(xInt)) {
            if (l.s.x == l.e.x) {
                if (c.l.s.x == c.l.e.x)
                    return (c.l.upP().y >= l.downP().y && c.l.downP().y <= l.upP().y);
                return l.onRange((xInt - c.l.s.x) * c.l.slope() + c.l.s.y);
            }
            if (c.l.s.x == c.l.e.x)
                return c.l.onRange((xInt - l.s.x) * l.slope() + l.s.y);
            //return Math.abs((xInt - l.s.x) * l.slope() + l.s.y - (xInt - c.l.s.x) * c.l.slope() - c.l.s.y) <= .001f;
            return true;
        }
        return false;
    }

    public boolean isColliding(RectCollider c) {
        //System.out.println("'isColliding(RectCollider c)' will not work if line is contained, do some coord transform shenanigans");
        for (LineCollider coll : c.edgeColliders()) {
            if (isColliding(coll)) return true;
        }
        return false;
    }

    public boolean isColliding(CircleCollider c) {
        System.out.println("to be implemented");
        return false;
    }

    public ArrayList<Geometry> getEdgeContacts(PointCollider c) {
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        if (isColliding(c)) ret.add(c.og);
        return ret;
    }

    public ArrayList<Geometry> getEdgeContacts(LineCollider c) {
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        //System.out.print("line: ");
        if (l.s.x == l.e.x && c.l.s.x == c.l.e.x) {
            if (l.s.x == c.l.s.x) {
                if (c.l.upP().y >= l.downP().y && c.l.downP().y <= l.upP().y)
                    ret.add(new Line(c.l.downP().y < l.downP().y ? l.downP() : c.l.downP(), c.l.upP().y > l.upP().y ? l.upP() : c.l.upP()));
            }
            return ret;
        }
        if (l.slope() == c.l.slope()) {
            if (l.yInt() == c.l.yInt()) {
                if (c.l.rightP().x >= l.leftP().x && c.l.leftP().x <= l.rightP().x)
                    ret.add(new Line(c.l.leftP().x < l.leftP().x ? l.leftP() : c.l.leftP(), c.l.rightP().x > l.rightP().x ? l.rightP() : c.l.rightP()));
            }
            return ret;
        }
        double xInt = 0;
        double yInt = 0;
        try {
            //xInt = ((l.dx() * (c.l.dx() * (l.s.y - c.l.s.y) + c.l.s.x * c.l.dy()) - l.s.x * c.l.dx() * l.dy())) / ((c.l.dy() * l.dx() - c.l.dx() * l.dy()));
            xInt = ((c.l.s.x * c.l.e.y - c.l.s.y * c.l.e.x) * (l.dx()) + (l.s.y * l.e.x - l.s.x * l.e.y) * (c.l.dx())) / ((c.l.dy() * l.dx() - c.l.dx() * l.dy()));
            yInt = ((c.l.s.x * c.l.e.y - c.l.s.y * c.l.e.x) * (l.dy()) + (l.s.y * l.e.x - l.s.x * l.e.y) * (c.l.dy())) / ((c.l.dy() * l.dx() - c.l.dx() * l.dy()));
        } catch (Exception e) {
            return ret;
        }
        if ((l.onDomain(xInt) || l.dx() == 0) && (c.l.onDomain(xInt) || c.l.dx() == 0)) {
            //System.out.print(xInt + ", " + yInt + " : ");
            if (l.s.x == l.e.x) {
                if (l.onRange(yInt) || l.dy() == 0)
                    ret.add(new Coord(l.s.x, yInt));
                return ret;
            }
            if (c.l.s.x == c.l.e.x) {
                if (c.l.onRange(yInt) || c.l.dy() == 0)
                    ret.add(new Coord(c.l.s.x, yInt));
                return ret;
            }
                /*if (Math.abs((xInt - l.s.x) * l.slope() + l.s.y - (xInt - c.l.s.x) * c.l.slope() - c.l.s.y) <= .001f)
                    ret.add(new Coord( xInt,  Math.round(((xInt - l.s.x) * l.slope() + l.s.y)*1000) / 1000f));*/
            ret.add(new Coord(xInt, yInt));
        }
        return ret;
    }

    public ArrayList<Geometry> getEdgeContacts(RectCollider c) {
        //System.out.println("probably improve 'getEdgeContacts(RectCollider c)'");
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        ArrayList<Geometry> temp;
        for (LineCollider coll : c.edgeColliders()) {
            temp = getEdgeContacts(coll);
            if (!temp.isEmpty()) {
                if (!ret.contains(temp.get(0))) ret.add(temp.get(0));
            }
        }
        //System.out.println("");
        return ret;
    }
}
