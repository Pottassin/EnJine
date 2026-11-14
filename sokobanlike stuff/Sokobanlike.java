import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import enjine.geoMath.*;
import enjine.Prog;

import javax.imageio.ImageIO;

@SuppressWarnings("WrongPackageStatement")
public class Sokobanlike extends Prog {
    public static String directory = "sokobanlike stuff/";
    public static String spriteDirectory = directory+"sprites/";
    public static String levelDirectory = directory+"levels/";
    public static String savesDirectory = directory+"saved levels/";
    public static File levelReg = new File(directory+"LevelRegistry");
    public static TreeMap<Integer, Level> levels = new TreeMap<Integer, Level>();

    public static MainMenu mainMenu;
    public static LevelSelect levelSelect;
    public static CustomMenu customMenu;
    public static LevelMenu levelMenu;

    public enum Sprites {
        zero(retrieve("0")),
        one(retrieve("1")),
        two(retrieve("2")),
        three(retrieve("3")),
        four(retrieve("4")),
        five(retrieve("5")),
        six(retrieve("6")),
        seven(retrieve("7")),
        eight(retrieve("8")),
        nine(retrieve("9")),
        negative(retrieve("Negative")),
        selectedTileTR(retrieve("SelectionTR")),
        selectedTileTL(retrieve("SelectionTL")),
        selectedTileTop(retrieve("SelectionTop")),
        selectedTileLeft(retrieve("SelectionLeft")),
        selectedTileBR(retrieve("SelectionBR")),
        selectedTileBL(retrieve("SelectionBL")),
        selectedTileBottom(retrieve("SelectionBottom")),
        selectedTileRight(retrieve("SelectionRight")),
        inventoryIcon(retrieve("InventoryIcon")),
        inventoryHoverIcon(retrieve("InventoryHoverIcon")),
        inventorySelectIcon(retrieve("InventorySelectIcon")),
        displayIcon(retrieve("DisplayIcon")),
        displayHoverIcon(retrieve("DisplayHoverIcon")),
        displaySelectIcon(retrieve("DisplaySelectIcon")),
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
        chudCrippleLeft(retrieve("ChudCrippleLeft")),
        chudCrippleRight(retrieve("ChudCrippleRight")),
        chudCrippleUp(retrieve("ChudCrippleUp")),
        chudCrippleDown(retrieve("ChudCrippleDown")),
        box(retrieve("Box")),
        beltLeft(retrieve("BeltLeft")),
        beltRight(retrieve("BeltRight")),
        beltUp(retrieve("BeltUp")),
        beltDown(retrieve("BeltDown")),
        closedGate1(projectColor(retrieve("BlankClosedGate"), Color.red)),
        closedGate2(projectColor(retrieve("BlankClosedGate"), Color.orange)),
        closedGate3(projectColor(retrieve("BlankClosedGate"), Color.yellow)),
        closedGate4(projectColor(retrieve("BlankClosedGate"), Color.green)),
        closedGate5(projectColor(retrieve("BlankClosedGate"), Color.cyan)),
        closedGate6(projectColor(retrieve("BlankClosedGate"), new Color(144, 0, 255))),
        openGate1(projectColor(retrieve("BlankOpenGate"), Color.red)),
        openGate2(projectColor(retrieve("BlankOpenGate"), Color.orange)),
        openGate3(projectColor(retrieve("BlankOpenGate"), Color.yellow)),
        openGate4(projectColor(retrieve("BlankOpenGate"), Color.green)),
        openGate5(projectColor(retrieve("BlankOpenGate"), Color.cyan)),
        openGate6(projectColor(retrieve("BlankOpenGate"), new Color(144, 0, 255))),
        button1(projectColor(retrieve("BlankButton"), Color.red)),
        button2(projectColor(retrieve("BlankButton"), Color.orange)),
        button3(projectColor(retrieve("BlankButton"), Color.yellow)),
        button4(projectColor(retrieve("BlankButton"), Color.green)),
        button5(projectColor(retrieve("BlankButton"), Color.cyan)),
        button6(projectColor(retrieve("BlankButton"), new Color(144, 0, 255))),
        buttonDown1(projectColor(retrieve("BlankButtonDown"), Color.red)),
        buttonDown2(projectColor(retrieve("BlankButtonDown"), Color.orange)),
        buttonDown3(projectColor(retrieve("BlankButtonDown"), Color.yellow)),
        buttonDown4(projectColor(retrieve("BlankButtonDown"), Color.green)),
        buttonDown5(projectColor(retrieve("BlankButtonDown"), Color.cyan)),
        buttonDown6(projectColor(retrieve("BlankButtonDown"), new Color(144, 0, 255))),
        spikes(retrieve("Spikes")),
        spikesUp1(projectColor(retrieve("BlankSpikesUp"), Color.red)),
        spikesUp2(projectColor(retrieve("BlankSpikesUp"), Color.orange)),
        spikesUp3(projectColor(retrieve("BlankSpikesUp"), Color.yellow)),
        spikesUp4(projectColor(retrieve("BlankSpikesUp"), Color.green)),
        spikesUp5(projectColor(retrieve("BlankSpikesUp"), Color.cyan)),
        spikesUp6(projectColor(retrieve("BlankSpikesUp"), new Color(144, 0, 255))),
        spikesDown1(projectColor(retrieve("BlankSpikesDown"), Color.red)),
        spikesDown2(projectColor(retrieve("BlankSpikesDown"), Color.orange)),
        spikesDown3(projectColor(retrieve("BlankSpikesDown"), Color.yellow)),
        spikesDown4(projectColor(retrieve("BlankSpikesDown"), Color.green)),
        spikesDown5(projectColor(retrieve("BlankSpikesDown"), Color.cyan)),
        spikesDown6(projectColor(retrieve("BlankSpikesDown"), new Color(144, 0, 255))),
        bomb(retrieve("Bomb")),
        bombLit(retrieve("BombLit")),
        fire(retrieve("Fire")),
        eternalFire(retrieve("EternalFire")),
        flagDown(retrieve("FlagDown")),
        flag(retrieve("Flag")),
        pasterRight(retrieve("PasterRight")),
        watcher1(projectColor(retrieve("Watcher"), Color.red)),
        watcher2(projectColor(retrieve("Watcher"), Color.orange)),
        watcher3(projectColor(retrieve("Watcher"), Color.yellow)),
        watcher4(projectColor(retrieve("Watcher"), Color.green)),
        watcher5(projectColor(retrieve("Watcher"), Color.cyan)),
        watcher6(projectColor(retrieve("Watcher"), new Color(144, 0, 255))),
        watcherUp1(projectColor(retrieve("WatcherUp"), Color.red)),
        watcherUp2(projectColor(retrieve("WatcherUp"), Color.orange)),
        watcherUp3(projectColor(retrieve("WatcherUp"), Color.yellow)),
        watcherUp4(projectColor(retrieve("WatcherUp"), Color.green)),
        watcherUp5(projectColor(retrieve("WatcherUp"), Color.cyan)),
        watcherUp6(projectColor(retrieve("WatcherUp"), new Color(144, 0, 255))),
        watcherDown1(projectColor(retrieve("WatcherDown"), Color.red)),
        watcherDown2(projectColor(retrieve("WatcherDown"), Color.orange)),
        watcherDown3(projectColor(retrieve("WatcherDown"), Color.yellow)),
        watcherDown4(projectColor(retrieve("WatcherDown"), Color.green)),
        watcherDown5(projectColor(retrieve("WatcherDown"), Color.cyan)),
        watcherDown6(projectColor(retrieve("WatcherDown"), new Color(144, 0, 255))),
        watcherLeft1(projectColor(retrieve("WatcherLeft"), Color.red)),
        watcherLeft2(projectColor(retrieve("WatcherLeft"), Color.orange)),
        watcherLeft3(projectColor(retrieve("WatcherLeft"), Color.yellow)),
        watcherLeft4(projectColor(retrieve("WatcherLeft"), Color.green)),
        watcherLeft5(projectColor(retrieve("WatcherLeft"), Color.cyan)),
        watcherLeft6(projectColor(retrieve("WatcherLeft"), new Color(144, 0, 255))),
        watcherRight1(projectColor(retrieve("WatcherRight"), Color.red)),
        watcherRight2(projectColor(retrieve("WatcherRight"), Color.orange)),
        watcherRight3(projectColor(retrieve("WatcherRight"), Color.yellow)),
        watcherRight4(projectColor(retrieve("WatcherRight"), Color.green)),
        watcherRight5(projectColor(retrieve("WatcherRight"), Color.cyan)),
        watcherRight6(projectColor(retrieve("WatcherRight"), new Color(144, 0, 255)));
        Image i;
        Sprites(Image image) { i = image; }

        static BufferedImage retrieve(String name) {
            try {
                return ImageIO.read(new File(spriteDirectory + name+".png"));
            }
            catch (IOException e) { throw new RuntimeException(e); }
        }

        static Image projectColor(BufferedImage i, Color c) {
            FilteredImageSource fis = new FilteredImageSource(i.getSource(), new ProjectFilter(c));
            return Toolkit.getDefaultToolkit().createImage(fis);
        }

