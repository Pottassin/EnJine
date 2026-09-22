import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import enjine.GeoMath.*;
import enjine.Prog;

import javax.imageio.ImageIO;

@SuppressWarnings("WrongPackageStatement")
public class Sokobanlike extends Prog {
    public static String directory = "Sokobanlike stuff/";
    public static String spriteDirectory = directory+"Sprites/";
    public static File levelReg = new File(directory+"LevelRegistry");
    public static TreeMap<Integer, Level> levels = new TreeMap<Integer, Level>();

    public static MainMenu mainMenu;
    public static LevelSelect levelSelect;
    public static CustomMenu customMenu;
    public static LevelMenu levelMenu;

    public enum Sprites {
        grass(retrieve("Grass")),
        wallLeft(retrieve("WallLeft")),
        wallRight(retrieve("WallRight")),
        wallFront(retrieve("WallFront")),
        wallTop(retrieve("WallTop")),
        wallPillar(retrieve("WallPillar")),
        chudLeft(retrieve("ChudLeft")),
        chudRight(retrieve("ChudRight")),
        chudUp(retrieve("ChudUp")),
        chudDown(retrieve("ChudDown")),
        box(retrieve("Box")),
        beltLeft(retrieve("BeltLeft")),
        beltRight(retrieve("BeltRight")),
        beltUp(retrieve("BeltUp")),
        beltDown(retrieve("BeltDown"));
        BufferedImage i;
        Sprites(BufferedImage image) { i = image; }

        static BufferedImage retrieve(String name) {
            try {
                return ImageIO.read(new File(spriteDirectory + name+".png"));
            }
            catch (IOException e) { throw new RuntimeException(e); }
        }
    }

    public static void main(String[] args) { new Sokobanlike(); }

    public Sokobanlike() {
        super("Sokoban-like", 600, 600);
        new Camera(0,0, 600, 600);
        levels = new TreeMap<Integer, Level>();
        loadLevels();
        mainMenu = new MainMenu();
        levelSelect = new LevelSelect();
        customMenu = new CustomMenu();
        levelMenu = new LevelMenu();
    }

