import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;

import enjine.GeoMath.*;
import enjine.util.*;
import enjine.Prog;

public class Pong extends Prog {

  public static void main(String[] args) {
    new Pong();
  }

  public Pong() {
    super("Pong", 500, 400);
    System.out.println("29");
    new Camera(0, 0, 500, 400);
    SwingUtilities.invokeLater(() -> new Background());
    SwingUtilities.invokeLater(() -> new ScoreDisplayer());
    SwingUtilities.invokeLater(() -> new Ball());
    Timer test = new Timer(10000, new BallSpawner()); test.start();
    SwingUtilities.invokeLater(() -> new Paddle(true));
    SwingUtilities.invokeLater(() -> new Paddle(false));
  }
  
  public class Ball extends Script {

    public float x;
    public float y;
    public float spdX;
    public float spdY;
    public int size = 5;

    public int startDelay = 1000;
    public boolean hasStarted = false;
    
    public Ball() {
      spdX = 4 * randomIntGen.nextInt(0, 2) - 2;
      spdY = randomIntGen.nextFloat(-1.0f, 1.0f);
      x = 250;
      y = 200;
      new StartBall(this);
      CameraManager.addToDraw(this, 1);
    }

    public class StartBall implements ActionListener {

      Timer t;
      Ball b;

      public StartBall(Ball b) {
        this.b = b;
        hasStarted = false;
        t = new Timer(startDelay, this);
        t.start();
      }

      public void actionPerformed(ActionEvent e) {
        toUpdate.add(b);
        hasStarted = true;
        t.stop();
      }
    }
    
    public void update() {
      x += spdX;
      y += spdY;
      if (Math.abs(x - Paddle.paddles[0].x) <= size + Paddle.paddles[0].width && Math.abs(y - Paddle.paddles[0].y) <= size + Paddle.paddles[0].height) {
        spdX = Math.abs(spdX) + .25f;
        spdY = spdY + (y - Paddle.paddles[0].y) * 2 / Paddle.paddles[0].height;
        spdY = Math.min(Math.abs(spdY), 6) * (int) Math.signum(spdY);
        Paddle.paddles[0].numHits++;
        System.out.println(Paddle.paddles[0].numHits);
      }
      else if (Math.abs(x - Paddle.paddles[1].x) <= size + Paddle.paddles[1].width && Math.abs(y - Paddle.paddles[1].y) <= size + Paddle.paddles[1].height) {
        spdX = -Math.abs(spdX) - .25f;
        spdY = spdY + (y - Paddle.paddles[1].y) * 2 / Paddle.paddles[1].height;
        spdY = Math.min(Math.abs(spdY), 6) * (int) Math.signum(spdY);
        Paddle.paddles[1].numHits++;
        System.out.println(Paddle.paddles[1].numHits);
      }
      if (x <= 0) {
        ScoreDisplayer.instance.rWs++;
        ScoreDisplayer.instance.displayScores();
        toUpdate.remove(this);
        //spdX = ScoreDisplayer.lWs > ScoreDisplayer.rws ? (ScoreDisplayer.lWs == ScoreDisplayer.rws ? 4 * randomIntGen.nextInt(0, 2) - 2 : -2) : 2;
        spdX = 2;
        spdY = randomIntGen.nextFloat(-2.0f, 2.0f);
        x = 75;
        y = 200;
        Paddle.paddles[0].y = 200;
        Paddle.paddles[1].y = 200;
        new StartBall(this);
        //spdX = -spdX;
      }
      if (x >= 500) {
        ScoreDisplayer.instance.lWs++;
        ScoreDisplayer.instance.displayScores();
        toUpdate.remove(this);
        spdX = -2;
        spdY = randomIntGen.nextFloat(-2.0f, 2.0f);
        x = 425;
        y = 200;
        Paddle.paddles[0].y = 200;
        Paddle.paddles[1].y = 200;
        new StartBall(this);
        //spdX = -spdX;
      }
      if (Math.abs(y - 200) >= 200) {
        spdY = -spdY;
      }
    }

    public void render(Graphics g) {
      //System.out.println("1");
      g.setColor(Color.black);
      g.fillOval((int) x - size, (int) y - size, size * 2, size * 2);
    }
  }

  public class Paddle extends Script {

    public static Paddle[] paddles = new Paddle[2];

    public int numHits = 0;

    boolean player;
    
    public float x;
    public float y;

    public int height = 20;
    public int width = 5;

    public float speed = 4;

    public Paddle(boolean p) {
      player = p;
      if (player) {
        paddles[0] = this;
        x = 40;
        y = 200;
      }
      else {
        paddles[1] = this;
        x = 460;
        y = 200;
      }
      CameraManager.addToDraw(this, 1);
      KeyDetect.instance.addH(this);
    }

    @Override
    public void render(Graphics g) {
      //System.out.println("2");
      g.setColor(Color.black);
      g.fillRect((int) x - width, (int) y - height, width * 2, height * 2);
    }

    @Override
    public void keyHeld(int e) { 
      //System.out.println(e);
      if (player) {
        //if (e == KeyEvent.VK_D) { posX++; }
        if (e == KeyEvent.VK_W && y - height > 0) { y -= speed; }
        //if (e == KeyEvent.VK_A) { posX--; }
        if (e == KeyEvent.VK_S && y + height < 400) { y += speed; }
      }
      else {
        if (e == KeyEvent.VK_UP && y - height > 0) { y -= speed; }
        if (e == KeyEvent.VK_DOWN && y + height < 400) { y += speed; }
      }
    }
  }

  public class Background extends Script {
    
    public Background() {
      CameraManager.addToDraw(this, 1);
    }
    
    @Override
    public void render(Graphics g) {
      //System.out.println("3");
      g.setColor(Color.gray);
      g.fillRect(248, 0, 4, 400);
      g.fillOval(200, 150, 100, 100);
      g.setColor(Color.white);
      g.fillOval(204, 154, 92, 92);
    }
  }

  public class ScoreDisplayer extends Script {

    public static ScoreDisplayer instance;

    public ScoreDisplayTimer timer;
    
    public int lWs;
    public int rWs;
    //public boolean 
    
    public ScoreDisplayer() {
      instance = this;
      timer = new ScoreDisplayTimer();
      //CameraManager.addToDraw(this, 1);
    }

    public void displayScores() {
      timer.start();
      CameraManager.addToDraw(this, 1);
    }

    public void hideScores() {
      timer.stop();
      CameraManager.removeToDraw(this, 1);
    }

    public class ScoreDisplayTimer implements ActionListener {
     
      Timer t;

      public ScoreDisplayTimer() {
        t = new Timer(2000, this);
      }

      public void start() { 
          t.start(); 
      }
      public void stop() { 
          t.stop();
      }

      public void actionPerformed(ActionEvent e) {
        ScoreDisplayer.instance.hideScores();
      }
    }
    
    @Override
    public void render(Graphics g) {
      //System.out.println("0");
      g.setColor(Color.blue);
      g.drawString(lWs + "", 233, 50);
      g.setColor(Color.black);
      g.drawString("/", 248, 50);
      g.setColor(Color.red);
      g.drawString(rWs + "", 263, 50);
    }
  }

  public class BallSpawner implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      SwingUtilities.invokeLater(() -> new Ball());
    }
  }
}