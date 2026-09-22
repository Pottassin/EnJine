import enjine.Prog;
import enjine.util.*;
import enjine.GeoMath.*;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Tester extends Prog {

    public static void main(String[] args) {
        new Tester();
    }

    public Tester() {
        super("test",400,400);
        new Camera(0,0,400,400);
        new Debug();
    }

    public void testMethod(Object[] o) {
        String s = (String) o[0]; int i = (int) o[1];
        System.out.println(s + i);
    }

    public class Debug extends Script {

        LineCollider l;

        RectCollider r;

        LineCollider l2;

        boolean targ = true;
        public Debug() {
            l = new LineCollider(new Coord(.10,.10), new Coord(3.90, 3.90));
            l2 = new LineCollider(new Coord(3.90,.10), new Coord(2.00, 2.00));
            r = new RectCollider(new Coord(1.00, 2.00), new PCoord(0, 2.00), 1.00);
            KeyDetect.instance.addH(this);
            KeyDetect.instance.addP(this);
            CameraManager.addToDraw(this, 1);
            addToUpdate(this);
        }

        public void keyHeld(int e) {
            if (e == KeyEvent.VK_A) l.l.s.x-=.01;
            if (e == KeyEvent.VK_W) l.l.s.y-=.01;
            if (e == KeyEvent.VK_S) l.l.s.y+=.01;
            if (e == KeyEvent.VK_D) l.l.s.x+=.01;
            if (e == KeyEvent.VK_LEFT) l.l.e.x-=.01;
            if (e == KeyEvent.VK_UP) l.l.e.y-=.01;
            if (e == KeyEvent.VK_DOWN) l.l.e.y+=.01;
            if (e == KeyEvent.VK_RIGHT) l.l.e.x+=.01;
            l.l.scale(100,100);
            l.l.s.x = l.l.s.x();
            l.l.s.y = l.l.s.y();
            l.l.e.x = l.l.e.x();
            l.l.e.y = l.l.e.y();
            l.l.scale(.01,.01);
        }

        public void keyPressed(int e) {
            if (e == KeyEvent.VK_Q) targ = !targ;
        }

        public void update() {
            /*for (Line l : r.edges()) {
                System.out.println(l.altToString());
            }*/
            /*ArrayList<Geometry> temp;
            for (Line coll : r.edges()) {
                temp = l.getEdgeContacts(new LineCollider(coll));
                if (!temp.isEmpty()) System.out.println("coll");
            }*/
            /*for (Line coll : r.edges()) {

                if (l.isColliding(new LineCollider(coll))) System.out.println("coll");
            }*/
            //System.out.println(l.isColliding(r));
            l2.l.s = MouseDetect.instance.mousePos();
            l2.l.s.scale(.01, .01);
            System.out.println(l.l.s.toString() + " -> " + l.l.e.toString());
        }

        public void render(Graphics g) {
            g.setColor(Color.black);
            g.fillRect(0,0,400,400);
            g.setColor(Color.blue);
            //g.drawRect(r.og.x(), (int) (r.og.y - r.width), (int) r.width*2, (int) r.length.d);
            for (Line l : r.edges()) { l.debugRender2().run(new Object[]{g, Color.blue, new Coord(0,0), new Coord(1,1), new Coord(100,100)}); }
            g.setColor(Color.yellow);
            g.drawLine((int) (l.l.s.x*100), (int) (l.l.s.y*100), (int) (l.l.e.x*100), (int) (l.l.e.y*100));
            g.setColor(Color.cyan);
            g.drawLine((int) (l2.l.s.x*100), (int) (l2.l.s.y*100), (int) (l2.l.e.x*100), (int) (l2.l.e.y*100));
            g.setColor(Color.red);
            Coord collCast;
            Line lineCast;
            System.out.println(l.getEdgeContacts(r).size());
            for (Geometry coll : targ ? l.getEdgeContacts(r) : l2.getEdgeContacts(l)) {
                if (coll instanceof Coord) {
                    collCast = (Coord) coll;
                    //System.out.println(collCast);
                    g.fillOval((int) (collCast.x*100) - 3, (int) (collCast.y*100) - 3, 6, 6);
                }
                else if (coll instanceof Line) {
                    lineCast = (Line) coll;
                    //System.out.println(lineCast);
                    g.drawLine((int) (lineCast.s.x*100), (int) (lineCast.s.y*100), (int) (lineCast.e.x*100), (int) (lineCast.e.y*100));
                }
            }


        }
    }

}
