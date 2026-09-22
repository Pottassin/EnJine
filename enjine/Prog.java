package enjine;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.ArrayList;
//import javax.swing.LookAndFeel;
//import javax.swing.plaf.basic.BasicLookAndFeel;
import java.lang.Thread;

import enjine.util.*;
import enjine.GeoMath.*;
    
// look at Area class

// add scenes / some type of grouping

public class Prog {

    public static Random randomIntGen;

    public int frame = 0;
    public int frameRate = 25;

    public PQ<Script> toUpdate = new PQ<Script>();

    //public HashSet<Script> toUpdate = new HashSet<Script>();
    public ArrayList<Script> toAddU = new ArrayList<Script>();
    public ArrayList<Script> toRemU = new ArrayList<Script>();

    public ArrayList<UpdateThread> ts = new ArrayList<UpdateThread>();
    int numThreads = 10;

    Timer updateTimer;

    long prevT;

    public Prog() {
        new Screen("Unnamed Program", 400, 400);
        initialize();
    }

    public Prog(String name, int x, int y) {
        new Screen(name, x, y);
        initialize();
    }

    public void initialize() {
        for (int x = 0; x < numThreads; x++) {
            ts.add(new UpdateThread());
        }
        randomIntGen = new Random();
        updateTimer = new Timer(frameRate, new UpdateTimer());
        updateTimer.start();
        //new TimerThread();
        //new Timer(frameRate, () -> update(), true);
        prevT = System.currentTimeMillis();
    }

    public void update() {
        //System.out.println("start: " + (System.currentTimeMillis() - prevT));
        prevT = System.currentTimeMillis();
        frame++;
        //toUpdate.size();
        KeyDetect.instance.checkKeysHeld();
        //System.out.println("k: " + (System.currentTimeMillis() - prevT));
        prevT = System.currentTimeMillis();
        for (Script s : toUpdate) {
            if (s.sim)
                while (true) {
                    if (freeThread() != null) {
                        freeThread().set(() -> s.update()).run();
                        break;
                    }
                    Thread.onSpinWait();
                    //SwingUtilities.invokeLater(() -> s.update());
                }
        }
        //System.out.println("u: " + (System.currentTimeMillis() - prevT));
        prevT = System.currentTimeMillis();
        //System.out.println(Screen.instance.isFocused());
        Screen.instance.draw.repaint();
        for (Script s : toAddU) {
            toUpdate.add(s);
        }
        for (Script s : toRemU) {
            toUpdate.remove(s);
        }
        toAddU.clear();
        toRemU.clear();
        MouseDetect.instance.updateMouse();
        //System.out.println("e: " + (System.currentTimeMillis() - prevT));
        prevT = System.currentTimeMillis();
    }

    public UpdateThread freeThread() {
        for (UpdateThread ut : ts) {
            if (ut.isFree) {
                return ut;
            }
        }
        return null;
    }

    /*public class Timer {
        int curDur;
        int dur;
        Runnable run;
        boolean repeat;
        public Timer(int d, Runnable r) {
            dur = d;
            curDur = d;
            run = r;
            repeat = false;
            TimerThread.add(this);
        }
        public Timer(int d, Runnable r, boolean rep) {
            dur = d;
            curDur = d;
            run = r;
            repeat = rep;
            TimerThread.add(this);
        }
        public void check(int e) {
            curDur -= e;
            if (curDur <= 0) {
                run.run();
                if (!repeat)
                    TimerThread.instance.remove(this);
                else
                    curDur = dur;
            }
        }
    }

    public class TimerThread extends Thread {
        static TimerThread instance;
        ArrayList<Timer> ts = new ArrayList<Timer>();
        long time = -1;
        public TimerThread() { instance = this; SwingUtilities.invokeLater(() -> run()); }
        public static void add(Timer t) {
            System.out.println("added");
            instance.ts.add(t);
        }
        public static void remove(Timer t) {
            instance.ts.remove(t);
        }
        public void run() {
            while (true) {
                if (time != System.currentTimeMillis()) {
                    for (int x = ts.size() - 1; x >= 0; x--) {
                        ts.get(x).check((int) (System.currentTimeMillis() - time));
                    }
                    time = System.currentTimeMillis();
                }
                Thread.onSpinWait();
            }
            //try {
            //  join();
            //}
            //catch (InterruptedException i) {
            //    i.printStackTrace();
            //}
        }
    }*/

