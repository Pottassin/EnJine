import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.ArrayList;
import java.lang.Math;

import enjine.GeoMath.*;
import enjine.util.*;
import enjine.Prog;

public class BallBoingler extends Prog {

    public static void main(String[] args) {
        new BallBoingler();
    }

    public BallBoingler() {
        new Camera(0, 0, 300, 600);
        new Camera(0, 0, 1000, 1000);
        new MouseIndicator();
        new Background();
        new PullIndicator();
        new Pin(200,300);
        new Pin(100,300);
        new Pin(50,250);
        new Pin(150,240, 20);
        new Pin(250,250);
        new Ball();
        //new Ball();
        //new Ball();
        for (Ball b : Ball.instances)
            b.start();
    }

    public class MouseIndicator extends Script {
        Coord pos = new Coord();
        public MouseIndicator() {
            addToUpdate(this);
            CameraManager.addToDraw(this, 2);
        }
        public void update() {
            pos = MouseDetect.instance.mousePos();
        }
        public void render(Graphics g) {
            g.setColor(Color.white);
            g.fillOval(pos.x()-2, pos.y()-2, 4, 4);
            g.setColor(Color.black);
            g.drawOval(pos.x()-2, pos.y()-2, 4, 4);
            
        }
    }

    public class Ball extends Script {
        static HashSet<Ball> instances = new HashSet<Ball>();

        static boolean ballGrabbed = false;

        Coord startPos = new Coord(150, 550);
        
        Coord pos = new Coord();
        int rad = 10;
        CircleCollider coll;
        Coord spd = new Coord();

        double bouncieness = .5;

        boolean isPulled = false;
        boolean isGrabbed = false;
        boolean callBall = false;

        Color c = new Color(255, 220, 30);

        Coord acc = new Coord();

        boolean bounced = false;

        public Ball() {
            instances.add(this);
            coll = new CircleCollider(pos, rad);
            CameraManager.addToDraw(this, 1);
            addToUpdate(this);
            MouseDetect.instance.addP(this);
            MouseDetect.instance.addR(this);
        }

        public void start() {
            pos.x = startPos.x;
            pos.y = startPos.y;
        }