    public void loadLevels() {
        //levels.clear();
        try {
            Scanner regReader = new Scanner(levelReg);
            String line;
            while (regReader.hasNext()) {
                line = regReader.next();
                try {
                    if (Integer.parseInt(line.substring(line.indexOf(":")+1)) > 0)
                        levels.put(Integer.parseInt(line.substring(line.indexOf(":")+1)), buildLevel(line.substring(0,line.indexOf(":"))));
                }
                catch (Exception e){
                    System.out.println("Error Building " +line.substring(0,line.indexOf(":")));
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            System.out.println("Level Loading Error");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public Level buildLevel(String name) throws LevelBuilderException, FileNotFoundException {
        Level builder = new Level();
        builder.file = new File(directory + name);
        Scanner levelReader = new Scanner(builder.file);
        String lrl = levelReader.next();
        String readT;
        Coord pos;
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                //System.out.print(pos + " : ");
                lrl = levelReader.next()+",";
                //System.out.println(lrl);
                builder.grid[pos.y()][pos.x()] = new Tile(pos);
                if (lrl.length() == 1) break;
                while (lrl.indexOf(',') != -1) {
                    if (lrl.contains("|") && lrl.indexOf("|") < lrl.indexOf(",")) {
                        tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf("|"))));
                        builder.grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoFromString(tt, lrl.substring(lrl.indexOf("|")+1, lrl.indexOf(",")))));
                    } else {
                        tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf(","))));
                        builder.grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoOfType(tt)));
                    }
                    lrl = lrl.substring(lrl.indexOf(',') + 1);

                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new LevelBuilderException();
        }
        return builder;
    }
    public Level buildLevel(File file) throws LevelBuilderException, FileNotFoundException {
        Level builder = new Level();
        builder.file = file;
        Scanner levelReader = new Scanner(file);
        String lrl = levelReader.next();
        Coord pos = new Coord();
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                //System.out.print(pos + " : ");
                lrl = levelReader.next()+",";
                //System.out.println(lrl);
                builder.grid[pos.y()][pos.x()] = new Tile(pos);
                if (lrl.length() == 1) break;
                while (lrl.indexOf(',') != -1) {
                    if (lrl.contains("|") && lrl.indexOf("|") < lrl.indexOf(",")) {
                        tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf("|"))));
                        builder.grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoFromString(tt, lrl.substring(lrl.indexOf("|")+1, lrl.indexOf(",")))));
                    } else {
                        tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf(","))));
                        builder.grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoOfType(tt)));
                    }
                    lrl = lrl.substring(lrl.indexOf(',') + 1);

                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new LevelBuilderException();
        }
        return builder;
    }
    public void buildEditor() throws LevelBuilderException, FileNotFoundException {
        LevelEditor builder;
        if (LevelEditor.instance == null) {
            builder = new LevelEditor();
            LevelEditor.instance = builder;
            builder.file = new File(directory+"LevelEditor");
            /*clearEditor();
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    System.out.println(LevelEditor.instance.grid[y][x].types.get(0));
                }
            }
            builder.start();
            return;*/
        }
        else {
            builder = LevelEditor.instance;
        }
        Scanner levelReader = new Scanner(builder.file);
        String lrl = levelReader.next();
        Coord pos = new Coord();
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                //System.out.print(pos + " : ");
                lrl = levelReader.next()+",";
                //System.out.println(lrl);
                builder.grid[pos.y()][pos.x()] = new Tile(pos);
                if (lrl.length() == 1) break;
                while (lrl.indexOf(',') != -1) {
                    if (lrl.contains("|") && lrl.indexOf("|") < lrl.indexOf(",")) {
                        tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf("|"))));
                        builder.grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoFromString(tt, lrl.substring(lrl.indexOf("|")+1, lrl.indexOf(",")))));
                    } else {
                        tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf(","))));
                        builder.grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoOfType(tt)));
                    }
                    lrl = lrl.substring(lrl.indexOf(',') + 1);

                }
            }
        }
        catch (Exception e) {
            System.out.println("Error Building Editor: "+e.getMessage()+", Attempting Default");
            builder = new LevelEditor();
            LevelEditor.instance = builder;
            builder.file = new File(directory+"LevelEditor");
            clearEditor();
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    System.out.println(LevelEditor.instance.grid[y][x].types.get(0));
                }
            }
            builder.start();
            throw new LevelBuilderException();
        }
        builder.start();
    }
    public void buildEditor(String s) throws LevelBuilderException, FileNotFoundException {
        LevelEditor builder;
        File toLoad = new File(directory + s);
        if (LevelEditor.instance == null) {
            builder = new LevelEditor();
            LevelEditor.instance = builder;
            builder.file = new File(directory + "LevelEditor");
            /*clearEditor();
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    System.out.println(LevelEditor.instance.grid[y][x].types.get(0));
                }
            }
            builder.start();
            return;*/
        }
        else {
            builder = LevelEditor.instance;
        }
        Scanner levelReader = new Scanner(toLoad);
        String lrl = levelReader.next();
        Coord pos = new Coord();
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                //System.out.print(pos + " : ");
                lrl = levelReader.next()+",";
                //System.out.println(lrl);
                builder.grid[pos.y()][pos.x()] = new Tile(pos);
                if (lrl.length() == 1) break;
                while (lrl.indexOf(',') != -1) {
                    if (lrl.contains("|") && lrl.indexOf("|") < lrl.indexOf(",")) {
                        tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf("|"))));
                        builder.grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoFromString(tt, lrl.substring(lrl.indexOf("|")+1, lrl.indexOf(",")))));
                    } else {
                        tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf(","))));
                        builder.grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoOfType(tt)));
                    }
                    lrl = lrl.substring(lrl.indexOf(',') + 1);

                }
            }
        }
        catch (Exception e) {
            System.out.println("Error Building Editor: "+e.getMessage()+", Attempting Default");
            builder = new LevelEditor();
            LevelEditor.instance = builder;
            builder.file = new File(directory + "LevelEditor");
            clearEditor();
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    System.out.println(LevelEditor.instance.grid[y][x].types.get(0));
                }
            }
            builder.start();
            throw new LevelBuilderException();
        }
        builder.start();
    }
    public void buildEditor(File f) throws LevelBuilderException, FileNotFoundException {
        LevelEditor builder;
        if (LevelEditor.instance == null) {
            builder = new LevelEditor();
            LevelEditor.instance = builder;
            builder.file = new File(directory + "LevelEditor");
            /*clearEditor();
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    System.out.println(LevelEditor.instance.grid[y][x].types.get(0));
                }
            }
            builder.start();
            return;*/
        }
        else {
            builder = LevelEditor.instance;
        }
        Scanner levelReader = new Scanner(f);
        String lrl = levelReader.next();
        Coord pos = new Coord();
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                //System.out.print(pos + " : ");
                lrl = levelReader.next()+",";
                //System.out.println(lrl);
                builder.grid[pos.y()][pos.x()] = new Tile(pos);
                if (lrl.length() == 1) break;
                while (lrl.indexOf(',') != -1) {
                    if (lrl.contains("|") && lrl.indexOf("|") < lrl.indexOf(",")) {
                        tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf("|"))));
                        builder.grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoFromString(tt, lrl.substring(lrl.indexOf("|")+1, lrl.indexOf(",")))));
                    } else {
                        tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf(","))));
                        builder.grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoOfType(tt)));
                    }
                    lrl = lrl.substring(lrl.indexOf(',') + 1);

                }
            }
        }
        catch (Exception e) {
            System.out.println("Error Building Editor: "+e.getMessage()+", Attempting Default");
            builder = new LevelEditor();
            LevelEditor.instance = builder;
            builder.file = new File(directory + "LevelEditor");
            clearEditor();
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    System.out.println(LevelEditor.instance.grid[y][x].types.get(0));
                }
            }
            builder.start();
            throw new LevelBuilderException();
        }
        builder.start();
    }
    public void clearEditor() throws LevelBuilderException, FileNotFoundException {
        LevelEditor.instance.grid = new Tile[5][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                LevelEditor.instance.grid[y][x] = new Tile(new Coord(x, y), new ArrayList<TypeInst>(List.of(new TypeInst(TileType.empty, TypeInfo.infoOfType(TileType.empty)))));
            }
        }
    }
    public void saveEditor() {
        try {
            FileWriter fw = new FileWriter(LevelEditor.instance.file, false);
            PrintWriter pw = new PrintWriter(fw);
            String saveDat = "";
            saveDat += LevelEditor.instance.grid[0].length + ":" + LevelEditor.instance.grid.length + "\n";
            for (Tile[] tileLine : LevelEditor.instance.grid) {
                for (Tile tile : tileLine) {
                    saveDat += tile.pos.x() + ":" + tile.pos.y() + "\n";
                    for (int x = 0; x < tile.types.size(); x++) {
                        saveDat += tile.types.get(x).tt.id+TypeInfo.infoToString(tile.types.get(x).ti);
                        if (x != tile.types.size() - 1)
                            saveDat += ",";
                    }
                    saveDat += "\n";
                }
            }
            pw.append(saveDat);
            fw.close();
            pw.close();
        }
        catch (Exception e) {
            System.out.println("Failed to Save Custom Level");
            e.printStackTrace();
        }
    }
    public void saveEditorAs(String name) {
        try {
            File newF = new File(directory + name);
            //if (!newF.exists())
            newF.createNewFile();
            FileWriter fw = new FileWriter(newF, false);
            PrintWriter pw = new PrintWriter(fw);
            String saveDat = "";
            saveDat += LevelEditor.instance.grid[0].length + ":" + LevelEditor.instance.grid.length + "\n";
            for (Tile[] tileLine : LevelEditor.instance.grid) {
                for (Tile tile : tileLine) {
                    saveDat += tile.pos.x() + ":" + tile.pos.y() + "\n";
                    for (int x = 0; x < tile.types.size(); x++) {
                        saveDat += tile.types.get(x).tt.id+TypeInfo.infoToString(tile.types.get(x).ti);;
                        if (x != tile.types.size() - 1)
                            saveDat += ",";
                    }
                    saveDat += "\n";
                }
            }
            pw.append(saveDat);
            fw.close();
            pw.close();
        }
        catch (Exception e) {
            System.out.println("Failed to Save Custom Level");
            e.printStackTrace();
        }
    }
    public static class LevelBuilderException extends RuntimeException {
        public LevelBuilderException() {
            super("invalid level");
        }
        public LevelBuilderException(String level) {
            super("invalid level " + level);
        }
        public LevelBuilderException(String level, Exception src) {
            super("invalid level" + level, src);
        }
    }

    public class MainMenu extends Script {

       StartButton start;
       CustomButton custom;
        public MainMenu() {
            start = new StartButton();
            custom = new CustomButton();
            start();
        }

        public void start() {
            CameraManager.addToDraw(this, 1);
            start.start();
            custom.start();
        }

        public void destroy() {
            start.destroy();
            custom.destroy();
            CameraManager.removeToDraw(this, 1);
        }

        public void render(Graphics g) {
            g.setColor(Color.gray);
            //g.fillRect(start.coll.og.x(), start.coll.og.y() + (int) start.coll.width + 30, 200, 310);
        }

        public class StartButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 50), new PCoord(0, 150), 25);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            boolean isSelected = false;
            boolean isPressed = false;

            //public StartButton() {}

            public void start() {
                CameraManager.addToDraw(this, 1);
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                CameraManager.removeToDraw(this, 1);
            }

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) { mainMenu.destroy(); levelSelect.start(); } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(StartButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(StartButton.ColorState.hover.c);
                else
                    g.setColor(StartButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Level Select", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }
        public class CustomButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 110), new PCoord(0, 150), 25);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;

                ColorState(Color c) {
                    this.c = c;
                }
            }

            boolean isSelected = false;
            boolean isPressed = false;

            //public CustomButton() {}

            public void start() {
                CameraManager.addToDraw(this, 1);
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                CameraManager.removeToDraw(this, 1);
            }

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) {
                if (isSelected) isPressed = true;
            }

            public void mouseReleased(MouseEvent e) {
                if (isPressed) { mainMenu.destroy(); customMenu.start(); }
            }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(CustomButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(CustomButton.ColorState.hover.c);
                else
                    g.setColor(CustomButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Custom Levels", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }
    }
    public class CustomMenu extends Script {

        EditorButton editor;
        LoadButton load;
        BackButton back;
        public CustomMenu() {
            editor = new EditorButton();
            load = new LoadButton();
            back = new BackButton();
        }

        public void start() {
            CameraManager.addToDraw(this, 1);
            editor.start();
            load.start();
            back.start();
        }

        public void destroy() {
            editor.destroy();
            load.destroy();
            back.destroy();
            CameraManager.removeToDraw(this, 1);
        }

        public void render(Graphics g) {
            g.setColor(Color.gray);
            //g.fillRect(start.coll.og.x(), start.coll.og.y() + (int) start.coll.width + 30, 200, 310);
        }

        public class EditorButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 50), new PCoord(0, 150), 25);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            boolean isSelected = false;
            boolean isPressed = false;

            //public StartButton() {}

            public void start() {
                CameraManager.addToDraw(this, 1);
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                CameraManager.removeToDraw(this, 1);
            }

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) {
                customMenu.destroy();
                try { buildEditor(); }
                catch (Exception ex) { System.out.println("Gimme a backup option"); ex.printStackTrace(); }
            } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(EditorButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(EditorButton.ColorState.hover.c);
                else
                    g.setColor(EditorButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Level Editor", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }
        public class LoadButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 110), new PCoord(0, 150), 25);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;

                ColorState(Color c) {
                    this.c = c;
                }
            }

            boolean isSelected = false;
            boolean isPressed = false;

            public TextField txt;

            public LoadButton() { txt = new TextField(); }

            public void start() {
                CameraManager.addToDraw(this, 1);
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                CameraManager.removeToDraw(this, 1);
                txt.destroy();
            }

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) {
                if (isSelected) isPressed = true;
            }

            public void mouseReleased(MouseEvent e) {
                if (isPressed) {
                    if (txt.sim)
                        txt.destroy();
                    else
                        txt.start();
                }
            }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(LoadButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(LoadButton.ColorState.hover.c);
                else
                    g.setColor(LoadButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Load Level", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }

            public class TextField extends Script {
                public String field = "";
                public TextField() { sim = false; }
                public void start() {
                    System.out.println("open field");
                    field = "";
                    sim = true;
                    //CameraManager.addToDraw(this, 1);
                    KeyDetect.instance.addP(this);
                }

                public void destroy() {
                    sim = false;
                    //CameraManager.removeToDraw(this, 1);
                    KeyDetect.instance.removeP(this);
                }

                public void keyPressed(int e) {

                    if (e == KeyEvent.VK_BACK_SPACE) {
                        if (field.length() > 0)
                            field = field.substring(0, field.length()-1);
                        System.out.println(field);
                    }
                    else if (e == KeyEvent.VK_ESCAPE) {
                        destroy();
                        System.out.println("closing field");
                    }
                    else if (e == KeyEvent.VK_ENTER) {
                        try {
                            buildLevel(field).start();
                            System.out.println("Loading Level " + field);
                            customMenu.destroy();
                        }
                        catch (Exception x) {
                            System.out.println("Error Loading Level " + field);
                        }
                    }
                    else {
                        if (KeyEvent.getKeyText(e).length() > 1) return;
                        if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_SHIFT)) {
                            System.out.println("adding "+(char) e);
                            field += (char) e;
                            System.out.println(field);
                            return;
                        }
                        System.out.println("adding "+(""+(char) e).toLowerCase());
                        field += (""+(char) e).toLowerCase();
                        System.out.println(field);
                    }
                }
            }
        }
        public class BackButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 170), new PCoord(0, 150), 25);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;

                ColorState(Color c) {
                    this.c = c;
                }
            }

            boolean isSelected = false;
            boolean isPressed = false;

            //public CustomButton() {}

            public void start() {
                CameraManager.addToDraw(this, 1);
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                CameraManager.removeToDraw(this, 1);
            }

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) {
                if (isSelected) isPressed = true;
            }

            public void mouseReleased(MouseEvent e) {
                if (isPressed) {
                    customMenu.destroy();
                    mainMenu.start();
                }
            }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(BackButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(BackButton.ColorState.hover.c);
                else
                    g.setColor(BackButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Back to Main Menu", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }
    }

    public class Level extends Script {
        public static Level activeLevel;
        public File file;
        public Tile[][] grid;
        public ArrayList<GridModRequest> moveQueue = new ArrayList<GridModRequest>();

        /*

        probably need a check queue too

        */
        public ArrayList<Input> inputStack;
        public boolean isPaused = false;

        public void start() {
            activeLevel = this;
            CameraManager.addToDraw(this, 1);
            KeyDetect.instance.addP(this);
            //if (activeLevel != LevelEditor.instance)
                //reload();
        }

        public void destroy() {
            CameraManager.removeToDraw(this, 1);
            KeyDetect.instance.removeP(this);
            activeLevel = null;
        }

        public void keyPressed(int e) {
            if (e == Input.esc.key) {
                isPaused = !isPaused;
                if (isPaused)
                    levelMenu.start();
                else
                    levelMenu.destroy();
            }
            if (isPaused || Level.activeLevel == LevelEditor.instance) return;
            for (int x = 0; x < grid[0].length; x++) {
                for (int y = 0; y < grid.length; y++) {
                    if (grid[y][x] == null) break;
                    for (TypeInst t : grid[y][x].types) {
                        t.tt.playerInput.apply((new Context(null, null, null)).newInputContext(this, grid[y][x], t, Input.inputOf(e)));
                    }
                }
            }
            System.out.println("First Check ______________/");
            resolveMoves();
            for (int x = 0; x < grid[0].length; x++) {
                for (int y = 0; y < grid.length; y++) {
                    if (grid[y][x] == null) break;
                    for (TypeInst t : grid[y][x].types) {
                        t.tt.playerInputEnd.apply((new Context(null, null, null)).newInputContext(this, grid[y][x], t, Input.inputOf(e)));
                    }
                }
            }
            System.out.println("Second Check ______________/");
            resolveMoves();
            System.out.println("\n");
        }

        public void resolveMoves() {
            TreeMap<Coord, GridModRequest> coords = new TreeMap<Coord, GridModRequest>();
            ArrayList<ArrayList<GridModRequest>> gmrs = new ArrayList<ArrayList<GridModRequest>>();
            //ArrayList<ArrayList<GridModRequest>> gmrsHolder = new ArrayList<ArrayList<GridModRequest>>();
            int iter = 0;
            while (true) {
                iter++;
                System.out.println("resolve iteration " + iter);
                for (GridModRequest r : moveQueue) {
                    pullRequests(r, gmrs, 0);
                }
                for (ArrayList<GridModRequest> rList : gmrs) {
                    for (GridModRequest r : rList) {
                        /*

                        vet moved typeInst's requests with block check

                        */
                        if (r.isConsumed) {
                            if (coords.containsKey(r.move.srcTile.pos)) {
                                if (coords.get(r.move.srcTile.pos) != null)
                                    coords.get(r.move.srcTile.pos).consume();
                                coords.put(r.move.srcTile.pos, null);
                            }
                            else {
                                coords.put(r.move.srcTile.pos, r);
                            }
                            continue;
                        }
                        if (coords.containsKey(r.move.tile.pos)) {
                            if (coords.get(r.move.tile.pos) != null)
                                coords.get(r.move.tile.pos).consume();
                            r.consume();
                            coords.put(r.move.tile.pos, null);
                        }
                        else {
                            coords.put(r.move.tile.pos, r);
                        }
                    }
                }
                moveQueue.clear();
                for (int d = gmrs.size()-1; d>=0; d--) {
                    for (GridModRequest r : gmrs.get(d)) {
                        //System.out.println(r.fromPos +" -> "+ r.toPos + ": " + coords.get(r.toPos));
                        if (r.move.depth < 0) continue;
                        if (r.isConsumed) {
                            if (r.move.tile.pos != null) {
                                r.move.type.tt.blocked.apply(r.move);
                            }
                        }
                        else {
                            //System.out.println(r.type + " move");
                            //System.out.print(grid[r.fromPos.y()][r.fromPos.x()].types + " -> ");
                            if (r.move.srcTile.pos != null) {
                                if (grid[r.move.srcTile.pos.y()][r.move.srcTile.pos.x()].types.contains(r.move.srcType)) {
                                    grid[r.move.srcTile.pos.y()][r.move.srcTile.pos.x()].types.remove(r.move.srcType);
                                    grid[r.move.tile.pos.y()][r.move.tile.pos.x()].types.add(r.move.srcType);
                                    
                                    grid[r.move.srcTile.pos.y()][r.move.srcTile.pos.x()].exit(r.move);
                                    grid[r.move.tile.pos.y()][r.move.tile.pos.x()].push(r.move);
                                } else {
                                    /*System.out.println("expected " + r.move.type.tt + " at " + r.move.srcTile.pos + ", found: ");
                                    for (TypeInst ti : grid[r.move.srcTile.pos.y()][r.move.srcTile.pos.x()].types)
                                        System.out.print(ti.tt + ", ");
                                    System.out.println();*/
                                    System.out.println("JUKED");
                                    r.consume();
                                    continue;
                                }
                            } else {
                                grid[r.move.tile.pos.y()][r.move.tile.pos.x()].types.add(r.move.srcType);
                            }
                            if (r.cascade != null) {
                                if (r.cascade.apply(r.cascadeContext)) addMove(r.cascadeContext.request);
                            }
                            else if (r.cascadeContext != null)  {
                                if (r.cascadeContext.tile.attemptPush(r.cascadeContext))
                                    addMove(r.cascadeContext.request);
                            }
                        }
                    }
                }
                gmrs.clear();
                if (moveQueue.isEmpty()) { /*System.out.println("No Cascade :(");*/ break; }
            }
            //System.out.println("Ending Move Resolving\n__________________\n");
        }
        public void pullRequests(GridModRequest r, ArrayList<ArrayList<GridModRequest>> plane, int d) {
            if (plane.size() <= d)
                plane.add(new ArrayList<GridModRequest>(List.of(r)));
            else
                plane.get(d).add(r);
            for (GridModRequest gmr : r.queue)
                pullRequests(gmr, plane, d+1);
        }

        public void render(Graphics g) {
            RenderContext rc;
            for (int x = 0; x < grid[0].length; x++) {
                for (int y = 0; y < grid.length; y++) {
                    rc = new RenderContext(this, grid[y][x], null, null, null);
                    if (grid[y][x] != null)
                        if (grid[y][x].types.size() > 0) {
                            if (grid[y][x].types.get(0) == null) break;
                            for (TypeInst ti : grid[y][x].types) {
                                rc.srcType = ti;
                                ti.tt.render.accept(g, rc);
                            }
                        }
                }
            }
        }

        public void reload() {
            try {
                //Level builder = new Level();
                Scanner levelReader = new Scanner(file);
                String lrl = levelReader.next();
                Coord pos;
                TileType tt;
                try {
                    grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
                    while (levelReader.hasNext()) {
                        lrl = levelReader.next();
                        pos = new Coord();
                        pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                        pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                        //System.out.print(pos + " : ");
                        lrl = levelReader.next()+",";
                        grid[pos.y()][pos.x()] = new Tile(pos);
                        if (lrl.length() == 1) break;
                        while (lrl.indexOf(',') != -1) {
                            if (lrl.contains("|") && lrl.indexOf("|") < lrl.indexOf(",")) {
                                tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf("|"))));
                                grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoFromString(tt, lrl.substring(lrl.indexOf("|")+1, lrl.indexOf(",")))));
                            } else {
                                tt = TileType.tileTypeOf(Integer.parseInt(lrl.substring(0, lrl.indexOf(","))));
                                grid[pos.y()][pos.x()].types.add(new TypeInst(tt, TypeInfo.infoOfType(tt)));
                            }
                            lrl = lrl.substring(lrl.indexOf(',') + 1);

                        }
                    }
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                    throw new LevelBuilderException();
                }
            }
            catch (Exception e) {
                System.out.println("Error Reloading File");
                e.printStackTrace();
            }
        }

        public GridModRequest requestMove(MoveContext mc) {
            GridModRequest ret = new GridModRequest(mc);
            mc.request = ret;
            moveQueue.add(ret);
            return ret;
        }
        public GridModRequest requestMove(MoveContext mc, ArrayList<GridModRequest> gmr) {
            GridModRequest ret = new GridModRequest(mc, null, null, gmr);
            mc.request = ret;
            moveQueue.add(ret);
            return ret;
        }
        public GridModRequest getMove(MoveContext mc) {
            GridModRequest ret = new GridModRequest(mc);
            mc.request = ret;
            return ret;
        }
        public GridModRequest getMove(MoveContext mc, ArrayList<GridModRequest> gmr) {
            GridModRequest ret = new GridModRequest(mc, null, null, gmr);
            mc.request = ret;
            return ret;
        }
        public void addMove(GridModRequest gmr) {
            moveQueue.add(gmr);
        }
    }
    public class LevelEditor extends Level {
        public static LevelEditor instance;
        public Tile copied;
        public LevelEditor() {
            new TileDisplay();
            new TileInventory();
        }

        public void start() {
            super.start();
            instance = this;
            MouseDetect.instance.addP(this);
            TileInventory.instance.start();
            KeyDetect.instance.addP(this);
        }

        public void destroy() {
            super.destroy();
            MouseDetect.instance.removeP(this);
            TileInventory.instance.destroy();
            KeyDetect.instance.removeP(this);
            if (TileDisplay.instance.isOpen)
                TileDisplay.instance.destroy();
        }

        public void mousePressed(MouseEvent e) {
            Coord c = MouseDetect.instance.mousePos();
            c.x /= 20;
            c.y /= 20;
            if (c.x < 0 || c.y < 0) return;
            if (c.x >= grid[0].length || c.y >= grid.length) return;
            if (grid[c.y()][c.x()] != null)
                TileDisplay.instance.openWith(grid[c.y()][c.x()]);
        }

        public void keyPressed(int e) {
            super.keyPressed(e);
            if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_CONTROL)) {
                if (e == KeyEvent.VK_UP) {
                    if (grid.length > 1) {
                        Tile[][] nGrid = new Tile[grid.length - 1][grid[0].length];
                        for (int x = 0; x < nGrid[0].length; x++) {
                            for (int y = 0; y < nGrid.length; y++) {
                                nGrid[y][x] = grid[y][x];
                            }
                        }
                        grid = nGrid;
                    }
                }
                if (e == KeyEvent.VK_DOWN) {
                    Tile[][] nGrid = new Tile[grid.length + 1][grid[0].length];
                    for (int x = 0; x < nGrid[0].length; x++) {
                        for (int y = 0; y < grid.length; y++) {
                            nGrid[y][x] = grid[y][x];
                        }
                        nGrid[grid.length][x] = new Tile(new Coord(x, grid.length), new ArrayList<TypeInst>(List.of(new TypeInst(TileType.empty, TypeInfo.infoOfType(TileType.empty)))));
                    }
                    grid = nGrid;
                }
                if (e == KeyEvent.VK_LEFT) {
                    if (grid[0].length > 1) {
                        Tile[][] nGrid = new Tile[grid.length][grid[0].length-1];
                        for (int x = 0; x < nGrid[0].length; x++) {
                            for (int y = 0; y < nGrid.length; y++) {
                                nGrid[y][x] = grid[y][x];
                            }
                        }
                        grid = nGrid;
                    }
                }
                if (e == KeyEvent.VK_RIGHT) {
                    Tile[][] nGrid = new Tile[grid.length][grid[0].length+1];
                    for (int x = 0; x < nGrid[0].length; x++) {
                        for (int y = 0; y < nGrid.length; y++) {
                            if (x >= grid[0].length) {
                                nGrid[y][x] = new Tile(new Coord(x, y), new ArrayList<TypeInst>(List.of(new TypeInst(TileType.empty, TypeInfo.infoOfType(TileType.empty)))));
                            }
                            else
                                nGrid[y][x] = grid[y][x];
                        }
                    }
                    grid = nGrid;
                }
                if (e == KeyEvent.VK_C) {
                    if (TileDisplay.instance.t != null && TileDisplay.instance.isOpen)
                        copied = TileDisplay.instance.t;
                }
                if (e == KeyEvent.VK_V) {
                    if (copied != null)
                        if (TileDisplay.instance.t != null && TileDisplay.instance.isOpen)
                            TileDisplay.instance.t.types = new ArrayList<TypeInst>(copied.types);
                }
                if (e == KeyEvent.VK_X) {
                    copied = null;
                }
            }
        }

        public void render(Graphics g) {
            super.render(g);
            if (TileDisplay.instance.t == null || !TileDisplay.instance.isOpen) return;
            if (copied != null) {
                g.setColor(Color.orange);
                g.drawRect(copied.pos.x()*20+1, copied.pos.y()*20+1, 18, 18);
                g.drawRect(copied.pos.x()*20, copied.pos.y()*20, 20, 20);
                g.drawLine(copied.pos.x()*20+1, copied.pos.y()*20+1, copied.pos.x()*20+5, copied.pos.y()*20+5);
                g.drawLine(copied.pos.x()*20+19, copied.pos.y()*20+1, copied.pos.x()*20+15, copied.pos.y()*20+5);
                g.drawLine(copied.pos.x()*20+1, copied.pos.y()*20+19, copied.pos.x()*20+5, copied.pos.y()*20+15);
                g.drawLine(copied.pos.x()*20+19, copied.pos.y()*20+19, copied.pos.x()*20+15, copied.pos.y()*20+15);
            }
            g.setColor(Color.black);
            g.drawRect(TileDisplay.instance.t.pos.x()*20, TileDisplay.instance.t.pos.y()*20, 20, 20);
        }

        public class TileDisplay extends Script {
            public static TileDisplay instance;
            public Coord pos = new Coord(0,0);
            public Tile t;
            public RectCollider grabber = new RectCollider(new Coord(0, 12), new PCoord(0, 10), 12);
            public RectCollider typePane = new RectCollider(new Coord(10, 12), new PCoord(0, 0), 12);
            boolean followMouse = false;
            boolean isOpen = false;

            public TileDisplay() { instance = this; }

            public void start() {
                CameraManager.addToDraw(this, 1);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
                addToUpdate(this);
                isOpen = true;
            }

            public void destroy() {
                CameraManager.removeToDraw(this, 1);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                removeToUpdate(this);
                followMouse = false;
                isOpen = false;
            }

            public void update() {
                if (followMouse) { pos.x += MouseDetect.instance.deltaMousePos().x;
                    pos.y += MouseDetect.instance.deltaMousePos().y;
                    grabber.og.x = pos.x;
                    grabber.og.y = 12+pos.y;
                    typePane.og.x = 10+pos.x;
                    typePane.og.y = 12+pos.y;
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (grabber.isColliding(new PointCollider(MouseDetect.instance.mousePos()))) {
                        followMouse = true;
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (typePane.isColliding(new PointCollider(MouseDetect.instance.mousePos()))) {
                        int i = (int) ((MouseDetect.instance.mousePos().x - typePane.og.x) / 22);
                        t.types.remove(i);
                        typePane.length.d-=22;
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (typePane.isColliding(new PointCollider(MouseDetect.instance.mousePos()))) {
                        int i = (int) ((MouseDetect.instance.mousePos().x - typePane.og.x) / 22);
                        t.types.get(i).ti.nudge(0, 1);
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                followMouse = false;
            }

            public void openWith(Tile t) {
                this.t = t;
                typePane.length.d = 22 * t.types.size();
                if (!isOpen)
                    start();
            }

            public void render(Graphics g) {
                g.setColor(Color.gray);
                g.fillRect(grabber.og.x(), (int) (grabber.og.y() - grabber.width), (int) grabber.length.d, (int) grabber.width * 2);
                g.setColor(Color.lightGray);
                g.drawRect(grabber.og.x(), (int) (grabber.og.y() - grabber.width), (int) grabber.length.d, (int) grabber.width * 2);
                g.setColor(Color.white);
                g.fillRect(typePane.og.x(), (int) (typePane.og.y() - typePane.width), (int) typePane.length.d, (int) typePane.width * 2);
                TypeInst type;
                for (int x = 0; x < t.types.size(); x++) {
                    type = t.types.get(x);
                    if (type == null) { t.types.remove(x); x--; continue; }
                    type.tt.render.accept(g, new RenderContext(LevelEditor.instance, t, type, new Coord(typePane.og.x + x*22 + 1, typePane.og.y-10), null));
                }
            }
        }

        public class TileInventory extends Script {

            public static TileInventory instance;
            public Coord pos = new Coord(0,0);
            public RectCollider grabber = new RectCollider(new Coord(0, 12), new PCoord(0, 10), 12);
            public RectCollider typePane = new RectCollider(new Coord(10, 12), new PCoord(0, 0), 12);
            boolean followMouse = false;
            boolean isOpen = false;
            int row = 0;
            int tpr = 5;

            public TileInventory() { instance = this; }

            public void start() {
                row = 0;
                if (tpr > TileType.tileTypes().length)
                    typePane.length.d = 22*TileType.tileTypes().length;
                else
                    typePane.length.d = 22*tpr;
                CameraManager.addToDraw(this, 1);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
                KeyDetect.instance.addP(this);
                addToUpdate(this);
                isOpen = true;
            }

            public void destroy() {
                CameraManager.removeToDraw(this, 1);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                KeyDetect.instance.removeP(this);
                removeToUpdate(this);
                followMouse = false;
                isOpen = false;
            }

            public void update() {
                if (followMouse) { pos.x += MouseDetect.instance.deltaMousePos().x;
                    pos.y += MouseDetect.instance.deltaMousePos().y;
                    grabber.og.x = pos.x;
                    grabber.og.y = 12+pos.y;
                    typePane.og.x = 10+pos.x;
                    typePane.og.y = 12+pos.y;
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (grabber.isColliding(new PointCollider(MouseDetect.instance.mousePos()))) {
                        followMouse = true;
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (TileDisplay.instance.t == null) return;
                    if (typePane.isColliding(new PointCollider(MouseDetect.instance.mousePos()))) {
                        int i = (int) ((MouseDetect.instance.mousePos().x - typePane.og.x) / 22 + tpr*row);
                        TypeInst toAdd = new TypeInst(TileType.tileTypeOf(i), TypeInfo.infoOfType(TileType.tileTypeOf(i)));
                        if (toAdd == null) return;
                        TileDisplay.instance.t.types.add(toAdd);
                        TileDisplay.instance.typePane.length.d+=22;
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                followMouse = false;
            }

            public void keyPressed(int e) {
                if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_CONTROL)) return;
                if (e == KeyEvent.VK_UP)
                    if (row > 0)
                        row--;
                if (e == KeyEvent.VK_DOWN)
                    if ((row+1)*tpr < TileType.tileTypes().length)
                        row++;
            }

            public void render(Graphics g) {
                g.setColor(Color.gray);
                g.fillRect(grabber.og.x(), (int) (grabber.og.y() - grabber.width), (int) grabber.length.d, (int) grabber.width * 2);
                g.setColor(Color.lightGray);
                g.drawRect(grabber.og.x(), (int) (grabber.og.y() - grabber.width), (int) grabber.length.d, (int) grabber.width * 2);
                //g.setColor(Color.black);
                //g.drawString(row+1+"",grabber.og.x()+2,grabber.og.y());
                g.setColor(Color.white);
                g.fillRect(typePane.og.x(), (int) (typePane.og.y() - typePane.width), (int) typePane.length.d, (int) typePane.width * 2);
                TypeInst type;
                for (int x = tpr*row; x < TileType.tileTypes().length && x < tpr*(row+1); x++) {
                    type = new TypeInst(TileType.tileTypes()[x], TypeInfo.infoOfType(TileType.tileTypes()[x]));
                    type.tt.render.accept(g, new RenderContext(LevelEditor.instance, null, type, new Coord(typePane.og.x + (x - tpr*row)*22 + 1, typePane.og.y-10), null));
                }
            }
        }
    }
    public class GridModRequest {
        public MoveContext move;
        public Function<MoveContext, Boolean> cascade;
        public MoveContext cascadeContext;
        public ArrayList<GridModRequest> queue = new ArrayList<GridModRequest>();
        public boolean isConsumed = false;
        public GridModRequest(MoveContext c) { move = c; }
        public GridModRequest(MoveContext c, Function<MoveContext, Boolean> cFunc, MoveContext cCont, ArrayList<GridModRequest> q) { move = c; cascade = cFunc; cascadeContext = cCont; queue = q; }
        public void consume() { if (isConsumed) return; isConsumed = true; if (queue != null) for (GridModRequest gmr : queue) gmr.consume(); }

        @Override
        public String toString() {
            return "moving " + move.srcType.tt + " from " + move.srcTile.pos + " to " + move.tile.pos + (cascade == null ? "no cascade " : "cascading ") + (queue.isEmpty() ? "no queue " : "with queue of length" + queue.size());
        }
    }

    public class LevelSelect extends Script {
        public static ResumeButton resume;
        public static ExitButton exit;

        public LevelSelect() { resume = new ResumeButton(); exit = new ExitButton(); }

        public void start() {
            resume.start();
            exit.start();
            CameraManager.addToDraw(this, 1);
        }

        public void destroy() {
            resume.destroy();
            exit.destroy();
            CameraManager.removeToDraw(this, 1);
        }

        public void render(Graphics g) {
            g.setColor(Color.gray);
            g.fillRect(0,0,250, 240);
            resume.render(g);
            exit.render(g);
        }

        public class ResumeButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 50), new PCoord(0, 150), 20);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            boolean isSelected = false;
            boolean isPressed = false;

            public void start() {
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
            }

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) { levels.get(1).start(); levelSelect.destroy(); } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.hover.c);
                else
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Resume Game", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }

        public class ExitButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 190), new PCoord(0, 150), 20);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            public void start() {
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
            }

            boolean isSelected = false;
            boolean isPressed = false;

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) { mainMenu.start(); levelSelect.destroy(); } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.hover.c);
                else
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Main Menu", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }
    }
    public class LevelMenu extends Script {
        public static ResumeButton resume;
        public static RestartButton restart;
        public static ExitButton exit;
        public static LoadButton load;
        public static SaveButton save;
        public static SaveAsButton saveAs;

        public LevelMenu() { resume = new ResumeButton(); restart = new RestartButton(); exit = new ExitButton(); save = new SaveButton(); saveAs = new SaveAsButton(); load = new LoadButton(); }

        public void start() {
            resume.start();
            restart.start();
            exit.start();
            if (Level.activeLevel == LevelEditor.instance) {
                save.start();
                saveAs.start();
                load.start();
            }
            CameraManager.addToDraw(this, 1);
        }

        public void destroy() {
            resume.destroy();
            restart.destroy();
            exit.destroy();
            if (Level.activeLevel == LevelEditor.instance) {
                save.destroy();
                saveAs.destroy();
                load.destroy();
            }
            CameraManager.removeToDraw(this, 1);
        }

        public void render(Graphics g) {
            g.setColor(Color.gray);
            if (Level.activeLevel == LevelEditor.instance) {
                g.fillRect(0,0,250, 450);
                resume.render(g);
                restart.render(g);
                exit.render(g);
                save.render(g);
                saveAs.render(g);
                load.render(g);
            }
            else {
                g.fillRect(0,0,250, 240);
                resume.render(g);
                restart.render(g);
                exit.render(g);
            }
        }

        public class ResumeButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 50), new PCoord(0, 150), 20);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            boolean isSelected = false;
            boolean isPressed = false;

            public void start() {
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
            }

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) { levelMenu.destroy(); Level.activeLevel.isPaused = false; } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.hover.c);
                else
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                if (Level.activeLevel == LevelEditor.instance)
                    g.drawString("Resume Editing", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
                else
                    g.drawString("Resume Level", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }

        public class RestartButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 120), new PCoord(0, 150), 20);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            boolean isSelected = false;
            boolean isPressed = false;

            public void start() {
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
            }

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) {
                levelMenu.destroy();
                if (Level.activeLevel == LevelEditor.instance) {
                    try {
                        clearEditor();
                    }
                    catch (Exception x) {
                        System.out.println("Editor Clear Failed");
                        x.printStackTrace();
                    }
                }
                else
                    Level.activeLevel.reload();
                Level.activeLevel.isPaused = false;
            } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.hover.c);
                else
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                if (Level.activeLevel == LevelEditor.instance)
                    g.drawString("Clear Editor", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
                else
                    g.drawString("Restart Game", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }

        public class ExitButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 190), new PCoord(0, 150), 20);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            public void start() {
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
            }

            boolean isSelected = false;
            boolean isPressed = false;

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) {
                Level.activeLevel.isPaused = false;
                levelMenu.destroy();
                if (Level.activeLevel == LevelEditor.instance)
                    customMenu.start();
                else
                    levelSelect.start();
                Level.activeLevel.destroy();
            } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.hover.c);
                else
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                if (Level.activeLevel == LevelEditor.instance)
                    g.drawString("Exit Editor", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
                else
                    g.drawString("Exit Level", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }

        public class LoadButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 260), new PCoord(0, 150), 20);
            TextField txt;

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            public LoadButton() { txt = new TextField(); }

            public void start() {
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                txt.destroy();
            }

            boolean isSelected = false;
            boolean isPressed = false;

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) {
                if (txt.sim)
                    txt.destroy();
                else
                    txt.start();
            } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.hover.c);
                else
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Load in Editor", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }

            public class TextField extends Script {
                public String field = "";
                public TextField() { sim = false; }
                public void start() {
                    System.out.println("open field");
                    field = "";
                    sim = true;
                    //CameraManager.addToDraw(this, 1);
                    KeyDetect.instance.addP(this);
                }

                public void destroy() {
                    sim = false;
                    //CameraManager.removeToDraw(this, 1);
                    KeyDetect.instance.removeP(this);
                }

                public void keyPressed(int e) {
                    if (e == KeyEvent.VK_BACK_SPACE) {
                        if (field.length() > 0)
                            field = field.substring(0, field.length()-1);
                        System.out.println(field);
                    }
                    else if (e == KeyEvent.VK_ESCAPE) {
                        destroy();
                        System.out.println("closing field");
                    }
                    else if (e == KeyEvent.VK_ENTER) {
                        try {
                            LevelEditor.instance.destroy();
                            buildEditor(field);
                            Level.activeLevel.isPaused = false;
                            destroy();
                            levelMenu.destroy();
                        }
                        catch (Exception x) {
                            System.out.println("Couldn't build level " + field);
                            x.printStackTrace();
                        }
                        System.out.println("Loading " + field);
                    }
                    else {
                        if (KeyEvent.getKeyText(e).length() > 1) return;
                        if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_SHIFT)) {
                            System.out.println("adding "+(char) e);
                            field += (char) e;
                            System.out.println(field);
                            return;
                        }
                        System.out.println("adding "+(""+(char) e).toLowerCase());
                        field += (""+(char) e).toLowerCase();
                        System.out.println(field);
                    }
                }
            }
        }

        public class SaveButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 330), new PCoord(0, 150), 20);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            boolean isSelected = false;
            boolean isPressed = false;

            public void start() {
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
            }

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) {
                saveEditor();
            } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.hover.c);
                else
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Save Level", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }

        public class SaveAsButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 400), new PCoord(0, 150), 20);
            TextField txt;

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            public SaveAsButton() { txt = new TextField(); }

            public void start() {
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                txt.destroy();
            }

            boolean isSelected = false;
            boolean isPressed = false;

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) {
                if (txt.sim)
                    txt.destroy();
                else
                    txt.start();
            } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.press.c);
                else if (isSelected)
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.hover.c);
                else
                    g.setColor(Minesweeper.MainMenu.StartButton.ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Save Level As", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }

            public class TextField extends Script {
                public String field = "";
                public TextField() { sim = false; }
                public void start() {
                    System.out.println("open field");
                    field = "";
                    sim = true;
                    //CameraManager.addToDraw(this, 1);
                    KeyDetect.instance.addP(this);
                }

                public void destroy() {
                    sim = false;
                    //CameraManager.removeToDraw(this, 1);
                    KeyDetect.instance.removeP(this);
                }

                public void keyPressed(int e) {

                    if (e == KeyEvent.VK_BACK_SPACE) {
                        if (field.length() > 0)
                            field = field.substring(0, field.length()-1);
                        System.out.println(field);
                    }
                    else if (e == KeyEvent.VK_ESCAPE) {
                        destroy();
                        System.out.println("closing field");
                    }
                    else if (e == KeyEvent.VK_ENTER) {
                        saveEditorAs(field);
                        System.out.println("saving as " + field);
                    }
                    else {
                        if (KeyEvent.getKeyText(e).length() > 1) return;
                        if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_SHIFT)) {
                            System.out.println("adding "+(char) e);
                            field += (char) e;
                            System.out.println(field);
                            return;
                        }
                        System.out.println("adding "+(""+(char) e).toLowerCase());
                        field += (""+(char) e).toLowerCase();
                        System.out.println(field);
                    }
                }
            }
        }
    }

    public class Tile {
        public ArrayList<TypeInst> types = new ArrayList<TypeInst>();
        public Coord pos;

        public Tile(Coord pos) {
            this.pos = pos;
            types = new ArrayList<TypeInst>();
        }

        public Tile(Coord pos, ArrayList<TypeInst> ts) {
            this.pos = pos;
            types = ts;
        }

        public Tile(Tile t) {
            pos = new Coord(t.pos);
            types = new ArrayList<>(t.types);
        }

        public Boolean attemptPull(MoveContext c) {
            c.tile = this;
            for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.attemptPull.apply(c)) return false;
            }
            return true;
        }
        public Boolean attemptPush(MoveContext c) {
            c.tile = this;
            for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.attemptPush.apply(c)) return false;
            }
            return true;
        }
        public Boolean attemptPass(MoveContext c) {
            c.tile = this;
            for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.attemptPass.apply(c)) return false;
            }
            return true;
        }
        public Boolean pull(MoveContext c) {
            c.tile = this;
            for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.pull.apply(c)) return false;
            }
            return true;
        }
        public Boolean push(MoveContext c) {
            c.tile = this;
            for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.push.apply(c)) return false;
            }
            return true;
        }
        public Boolean pass(MoveContext c) {
            c.tile = this;
            for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.pass.apply(c)) return false;
            }
            return true;
        }
        public Boolean exit(MoveContext c) {
            //c.tile = this;
            for (TypeInst t : types) {
                //c.type = t;
                if (!t.tt.exit.apply(c)) return false;
            }
            return true;
        }

        public boolean hasType(TileType tt) {
            for (TypeInst ti : types) { if (ti.tt == tt) return true; }
            return false;
        }
        public boolean hasType(int id) {
            TileType tt = TileType.tileTypeOf(id);
            for (TypeInst ti : types) { if (ti.tt == tt) return true; }
            return false;
        }

        @Override
        public String toString() {
            String ret = "";
            for (TypeInst ti : types) {
                ret += ti.tt + ", ";
            }
            return pos + " with " + ret;
        }
    }
    public enum TileType {
        empty(0,
                GenericInput.ignore.func, GenericInput.ignore.func,
                GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null)
                g.drawImage(Sprites.grass.i, c.srcTile.pos.x()*20, c.srcTile.pos.y()*20, 20, 20, null);
                //g.fillRect(c.srcTile.pos.x()*20, c.srcTile.pos.y()*20, 20, 20);
            else
                g.drawImage(Sprites.grass.i, c.c.x(), c.c.y(), 20, 20, null);
                //g.fillRect(c.c.x(), c.c.y(), 20, 20);
        }),
        wall(1,
                GenericInput.ignore.func, GenericInput.ignore.func,
                GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func,
                GenericMove.allow.func, GenericMove.block.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null) {
                if (c.srcTile.pos.y()+1<c.curLevel.grid.length) {
                    if (c.curLevel.grid[c.srcTile.pos.y()+1][c.srcTile.pos.x()] != null) {
                        if (c.curLevel.grid[c.srcTile.pos.y()+1][c.srcTile.pos.x()].hasType(1)) {
                            g.drawImage(Sprites.wallTop.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                            return;
                        }
                    }
                }
                boolean hasLeft = false;
                if (c.srcTile.pos.x()-1>=0) {
                    if (c.curLevel.grid[c.srcTile.pos.y()][c.srcTile.pos.x()-1] != null) {
                        if (c.curLevel.grid[c.srcTile.pos.y()][c.srcTile.pos.x()-1].hasType(1)) {
                            hasLeft = true;
                        }
                    }
                }
                if (c.srcTile.pos.x()+1<c.curLevel.grid[0].length) {
                    if (c.curLevel.grid[c.srcTile.pos.y()][c.srcTile.pos.x()+1] != null) {
                        if (c.curLevel.grid[c.srcTile.pos.y()][c.srcTile.pos.x()+1].hasType(1)) {
                            if (hasLeft)
                                g.drawImage(Sprites.wallFront.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                            else
                                g.drawImage(Sprites.wallLeft.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                            return;
                        }
                    }
                }
                if (hasLeft)
                    g.drawImage(Sprites.wallRight.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                else
                    g.drawImage(Sprites.wallPillar.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
            }
            else {
                g.drawImage(Sprites.wallPillar.i, c.c.x(), c.c.y(), 20, 20, null);
            }
        }),
        box(2,
                GenericInput.ignore.func, GenericInput.ignore.func,
                GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func,
                GenericMove.allow.func, GenericMove.line.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null)
                g.drawImage(Sprites.box.i, c.srcTile.pos.x()*20, c.srcTile.pos.y()*20, 20, 20, null);
            else
                g.drawImage(Sprites.box.i, c.c.x(), c.c.y(), 20, 20, null);
        }),
        player(3,
               GenericInput.follow.func, GenericInput.ignore.func,
               GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func,
               GenericMove.ignore.func, GenericMove.line.func, GenericMove.ignore.func,
               GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null) {
                if (c.srcType.ti instanceof TypeInfo.PlayerInfo pi) {
                    if (pi.dir.y == -1)
                        g.drawImage(Sprites.chudUp.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                    else if (pi.dir.y == 1)
                        g.drawImage(Sprites.chudDown.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                    else if (pi.dir.x == -1)
                        g.drawImage(Sprites.chudLeft.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                    else
                        g.drawImage(Sprites.chudRight.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                }
                else {
                    g.drawImage(Sprites.chudRight.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                }
            }
            else
                g.drawImage(Sprites.chudRight.i, c.c.x(), c.c.y(), 20, 20, null);
        }),
        belt(4,
                GenericInput.ignore.func, GenericInput.push.func,
                GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null) {
                if (c.srcType.ti instanceof TypeInfo.BeltInfo bi) {
                    if (bi.dir.y == -1)
                        g.drawImage(Sprites.beltUp.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                    else if (bi.dir.y == 1)
                        g.drawImage(Sprites.beltDown.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                    else if (bi.dir.x == -1)
                        g.drawImage(Sprites.beltLeft.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                    else
                        g.drawImage(Sprites.beltRight.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                }
                else {
                    g.drawImage(Sprites.beltRight.i, c.srcTile.pos.x() * 20, c.srcTile.pos.y() * 20, 20, 20, null);
                }
            }
            else
                g.drawImage(Sprites.beltRight.i, c.c.x(), c.c.y(), 20, 20, null);
        }),
        ice(5,
                GenericInput.ignore.func, GenericInput.ignore.func,
                GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func,
                GenericMove.allow.func, GenericMove.slide.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            g.setColor(new Color(130,190,255));
            if (c.c == null)
                g.fillRect(c.srcTile.pos.x()*20, c.srcTile.pos.y()*20, 20, 20);
            else
                g.fillRect(c.c.x(), c.c.y(), 20, 20);
        }),
        gate(6,
                GenericInput.ignore.func, GenericInput.ignore.func,
                GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func,
                GenericMove.allow.func, GenericMove.check.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            g.setColor(Color.orange);
            if (c.c == null)
                g.fillRect(c.srcTile.pos.x()*20, c.srcTile.pos.y()*20, 20, 20);
            else
                g.fillRect(c.c.x(), c.c.y(), 20, 20);
        }),
        button(7,
                GenericInput.ignore.func, GenericInput.ignore.func,
                GenericMove.ignore.func, GenericMove.hold.func, GenericMove.ignore.func, GenericMove.unhold.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            g.setColor(Color.orange);
            if (c.c == null)
                g.fillRect(c.srcTile.pos.x()*20+3, c.srcTile.pos.y()*20+3, 14, 14);
            else
                g.fillRect(c.c.x(), c.c.y(), 20, 20);
        });
        public Function<InputContext, Boolean> playerInput;
        public Function<InputContext, Boolean> playerInputEnd;
        public Function<MoveContext, Boolean> pull;
        public Function<MoveContext, Boolean> push;
        public Function<MoveContext, Boolean> pass;
        public Function<MoveContext, Boolean> exit;
        public Function<MoveContext, Boolean> attemptPull;
        public Function<MoveContext, Boolean> attemptPush;
        public Function<MoveContext, Boolean> attemptPass;
        public Function<MoveContext, Boolean> blocked;
        public Function<MoveContext, Boolean> interact;
        public BiConsumer<Graphics, RenderContext> render;
        public int id;
        TileType(int id, Function<InputContext, Boolean> pi, Function<InputContext, Boolean> pie, Function<MoveContext, Boolean> pull, Function<MoveContext, Boolean> push, Function<MoveContext, Boolean> pass, Function<MoveContext, Boolean> exit, Function<MoveContext, Boolean> attPull, Function<MoveContext, Boolean> attPush, Function<MoveContext, Boolean> attpass, Function<MoveContext, Boolean> blocked, Function<MoveContext, Boolean> interact, BiConsumer<Graphics, RenderContext> r) {
            this.id = id;
            playerInput = pi;
            playerInputEnd = pie;
            this.pull = pull;
            this.push = push;
            this.pass = pass;
            this.exit = exit;
            attemptPull = attPull;
            attemptPush = attPush;
            attemptPass = attpass;
            this.blocked = blocked;
            this.interact = interact;
            render = r;
        }

        public static TileType tileTypeOf(int id) {
            for (TileType t : tileTypes()) { if (t.id == id) return t; }
            return null;
        }
        public static TileType[] tileTypes() {
            return new TileType[]{empty, wall, box, player, belt, ice, gate, button};
        }
        public static enum GenericInput {
            follow((InputContext c) -> {
                Coord moveDir = null;
                if (c.input == Input.up) {
                    moveDir = new Coord(0,-1);
                }
                else if (c.input == Input.down) {
                    moveDir = new Coord(0,1);
                }
                else if (c.input == Input.left) {
                    moveDir = new Coord(-1,0);
                }
                else if (c.input == Input.right) {
                    moveDir = new Coord(1,0);
                }
                if (moveDir != null) {
                    if (c.srcType.ti instanceof TypeInfo.PlayerInfo pi) pi.dir = moveDir;
                    Coord toPos = c.srcTile.pos.add(moveDir);
                    if (toPos.y() < 0 || toPos.y() >= c.curLevel.grid.length) return false;
                    if (toPos.x() < 0 || toPos.x() >= c.curLevel.grid[0].length) return false;
                    if (c.curLevel.grid[toPos.y()][toPos.x()] == null) return false;
                    GridModRequest gmr = c.curLevel.getMove(c.newMoveContext(c.curLevel, c.srcTile, c.srcType, c.curLevel.grid[toPos.y()][toPos.x()], null, moveDir, 2, 0));
                    System.out.println(gmr);
                    if (!c.curLevel.grid[toPos.y()][toPos.x()].attemptPush(gmr.move)) return false;
                    //c.curLevel.grid[toPos.y()][toPos.x()].push(c.newMoveContext(c.curLevel, c.srcTile, c.srcType, null, null, moveDir, 2, gmr.connect));
                    c.srcType.requests.add(gmr);
                    c.curLevel.addMove(gmr);
                }
                return true;
            }),
            listen((InputContext c) -> {return true;}),
            ignore((InputContext c) -> {return false;}),
            push((InputContext c) -> {
                Coord toPos = new Coord(c.srcTile.pos);
                toPos.x += ((TypeInfo.BeltInfo) c.srcType.ti).dir.x;
                toPos.y += ((TypeInfo.BeltInfo) c.srcType.ti).dir.y;
                if (toPos.y() < 0 || toPos.y() >= c.curLevel.grid.length) return false;
                if (toPos.x() < 0 || toPos.x() >= c.curLevel.grid[0].length) return false;
                if (c.curLevel.grid[toPos.y()][toPos.x()] == null) return false;
                GridModRequest gmr = c.curLevel.getMove(c.newMoveContext(c.curLevel, c.srcTile, c.srcType, null, null, ((TypeInfo.BeltInfo) c.srcType.ti).dir, 2, -1));
                if (!c.srcTile.attemptPush(gmr.move)) return false;
                for (GridModRequest subGmr : gmr.queue) {
                    c.srcType.requests.add(subGmr);
                    c.curLevel.addMove(subGmr);
                }
                //c.srcTile.push(c.newMoveContext(c.curLevel, c.srcTile, c.srcType, null, null, ((TypeInfo.BeltInfo) c.srcType.ti).dir, 2));
                return true;
            });
            public Function<InputContext, Boolean> func;
            GenericInput(Function<InputContext, Boolean> f) { func = f; }
        }
        public static enum GenericMove {
            allow((MoveContext c) -> {return true;}),
            check((MoveContext c) -> {
                if (c.type.ti instanceof TypeInfo.GateInfo gi) {
                    System.out.println(TypeInfo.GateInfo.states.get(gi.id) + ", " + gi.not);
                    return TypeInfo.GateInfo.states.get(gi.id) != gi.not;
                }
                return false;
            }),
            line((MoveContext c) -> {
                if (c.tile.pos.y() + c.dir.y() < 0 || c.tile.pos.y() + c.dir.y() >= c.curLevel.grid.length) return false;
                if (c.tile.pos.x() + c.dir.x() < 0 || c.tile.pos.x() + c.dir.x() >= c.curLevel.grid[0].length) return false;
                if (c.curLevel.grid[c.tile.pos.y() + c.dir.y()][c.tile.pos.x() + c.dir.x()] == null) return false;
                if (c.max-1 == -1) return false;
                System.out.println("Pushing with " + c);
                GridModRequest gmr = c.curLevel.getMove(c.newMoveContext(c.curLevel, c.tile, c.type, null, null, c.dir, c.max-1, c.depth+1));
                c.srcType.requests.add(gmr);
                c.request.queue.add(gmr);
                System.out.println("Context: " + gmr.move);
                System.out.println("Checking Downstream\n");
                if (!c.curLevel.grid[c.tile.pos.y() + c.dir.y()][c.tile.pos.x() + c.dir.x()].attemptPush(gmr.move)) return false;
                System.out.println("Push Succeeded\n");
                return true;
            }),
            ignore((MoveContext c) -> {return false;}),
            slide((MoveContext c) -> {
                System.out.println("Sliding with " + c);
                Coord toPos = c.tile.pos.add(c.dir);
                if (toPos.y() < 0 || toPos.y() >= c.curLevel.grid.length) return true;
                if (toPos.x() < 0 || toPos.x() >= c.curLevel.grid[0].length) return true;
                if (c.curLevel.grid[toPos.y()][toPos.x()] == null) return true;
                MoveContext move = c.newMoveContext(c.curLevel, c.tile, c.srcType, c.curLevel.grid[toPos.y()][toPos.x()], c.srcType, c.dir, c.max, 0);
                GridModRequest gmr = c.curLevel.getMove(move);
                //c.request.cascade = GenericMove.line.func;
                c.request.cascadeContext = move;
                System.out.println("Context: " + move);
                System.out.println("Sliding Successful\n");
                //if (!c.curLevel.grid[toPos.y()][toPos.x()].attemptPush(c.newMoveContext(c.curLevel, c.tile, c.type, null, null, c.dir, c.max, gmr.connect))) return false;
                //c.curLevel.grid[toPos.y()][toPos.x()].push(c.newMoveContext(c.curLevel, c.tile, c.srcType, null, null, c.dir, c.max, gmr.connect));
                //else return false;
                return true;
            }),
            block((MoveContext c) -> {return false;}),
            hold((MoveContext c) -> {
                System.out.println("Check Hold\n");
                if (c.type.ti instanceof TypeInfo.ToggleInfo ti) {
                    System.out.println("Hold Button\n");
                    TypeInfo.GateInfo.states.put(ti.id, true);
                    TypeInfo.ToggleInfo.helds.put(ti.id, TypeInfo.ToggleInfo.helds.get(ti.id)+1);
                }
                return true; }),
            unhold((MoveContext c) -> {
                System.out.println("Check Unhold\n");
                if (c.type.ti instanceof TypeInfo.ToggleInfo ti) {
                    System.out.println("Off Button\n");
                    if (TypeInfo.ToggleInfo.helds.get(ti.id) >= 1) {
                        TypeInfo.GateInfo.states.put(ti.id, false);
                    }
                    TypeInfo.ToggleInfo.helds.put(ti.id, TypeInfo.ToggleInfo.helds.get(ti.id)-1);
                }
                return false;});
            public Function<MoveContext, Boolean> func;
            GenericMove(Function<MoveContext, Boolean> f) { func = f; }
        }
        public static enum GenericHold {
            listen((Context c) -> {return true;}),
            ignore((Context c) -> {return false;});
            public Function<Context, Boolean> func;
            GenericHold(Function<Context, Boolean> f) { func = f; }
        }
        /*public static enum GenericRender {
            square(),

        }*/
    }

    public enum Input {
        up(KeyEvent.VK_W),
        down(KeyEvent.VK_S),
        left(KeyEvent.VK_A),
        right(KeyEvent.VK_D),
        wait(KeyEvent.VK_SPACE),
        esc(KeyEvent.VK_ESCAPE);
        public int key;
        Input(int k) {
            key = k;
        }

        public static Input inputOf(int e) {
            if (e == up.key) return up;
            if (e == down.key) return down;
            if (e == left.key) return left;
            if (e == right.key) return right;
            if (e == wait.key) return wait;
            if (e == esc.key) return esc;
            return null;
        }
    }
    public class Context {
        public Level curLevel;
        public Tile srcTile;
        public TypeInst srcType;
        public Context(Level l, Tile t, TypeInst tt) { curLevel = l; srcTile = t; srcType = tt; }
        public Context newContext(Level l, Tile t, TypeInst tt) { return new Context(l, t, tt); }
        public RenderContext newRenderContext(Level l, Tile t, TypeInst tt, Coord pos, Coord scale) { return new RenderContext(l, t, tt, pos, scale); }
        public MoveContext newMoveContext(Level l, Tile t, TypeInst toMove, Tile srcT, TypeInst tt, Coord d, int maxD, int depth) { return new MoveContext(l, t, toMove, srcT, tt, d, maxD, depth); }
        public MoveContext newMoveContext(Level l, Tile t, TypeInst toMove, Tile srcT, TypeInst tt, Coord d, int maxD, int depth, GridModRequest gmr) { return new MoveContext(l, t, toMove, srcT, tt, d, maxD, depth, gmr); }
        public InputContext newInputContext(Level l, Tile t, TypeInst tt, Input i) { return new InputContext(l, t, tt, i); }

        @Override
        public String toString() {
            return (curLevel == null ? "no level" : curLevel.file) + " on tile " + (srcTile == null ? "NULL" : srcTile.pos) + " with type " + (srcType == null ? "NULL" : srcType.tt);
        }
    }
    public class RenderContext extends Context {
        Coord c;
        Coord s;
        public RenderContext(Level l, Tile t, TypeInst tt, Coord pos, Coord scale) { super(l, t, tt); c = pos; s = scale; }
    }
    public class InputContext extends Context {
        public Input input;
        public InputContext(Level l, Tile t, TypeInst tt, Input i) { super(l, t, tt); input = i; }

        @Override
        public String toString() {
            return super.toString() + " with input " + input.key;
        }
    }
    public class MoveContext extends Context {
        public int depth;
        public Coord dir;
        public Tile tile;
        public TypeInst type;
        public int max;
        public GridModRequest request;
        public MoveContext(Level l, Tile t, TypeInst tt, Tile fromTile, TypeInst toMove, Coord d, int maxD, int depth) { super(l, t, tt); /*if (srcT != null) System.out.println("attempt move at " + srcT.pos);*/ dir = d; tile = fromTile; type = toMove; max = maxD; this.depth = depth; }
        public MoveContext(Level l, Tile t, TypeInst tt, Tile fromTile, TypeInst toMove, Coord d, int maxD, int depth, GridModRequest gmr) { super(l, t, tt); /*if (srcT != null) System.out.println("attempt move at " + srcT.pos);*/ dir = d; tile = fromTile; type = toMove; max = maxD; this.depth = depth; request = gmr; }

        @Override
        public String toString() {
            return super.toString() + " moving " + (type == null ? "NULL type" : type.tt) + " on " + (tile == null ? "NULL tile" : tile.pos) + " by " + dir;
        }
    }
    public class TypeInst {

        public ArrayList<GridModRequest> requests = new ArrayList<GridModRequest>();
        public TypeInfo ti;
        public TileType tt;
        public TypeInst(TileType tt) { this.tt = tt; ti = new TypeInfo(); }
        public TypeInst(TileType tt, TypeInfo ti) { this.tt = tt; this.ti = ti; }
    }
    public static class TypeInfo {
        public boolean isMoving = false;

        public static class PlayerInfo extends TypeInfo {
            public Coord dir;

            public PlayerInfo() {
                dir = new Coord(1, 0);
            }

            public PlayerInfo(Coord d) {
                dir = d;
            }

            @Override
            public void nudge(int i, int x) {
                System.out.println("nudge");
                switch (i) {
                    case 0:
                        dir.turn90(x);
                        break;
                    default:
                        System.out.println("N/A");
                        break;
                }
            }
        }

        public static class BeltInfo extends TypeInfo {
            public Coord dir;

            public BeltInfo() {
                dir = new Coord(1, 0);
            }

            public BeltInfo(Coord d) {
                dir = d;
            }

            @Override
            public void nudge(int i, int x) {
                System.out.println("nudge");
                switch (i) {
                    case 0:
                        dir.turn90(x);
                        break;
                    default:
                        System.out.println("N/A");
                        break;
                }
            }
        }
        public static class GateInfo extends TypeInfo {
            public static TreeMap<Integer, Boolean> states = new TreeMap<Integer, Boolean>();
            public Integer id;
            public boolean not;

            public GateInfo() {
                id = 0;
                states.put(id, false);
            }

            public GateInfo(int n, boolean b) {
                id = n;
                not = b;
                states.put(id, false);
            }

            @Override
            public void nudge() {
                id++;
                id %= 6;
                states.put(id, false);
            }
        }
        public static class ToggleInfo extends TypeInfo {
            public static TreeMap<Integer, Integer> helds = new TreeMap<Integer, Integer>();
            public Integer id;

            public ToggleInfo() {
                id = 0;
                GateInfo.states.put(id, false);
            }

            public ToggleInfo(int n) {
                id = n;
                GateInfo.states.put(id, false);
            }

            @Override
            public void nudge() {
                id++;
                id %= 6;
                GateInfo.states.put(id, false);
            }
        }

        public void nudge(int i, int x) {
            System.out.println("nudged");
        }

        public void nudge() {
            System.out.println("nudged");
        }

        public static TypeInfo infoOfType(TileType tt) {
            if (tt == TileType.belt) return new BeltInfo();
            if (tt == TileType.player) return new PlayerInfo();
            if (tt == TileType.gate) return new GateInfo();
            if (tt == TileType.button) return new ToggleInfo();
            return new TypeInfo();
        }

        public static TypeInfo infoFromString(TileType tt, String s) {
            s += "|";
            //System.out.println(s);
            try {
                if (tt == TileType.belt) {
                    Coord dir = new Coord(-1, 0);
                    while (s.contains("|")) {
                        if (s.charAt(0) == 'c') {
                            dir = new Coord(Integer.parseInt(s.substring(1, s.indexOf(":"))), Integer.parseInt(s.substring(s.indexOf(":")+1, s.indexOf("|"))));
                            //System.out.println("dir is " + dir);
                        }
                        else {
                            throw new InfoFormattingException(tt);
                        }
                        s = s.substring(s.indexOf("|")+1);
                    }
                    return new BeltInfo(dir);
                }
                if (tt == TileType.player) {
                    Coord dir = new Coord(-1, 0);
                    while (s.contains("|")) {
                        if (s.charAt(0) == 'c') {
                            dir = new Coord(Integer.parseInt(s.substring(1, s.indexOf(":"))), Integer.parseInt(s.substring(s.indexOf(":")+1, s.indexOf("|"))));
                            //System.out.println("dir is " + dir);
                        }
                        else {
                            throw new InfoFormattingException(tt);
                        }
                        s = s.substring(s.indexOf("|")+1);
                    }
                    return new PlayerInfo(dir);
                }
                if (tt == TileType.gate) {
                    int id = 0;
                    boolean not = false;
                    while (s.contains("|")) {
                        if (s.charAt(0) == 'i') {
                            id = Integer.parseInt(s.substring(1, s.indexOf("|")));
                        } else if (s.charAt(0) == 'b') {
                            not = Integer.parseInt(s.substring(1, s.indexOf("|"))) != 0;
                        } else {
                            throw new InfoFormattingException(tt);
                        }
                        s = s.substring(s.indexOf("|")+1);
                    }
                    return new GateInfo(id, not);
                }
                if (tt == TileType.button) {
                    int id = 0;
                    while (s.contains("|")) {
                        if (s.charAt(0) == 'i') {
                            id = Integer.parseInt(s.substring(1, s.indexOf("|")));
                        } else {
                            throw new InfoFormattingException(tt);
                        }
                        s = s.substring(s.indexOf("|")+1);
                    }
                    return new ToggleInfo(id);
                }
                else return new TypeInfo();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            return new TypeInfo();
        }

        public static String infoToString(TypeInfo ti) {
            String ret = "";
            if (ti instanceof PlayerInfo pi) {
                ret = "|c"+pi.dir.x()+":"+pi.dir.y();
            }
            else if (ti instanceof BeltInfo bi) {
                ret = "|c"+bi.dir.x()+":"+bi.dir.y();
            }
            else if (ti instanceof GateInfo gi) {
                ret = "|i"+gi.id+"|b"+(gi.not?1:0);
            }
            else if (ti instanceof ToggleInfo tgi) {
                ret = "|i"+tgi.id;
            }
            return ret;
        }

        public static class InfoFormattingException extends RuntimeException {
            public InfoFormattingException() {
                super("Invalid Tile Info Formatting");
            }

            public InfoFormattingException(TileType tt) {
                super("Invalid Tile Info Formatting for " + tt);
            }

            public InfoFormattingException(TileType tt, Exception src) {
                super("Invalid Tile Info Formatting for " + tt);
            }
        }
    }
}