    public class UpdateTimer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            update();
        }

    }

    public void addToUpdate(Script s) {
        toAddU.add(s);
    }

    public void removeToUpdate(Script s) {
        toRemU.add(s);
    }

    public class UpdateThread extends Thread {
        Runnable r;
        boolean isFree = true;
        public UpdateThread() {}
        public UpdateThread set(Runnable r) {
            this.r = r;
            return this;
        }
        public void run() {
            isFree = false;
            r.run();
            try {
              join();
            }
            catch (InterruptedException i) {
                i.printStackTrace();
            }
            isFree = true;
            //return;
        }
    }

    public class Screen extends JFrame {

      public static Screen instance;
      public CameraManager draw;

      public Screen() {
          instance = this;
          addKeyListener(new KeyDetect());
          addMouseListener(new MouseDetect());
          draw = new CameraManager();
          add(draw);
          setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
          setSize(500, 400 /*+ 26*/);
          draw.setSize(500, 400 /*+ 26*/);
          setVisible(true);
          System.out.println("new screen");
      }

      public Screen(String name, int x, int y) {
          super(name);
          instance = this;
          addKeyListener(new KeyDetect());
          addMouseListener(new MouseDetect());
          draw = new CameraManager();
          add(draw);
          setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
          setSize(x, y /*+ 27*/);
          draw.setSize(x, y /*+ 27*/);
          setVisible(true);
          System.out.println("new screen");
      }

    }

    public class CameraManager extends JPanel {

        public CameraManager() {
            super();
        }

        public static void addToDraw(Script s) {
            for (Camera c : Camera.cams)
                c.toDraw.add(s);
        }

        public static void addToDraw(Script s, int cs) {
            for (int x = 0; x < Camera.cams.size(); x++) {
                if (cs % 2 == 1)
                    Camera.cams.get(x).toDraw.add(s);
                cs = cs >> 1;
            }
        }

        public static void removeToDraw(Script s) {
            for (Camera c : Camera.cams)
                c.toDraw.remove(s);
        }

        public static void removeToDraw(Script s, int cs) {
            for (int x = 0; x < Camera.cams.size(); x++) {
                if (cs % 2 == 1)
                    Camera.cams.get(x).toDraw.remove(s);
                cs = cs >> 1;
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (Camera c : Camera.cams)
                c.paint(g);
        }

    }

    public class Camera {
        public static ArrayList<Camera> cams = new ArrayList<Camera>();
        public ArrayList<Script> toDraw = new ArrayList<Script>();
        int x; int y; int w; int h;
        public Camera(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            cams.add(this);
        }

        public void paint(Graphics g) {
            g.setClip(x, y, w, h);
            for (Script s : toDraw) {
                if (s.sim)
                  s.render(g);
            }
        }
    }

    public class KeyDetect extends KeyAdapter {

      public static KeyDetect instance;

      public TreeSet<Integer> heldKeys = new TreeSet<Integer>();

      HashSet<Script> pressListen = new HashSet<Script>();
      HashSet<Script> holdListen = new HashSet<Script>();
      HashSet<Script> releaseListen = new HashSet<Script>();

      boolean isHandlingKeys = false;

      public KeyDetect() {
          instance = this;
      }

      public void keyPressed(KeyEvent e) {
          isHandlingKeys = true;
          if (heldKeys.contains(e.getKeyCode())) return;
          heldKeys.add(e.getKeyCode());
          for (Script s : pressListen) {
              s.keyPressed(e.getKeyCode());
          }
          isHandlingKeys = false;
      }

      public void checkKeysHeld() {
          isHandlingKeys = true;
          for (int key : heldKeys) {
              for (Script s : holdListen) {
                  s.keyHeld(key);
              }
          }
          isHandlingKeys = false;
      }

      public void keyReleased(KeyEvent e) {
          isHandlingKeys = true;
          heldKeys.remove(e.getKeyCode());
          for (Script s : releaseListen) {
              s.keyReleased(e.getKeyCode());
          }
          isHandlingKeys = false;
      }

      public void addP(Script s) { if (isHandlingKeys) SwingUtilities.invokeLater(() -> pressListen.add(s)); else pressListen.add(s); }
      public void addH(Script s) { if (isHandlingKeys) SwingUtilities.invokeLater(() -> holdListen.add(s)); else holdListen.add(s); }
      public void addR(Script s) { if (isHandlingKeys) SwingUtilities.invokeLater(() -> releaseListen.add(s)); else releaseListen.add(s); }

      public void removeP(Script s) { if (isHandlingKeys) SwingUtilities.invokeLater(() -> pressListen.remove(s)); else pressListen.remove(s); }
      public void removeH(Script s) { if (isHandlingKeys) SwingUtilities.invokeLater(() -> holdListen.remove(s)); else holdListen.remove(s); }
      public void removeR(Script s) { if (isHandlingKeys) SwingUtilities.invokeLater(() -> releaseListen.remove(s)); else releaseListen.remove(s); }
    }

    public class MouseDetect extends MouseAdapter {

        public static MouseDetect instance;

        //TreeSet<Integer> heldKeys = new TreeSet<Integer>();

        HashSet<Script> clickListen = new HashSet<Script>();
        HashSet<Script> pressListen = new HashSet<Script>();
        HashSet<Script> releaseListen = new HashSet<Script>();
        HashSet<Script> moveListen = new HashSet<Script>();

        public Coord prevMousePos = new Coord();

        public boolean isMouseDown = false;

        boolean isHandlingMouse = false;

        public int framesDown = 0;
        int cDir = -1;

        public MouseDetect() {
            instance = this;
        }

        public void updateMouse() {
            framesDown += cDir;
            prevMousePos = mousePos();
        }

        public Coord deltaMousePos() {
            return new Coord(mousePos().x - prevMousePos.x, mousePos().y - prevMousePos.y);
        }

        public void mouseMoved(MouseEvent e) {
            isHandlingMouse = true;
            for (Script s : moveListen) {
                s.mouseMoved(e);
            }
            isHandlingMouse = false;
        }

        public void mouseClicked(MouseEvent e) {
            isHandlingMouse = true;
            for (Script s : clickListen) {
                s.mouseClicked(e);
            }
            isHandlingMouse = false;
        }

        public void mousePressed(MouseEvent e) {
            isMouseDown = true;
            isHandlingMouse = true;
            for (Script s : pressListen) {
                s.mousePressed(e);
            }
            framesDown = 0;
            cDir = 1;
            isHandlingMouse = false;
        }

        public void mouseReleased(MouseEvent e) {
            isMouseDown = false;
            isHandlingMouse = true;
            for (Script s : releaseListen) {
                s.mouseReleased(e);
            }
            framesDown = 0;
            cDir = -1;
            isHandlingMouse = false;
        }

        //public Coord mousePos() { return new Coord(MouseInfo.getPointerInfo().getLocation()); }
        public Coord mousePos() {
            Coord c = new Coord(Screen.instance.getMousePosition());
            c.y -= 29.5f;
            c.x -= 6.25f;
            return c;
        }

        public void addM(Script s) { if (isHandlingMouse) SwingUtilities.invokeLater(() -> moveListen.add(s)); else moveListen.add(s); }
        public void addC(Script s) { if (isHandlingMouse) SwingUtilities.invokeLater(() -> clickListen.add(s)); else clickListen.add(s); }
        public void addP(Script s) { if (isHandlingMouse) SwingUtilities.invokeLater(() -> pressListen.add(s)); else pressListen.add(s); }
        public void addR(Script s) { if (isHandlingMouse) SwingUtilities.invokeLater(() -> releaseListen.add(s)); else releaseListen.add(s); }

        public void removeM(Script s) { if (isHandlingMouse) SwingUtilities.invokeLater(() -> moveListen.remove(s)); else moveListen.remove(s); }
        public void removeC(Script s) { if (isHandlingMouse) SwingUtilities.invokeLater(() -> clickListen.remove(s)); else clickListen.remove(s); }
        public void removeP(Script s) { if (isHandlingMouse) SwingUtilities.invokeLater(() -> pressListen.remove(s)); else pressListen.remove(s); }
        public void removeR(Script s) { if (isHandlingMouse) SwingUtilities.invokeLater(() -> releaseListen.remove(s)); else releaseListen.remove(s); }
    }

    public class Script {

        //public static HashSet<Script> allInstances = new HashSet<Script>();

        public boolean sim = true;

        /*public void Script() {
            allInstances.add(this);
        }*/

        public void start() {}
        public void destroy() {}
        public void keyPressed(int e) {}
        public void keyHeld(int e) {}
        public void keyReleased(int e) {}
        public void mouseMoved(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void update() { /*System.out.println("update");*/ }
        public void render(Graphics g) {
            g.setColor(Color.green);
            g.fillRect(0, 0, 100, 200);
            System.out.println("render");
        }

    }

    public class Group extends Script {
        public HashSet<Script> items = new HashSet<Script>();
    }

    //public class PhysicsBody {}

}

