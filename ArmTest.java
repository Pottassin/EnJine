import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import enjine.GeoMath.*;
import enjine.util.*;
import enjine.Prog;

public class ArmTest extends Prog {

    public ArmTest() {
        super("Arm Test", 400, 400);
        SwingUtilities.invokeLater(() -> new Pointer());
    }

    public class Pointer extends Script {

        Coord pos = new Coord();
          
        public Pointer() {
            KeyDetect.instance.addH(this);
            CameraManager.addToDraw(this, 1);
            //toUpdate.add(this);
        }

        public void keyHeld(int e) {
          if (e == KeyEvent.VK_UP) { pos.y -= 2; }
          if (e == KeyEvent.VK_DOWN) { pos.y += 2; }
          if (e == KeyEvent.VK_LEFT) { pos.x -= 2; }
          if (e == KeyEvent.VK_RIGHT) { pos.x += 2; }
        }

        public void render(Graphics g) {
            g.setColor(Color.black);
            g.fillOval((int) pos.x - 2, (int) pos.y - 2, 4, 4);
        }
    }

    public class Arm extends Script {

        int numSeg = 3;
        public ArrayList<Segment> segs = new ArrayList<Segment>();

        public Arm() {
            CameraManager.addToDraw(this, 1);
            toUpdate.add(this);
            for (int x = 0; x < numSeg; x++) {
                segs.add(new Segment(x, 10.0f));
            }
        }
    }

    public class Segment {

        int i;
        Coord pos = new Coord();
        PCoord dir = new PCoord();
        
        public Segment(int index, float length) {
            i = index;
        }
    }
}