        public void mousePressed(MouseEvent e) {
            if (-MouseDetect.instance.framesDown > 5) {
                callBall = false;
            }
            if (e.getButton() == 1) {
                if (coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()))) {
                    if (!ballGrabbed) {
                        ballGrabbed = true;
                        callBall = false;
                        isPulled = true;
                        PullIndicator.instance.set(pos);
                    }
                }
            }
            else if (e.getButton() == 3) {
                if (coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()))) {
                    if (!ballGrabbed) {
                        ballGrabbed = true;
                        callBall = false;
                        isGrabbed = true;
                        spd.x = 0;
                        spd.y = 0;
                    }
                }
                if (callBall) {
                    pos.x = MouseDetect.instance.mousePos().x;
                    pos.y = MouseDetect.instance.mousePos().y;
                    callBall = false;
                    isGrabbed = true;
                    spd.x = 0;
                    spd.y = 0;
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (isPulled) {
                ballGrabbed = false;
                spd.x = (pos.x - MouseDetect.instance.mousePos().x);
                spd.y = (pos.y - MouseDetect.instance.mousePos().y);
                PullIndicator.instance.close();
            }
            if (isGrabbed) {
                ballGrabbed = false;
                spd.x = MouseDetect.instance.deltaMousePos().x;
                spd.y = MouseDetect.instance.deltaMousePos().y;
            }
            if (!isGrabbed && !isPulled && MouseDetect.instance.framesDown <= 5) {
                if (callBall) {
                    pos.x = MouseDetect.instance.mousePos().x;
                    pos.y = MouseDetect.instance.mousePos().y;
                }
                callBall = !callBall;
            }
            else callBall = false;
            isGrabbed = false;
            isPulled = false;
        }

        public void update() {
            System.out.println(((590 - pos.y) * 10) + " / " + ((Math.pow(spd.x, 2) + Math.pow(spd.y, 2)) / 2.) + " -> " + ((590 - pos.y) * 10 + (Math.pow(spd.x, 2) + Math.pow(spd.y, 2)) / 2.));
            if (!isPulled && !isGrabbed) {
                
                if (spd.y < 50)
                    acc.y += 10;

                pos.x += spd.x * frameRate / 100.;
                pos.y += spd.y * frameRate / 100.;

                spd.x += acc.x * frameRate / 100.;
                spd.y += acc.y * frameRate / 100.;

                pos.x += acc.x * Math.pow(frameRate / 100., 2) / 2.;
                pos.y += acc.y * Math.pow(frameRate / 100., 2) / 2.;
                
                for (Pin p : Pin.ps) {
                    if (coll.isColliding(p.coll)) {
                        bounce(new PCoord(Math.atan2(pos.y-p.pos.y, pos.x-p.pos.x), 1), 1f);
                    }
                }
                if (pos.y < rad && spd.y < 0)
                    bounce(new PCoord(-Math.PI/2 /*+ randomIntGen.nextFloat(-.001f, .001f)*/, 1), bouncieness);
                if (pos.y > 600 - rad && spd.y > 0)
                    bounce(new PCoord(Math.PI/2 /*+ randomIntGen.nextFloat(-.001f, .001f)*/, 1), bouncieness);
                if (pos.x < rad && spd.x < 0)
                    bounce(new PCoord(0 /*randomIntGen.nextFloat(-.001f, .001f)*/, 1), bouncieness);
                if (pos.x > 300 - rad && spd.x > 0)
                    bounce(new PCoord(Math.PI /*+ randomIntGen.nextFloat(-.001f, .001f)*/, 1), bouncieness);
                
                
                
                acc.x = 0;
                acc.y = 0;
                //System.out.println(spd.mag());
            }
            else if (isGrabbed) {
                pos.x = MouseDetect.instance.mousePos().x;
                pos.y = MouseDetect.instance.mousePos().y;
            }
            //spd.x /= 1.00;
            //spd.y /= 1.00;
            bounced = false;
        }

        public void bounce(PCoord dir, double b) {
            bounced = true;
            Coord nSpd = new Coord();
            //+acc.mag()
            //nSpd.x = -(spd.mag()+acc.mag()*(float)(Math.round(Math.cos(2*dir.a-spd.angle())*1000000)/1000000f)) * (float)(Math.round(Math.cos(2*dir.a-spd.angle())*1000000)/1000000f) - (b-1)*spd.x;
            //nSpd.y = -(spd.mag()+acc.mag()*(float)(Math.round(Math.sin(2*dir.a-spd.angle())*1000000)/1000000f)) * (float)(Math.round(Math.sin(2*dir.a-spd.angle())*1000000)/1000000f) - (b-1)*spd.y;
            nSpd.x = -(spd.mag()) * Math.cos(2*dir.a-spd.angle()) - (Math.abs((b-1)*spd.x) + (2-b)*acc.x) * Math.cos(dir.a);
            nSpd.y = -(spd.mag()) * Math.sin(2*dir.a-spd.angle()) - (-Math.abs((b-1)*spd.y) + (2-b)*acc.y) * Math.sin(dir.a);
            spd = nSpd;
        }

        public void render(Graphics g) {
            g.setColor(c);
            g.fillOval(pos.x() - rad, pos.y() - rad, rad*2, rad*2);
        }
    }

    public class PullIndicator extends Script {
        static PullIndicator instance;
        
        Coord init = new Coord();
        Coord end = new Coord();

        public PullIndicator() {
            instance = this;
        }

        public void set(Coord c) {
            init = c;
            end = MouseDetect.instance.mousePos();
            addToUpdate(this);
            CameraManager.addToDraw(this, 1);
        }
        public void close() {
            removeToUpdate(this);
            CameraManager.removeToDraw(this, 1);
        }

        public void update() {
            end = MouseDetect.instance.mousePos();
        }

        public void render(Graphics g) {
            g.setColor(Color.white);
            g.fillOval(end.x() - 3, end.y() - 3, 6, 6);
        }
    }

    public class Pin extends Script {
        static ArrayList<Pin> ps = new ArrayList<Pin>();
        CircleCollider coll;
        Coord pos;
        int rad = 10;
        public Pin(int x, int y) {
            ps.add(this);
            pos = new Coord (x, y);
            coll = new CircleCollider(pos, rad);
            CameraManager.addToDraw(this, 1);
        }
        public Pin(int x, int y, int r) {
            ps.add(this);
            rad = r;
            pos = new Coord (x, y);
            coll = new CircleCollider(pos, rad);
            CameraManager.addToDraw(this, 1);
        }

        public void render(Graphics g) {
            g.setColor(Color.green);
            g.fillOval(pos.x() - rad, pos.y() - rad, rad*2, rad*2);
        }
    }

    public class Background extends Script {
        Color c = new Color(10, 0, 10);
        public Background() {
            CameraManager.addToDraw(this, 1);
        }

        public void render(Graphics g) {
            g.setColor(c);
            g.fillRect(0, 0, 300, 600);
            g.setColor(Color.red);
            g.drawLine(0, 550, 300, 550);
        }
    }
}