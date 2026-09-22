package enjine.GeoMath;

import java.util.ArrayList;

public class CircleCollider extends Collider {
    public Coord og;
    public double rad;

    public CircleCollider(Coord c, double r) {
        og = c;
        rad = r;
    }

    public boolean isColliding(PointCollider c) {
        return og.distanceTo(c.og) <= rad;
    }

    public boolean isColliding(LineCollider c) {
        System.out.println("'isColliding(LineCollider c)' to be implemented");
        return false;
    }

    public boolean isColliding(RectCollider c) {
        double transformedX = (-Math.cos(c.length.a) * (c.og.x - og.x) + Math.sin(c.length.a) * (-c.og.y + og.y));
        double transformedY = (-Math.sin(c.length.a) * (c.og.x - og.x) - Math.cos(c.length.a) * (-c.og.y + og.y));
        byte trues = (byte) 0;
        byte falses = (byte) 0;
        if (c.length.d - transformedX + rad < 0) {
            return false;
        } else if (c.length.d - transformedX + rad >= rad) {
            trues++;
        }
        if (c.width - transformedY + rad < 0) {
            return false;
        } else if (c.width - transformedY + rad >= rad) {
            trues++;
            falses += 1;
        }
        if (transformedX + rad < 0) {
            return false;
        } else if (transformedX + rad >= rad) {
            trues++;
            falses += 2;
        }
        if (c.width + transformedY + rad < 0) {
            return false;
        } else if (c.width + transformedY + rad >= rad) {
            trues++;
            falses += 4;
        }
        if (trues >= 3) {
            return true;
        } else {
            if (falses == 1) {
                return (rad - c.og.distanceTo(new Coord(transformedX - c.length.d, transformedY - c.width)) <= rad);
            }
            if (falses == 3) {
                return (rad - c.og.distanceTo(new Coord(transformedX, transformedY - c.width)) <= rad);
            }
            if (falses == 6) {
                return (rad - c.og.distanceTo(new Coord(transformedX, transformedY + c.width)) <= rad);
            }
            if (falses == 4) {
                return (rad - c.og.distanceTo(new Coord(transformedX - c.length.d, transformedY + c.width)) <= rad);
            }
        }
        return false;
    }

    public boolean isColliding(CircleCollider c) {
        return og.distanceTo(c.og) <= c.rad + rad;
    }

    public ArrayList<Geometry> getEdgeContacts(PointCollider c) {
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        if (og.distanceTo(c.og) == rad) {
            ret.add(c.og);
        }
        return ret;
    }

    public ArrayList<Geometry> getEdgeContacts(LineCollider c) {
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        System.out.println("'getEdgeContacts(LineCollider c)' to be implemented");
        return ret;
    }

    public ArrayList<Geometry> getEdgeContacts(CircleCollider c) {
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        double dx = (og.x - c.og.x);
        double dy = (og.y - c.og.y);
        if (og.distanceTo(c.og) < Math.abs(rad - c.rad)) {
            return ret;
        } // should be guaranteed
        if (og.distanceTo(c.og) == 0) {
            if (rad == c.rad) ret.add(new Circle(og, rad));
            return ret;
        }
        if (og.y != c.og.y) {
            double a = Math.pow((dx * dx + dy * dy - c.rad * c.rad + rad * rad)
                    / (2 * dy), 2) - rad * rad
                    / (1 + Math.pow(dx / dy, 2));
            double b = (dx * dx + dy * dy - c.rad * c.rad + rad * rad) * dx / dy
                    / (dy + dx * dx / dy);
            double zero = Math.sqrt(b * b - 4 * a);
            if (Math.pow(b, 2) - 4 * a > 0) {
                ret.add(new Coord((-b + zero) / 2, (((-b + zero) / 2) * -2 * dx + dy * dy + dx * dx - c.rad * c.rad + rad * rad) / 2 * dy));
                ret.add(new Coord((-b - zero) / 2, (((-b - zero) / 2) * -2 * dx + dy * dy + dx * dx - c.rad * c.rad + rad * rad) / 2 * dy));
            } else if (Math.pow(b, 2) - 4 * a == 0) {
                ret.add(new Coord((-b + zero) / 2, (((-b + zero) / 2) * -2 * dx + dy * dy + dx * dx - c.rad * c.rad + rad * rad) / 2 * dy));
            }
        } else {
            dy = dx;
            dx = 0;
            double a = -(Math.pow((dy * dy - c.rad * c.rad + rad * rad) / (2 * dy), 2) - rad * rad);
            if (a > 0) {
                ret.add(new Coord((dy * dy - c.rad * c.rad + rad * rad) / 2 * dy, Math.sqrt(a)));
                ret.add(new Coord(((Coord) ret.get(0)).x, -((Coord) ret.get(0)).y));
            } else if (a == 0) {
                ret.add(new Coord((dy * dy - c.rad * c.rad + rad * rad) / 2 * dy, Math.sqrt(a)));
            }
        }
        return ret;
    }

    public ArrayList<Geometry> getEdgeContacts(RectCollider c) {
        ArrayList<Geometry> ret = new ArrayList<Geometry>();
        System.out.println("'getEdgeContacts(RectCollider c)' to be implemented");
        return ret;
    }

}
