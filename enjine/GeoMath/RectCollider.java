package enjine.GeoMath;

public class RectCollider extends Collider {
    public Coord og;
    public PCoord length;
    public double width;

    public RectCollider(Coord c, PCoord l, double w) {
        og = c;
        length = l;
        width = w;
    }

    public Line[] edges() {
        Line[] ret = new Line[4];
        Coord bl = new Coord(Math.sin(length.a) * width + og.x, Math.cos(length.a) * width + og.y);
        Coord tl = new Coord(-Math.sin(length.a) * width + og.x, -Math.cos(length.a) * width + og.y);
        Coord tr = new Coord((-Math.sin(length.a) * width + Math.cos(length.a) * length.d) + og.x, -((Math.cos(length.a) * width) + Math.sin(length.a) * length.d) + og.y);
        Coord br = new Coord((Math.sin(length.a) * width + Math.cos(length.a) * length.d) + og.x, ((Math.cos(length.a) * width) + Math.sin(length.a) * length.d) + og.y);
        ret[0] = (new Line(bl, tl));
        ret[1] = (new Line(tl, tr));
        ret[2] = (new Line(tr, br));
        ret[3] = (new Line(br, bl));
        return ret;
    }

    public LineCollider[] edgeColliders() {
        LineCollider[] ret = new LineCollider[4];
        Coord bl = new Coord(Math.sin(length.a) * width + og.x, -Math.cos(length.a) * width + og.y);
        Coord tl = new Coord(-Math.sin(length.a) * width + og.x, Math.cos(length.a) * width + og.y);
        Coord tr = new Coord((-Math.sin(length.a) * width + Math.cos(length.a) * length.d) + og.x, ((Math.cos(length.a) * width) + Math.sin(length.a) * length.d) + og.y);
        Coord br = new Coord((Math.sin(length.a) * width + Math.cos(length.a) * length.d) + og.x, -((Math.cos(length.a) * width) + Math.sin(length.a) * length.d) + og.y);
        ret[0] = (new LineCollider(bl, tl));
        ret[1] = (new LineCollider(tl, tr));
        ret[2] = (new LineCollider(tr, br));
        ret[3] = (new LineCollider(br, bl));
        return ret;
    }

    public boolean isColliding(PointCollider c) {
        double transformedX = (-Math.cos(length.a) * (og.x - c.og.x) + Math.sin(length.a) * (-og.y + c.og.y));
        double transformedY = (-Math.sin(length.a) * (og.x - c.og.x) - Math.cos(length.a) * (-og.y + c.og.y));
        if (length.d - transformedX < 0) {
            return false;
        }
        if (width - transformedY < 0) {
            return false;
        }
        if (transformedX < 0) {
            return false;
        }
        if (width + transformedY < 0) {
            return false;
        }
        return true;
    }

    public boolean isColliding(LineCollider c) {
        System.out.println("'isColliding(LineCollider c)' to be implemented");
        return c.isColliding(this);
    }

    public boolean isColliding(RectCollider c) {
        System.out.println("'isColliding(RectCollider c)' to be implemented");
        return false;
    }

    public boolean isColliding(CircleCollider c) {
        double transformedX = (-Math.cos(length.a) * (og.x - c.og.x) + Math.sin(length.a) * (-og.y + c.og.y));
        double transformedY = (-Math.sin(length.a) * (og.x - c.og.x) - Math.cos(length.a) * (-og.y + c.og.y));
        byte trues = (byte) 0;
        byte falses = (byte) 0;
        if (length.d - transformedX + c.rad < 0) {
            return false;
        } else if (length.d - transformedX + c.rad >= c.rad) {
            trues++;
        }
        if (width - transformedY + c.rad < 0) {
            return false;
        } else if (width - transformedY + c.rad >= c.rad) {
            trues++;
            falses += 1;
        }
        if (transformedX + c.rad < 0) {
            return false;
        } else if (transformedX + c.rad >= c.rad) {
            trues++;
            falses += 2;
        }
        if (width + transformedY + c.rad < 0) {
            return false;
        } else if (width + transformedY + c.rad >= c.rad) {
            trues++;
            falses += 4;
        }
        if (trues >= 3) {
            return true;
        } else {
            if (falses == 1) {
                if (c.rad - og.distanceTo(new Coord(transformedX - length.d, transformedY - width)) <= c.rad) {
                    return true;
                }
                return false;
            }
            if (falses == 3) {
                if (c.rad - og.distanceTo(new Coord(transformedX, transformedY - width)) <= c.rad) {
                    return true;
                }
                return false;
            }
            if (falses == 6) {
                if (c.rad - og.distanceTo(new Coord(transformedX, transformedY + width)) <= c.rad) {
                    return true;
                }
                return false;
            }
            if (falses == 4) {
                if (c.rad - og.distanceTo(new Coord(transformedX - length.d, transformedY + width)) <= c.rad) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }


}