        public static class ProjectFilter extends RGBImageFilter {
            public Color dye;
            public ProjectFilter(Color d) { dye = d; }
            @Override
            public int filterRGB(int x, int y, int rgb) {
                int grey = (((rgb >> 16) & 0xff) + ((rgb >> 8) & 0xff) + (rgb & 0xff) ) / 3;
                return (rgb & 0xff000000) | ((int)(grey/255.*dye.getRed()) << 16) | ((int)(grey/255.*dye.getGreen()) << 8) | (int)(grey/255.*dye.getBlue());
            }
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
        Coord pos;
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                lrl = levelReader.next()+",";
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
    public Level buildLevel(String name, int dir) throws LevelBuilderException, FileNotFoundException {
        Level builder = new Level();
        if (dir == 2)
            builder.file = new File(savesDirectory + name);
        else if (dir == 1)
            builder.file = new File(levelDirectory + name);
        else
            builder.file = new File(directory + name);
        Scanner levelReader = new Scanner(builder.file);
        String lrl = levelReader.next();
        Coord pos;
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                lrl = levelReader.next()+",";
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
        Coord pos;
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                lrl = levelReader.next()+",";
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
        }
        else {
            builder = LevelEditor.instance;
        }
        Scanner levelReader = new Scanner(builder.file);
        String lrl = levelReader.next();
        Coord pos;
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                lrl = levelReader.next()+",";
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
        }
        else {
            builder = LevelEditor.instance;
        }
        Scanner levelReader = new Scanner(toLoad);
        String lrl = levelReader.next();
        Coord pos;
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                lrl = levelReader.next()+",";
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
    public void buildEditor(String s, int dir) throws LevelBuilderException, FileNotFoundException {
        LevelEditor builder;
        File toLoad;
        if (dir == 2) {
            toLoad = new File(savesDirectory + s);
        }
        else if (dir == 1) {
            System.out.println("->"+levelDirectory + s);
            toLoad = new File(levelDirectory + s);
        }
        else {
            toLoad = new File(directory + s);
        }
        if (LevelEditor.instance == null) {
            builder = new LevelEditor();
            LevelEditor.instance = builder;
            builder.file = new File(savesDirectory + "LevelEditor");
        }
        else {
            builder = LevelEditor.instance;
        }
        Scanner levelReader = new Scanner(toLoad);
        String lrl = levelReader.next();
        Coord pos;
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                lrl = levelReader.next()+",";
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
        }
        else {
            builder = LevelEditor.instance;
        }
        Scanner levelReader = new Scanner(f);
        String lrl = levelReader.next();
        Coord pos;
        TileType tt;
        try {
            builder.grid = new Tile[Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1))][Integer.parseInt(lrl.substring(0, lrl.indexOf(":")))];
            while (levelReader.hasNext()) {
                lrl = levelReader.next();
                pos = new Coord();
                pos.x = Integer.parseInt(lrl.substring(0, lrl.indexOf(":")));
                pos.y = Integer.parseInt(lrl.substring(lrl.indexOf(":") + 1));
                lrl = levelReader.next()+",";
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
    public void clearEditor() {
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
            FileWriter fw = new FileWriter(newF, false);
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
    public void saveEditorAs(String name, int dir) {
        try {
            File newF;
            if (dir == 2)
                newF = new File(savesDirectory + name);
            else if (dir == 1)
                newF = new File(levelDirectory + name);
            else
                newF = new File(directory + name);
            FileWriter fw = new FileWriter(newF, false);
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

            public int dir;

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
                            buildLevel(field, dir).start();
                            System.out.println("Loading Level " + field);
                            destroy();
                            customMenu.destroy();
                        }
                        catch (Exception x) {
                            System.out.println("Error Loading Level " + field);
                        }
                    }
                    else {
                        if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_CONTROL)) {
                            if (e == KeyEvent.VK_COMMA) {
                                System.out.println("Changing directory to " + directory);
                                dir = 0;
                                return;
                            }
                            else if (e == KeyEvent.VK_PERIOD) {
                                System.out.println("Changing directory to " + levelDirectory);
                                dir = 1;
                                return;
                            }
                            else if (e == KeyEvent.VK_SLASH) {
                                System.out.println("Changing directory to " + savesDirectory);
                                dir = 2;
                                return;
                            }
                        }
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
        public ArrayList<Context> removeQueue = new ArrayList<>();
        public boolean gameOver = false;
        public boolean gameWon = false;
        public ArrayList<Input> inputStack = new ArrayList<Input>();
        public boolean isPaused = false;
        public boolean settingUp = false;
        public boolean moveResolveEmpty = false;
        public Coord offset = new Coord();

        public void start() {
            activeLevel = this;
            CameraManager.addToDraw(this, 1);
            KeyDetect.instance.addP(this);
            settingUp = true;
            if (this != LevelEditor.instance) {
                reload();
            }
            else {
                reloadData();
            }
            settingUp = false;
        }

        public void clearData() {
            gameWon = false;
            gameOver = false;
            isPaused = false;
            TypeInfo.clear();
            /*TypeInfo.PlayerInfo.awake = 0;
            TypeInfo.FlagInfo.numUp = 0;
            if (LevelEditor.instance != null && LevelEditor.instance.hidden) {
                for (Integer i : TypeInfo.ToggleInfo.helds.keySet())
                    TypeInfo.ToggleInfo.helds.put(i, 0);
                for (Integer i : TypeInfo.GateInfo.states.keySet())
                    TypeInfo.GateInfo.states.put(i, false);
            }
            else {
                TypeInfo.ToggleInfo.helds.clear();
                TypeInfo.GateInfo.states.clear();
            }*/
        }

        public void destroy() {
            CameraManager.removeToDraw(this, 1);
            KeyDetect.instance.removeP(this);
            activeLevel = null;
        }

        public void pause() {
            isPaused = true;
            levelMenu.start();
        }

        public void unpause() {
            isPaused = false;
            levelMenu.destroy();
        }

        public void keyPressed(int e) {
            if (e == Input.esc.key) {
                if (isPaused)
                    unpause();
                else
                    pause();
                return;
            }
            if (e == Input.debug.key) {
                System.out.println("DEBUG #1 ---------------");
                for (Integer i : TypeInfo.GateInfo.states.keySet()) {
                    System.out.println("Key: " + i + ", Open?: " + TypeInfo.GateInfo.states.get(i));
                }
                System.out.println("");
                for (Integer i : TypeInfo.ToggleInfo.helds.keySet()) {
                    System.out.println("Key: " + i + ", Held: " + TypeInfo.ToggleInfo.helds.get(i));
                }
                System.out.println("END DEBUG #1 -----------");
                return;
            }
            if (isPaused || gameOver || gameWon || Level.activeLevel == LevelEditor.instance) return;
            if (Input.inputOf(e) == null) return;
            inputStack.add(Input.inputOf(e));
            for (int x = 0; x < grid[0].length; x++) {
                for (int y = 0; y < grid.length; y++) {
                    if (grid[y][x] == null) break;
                    for (TypeInst t : grid[y][x].types) {
                        t.tt.playerInput.apply(new InputContext(this, grid[y][x], t, Input.inputOf(e)));
                    }
                }
            }
            if (e == Input.debug.key) {
                System.out.println("DEBUG #2 ---------------");
                for (Integer i : TypeInfo.GateInfo.states.keySet()) {
                    System.out.println("Key: " + i + ", Open?: " + TypeInfo.GateInfo.states.get(i));
                }
                System.out.println("");
                for (Integer i : TypeInfo.ToggleInfo.helds.keySet()) {
                    System.out.println("Key: " + i + ", Held: " + TypeInfo.ToggleInfo.helds.get(i));
                }
                System.out.println("END DEBUG #2 -----------");
                return;
            }
            //System.out.println("\nFirst Check ______________/");
            resolveMoves();
            for (int x = 0; x < grid[0].length; x++) {
                for (int y = 0; y < grid.length; y++) {
                    grid[y][x].stand(this);
                }
            }
            TypeInfo.BeltInfo.moved.clear();
        }

        public void resolveMoves() {
            TreeMap<Coord, GridModRequest> coords = new TreeMap<Coord, GridModRequest>();
            TreeMap<Integer, ArrayList<GridModRequest>> allMoves = new TreeMap<Integer, ArrayList<GridModRequest>>();
            HashSet<TypeInst> moved = new HashSet<TypeInst>();
            ArrayList<MoveContext> conseqs = new ArrayList<MoveContext>();
            int iter = 0;
            while (true) {
                iter++;
                //System.out.println("\nresolve iteration " + iter);
                coords.clear();
                // pull requests
                for (GridModRequest r : moveQueue) {
                    pullRequests(r, allMoves);
                }
                // build the moved set
                for (int d : allMoves.keySet())
                    for (GridModRequest r : allMoves.get(d))
                        moved.add(r.move.srcType);
                // sort the moveds requests
                for (TypeInst ti : moved) {
                    ti.requests.sort(GridModRequest::compareTo);
                    /*System.out.println("Sorteds " + ti + ": {");
                    for (GridModRequest gmr : ti.requests) {
                        System.out.println(gmr);
                    }
                    System.out.println("}");*/
                }
                boolean superseded;
                int dirs;
                for (int d : allMoves.keySet()) {
                    for (TypeInst ti : moved) {
                        superseded = false;
                        int dir = 0;
                        for (GridModRequest gmr : ti.requests) {
                            if (!gmr.isConsumed) {
                                if (gmr.move.depth < d) { superseded = true; /*System.out.println("found higher priority move");*/ }
                                else if (gmr.move.depth == d) { if (superseded) { /*System.out.println("superseded");*/ gmr.supersede(); /*coords.remove(gmr.move.tile.pos);*/ } superseded = true; }
                                else if (superseded) {  /*System.out.println("superseded");*/ gmr.supersede(); /*coords.remove(gmr.move.tile.pos);*/ }
                            }
                        }
                    }
                    for (GridModRequest r : allMoves.get(d)) {
                        if (r.superseded) continue;
                        if (r.isConsumed) {
                            continue;
                        }
                        if (coords.containsKey(r.move.tile.pos)) {
                            if (coords.get(r.move.tile.pos).isConsumed)
                                coords.put(r.move.tile.pos, r);
                            else {
                                if (coords.get(r.move.tile.pos).move.depth == r.move.depth)
                                    coords.get(r.move.tile.pos).consume();
                                r.consume();
                            }
                        }
                        else {
                            coords.put(r.move.tile.pos, r);
                        }
                    }
                }
                moveQueue.clear();
                // clear the requests
                for (TypeInst ti : moved) {
                    ti.requests.clear();
                }
                for (int d : allMoves.descendingKeySet()) {
                    for (GridModRequest r : allMoves.get(d)) {
                        if (r.move.depth < 0) continue;
                        if (r.superseded) continue;
                        if (r.isConsumed) {
                            //System.out.println("Om nom nom yummy move");
                            if (r.move.tile.pos != null) {
                                r.move.type.tt.blocked.apply(r.move);
                            }
                        }
                        else {
                            //System.out.println("Executing move: " + r);
                            if (r.move.srcTile.pos != null) {
                                if (r.move.srcTile.types.contains(r.move.srcType)) {
                                    r.move.srcTile.types.remove(r.move.srcType);
                                    r.move.tile.types.add(r.move.srcType);
                                    MoveContext pushC = new MoveContext(r.move.curLevel, r.move.tile, r.move.srcType, null, null, r.move.dir, r.move.max, r.move.depth);
                                    r.move.tile.push(pushC);
                                    r.move.srcTile.exit(pushC);
                                    if (r.cascadeContext != null)
                                        conseqs.add(r.cascadeContext);
                                } else {
                                    //System.out.println("JUKED, expected " + r.move.srcType.tt + " at " + r.move.srcTile.pos);
                                    r.consume();
                                    continue;
                                }
                            } else {
                                grid[r.move.tile.pos.y()][r.move.tile.pos.x()].types.add(r.move.srcType);
                            }
                        }
                    }
                }
                allMoves.clear();
                for (MoveContext mc : conseqs) {
                    if (grid[mc.tile.pos.y()][mc.tile.pos.x()].attemptPush(mc)) {
                        mc.srcType.requests.add(mc.request);
                        addMove(mc.request);
                    }
                }
                conseqs.clear();
                moved.clear();
                moveResolveEmpty = moveQueue.isEmpty();
                for (Context c : removeQueue) {
                    c.srcTile.types.remove(c.srcType);
                }
                removeQueue.clear();
                /*System.out.println("End queue: ");
                for (GridModRequest r : moveQueue) {
                    System.out.println(r);
                }*/
                for (int x = 0; x < grid[0].length; x++) {
                    for (int y = 0; y < grid.length; y++) {
                        grid[y][x].rmCycle(this);
                    }
                }
                if (moveQueue.isEmpty()) { break; }
            }
        }

        public void pullRequests(GridModRequest r, TreeMap<Integer, ArrayList<GridModRequest>> moves) {
            if (moves.containsKey(r.move.depth))
                moves.get(r.move.depth).add(r);
            else
                moves.put(r.move.depth, new ArrayList<GridModRequest>(List.of(r)));
            for (GridModRequest gmr : r.queue)
                pullRequests(gmr, moves);
        }

        public void render(Graphics g) {
            RenderContext rc;
            for (int x = 0; x < grid[0].length; x++) {
                for (int y = 0; y < grid.length; y++) {
                    rc = new RenderContext(this, grid[y][x], null, null, offset, null);
                    if (grid[y][x] != null)
                        if (!grid[y][x].types.isEmpty()) {
                            for (TypeInst ti : grid[y][x].types) {
                                if (ti.tt == null) break;
                                rc.srcType = ti;
                                ti.tt.render.accept(g, rc);
                            }
                        }
                }
            }
        }

        public void reload() {
            clearData();
            try {
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
                return;
            }
            for (int x = 0; x < grid[0].length; x++) {
                for (int y = 0; y < grid.length; y++) {
                    grid[y][x].stand(this);
                }
            }
        }

        public void reloadData() {
            for (int x = 0; x < grid[0].length; x++) {
                for (int y = 0; y < grid.length; y++) {
                    for (TypeInst ti : grid[y][x].types) {
                        ti.ti.reloadData();
                    }
                }
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
        public void addMove(GridModRequest gmr) { moveQueue.add(gmr); /*gmr.move.srcType.addRequest(gmr);*/ }

        public void winGame() {
            gameWon = true;
            isPaused = true;
            levelMenu.start();
        }

        public boolean onBoard(Coord c) {
            return c.x >= 0 && c.x < grid[0].length && c.y >= 0 && c.y < grid.length;
        }
        public boolean onBoard(int x, int y) {
            return x >= 0 && x < grid[0].length && y >= 0 && y < grid.length;
        }
    }
    public class LevelEditor extends Level {
        public static LevelEditor instance;
        public Tile[][] copied;
        public boolean dragging = false;
        public boolean hidden = false;
        public LevelEditor() {
            new TileDisplay();
            new TileInventory();
            new WindowBar();
            offset.y=50;
        }

        public void start() {
            super.start();
            hidden = false;
            instance = this;
            MouseDetect.instance.addP(this);
            //TileInventory.instance.start();
            KeyDetect.instance.addP(this);
            WindowBar.instance.start();
        }

        public void destroy() {
            super.destroy();
            hidden = false;
            MouseDetect.instance.removeP(this);
            TileInventory.instance.destroy();
            KeyDetect.instance.removeP(this);
            if (TileDisplay.instance.isOpen)
                TileDisplay.instance.destroy();
            WindowBar.instance.destroy();
        }

        public void hide() {
            super.destroy();
            hidden = true;
            MouseDetect.instance.removeP(this);
            TileInventory.instance.destroy();
            KeyDetect.instance.removeP(this);
            if (TileDisplay.instance.isOpen)
                TileDisplay.instance.destroy();
            WindowBar.instance.destroy();
        }

        public void mousePressed(MouseEvent e) {
            Coord c = MouseDetect.instance.mousePos().subtract(offset);
            c.x /= 20;
            c.y /= 20;
            if (c.x < 0 || c.y < 0) return;
            if (c.x >= grid[0].length || c.y >= grid.length) return;
            if (grid[c.y()][c.x()] != null)
                TileDisplay.instance.openWith(grid[c.y()][c.x()]);
            if (dragging) {
                if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_CONTROL) && KeyDetect.instance.heldKeys.contains(KeyEvent.VK_C)) {
                    int xPos = copied[0][0].pos.x();
                    int yPos = copied[0][0].pos.y();
                    copied = new Tile[Math.abs(yPos - c.y()) + 1][Math.abs(xPos - c.x()) + 1];
                    if (xPos > c.x())
                        xPos = c.x();
                    if (yPos > c.y())
                        yPos = c.y();
                    for (int x = 0; x < copied[0].length; x++) {
                        for (int y = 0; y < copied.length; y++) {
                            copied[y][x] = grid[yPos + y][xPos + x];
                        }
                    }
                }
                dragging = false;
            }
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
                    if (TileDisplay.instance.t != null && TileDisplay.instance.isOpen) {
                        copied = new Tile[1][1];
                        copied[0][0] = TileDisplay.instance.t;
                        dragging = true;
                    }
                }
                if (e == KeyEvent.VK_V) {
                    if (copied != null)
                        if (TileDisplay.instance.t != null && TileDisplay.instance.isOpen) {
                            for (int x = 0; x < copied[0].length; x++) {
                                for (int y = 0; y < copied.length; y++) {
                                    if (onBoard(x+TileDisplay.instance.t.pos.x(), y+TileDisplay.instance.t.pos.y()))
                                        grid[y+TileDisplay.instance.t.pos.y()][x+TileDisplay.instance.t.pos.x()].types = new ArrayList<TypeInst>(copied[y][x].types);
                                }
                            }
                        }
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
                g.drawImage(Sprites.selectedTileTL.i, copied[0][0].pos.x()*20 + offset.x(), copied[0][0].pos.y()*20 + offset.y(), 20, 20, null);
                g.drawImage(Sprites.selectedTileTR.i, copied[0][copied[0].length-1].pos.x()*20 + offset.x(), copied[0][copied[0].length-1].pos.y()*20 + offset.y(), 20, 20, null);
                g.drawImage(Sprites.selectedTileBL.i, copied[copied.length-1][0].pos.x()*20 + offset.x(), copied[copied.length-1][0].pos.y()*20 + offset.y(), 20, 20, null);
                g.drawImage(Sprites.selectedTileBR.i, copied[copied.length-1][copied[0].length-1].pos.x()*20 + offset.x(), copied[copied.length-1][copied[0].length-1].pos.y()*20 + offset.y(), 20, 20, null);
                for (int x = 1; x < copied[0].length-1; x++) {
                    g.drawImage(Sprites.selectedTileTop.i, copied[0][x].pos.x()*20 + offset.x(), copied[0][x].pos.y()*20 + offset.y(), 20, 20, null);
                }
                for (int y = 1; y < copied.length-1; y++) {
                    g.drawImage(Sprites.selectedTileLeft.i, copied[y][0].pos.x()*20 + offset.x(), copied[y][0].pos.y()*20 + offset.y(), 20, 20, null);
                }
                for (int x = 1; x < copied[0].length-1; x++) {
                    g.drawImage(Sprites.selectedTileBottom.i, copied[copied.length-1][x].pos.x()*20 + offset.x(), copied[copied.length-1][x].pos.y()*20 + offset.y(), 20, 20, null);
                }
                for (int y = 1; y < copied.length-1; y++) {
                    g.drawImage(Sprites.selectedTileRight.i, copied[y][copied[0].length-1].pos.x()*20 + offset.x(), copied[y][copied[0].length-1].pos.y()*20 + offset.y(), 20, 20, null);
                }
            }
            g.setColor(Color.black);
            g.drawRect(TileDisplay.instance.t.pos.x()*20 + offset.x(), TileDisplay.instance.t.pos.y()*20 + offset.y(), 20, 20);
        }

