import enjine.GeoMath.Coord;
import enjine.Prog;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.io.File;

public class ImageTest extends Prog {
    public BufferedImage i;
    public Image image;
    public Image image2;
    public TestFilter filter = new TestFilter();

    public static void main(String[] args) {
        new ImageTest();
    }

    public ImageTest() {
        super("Image Test", 500,500);
        new Camera(0, 0, 500, 500);
        try {
            i = ImageIO.read(new File("TestImage.png"));
        }
        catch (Exception e) {}
        new TestRender();
    }

    public class TestRender extends Script {
        Coord scale = new Coord(100, 100);
        public TestRender() {
            CameraManager.addToDraw(this);
            KeyDetect.instance.addP(this);
            FilteredImageSource fis = new FilteredImageSource(i.getScaledInstance(scale.x(), scale.y(), 0).getSource(), filter);
            image2 = Toolkit.getDefaultToolkit().createImage(fis);
            image = i.getScaledInstance(scale.x(), scale.y(), 0);
            System.out.println("\n\n");
        }

        public void keyPressed(int e) {
            if (e == KeyEvent.VK_UP) scale = scale.multiply(1.125);
            if (e == KeyEvent.VK_DOWN) scale = scale.divide(1.125);
            //WritableRaster r = i.getRaster();
            //r.setRect(scale.x(), scale.y(), i.getRaster());
            //i.setData(r);
        }

        public void render(Graphics g) {
            //g.setColor(Color.red);
            //g.fillRect(0, 0, 500, 500);
            g.drawImage(image, 0, 0, scale.x(), scale.y(), null);
            g.drawImage(image2, scale.x(), 0, scale.x(), scale.y(), null);
        }
    }

    public class TestFilter extends RGBImageFilter {
        @Override
        public int filterRGB(int x, int y, int rgb) {
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;
            System.out.println(r + ", " + g +", " + b);
            return (rgb & 0xff000000) | (Math.abs(r-g) << 16) | (g << 8) | b;
        }
    }
}