        public class TileDisplay extends Script {
            public static TileDisplay instance;
            public Coord pos = new Coord(0,50);
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
                if (!isOpen) return;
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
                        if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_SHIFT)) {
                            if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_CONTROL))
                                t.types.get(i).ti.antinudge(1);
                            else
                                t.types.get(i).ti.antinudge(0);
                        }
                        else {
                            if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_CONTROL))
                                t.types.get(i).ti.nudge(1);
                            else
                                t.types.get(i).ti.nudge(0);
                        }
                    }
                }
            }

            public void mouseReleased(MouseEvent e) { if (!isOpen) return; followMouse = false; }

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
                if (t == null) return;
                TypeInst type;
                for (int x = 0; x < t.types.size(); x++) {
                    type = t.types.get(x);
                    if (type == null) { t.types.remove(x); x--; continue; }
                    type.tt.render.accept(g, new RenderContext(LevelEditor.instance, t, type, new Coord(typePane.og.x + x*22 + 1, typePane.og.y-10), null, null));
                }
            }
        }

        public class TileInventory extends Script {

            public static TileInventory instance;
            public Coord pos = new Coord(0,50);
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
                        if (TileType.tileTypeOf(i) == null) return;
                        TypeInst toAdd = new TypeInst(TileType.tileTypeOf(i), TypeInfo.infoOfType(TileType.tileTypeOf(i)));
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
                g.setColor(Color.black);
                g.drawString(row+1+"",grabber.og.x()+2,grabber.og.y());
                g.setColor(Color.white);
                g.fillRect(typePane.og.x(), (int) (typePane.og.y() - typePane.width), (int) typePane.length.d, (int) typePane.width * 2);
                TypeInst type;
                for (int x = tpr*row; x < TileType.tileTypes().length && x < tpr*(row+1); x++) {
                    type = new TypeInst(TileType.tileTypes()[x], TypeInfo.infoOfType(TileType.tileTypes()[x]));
                    type.tt.render.accept(g, new RenderContext(LevelEditor.instance, null, type, new Coord(typePane.og.x + (x - tpr*row)*22 + 1, typePane.og.y-10), null, null));
                }
            }
        }

        public class WindowBar extends Script {
            public static WindowBar instance;
            public DisplayIcon di;
            public InventoryIcon ii;
            public WindowBar() {
                instance = this;
                di = new DisplayIcon();
                ii = new InventoryIcon();
            }
            public void start() {
                CameraManager.addToDraw(this);
                di.start();
                ii.start();
            }

            public void destroy() {
                CameraManager.removeToDraw(this);
                di.destroy();
                ii.destroy();
            }
            public class DisplayIcon extends Script {

                RectCollider coll = new RectCollider(new Coord(5, 25), new PCoord(0, 40), 20);

                /*public enum ColorState {
                    rest(new Color(0, 255, 0)),
                    hover(new Color(0, 225, 0)),
                    press(new Color(200, 20, 0));
                    Color c;
                    ColorState(Color c) { this.c = c; }
                }*/

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
                    if (TileDisplay.instance.isOpen) {
                        TileDisplay.instance.destroy();
                    }
                    else {
                        TileDisplay.instance.start();
                    }
                    isPressed = false;
                } }

                public void render(Graphics g) {
                    if (isPressed)
                        g.drawImage(Sprites.displaySelectIcon.i, 5, 5, 40, 40, null);
                    else if (isSelected)
                        g.drawImage(Sprites.displayHoverIcon.i, 5, 5, 40, 40, null);
                    else
                        g.drawImage(Sprites.displayIcon.i, 5, 5, 40, 40, null);
                }
            }
            public class InventoryIcon extends Script {
                RectCollider coll = new RectCollider(new Coord(55, 25), new PCoord(0, 40), 20);

                /*public enum ColorState {
                    rest(new Color(0, 255, 0)),
                    hover(new Color(0, 225, 0)),
                    press(new Color(200, 20, 0));
                    Color c;
                    ColorState(Color c) { this.c = c; }
                }*/

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
                    if (TileInventory.instance.isOpen) {
                        TileInventory.instance.destroy();
                    }
                    else {
                        TileInventory.instance.start();
                    }
                    isPressed = false;
                } }

                public void render(Graphics g) {
                    if (isPressed)
                        g.drawImage(Sprites.inventorySelectIcon.i, 55, 5, 40, 40, null);
                    else if (isSelected)
                        g.drawImage(Sprites.inventoryHoverIcon.i, 55, 5, 40, 40, null);
                    else
                        g.drawImage(Sprites.inventoryIcon.i, 55, 5, 40, 40, null);
                }
            }

            public void render(Graphics g) {
                g.setColor(Color.darkGray);
                g.drawRect(0, 0, 1000, 50);
                di.render(g);
                ii.render(g);
            }
        }
    }

    public class GridModRequest implements Comparable<GridModRequest> {
        public MoveContext move;
        public Function<MoveContext, Boolean> cascade;
        public MoveContext cascadeContext;
        public MoveContext src;
        public ArrayList<GridModRequest> queue = new ArrayList<GridModRequest>();
        public int priority = 0;
        public boolean isConsumed = false;
        public boolean superseded = false;
        public GridModRequest(MoveContext c) { move = c; }
        public GridModRequest(MoveContext c, Function<MoveContext, Boolean> cFunc, MoveContext cCont, ArrayList<GridModRequest> q) { move = c; cascade = cFunc; cascadeContext = cCont; queue = q; }
        public void consume() { /*System.out.println("Consuming: " + this);*/ if (isConsumed) return; isConsumed = true; if (src != null) src.request.consume(); if (queue != null) for (GridModRequest gmr : queue) gmr.consume(); }
        public void supersede() { /*System.out.println("Supersede: " + this);*/ if (superseded) return; superseded = true; if (queue != null) for (GridModRequest gmr : queue) gmr.supersede(); }

        public int compareTo(GridModRequest o) {
            //if (priority > o.priority) return 1;
            //if (priority < o.priority) return -1;
            return Integer.compare(move.depth, o.move.depth);
        }

        @Override
        public String toString() {
            return "moving " + move.srcType.tt+" ("+move.srcType.toString().substring(move.srcType.toString().indexOf("@")) + " from " + move.srcTile.pos + " to " + move.tile.pos + (cascade == null ? " no cascade " : " cascading ") + (queue.isEmpty() ? "no queue" : "with queue of length " + queue.size()) + " at depth " + move.depth + " " + move.src + (priority != 0 ? " prioritized at " + priority : "");
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
        public static TestPlayButton testPlay;
        public static WinScreen winScreen;

        public LevelMenu() { resume = new ResumeButton(); restart = new RestartButton(); exit = new ExitButton(); save = new SaveButton(); saveAs = new SaveAsButton(); load = new LoadButton(); testPlay = new TestPlayButton(); }

        public void start() {
            resume.start();
            restart.start();
            exit.start();
            if (Level.activeLevel == LevelEditor.instance) {
                save.start();
                saveAs.start();
                load.start();
                testPlay.start();
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
                testPlay.destroy();
            }
            CameraManager.removeToDraw(this, 1);
        }

        public void render(Graphics g) {
            g.setColor(Color.gray);
            if (Level.activeLevel == LevelEditor.instance) {
                g.fillRect(0,0,250, 520);
                resume.render(g);
                restart.render(g);
                exit.render(g);
                save.render(g);
                saveAs.render(g);
                load.render(g);
                testPlay.render(g);
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
            public void mouseReleased(MouseEvent e) { if (isPressed) { Level.activeLevel.unpause(); } }

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
                Level.activeLevel.unpause();
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
                Level.activeLevel.unpause();
                if (Level.activeLevel == LevelEditor.instance)
                    customMenu.start();
                else {
                    if (LevelEditor.instance != null && LevelEditor.instance.hidden) {
                        Level.activeLevel.destroy();
                        LevelEditor.instance.start();
                        return;
                    }
                    else {
                        levelSelect.start();
                    }
                }
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
                else {
                    if (LevelEditor.instance != null && LevelEditor.instance.hidden)
                        g.drawString("Return to Editor", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
                    else
                        g.drawString("Exit Level", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
                }
            }
        }

        public class LoadButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 260), new PCoord(0, 150), 20);
            TextField txt;
            public int dir;

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
                            if (Level.activeLevel != null)
                                Level.activeLevel.destroy();
                            buildEditor(field, dir);
                            Level.activeLevel.unpause();
                            destroy();
                        }
                        catch (Exception x) {
                            System.out.println("Couldn't build level " + field);
                            x.printStackTrace();
                        }
                        System.out.println("Loading " + field);
                    }
                    else {
                        if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_CONTROL)) {
                            if (e == KeyEvent.VK_COMMA) {
                                System.out.println("Changing directory to " + directory);
                                dir = 0;
                                return;
                            }
                            else if (e == KeyEvent.VK_PERIOD) {
                                System.out.println("Changing directory to " + levelDirectory);
                                dir = 1;
                                return;
                            }
                            else if (e == KeyEvent.VK_SLASH) {
                                System.out.println("Changing directory to " + savesDirectory);
                                dir = 2;
                                return;
                            }
                        }
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
            public int dir;

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
                dir = 2;
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
                        saveEditorAs(field, dir);
                        System.out.println("saving as " + field + " to " + (dir == 2 ? savesDirectory : (dir == 1 ? levelDirectory : directory)));
                        destroy();
                    }
                    else {
                        if (KeyDetect.instance.heldKeys.contains(KeyEvent.VK_CONTROL)) {
                            if (e == KeyEvent.VK_COMMA) {
                                System.out.println("Changing directory to " + directory);
                                dir = 0;
                                return;
                            }
                            else if (e == KeyEvent.VK_PERIOD) {
                                System.out.println("Changing directory to " + levelDirectory);
                                dir = 1;
                                return;
                            }
                            else if (e == KeyEvent.VK_SLASH) {
                                System.out.println("Changing directory to " + savesDirectory);
                                dir = 2;
                                return;
                            }
                        }
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

        public class TestPlayButton extends Script {

            RectCollider coll = new RectCollider(new Coord(50, 470), new PCoord(0, 150), 20);
            TextField txt;

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(200, 20, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            public TestPlayButton() { }

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
                try {
                    Level.activeLevel.unpause();
                    LevelEditor.instance.hide();
                    buildLevel("LevelEditor").start();
                }
                catch (Exception ex) {
                    System.out.println("Build Unsuccessful");
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                    LevelEditor.instance.start();
                }
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
                g.drawString("Test Saved Editor", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
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

        public class WinScreen extends Script {
            public int framesActive;
            public WinScreen() { sim = false; }

            public void start() {
                sim = true;
                framesActive = 0;
                addToUpdate(this);
                CameraManager.addToDraw(this, 1);
            }

            public void destroy() {
                sim = true;
                framesActive = 0;
                removeToUpdate(this);
                CameraManager.removeToDraw(this, 1);
            }

            public void update() {
                framesActive++;
            }

            public void render(Graphics g) {

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

        public boolean attemptPull(MoveContext c) {
            c.tile = this;
            for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.attemptPull.apply(c)) return false;
            }
            return true;
        }
        public boolean attemptPush(MoveContext c) {
            c.tile = this;
            /*for (TypeInst t : types) {
                for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.attemptPush.apply(c)) return false;
            }*/
            for (int x = types.size()-1; x >= 0; x--) {
                c.type = types.get(x);
                if (!types.get(x).tt.attemptPush.apply(c)) return false;
            }
            return true;
        }
        public boolean attemptPass(MoveContext c) {
            c.tile = this;
            for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.attemptPass.apply(c)) return false;
            }
            return true;
        }
        public boolean pull(MoveContext c) {
            c.tile = this;
            for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.pull.apply(c)) return false;
            }
            return true;
        }
        public boolean push(MoveContext c) {
            c.tile = this;
            for (int x = types.size()-1; x >= 0; x--) {
                c.type = types.get(x);
                if (!types.get(x).tt.push.apply(c)) return false;
            }
            return true;
        }
        public boolean pass(MoveContext c) {
            c.tile = this;
            for (TypeInst t : types) {
                c.type = t;
                if (!t.tt.pass.apply(c)) return false;
            }
            return true;
        }
        public boolean exit(MoveContext c) {
            c.tile = this;
            for (int x = types.size()-1; x >= 0; x--) {
                c.type = types.get(x);
                if (!types.get(x).tt.exit.apply(c)) return false;
            }
            return true;
        }
        public void stand(Level l) {
            Context c = new Context(l, this, null);
            for (TypeInst t : types) {
                t.ti.hasPushed = false;
                t.ti.isMoving = false;
                c.srcType = t;
                t.tt.stand.apply(c);
            }
        }
        public void rmCycle(Level l) {
            Context c = new Context(l, this, null);
            for (TypeInst t : types) {
                c.srcType = t;
                t.tt.moveResolveCycle.apply(c);
            }
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
            for (TypeInst ti : types.reversed()) {
                ret += ti.tt + ", ";
            }
            return pos + " with " + ret;
        }
    }
    public enum TileType {
        empty(0,
                GenericInput.ignore.func, GenericHold.ignore.func, GenericHold.ignore.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null)
                g.drawImage(Sprites.grass.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);

            else
                g.drawImage(Sprites.grass.i, c.c.x(), c.c.y(), 20, 20, null);
        }),
        wall(1,
                GenericInput.ignore.func, GenericHold.ignore.func, GenericHold.ignore.func,
                GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.block.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null) {
                if (c.srcTile.pos.y()+1<c.curLevel.grid.length) {
                    if (c.curLevel.grid[c.srcTile.pos.y()+1][c.srcTile.pos.x()] != null) {
                        if (c.curLevel.grid[c.srcTile.pos.y()+1][c.srcTile.pos.x()].hasType(1)) {
                            g.drawImage(Sprites.wallTop.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
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
                                g.drawImage(Sprites.wallFront.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                            else
                                g.drawImage(Sprites.wallLeft.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                            return;
                        }
                    }
                }
                if (hasLeft)
                    g.drawImage(Sprites.wallRight.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                else
                    g.drawImage(Sprites.wallPillar.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
            }
            else {
                g.drawImage(Sprites.wallPillar.i, c.c.x(), c.c.y(), 20, 20, null);
            }
        }),
        box(2,
                GenericInput.ignore.func, GenericHold.ignore.func, GenericHold.ignore.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.line.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null)
                g.drawImage(Sprites.box.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
            else
                g.drawImage(Sprites.box.i, c.c.x(), c.c.y(), 20, 20, null);
        }),
        player(3,
               GenericInput.follow.func, GenericHold.ignore.func, GenericHold.ignore.func,
               GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
               GenericMove.ignore.func, GenericMove.line.func, GenericMove.ignore.func,
               GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null) {
                if (c.srcType.ti instanceof TypeInfo.PlayerInfo pi) {
                    if (pi.sleepy) {
                        g.drawImage(Sprites.chudRight.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y() + 20, 20, -20, null);
                        return;
                    }
                    if (pi.crippled) {
                        if (pi.dir.y == -1)
                            g.drawImage(Sprites.chudCrippleUp.i, c.srcTile.pos.x() * 20 + c.off.x(), c.srcTile.pos.y() * 20 + c.off.y(), 20, 20, null);
                        else if (pi.dir.y == 1)
                            g.drawImage(Sprites.chudCrippleDown.i, c.srcTile.pos.x() * 20 + c.off.x(), c.srcTile.pos.y() * 20 + c.off.y(), 20, 20, null);
                        else if (pi.dir.x == -1)
                            g.drawImage(Sprites.chudCrippleLeft.i, c.srcTile.pos.x() * 20 + c.off.x(), c.srcTile.pos.y() * 20 + c.off.y(), 20, 20, null);
                        else
                            g.drawImage(Sprites.chudCrippleRight.i, c.srcTile.pos.x() * 20 + c.off.x(), c.srcTile.pos.y() * 20 + c.off.y(), 20, 20, null);
                    }
                    else {
                        if (pi.dir.y == -1)
                            g.drawImage(Sprites.chudUp.i, c.srcTile.pos.x() * 20 + c.off.x(), c.srcTile.pos.y() * 20 + c.off.y(), 20, 20, null);
                        else if (pi.dir.y == 1)
                            g.drawImage(Sprites.chudDown.i, c.srcTile.pos.x() * 20 + c.off.x(), c.srcTile.pos.y() * 20 + c.off.y(), 20, 20, null);
                        else if (pi.dir.x == -1)
                            g.drawImage(Sprites.chudLeft.i, c.srcTile.pos.x() * 20 + c.off.x(), c.srcTile.pos.y() * 20 + c.off.y(), 20, 20, null);
                        else
                            g.drawImage(Sprites.chudRight.i, c.srcTile.pos.x() * 20 + c.off.x(), c.srcTile.pos.y() * 20 + c.off.y(), 20, 20, null);
                    }
                }
                else {
                    g.drawImage(Sprites.chudRight.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                }
            }
            else {
                g.drawImage(Sprites.chudRight.i, c.c.x(), c.c.y(), 20, 20, null);
            }
        }),
        belt(4,
                GenericInput.ignore.func, GenericHold.ignore.func, GenericHold.attemptConvey.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null) {
                if (c.srcType.ti instanceof TypeInfo.BeltInfo bi) {
                    if (bi.dir.y == -1)
                        g.drawImage(Sprites.beltUp.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                    else if (bi.dir.y == 1)
                        g.drawImage(Sprites.beltDown.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                    else if (bi.dir.x == -1)
                        g.drawImage(Sprites.beltLeft.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                    else
                        g.drawImage(Sprites.beltRight.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                }
                else {
                    g.drawImage(Sprites.beltRight.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                }
            }
            else
                g.drawImage(Sprites.beltRight.i, c.c.x(), c.c.y(), 20, 20, null);
        }),
        ice(5,
                GenericInput.ignore.func, GenericHold.ignore.func, GenericHold.ignore.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.slide.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            g.setColor(new Color(130,190,255));
            if (c.c == null)
                g.fillRect(c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20);
            else
                g.fillRect(c.c.x(), c.c.y(), 20, 20);
        }),
        gate(6,
                GenericInput.ignore.func, GenericHold.ignore.func, GenericHold.ignore.func,
                GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.ignore.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.check.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            TypeInfo.GateInfo gi = (TypeInfo.GateInfo)c.srcType.ti;
            if (c.c == null) {
                if (TypeInfo.GateInfo.states.get(gi.id)==gi.not) {
                    switch (gi.id) {
                        case 0: g.drawImage(Sprites.closedGate1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.closedGate2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.closedGate3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.closedGate4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.closedGate5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.closedGate6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
                else {
                    switch (gi.id) {
                        case 0: g.drawImage(Sprites.openGate1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.openGate2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.openGate3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.openGate4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.openGate5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.openGate6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
            }
            else {
                if (TypeInfo.GateInfo.states.get(gi.id)==gi.not) {
                    switch (gi.id) {
                        case 0: g.drawImage(Sprites.closedGate1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.closedGate2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.closedGate3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.closedGate4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.closedGate5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.closedGate6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
                else {
                    switch (gi.id) {
                        case 0: g.drawImage(Sprites.openGate1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.openGate2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.openGate3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.openGate4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.openGate5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.openGate6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
            }
        }),
        button(7,
                GenericInput.ignore.func, GenericHold.ignore.func, GenericHold.checkHold.func,
                GenericMove.allow.func, GenericMove.hold.func, GenericMove.allow.func, GenericMove.unhold.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            TypeInfo.ToggleInfo bi = (TypeInfo.ToggleInfo)c.srcType.ti;
            if (c.c == null) {
                if (bi.on) {
                    switch (bi.id) {
                        case 0: g.drawImage(Sprites.buttonDown1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.buttonDown2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.buttonDown3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.buttonDown4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.buttonDown5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.buttonDown6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
                else {
                    switch (bi.id) {
                        case 0: g.drawImage(Sprites.button1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.button2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.button3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.button4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.button5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.button6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
            }
            else {
                if (bi.on) {
                    switch (bi.id) {
                        case 0: g.drawImage(Sprites.buttonDown1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.buttonDown2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.buttonDown3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.buttonDown4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.buttonDown5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.buttonDown6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
                else {
                    switch (bi.id) {
                        case 0: g.drawImage(Sprites.button1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.button2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.button3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.button4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.button5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.button6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
            }
        }),
        spikes(8,
                GenericInput.ignore.func, GenericHold.holdCheckStab.func, GenericHold.checkStab.func,
                GenericMove.allow.func, GenericMove.ignore.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null)
                g.drawImage(Sprites.spikes.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
            else
                g.drawImage(Sprites.spikes.i, c.c.x(), c.c.y(), 20, 20, null);
        }),
        toggleSpikes(9,
                GenericInput.ignore.func, GenericHold.holdCheckStab.func, GenericHold.checkStab.func,
                GenericMove.allow.func, GenericMove.ignore.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            TypeInfo.GateInfo gi = (TypeInfo.GateInfo)c.srcType.ti;
            if (c.c == null) {
                if (TypeInfo.GateInfo.states.get(gi.id)==gi.not) {
                    switch (gi.id) {
                        case 0: g.drawImage(Sprites.spikesUp1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.spikesUp2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.spikesUp3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.spikesUp4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.spikesUp5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.spikesUp6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
                else {
                    switch (gi.id) {
                        case 0: g.drawImage(Sprites.spikesDown1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.spikesDown2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.spikesDown3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.spikesDown4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.spikesDown5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.spikesDown6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
            }
            else {
                if (TypeInfo.GateInfo.states.get(gi.id)==gi.not) {
                    switch (gi.id) {
                        case 0: g.drawImage(Sprites.spikesUp1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.spikesUp2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.spikesUp3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.spikesUp4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.spikesUp5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.spikesUp6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
                else {
                    switch (gi.id) {
                        case 0: g.drawImage(Sprites.spikesDown1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.spikesDown2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.spikesDown3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.spikesDown4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.spikesDown5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.spikesDown6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
            }
        }),
        bomb(10,
                GenericInput.checkBomb.func, GenericHold.tickBomb.func, GenericHold.ignore.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.line.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            TypeInfo.BombInfo bi = (TypeInfo.BombInfo) c.srcType.ti;
            if (c.c == null) {
                if (bi.isLit)
                    g.drawImage(Sprites.bombLit.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                else
                    g.drawImage(Sprites.bomb.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                renderNumbers(c.srcTile.pos.multiply(20).add(5), new Coord(10, 10), bi.countdown, g);
            }
            else {
                if (bi.isLit)
                    g.drawImage(Sprites.bombLit.i, c.c.x(), c.c.y(), 20, 20, null);
                else
                    g.drawImage(Sprites.bomb.i, c.c.x(), c.c.y(), 20, 20, null);
                renderNumbers(c.c.add(5), new Coord(10, 10), bi.countdown, g);
            }
        }),
        fire(11,
                GenericInput.ignore.func, GenericHold.checkBurn.func, GenericHold.ignore.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null)
                g.drawImage(Sprites.fire.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
            else
                g.drawImage(Sprites.fire.i, c.c.x(), c.c.y(), 20, 20, null);
        }),
        eternalFire(12,
                GenericInput.ignore.func, GenericHold.checkBurn.func, GenericHold.ignore.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null)
                g.drawImage(Sprites.eternalFire.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
            else
                g.drawImage(Sprites.eternalFire.i, c.c.x(), c.c.y(), 20, 20, null);
        }),
        flag(13,
                GenericInput.ignore.func, GenericHold.checkFlag.func, GenericHold.ignore.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            TypeInfo.FlagInfo fi = (TypeInfo.FlagInfo) c.srcType.ti;
            if (c.c == null) {
                if (fi.isUp)
                    g.drawImage(Sprites.flag.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
                else
                    g.drawImage(Sprites.flagDown.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
            }
            else {
                if (fi.isUp)
                    g.drawImage(Sprites.flag.i, c.c.x(), c.c.y(), 20, 20, null);
                else
                    g.drawImage(Sprites.flagDown.i, c.c.x(), c.c.y(), 20, 20, null);
            }

        }),
        paster(14,
                GenericInput.ignore.func, GenericHold.uncool.func, GenericHold.ignore.func,
                GenericMove.allow.func, GenericMove.paste.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            if (c.c == null)
                g.drawImage(Sprites.pasterRight.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null);
            else
                g.drawImage(Sprites.pasterRight.i, c.c.x(), c.c.y(), 20, 20, null);
        }),

        // could be interesting to have unique behaviour if multiple valid targets
        watcher(15,
                GenericInput.ignore.func, GenericHold.ignore.func, GenericHold.checkWatch.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.allow.func, GenericMove.allow.func, GenericMove.allow.func,
                GenericMove.ignore.func, GenericMove.ignore.func, (Graphics g, RenderContext c) -> {
            TypeInfo.WatcherInfo wi = (TypeInfo.WatcherInfo)c.srcType.ti;
            if (c.c == null) {
                if (wi.dir.x == 0 && wi.dir.y == 0) {
                    switch (wi.id) {
                        case 0: g.drawImage(Sprites.watcher1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.watcher2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.watcher3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.watcher4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.watcher5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.watcher6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
                else if (wi.dir.x == 1 && wi.dir.y == 0) {
                    switch (wi.id) {
                        case 0: g.drawImage(Sprites.watcherRight1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.watcherRight2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.watcherRight3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.watcherRight4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.watcherRight5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.watcherRight6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
                else if (wi.dir.x == 0 && wi.dir.y == 1) {
                    switch (wi.id) {
                        case 0: g.drawImage(Sprites.watcherDown1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.watcherDown2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.watcherDown3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.watcherDown4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.watcherDown5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.watcherDown6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
                else if (wi.dir.x == -1 && wi.dir.y == 0) {
                    switch (wi.id) {
                        case 0: g.drawImage(Sprites.watcherLeft1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.watcherLeft2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.watcherLeft3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.watcherLeft4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.watcherLeft5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.watcherLeft6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
                else if (wi.dir.x == 0 && wi.dir.y == -1) {
                    switch (wi.id) {
                        case 0: g.drawImage(Sprites.watcherUp1.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.watcherUp2.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.watcherUp3.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.watcherUp4.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.watcherUp5.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.watcherUp6.i, c.srcTile.pos.x()*20 + c.off.x(), c.srcTile.pos.y()*20 + c.off.y(), 20, 20, null); break;
                    }
                }
            }
            else {
                if (wi.dir.x == 0 && wi.dir.y == 0) {
                    switch (wi.id) {
                        case 0: g.drawImage(Sprites.watcher1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.watcher2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.watcher3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.watcher4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.watcher5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.watcher6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
                else if (wi.dir.x == 1 && wi.dir.y == 0) {
                    switch (wi.id) {
                        case 0: g.drawImage(Sprites.watcherRight1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.watcherRight2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.watcherRight3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.watcherRight4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.watcherRight5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.watcherRight6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
                else if (wi.dir.x == 0 && wi.dir.y == 1) {
                    switch (wi.id) {
                        case 0: g.drawImage(Sprites.watcherDown1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.watcherDown2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.watcherDown3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.watcherDown4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.watcherDown5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.watcherDown6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
                else if (wi.dir.x == -1 && wi.dir.y == 0) {
                    switch (wi.id) {
                        case 0: g.drawImage(Sprites.watcherLeft1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.watcherLeft2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.watcherLeft3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.watcherLeft4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.watcherLeft5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.watcherLeft6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
                else if (wi.dir.x == 0 && wi.dir.y == -1) {
                    switch (wi.id) {
                        case 0: g.drawImage(Sprites.watcherUp1.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 1: g.drawImage(Sprites.watcherUp2.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 2: g.drawImage(Sprites.watcherUp3.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 3: g.drawImage(Sprites.watcherUp4.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 4: g.drawImage(Sprites.watcherUp5.i, c.c.x(), c.c.y(), 20, 20, null); break;
                        case 5: g.drawImage(Sprites.watcherUp6.i, c.c.x(), c.c.y(), 20, 20, null); break;
                    }
                }
            }
        });
        public Function<InputContext, Boolean> playerInput;
        public Function<Context, Boolean> stand;
        public Function<Context, Boolean> moveResolveCycle;
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
        TileType(int id, Function<InputContext, Boolean> pi, Function<Context, Boolean> s, Function<Context, Boolean> mrc, Function<MoveContext, Boolean> pull, Function<MoveContext, Boolean> push, Function<MoveContext, Boolean> pass, Function<MoveContext, Boolean> exit, Function<MoveContext, Boolean> attPull, Function<MoveContext, Boolean> attPush, Function<MoveContext, Boolean> attpass, Function<MoveContext, Boolean> blocked, Function<MoveContext, Boolean> interact, BiConsumer<Graphics, RenderContext> r) {
            this.id = id;
            playerInput = pi;
            stand = s;
            moveResolveCycle = mrc;
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
            return new TileType[]{empty, wall, box, player, belt, ice, gate, button, spikes, toggleSpikes, bomb, fire, eternalFire, flag, paster, watcher};
        }
        public static enum GenericInput {
            follow((InputContext c) -> {
                if (c.srcType.ti instanceof TypeInfo.PlayerInfo pi) {
                    if (pi.sleepy) return false;
                }
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
                    if (c.srcType.ti instanceof TypeInfo.PlayerInfo pi) { pi.dir = moveDir; if (pi.crippled) return false; }
                    Coord toPos = c.srcTile.pos.add(moveDir);
                    if (toPos.y() < 0 || toPos.y() >= c.curLevel.grid.length) return false;
                    if (toPos.x() < 0 || toPos.x() >= c.curLevel.grid[0].length) return false;
                    if (c.curLevel.grid[toPos.y()][toPos.x()] == null) return false;
                    GridModRequest gmr = c.curLevel.getMove(c.newMoveContext(c.curLevel, c.srcTile, c.srcType, c.curLevel.grid[toPos.y()][toPos.x()], null, moveDir, 2, 1));
                    gmr.move.src = "from input";
                    //System.out.println(gmr);
                    if (!c.curLevel.grid[toPos.y()][toPos.x()].attemptPush(gmr.move)) return false;
                    c.srcType.addRequest(gmr);
                    c.curLevel.addMove(gmr);
                }
                return true;
            }),
            listen((InputContext c) -> {return true;}),
            ignore((InputContext c) -> {return false;}),
            checkBomb((InputContext c) -> {
                TypeInfo.BombInfo bi = (TypeInfo.BombInfo) c.srcType.ti;
                if (bi.countdown <= 0 && bi.isLit) {
                    MoveContext mc;
                    GridModRequest gmr;
                    if (c.srcTile.pos.x > 0) {
                        mc = c.newMoveContext(c.curLevel, c.srcTile, c.srcType, null, null, new Coord(-1, 0), 3, 1);
                        mc.src = "from bomb";
                        gmr = c.curLevel.getMove(mc);
                        if (c.curLevel.grid[c.srcTile.pos.y()][c.srcTile.pos.x()-1].attemptPush(mc)) {
                            for (GridModRequest qgmr : gmr.queue)
                                c.curLevel.addMove(qgmr);
                        }
                    }
                    if (c.srcTile.pos.x < c.curLevel.grid[0].length-1) {
                        mc = c.newMoveContext(c.curLevel, c.srcTile, c.srcType, null, null, new Coord(1, 0), 3, 1);
                        mc.src = "from bomb";
                        gmr = c.curLevel.getMove(mc);
                        if (c.curLevel.grid[c.srcTile.pos.y()][c.srcTile.pos.x()+1].attemptPush(mc)) {
                            for (GridModRequest qgmr : gmr.queue)
                                c.curLevel.addMove(qgmr);
                        }
                    }
                    if (c.srcTile.pos.y > 0) {
                        mc = c.newMoveContext(c.curLevel, c.srcTile, c.srcType, null, null, new Coord(0, -1), 3, 1);
                        mc.src = "from bomb";
                        gmr = c.curLevel.getMove(mc);
                        if (c.curLevel.grid[c.srcTile.pos.y()-1][c.srcTile.pos.x()].attemptPush(mc)) {
                            for (GridModRequest qgmr : gmr.queue)
                                c.curLevel.addMove(qgmr);
                        }
                    }
                    if (c.srcTile.pos.y < c.curLevel.grid.length-1) {
                        mc = c.newMoveContext(c.curLevel, c.srcTile, c.srcType, null, null, new Coord(0, 1), 3, 1);
                        mc.src = "from bomb";
                        gmr = c.curLevel.getMove(mc);
                        if (c.curLevel.grid[c.srcTile.pos.y()+1][c.srcTile.pos.x()].attemptPush(mc)) {
                            for (GridModRequest qgmr : gmr.queue)
                                c.curLevel.addMove(qgmr);
                        }
                    }
                    c.curLevel.removeQueue.add(c.newContext(c.curLevel, c.srcTile, c.srcType));
                    return true;
                }
                return false;
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
                if (c.max == -1) return false;
                if (c.type.ti instanceof TypeInfo.BombInfo bi) {
                    if (bi.countdown <= 0 && bi.isLit) return false;
                }
                if (c.tile.pos.y() + c.dir.y() < 0 || c.tile.pos.y() + c.dir.y() >= c.curLevel.grid.length) return false;
                if (c.tile.pos.x() + c.dir.x() < 0 || c.tile.pos.x() + c.dir.x() >= c.curLevel.grid[0].length) return false;
                if (c.curLevel.grid[c.tile.pos.y() + c.dir.y()][c.tile.pos.x() + c.dir.x()] == null) return false;
                if (c.max-1 == -1) return false;
                GridModRequest gmr = c.curLevel.getMove(c.newMoveContext(c.curLevel, c.tile, c.type, null, null, c.dir, c.max-1, c.depth+1));
                gmr.priority = c.request.priority;
                gmr.move.src = "push " + c.src;
                gmr.src = c;
                if (!c.curLevel.grid[c.tile.pos.y() + c.dir.y()][c.tile.pos.x() + c.dir.x()].attemptPush(gmr.move)) return false;
                c.srcType.ti.hasPushed = true;
                c.type.addRequest(gmr);
                c.request.queue.add(gmr);
                return true;
            }),
            ignore((MoveContext c) -> {return false;}),
            slide((MoveContext c) -> {
                if (c.max == -1) return false;
                /*
                check and return if has pushed
                */
                Coord toPos = c.tile.pos.add(c.dir);
                if (toPos.y() < 0 || toPos.y() >= c.curLevel.grid.length) return true;
                if (toPos.x() < 0 || toPos.x() >= c.curLevel.grid[0].length) return true;
                if (c.curLevel.grid[toPos.y()][toPos.x()] == null) return true;
                MoveContext move;
                if (c.srcType.ti.hasPushed)
                    move = c.newMoveContext(c.curLevel, c.tile, c.srcType, c.curLevel.grid[toPos.y()][toPos.x()], null, c.dir, 0, 1);
                else
                    move = c.newMoveContext(c.curLevel, c.tile, c.srcType, c.curLevel.grid[toPos.y()][toPos.x()], c.srcType, c.dir, 1, 1);
                move.src = "from slide " + c.srcType;
                GridModRequest gmr = c.curLevel.getMove(move);
                gmr.priority = -1;
                c.request.cascadeContext = move;
                return true;
            }),
            block((MoveContext c) -> {return false;}),
            hold((MoveContext c) -> {
                if (c.max == -1) return false;
                System.out.println("Check Hold\n");
                if (c.type.ti instanceof TypeInfo.ToggleInfo ti) {
                    if (ti.on) return true;
                    System.out.println("Hold Button\n");
                    ti.on = true;
                    TypeInfo.GateInfo.states.put(ti.id, true);
                    TypeInfo.ToggleInfo.helds.put(ti.id, TypeInfo.ToggleInfo.helds.get(ti.id)+1);
                }
                checkToggles();
                return true; }),
            unhold((MoveContext c) -> {
                if (c.max == -1) return false;
                System.out.println("Check Unhold\n");
                if (c.type.ti instanceof TypeInfo.ToggleInfo ti) {
                    if (!ti.on) return false;
                    System.out.println("Off Button\n");
                    ti.on = false;
                    if (TypeInfo.ToggleInfo.helds.get(ti.id) <= 1) {
                        TypeInfo.GateInfo.states.put(ti.id, false);
                    }
                    TypeInfo.ToggleInfo.helds.put(ti.id, TypeInfo.ToggleInfo.helds.get(ti.id)-1);
                }
                checkToggles();
                return false;}),
            stab((MoveContext c) -> {
                if (c.max == -1) return false;
                if (c.srcType.ti instanceof TypeInfo.PlayerInfo pi) {
                    c.curLevel.gameOver = true;
                    pi.sleepy = true;
                    TypeInfo.PlayerInfo.awake--;
                }
                return true;
            }),
            checkStab((MoveContext c) -> {
                if (c.max == -1) return false;
                if (c.srcType.ti instanceof TypeInfo.PlayerInfo pi) {
                    if (c.type.ti instanceof TypeInfo.GateInfo gi) {
                        System.out.println(c.tile + ", on = " + TypeInfo.GateInfo.states.get(gi.id));
                        if (TypeInfo.GateInfo.states.get(gi.id) == gi.not) {
                            pi.sleepy = true;
                            TypeInfo.PlayerInfo.awake--;
                            if (TypeInfo.PlayerInfo.awake <= 0)
                                c.curLevel.gameOver = true;
                        }
                    }
                    else {
                        pi.sleepy = true;
                        TypeInfo.PlayerInfo.awake--;
                        if (TypeInfo.PlayerInfo.awake <= 0)
                            c.curLevel.gameOver = true;
                    }
                }
                return true;
            }),
            paste((MoveContext c) -> {
                if (c.max == -1) return false;
                TypeInfo.PasteInfo pi = (TypeInfo.PasteInfo) c.type.ti;
                if (pi.onCool) return false;
                if (c.tile.pos.x+1 < c.curLevel.grid[0].length) {
                    //ArrayList<TypeInst> types = c.curLevel.grid[c.tile.pos.y()][c.tile.pos.x()+1].types;
                    /*if (c.curLevel.grid[c.tile.pos.y()][c.tile.pos.x()+1].types.getLast().tt == TileType.empty) return false;
                    TypeInst ti = c.curLevel.grid[c.tile.pos.y()][c.tile.pos.x()+1].types.getLast();
                    ti.tt = c.srcType.tt;
                    ti.ti = c.srcType.ti;
                    pi.onCool = true;*/
                    for (TypeInst ti : c.curLevel.grid[c.tile.pos.y()][c.tile.pos.x()+1].types.reversed()) {
                        if (ti.tt.attemptPush != line.func) continue;
                        ti.tt = c.srcType.tt;
                        ti.ti = c.srcType.ti;
                        pi.onCool = true;
                        break;
                    }
                }
                return true;
            });
            public Function<MoveContext, Boolean> func;
            GenericMove(Function<MoveContext, Boolean> f) { func = f; }
        }
        public static enum GenericHold {
            listen((Context c) -> {return true;}),
            ignore((Context c) -> {return false;}),
            checkStab((Context c) -> {
                if (c.curLevel.moveResolveEmpty) return false;
                if (c.srcType.ti instanceof TypeInfo.GateInfo gi) {
                    if (TypeInfo.GateInfo.states.get(gi.id) != gi.not) return false;
                }
                for (TypeInst ti : c.srcTile.types.reversed()) {
                    if (ti == c.srcType) break;
                    // nothing else gets stab checked, so this is faster for now
                    if (ti.tt == TileType.player) {
                        for (GridModRequest gmr : c.curLevel.moveQueue) {
                            if (gmr.move.srcType == ti) {
                                if (!((TypeInfo.PlayerInfo) ti.ti).sleepy) {
                                    ((TypeInfo.PlayerInfo) ti.ti).sleepy = true;
                                    TypeInfo.PlayerInfo.awake--;
                                }
                                break;
                            }
                        }
                    }
                    /*for (GridModRequest gmr : c.curLevel.moveQueue) {
                        if (gmr.move.srcType == ti) {
                            if (ti.tt == TileType.player) {
                                if (!((TypeInfo.PlayerInfo) ti.ti).sleepy) {
                                    ((TypeInfo.PlayerInfo) ti.ti).sleepy = true;
                                    TypeInfo.PlayerInfo.awake--;
                                }
                            }
                            break;
                        }
                    }*/
                }
                return true;
            }),
            holdCheckStab((Context c) -> {
                if (c.curLevel.settingUp) return false;
                if (c.srcType.ti instanceof TypeInfo.GateInfo gi) {
                    if (TypeInfo.GateInfo.states.get(gi.id) != gi.not) return false;
                }
                for (TypeInst ti : c.srcTile.types) {
                    if (ti.tt == TileType.player) {
                        if (! ((TypeInfo.PlayerInfo)ti.ti).sleepy) {
                            ((TypeInfo.PlayerInfo) ti.ti).sleepy = true;
                            TypeInfo.PlayerInfo.awake--;
                        }
                    }
                }
                return true;
            }),
            checkBurn((Context c) -> {
                boolean doRemove = c.srcType.tt == TileType.fire;
                for (TypeInst ti : c.srcTile.types) {
                    if (ti.ti instanceof TypeInfo.PlayerInfo pi) {
                        if (!pi.sleepy)
                            TypeInfo.PlayerInfo.awake--;
                        pi.sleepy = true;
                        if (doRemove) { doRemove = false; c.curLevel.removeQueue.add(c.newContext(c.curLevel, c.srcTile, c.srcType)); }
                    } else if (ti.ti instanceof TypeInfo.BombInfo bi) {
                        if (!bi.isLit) bi.countdown++;
                        bi.isLit = true;
                        if (doRemove) { doRemove = false; c.curLevel.removeQueue.add(c.newContext(c.curLevel, c.srcTile, c.srcType)); }
                    }
                }
                return false;
            }),
            tickBomb((Context c) -> {
                TypeInfo.BombInfo bi = (TypeInfo.BombInfo) c.srcType.ti;
                if (bi.isLit) {
                    bi.countdown--;
                }
                return false;
            }),
            checkFlag((Context c) -> {
                TypeInfo.FlagInfo fi = (TypeInfo.FlagInfo) c.srcType.ti;
                boolean wasUp = fi.isUp;
                fi.isUp = false;
                for (TypeInst ti : c.srcTile.types) {
                    if (ti.tt == TileType.player) {
                        fi.isUp = true;
                        break;
                    }
                }
                if (fi.isUp && !wasUp) {
                    TypeInfo.FlagInfo.numUp++;
                    if (TypeInfo.FlagInfo.numUp == 1) {
                        c.curLevel.winGame();
                    }
                }
                else if (!fi.isUp && wasUp)
                    TypeInfo.FlagInfo.numUp--;
                return true;
            }),
            uncool((Context c) -> {
                if (c.srcType.ti instanceof TypeInfo.PasteInfo pi) {
                    pi.onCool = false;
                }
                return true;
            }),
            attemptConvey((Context c) -> {
                if (c.curLevel.settingUp) return false;
                //System.out.println("Do convey: " + c.curLevel.moveResolveEmpty);
                Coord toPos = new Coord(c.srcTile.pos);
                toPos.x += ((TypeInfo.BeltInfo) c.srcType.ti).dir.x;
                toPos.y += ((TypeInfo.BeltInfo) c.srcType.ti).dir.y;
                if (toPos.y() < 0 || toPos.y() >= c.curLevel.grid.length) return false;
                if (toPos.x() < 0 || toPos.x() >= c.curLevel.grid[0].length) return false;
                if (c.curLevel.grid[toPos.y()][toPos.x()] == null) return false;
                for (TypeInst ti : c.curLevel.grid[c.srcTile.pos.y()][c.srcTile.pos.x()].types.reversed()) {
                    if (ti.tt == belt) break;
                    for (GridModRequest gmr : ti.requests) {
                        if (gmr.priority == -1 && gmr.move.depth == 1) { return false; }
                    }
                    if (!TypeInfo.BeltInfo.moved.contains(ti)) {
                        //System.out.println("Push thingy: "+ti.tt);
                        TypeInfo.BeltInfo.moved.add(ti);
                        GridModRequest gmr = c.curLevel.getMove(c.newMoveContext(c.curLevel, c.srcTile, c.srcType, c.srcTile, ti, ((TypeInfo.BeltInfo) c.srcType.ti).dir, 2, -1));
                        gmr.move.src = "from belt";
                        if (!ti.tt.attemptPush.apply(gmr.move)) return false;
                        for (GridModRequest subGmr : gmr.queue) {
                            c.srcType.addRequest(subGmr);
                            c.curLevel.addMove(subGmr);
                        }
                    }
                }
                return true;
            }),
            checkHold((Context c) -> {
                if (!c.curLevel.settingUp) return false;
                for (TypeInst ti : c.curLevel.grid[c.srcTile.pos.y()][c.srcTile.pos.x()].types.reversed()) {
                    if (ti.tt == button) break;
                    else {
                        TypeInfo.ToggleInfo tgi = (TypeInfo.ToggleInfo) c.srcType.ti;
                        TypeInfo.ToggleInfo.helds.put(tgi.id, TypeInfo.ToggleInfo.helds.get(tgi.id)+1);
                        TypeInfo.GateInfo.states.put(tgi.id, true);
                    }
                }
                checkToggles();
                return true;
            }),
            checkWatch((Context c) -> {
                TypeInfo.WatcherInfo wi = (TypeInfo.WatcherInfo) c.srcType.ti;
                boolean wasActive = wi.on;
                wi.gaze.clear();
                if (c.srcTile.types.getLast().tt == TileType.player) {
                    wi.on = true;
                    wi.dir.x = 0;
                    wi.dir.y = 0;
                    wi.gaze.add(c.srcTile.pos);
                }
                else {
                    int[] closests = new int[4];
                    Coord checkPos;
                    Coord dir;
                    MoveContext lookMove;
                    for (int x = 0; x < 4; x++) {
                        checkPos = c.srcTile.pos;
                        dir = switch (x) {
                            case 0 -> new Coord(0, 1);
                            case 1 -> new Coord(0, -1);
                            case 2 -> new Coord(1, 0);
                            case 3 -> new Coord(-1, 0);
                            default -> new Coord();
                        };
                        lookMove = c.newMoveContext(c.curLevel, c.srcTile, c.srcType, null, null, dir, -1, -1);
                        while (true) {
                            if (!c.curLevel.onBoard(checkPos)) {
                                closests[x] = -1;
                                break;
                            }
                            if (c.curLevel.grid[checkPos.y()][checkPos.x()].types.getLast().tt == TileType.player) break;
                            if (!c.curLevel.grid[checkPos.y()][checkPos.x()].attemptPush(lookMove)) {
                                closests[x] = -1;
                                break;
                            }
                            closests[x]++;
                            checkPos = checkPos.add(dir);
                        }
                    }
                    int best = -1;
                    for (int x = 0; x < 4; x++) {
                        if (closests[x] != -1) {
                            if (best == -1) best = x;
                            else if (closests[x] < closests[best]) best = x;
                        }
                    }
                    if (best == -1)
                        wi.on = false;
                    else {
                        wi.on = true;
                        dir = switch (best) {
                            case 0 -> new Coord(0, 1);
                            case 1 -> new Coord(0, -1);
                            case 2 -> new Coord(1, 0);
                            case 3 -> new Coord(-1, 0);
                            default -> new Coord();
                        };
                        wi.dir.copy(dir);
                        checkPos = c.srcTile.pos;
                        for (int x = 0; x <= closests[best]; x++) {
                            wi.gaze.add(checkPos);
                            checkPos = checkPos.add(dir);
                        }
                    }
                }
                if (wi.on && !wasActive) {
                    TypeInfo.ToggleInfo.helds.put(wi.id, TypeInfo.ToggleInfo.helds.get(wi.id)+1);
                }
                if (!wi.on && wasActive) {
                    TypeInfo.ToggleInfo.helds.put(wi.id, TypeInfo.ToggleInfo.helds.get(wi.id)-1);
                }
                checkToggles();
                return true;
            });
            public Function<Context, Boolean> func;
            GenericHold(Function<Context, Boolean> f) { func = f; }
        }
    }

    public static void checkToggles() {
        for (int x = 0; x < 6; x++) {
            if (TypeInfo.ToggleInfo.helds.get(x) != null)
                TypeInfo.GateInfo.states.put(x, TypeInfo.ToggleInfo.helds.get(x) > 0);
        }
    }

    public enum Input {
        up(KeyEvent.VK_W),
        down(KeyEvent.VK_S),
        left(KeyEvent.VK_A),
        right(KeyEvent.VK_D),
        wait(KeyEvent.VK_SPACE),
        esc(KeyEvent.VK_ESCAPE),
        debug(KeyEvent.VK_SHIFT),
        undo(KeyEvent.VK_Z);
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
        public RenderContext newRenderContext(Level l, Tile t, TypeInst tt, Coord pos, Coord offset, Coord scale) { return new RenderContext(l, t, tt, pos, offset, scale); }
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
        Coord off;
        Coord s;
        public RenderContext(Level l, Tile t, TypeInst tt, Coord pos, Coord offset, Coord scale) { super(l, t, tt); c = pos; off = offset; s = scale; }
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

        public String src = "";

        public MoveContext(Level l, Tile t, TypeInst tt, Tile fromTile, TypeInst toMove, Coord d, int maxD, int depth) { super(l, t, tt); /*if (srcT != null) System.out.println("attempt move at " + srcT.pos);*/ dir = d; tile = fromTile; type = toMove; max = maxD; this.depth = depth; }
        public MoveContext(Level l, Tile t, TypeInst tt, Tile fromTile, TypeInst toMove, Coord d, int maxD, int depth, GridModRequest gmr) { super(l, t, tt); /*if (srcT != null) System.out.println("attempt move at " + srcT.pos);*/ dir = d; tile = fromTile; type = toMove; max = maxD; this.depth = depth; request = gmr; }

        @Override
        public String toString() {
            return super.toString() + " moving " + (type == null ? "NULL type" : type.tt) + " on " + (tile == null ? "NULL tile" : tile.pos) + " by " + dir + "at depth " + depth + " " + src;
        }
    }
    public class TypeInst {
        // Coord tpos = new Coord(); // could solve the problem of type removal not tracking moving types
        public ArrayList<GridModRequest> requests = new ArrayList<GridModRequest>();
        public TypeInfo ti;
        public TileType tt;
        public TypeInst(TileType tt) { this.tt = tt; ti = new TypeInfo(); }
        public TypeInst(TileType tt, TypeInfo ti) { this.tt = tt; this.ti = ti; }

        public void addRequest(GridModRequest gmr) {
            //System.out.println("Adding request to " + this + " " + gmr.move.src);
            requests.add(gmr);
        }

        public String toString() {
            return tt+" ("+super.toString().substring(super.toString().indexOf("@"))+")";
        }
    }
    public static class TypeInfo {
        public boolean isMoving = false;
        public boolean hasPushed = false;
        public static void clear() {
            PlayerInfo.awake = 0;
            BeltInfo.moved.clear();
            GateInfo.states.clear();
            ToggleInfo.helds.clear();
            FlagInfo.numUp = 0;
        }

        public static class PlayerInfo extends TypeInfo {
            public Coord dir;
            public boolean sleepy = false;
            public boolean crippled = false;
            public static int awake = 0;

            /*
            Might need a contingency tracker for cases where player immediately steps on a toggleable spike.
            */

            public PlayerInfo() {
                awake++;
                dir = new Coord(1, 0);
            }

            public PlayerInfo(Coord d, boolean a, boolean c) {
                sleepy = !a;
                if (a)
                    awake++;
                crippled = c;
                dir = d;
            }

            @Override
            public void nudge(int i) {
                switch (i) {
                    case 0:
                        sleepy = !sleepy; break;
                    case 1:
                        crippled = !crippled; break;
                }
            }
            @Override
            public void antinudge(int i) { nudge(i); }
        }
        public static class BeltInfo extends TypeInfo {
            public static HashSet<TypeInst> moved = new HashSet<TypeInst>();
            public Coord dir;

            public BeltInfo() {
                dir = new Coord(1, 0);
            }

            public BeltInfo(Coord d) {
                dir = d;
            }

            @Override
            public void nudge(int i) {
                dir.turn90(1);
            }

            @Override
            public void antinudge(int i) {
                dir.turn90(3);
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
            public void nudge(int i) {
                switch (i) {
                    case 1:
                        not = !not;
                        break;
                    default:
                        id++;
                        id %= 6;
                        states.put(id, false);
                        break;
                }
            }

            @Override
            public void antinudge(int i) {
                switch (i) {
                    case 1:
                        not = !not;
                        break;
                    default:
                        id--;
                        id = (id+6) % 6;
                        states.put(id, false);
                        break;
                }
            }

            @Override
            public void reloadData() {
                states.put(id, false);
            }
        }
        public static class ToggleInfo extends TypeInfo {
            public static TreeMap<Integer, Integer> helds = new TreeMap<Integer, Integer>();
            public Integer id;
            public boolean on = false;

            public ToggleInfo() {
                id = 0;
                GateInfo.states.put(id, false);
                helds.put(id, 0);
            }

            public ToggleInfo(int n) {
                id = n;
                GateInfo.states.put(id, false);
                helds.put(id, 0);
            }

            @Override
            public void nudge(int i) {
                id++;
                id %= 6;
                GateInfo.states.put(id, false);
                helds.put(id, 0);
            }

            @Override
            public void antinudge(int i) {
                id--;
                id = (id+6) % 6;
                GateInfo.states.put(id, false);
                helds.put(id, 0);
            }

            @Override
            public void reloadData() {
                GateInfo.states.put(id, false);
                helds.put(id, 0);
            }
        }
        public static class BombInfo extends TypeInfo {
            public int countdown;
            public boolean isLit;
            public BombInfo() {
                countdown = 0;
                isLit = false;
            }
            public BombInfo(int c, boolean l) {
                countdown = c;
                isLit = l;
            }

            @Override
            public void nudge(int i) {
                countdown++;
            }

            @Override
            public void antinudge(int i) {
                countdown--;
            }
        }
        public static class FlagInfo extends TypeInfo {
            public boolean isUp;
            public static int numUp = 0;
            public FlagInfo() {
                isUp = false;
            }
            public FlagInfo(boolean u) {
                isUp = u;
                if (isUp) numUp++;
                //else numUp--;
            }

            @Override
            public void nudge(int i) {
                if (isUp) numUp--;
                else numUp++;
                isUp = !isUp;
            }
            @Override
            public void antinudge(int i) { nudge(); }
        }
        public static class PasteInfo extends TypeInfo {
            public boolean onCool = false;
            public PasteInfo() { onCool = false; }
        }
        public static class WatcherInfo extends ToggleInfo {
            public Coord dir;
            public ArrayList<Coord> gaze = new ArrayList<Coord>();

            WatcherInfo() { super(); dir = new Coord(); GateInfo.states.put(0, false); ToggleInfo.helds.put(id, 0); }
            WatcherInfo(int i, Coord d) { super(i); dir = d; }

            @Override
            public void nudge(int i) {
                switch (i) {
                    case 1:
                        if (dir.x == 0 && dir.y == 0)
                            dir.x = 1;
                        else {
                            if (dir.x == 0 && dir.y == -1)
                                dir.y = 0;
                            else
                                dir.turn90(1);
                        }
                        break;
                    default:
                        id++;
                        id %= 6;
                        GateInfo.states.put(id, false);
                        ToggleInfo.helds.put(id, 0);
                        break;
                }
            }

            @Override
            public void antinudge(int i) {
                switch (i) {
                    case 1:
                        if (dir.x == 0 && dir.y == 0)
                            dir.y = -1;
                        else {
                            if (dir.x == 1 && dir.y == 0)
                                dir.x = 0;
                            else
                                dir.turn90(3);
                        }
                        break;
                    default:
                        id--;
                        id = (id+6) % 6;
                        GateInfo.states.put(id, false);
                        ToggleInfo.helds.put(id, 0);
                        break;
                }
            }
        }

        public void nudge(int i) {
            System.out.println("nudged");
        }

        public void nudge() {
            System.out.println("nudged");
        }

        public void antinudge(int i) {
            System.out.println("anti-nudged");
        }

        public void antinudge() {
            System.out.println("anti-nudged");
        }

        public void reloadData() {}

        public static TypeInfo infoOfType(TileType tt) {
            if (tt == TileType.belt) return new BeltInfo();
            if (tt == TileType.player) return new PlayerInfo();
            if (tt == TileType.gate || tt == TileType.toggleSpikes) return new GateInfo();
            if (tt == TileType.button) return new ToggleInfo();
            if (tt == TileType.bomb) return new BombInfo();
            if (tt == TileType.flag) return new FlagInfo();
            if (tt == TileType.paster) return new PasteInfo();
            if (tt == TileType.watcher) return new WatcherInfo();
            return new TypeInfo();
        }

        public static TypeInfo infoFromString(TileType tt, String s) {
            s += "|";
            try {
                if (tt == TileType.belt) {
                    Coord dir = new Coord(-1, 0);
                    while (s.contains("|")) {
                        if (s.charAt(0) == 'c') {
                            dir = new Coord(Integer.parseInt(s.substring(1, s.indexOf(":"))), Integer.parseInt(s.substring(s.indexOf(":")+1, s.indexOf("|"))));
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
                    boolean awake = true;
                    boolean crippled = false;
                    while (s.contains("|")) {
                        if (s.charAt(0) == 'c') {
                            dir = new Coord(Integer.parseInt(s.substring(1, s.indexOf(":"))), Integer.parseInt(s.substring(s.indexOf(":")+1, s.indexOf("|"))));
                        } else if (s.charAt(0) == 'b') {
                            awake = Integer.parseInt(s.substring(1, s.indexOf("|"))) != 0;
                            if (s.charAt(s.indexOf("|")+1) <= 57 && s.charAt(s.indexOf("|")+1) >= 48) {
                                s = s.substring(s.indexOf("|")+1);
                                crippled = Integer.parseInt(s.substring(0, s.indexOf("|"))) != 0;
                            } else {
                                throw new InfoFormattingException(tt);
                            }
                        } else {
                            throw new InfoFormattingException(tt);
                        }
                        s = s.substring(s.indexOf("|")+1);
                    }
                    return new PlayerInfo(dir, awake, crippled);
                }
                if (tt == TileType.gate || tt == TileType.toggleSpikes) {
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
                if (tt == TileType.bomb) {
                    int c = 0;
                    boolean lit = false;
                    while (s.contains("|")) {
                        if (s.charAt(0) == 'i') {
                            c = Integer.parseInt(s.substring(1, s.indexOf("|")));
                        } else if (s.charAt(0) == 'b') {
                            lit = Integer.parseInt(s.substring(1, s.indexOf("|"))) != 0;
                        } else {
                            throw new InfoFormattingException(tt);
                        }
                        s = s.substring(s.indexOf("|")+1);
                    }
                    return new BombInfo(c, lit);
                }
                if (tt == TileType.flag) {
                    boolean up = false;
                    while (s.contains("|")) {
                        if (s.charAt(0) == 'b') {
                            up = Integer.parseInt(s.substring(1, s.indexOf("|"))) != 0;
                        } else {
                            throw new InfoFormattingException(tt);
                        }
                        s = s.substring(s.indexOf("|")+1);
                    }
                    return new FlagInfo(up);
                }
                if (tt == TileType.paster) {
                    return new PasteInfo();
                }
                if (tt == TileType.watcher) {
                    int id = 0;
                    Coord dir = new Coord(0, 0);
                    while (s.contains("|")) {
                        if (s.charAt(0) == 'i') {
                            id = Integer.parseInt(s.substring(1, s.indexOf("|")));
                        } else if (s.charAt(0) == 'c') {
                            dir = new Coord(Integer.parseInt(s.substring(1, s.indexOf(":"))), Integer.parseInt(s.substring(s.indexOf(":")+1, s.indexOf("|"))));
                        } else {
                            throw new InfoFormattingException(tt);
                        }
                        s = s.substring(s.indexOf("|")+1);
                    }
                    return new WatcherInfo(id, dir);
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
                ret = "|c"+pi.dir.x()+":"+pi.dir.y()+"|b"+(pi.sleepy?0:1)+"|"+(pi.crippled?1:0);
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
            else if (ti instanceof BombInfo bi) {
                ret = "|i"+bi.countdown+"|b"+(bi.isLit?1:0);
            }
            else if (ti instanceof FlagInfo fi) {
                ret = "|b"+(fi.isUp?1:0);
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

    public static void renderNumbers(Coord pos, Coord bounds, int num, Graphics g) {
        boolean neg = num < 0;
        int digits = (""+num).length();
        num = Math.abs(num);
        for (int x = digits-1; x >= 0; x--) {
            if (x == 0 && neg) {
                g.drawImage(Sprites.negative.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null);
                break;
            }
            switch (num%10) {
                case 0: g.drawImage(Sprites.zero.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null); break;
                case 1: g.drawImage(Sprites.one.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null); break;
                case 2: g.drawImage(Sprites.two.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null); break;
                case 3: g.drawImage(Sprites.three.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null); break;
                case 4: g.drawImage(Sprites.four.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null); break;
                case 5: g.drawImage(Sprites.five.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null); break;
                case 6: g.drawImage(Sprites.six.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null); break;
                case 7: g.drawImage(Sprites.seven.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null); break;
                case 8: g.drawImage(Sprites.eight.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null); break;
                case 9: g.drawImage(Sprites.nine.i, (int) (pos.x + bounds.x*x/digits), pos.y(), (int) (bounds.x/digits), bounds.y(), null); break;
            }
            num /= 10;
        }
    }
}
