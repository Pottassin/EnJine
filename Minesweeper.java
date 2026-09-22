import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
/*import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;*/

import enjine.GeoMath.*;
import enjine.util.*;
import enjine.Prog;

import javax.imageio.ImageIO;

public class Minesweeper extends Prog {

    public static void main(String[] args) {
        new Minesweeper();
    }

    MainMenu mainMenu;

    Board board;

    BufferedImage mineImg;
    BufferedImage tileImg;

    public Minesweeper() {
        super("Minesweeper", 1000,1000);
        new Camera(0,0,1000,1000);
        mainMenu = new MainMenu();
        board = new Board();
        try {
            mineImg = ImageIO.read(new File("MineImage.png"));
            tileImg = ImageIO.read(new File("TileImage.png"));
        }
        catch (Exception e) {}
    }

    public class MainMenu extends Script {

        StartButton start;
        public MainMenu() {
            start = new StartButton();
            start();
        }

        public void start() {
            CameraManager.addToDraw(this, 1);
            start.start();
        }

        public void destroy() {
            start.destroy();
            CameraManager.removeToDraw(this, 1);
        }

        public void render(Graphics g) {
            g.setColor(Color.gray);
            g.fillRect(start.coll.og.x(), start.coll.og.y() + (int) start.coll.width + 30, 200, 310);
            g.setColor(Color.black);
            g.drawString("Z: Show Tile info", start.coll.og.x() + 10, start.coll.og.y() + (int) start.coll.width + 60);
            g.drawString("X: Alt tile view", start.coll.og.x() + 10, start.coll.og.y() + (int) start.coll.width + 90);
            g.drawString("C: Lock portals", start.coll.og.x() + 10, start.coll.og.y() + (int) start.coll.width + 120);
            g.drawString("V: Lock mouse pos", start.coll.og.x() + 10, start.coll.og.y() + (int) start.coll.width + 150);
            g.drawString("WASD: Move screen", start.coll.og.x() + 10, start.coll.og.y() + (int) start.coll.width + 180);
            g.drawString("QE: Zoom screen", start.coll.og.x() + 10, start.coll.og.y() + (int) start.coll.width + 210);
            g.drawString("0 - 9: Flag Select", start.coll.og.x() + 10, start.coll.og.y() + (int) start.coll.width + 240);
            g.drawString("Left click: Uncover tile", start.coll.og.x() + 10, start.coll.og.y() + (int) start.coll.width + 270);
            g.drawString("Right click: Flag tile", start.coll.og.x() + 10, start.coll.og.y() + (int) start.coll.width + 300);
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
            public void mouseReleased(MouseEvent e) { if (isPressed) { mainMenu.destroy(); board.start(); } }

            public void render(Graphics g) {
                if (isSelected && isPressed)
                    g.setColor(ColorState.press.c);
                else if (isSelected)
                    g.setColor(ColorState.hover.c);
                else
                    g.setColor(ColorState.rest.c);
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                g.setColor(Color.black);
                g.drawString("Start Game", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
            }
        }


    }

    public class Board extends Script {

        int[][] board;
        Tile[][] boardDisplay;

        int nonmineLeft = 0;

        Coord boardSize = new Coord();

        Coord boardPos = new Coord();

        double boardScale = 1;

        int tileSize = 40;

        Coord selectedTile = new Coord();
        Coord pressedTile = new Coord();
        boolean tileSelected;
        boolean altView = false;
        boolean lockPos = false;

        ArrayList<Coord> minePos = new ArrayList<Coord>();

        EndScreen endScreen;
        GameState gameState;
        Settings settings;
        TileInfo tileInfo;
        FlagColorizer colorizer;

        public enum GameState {
            pre(0),
            run(1),
            end(2);
            public final int s;
            GameState(int s) { this.s = s; }
        }

        FlagColor flag = FlagColor.zero;
        int[] flagCounts;

        public enum FlagColor {
            none(null, 0),
            zero(Color.green, 0),
            one(Color.yellow, 1),
            two(Color.orange, 2),
            three(Color.red, 3),
            four(Color.magenta, 4),
            five(Color.blue, 5),
            six(Color.cyan, 6),
            seven(Color.pink, 7),
            eight(Color.white, 8),
            nine(Color.gray, 9);
            public Color c;
            public int n;
            FlagColor(Color col, int num) { c = col; n = num; }
            public void changeColor(Color col, int num) {
                switch (num) {
                    case 0:
                        zero.c = col;
                    case 1:
                        one.c = col;
                    case 2:
                        two.c = col;
                    case 3:
                        three.c = col;
                    case 4:
                        four.c = col;
                    case 5:
                        five.c = col;
                    case 6:
                        six.c = col;
                    case 7:
                        seven.c = col;
                    case 8:
                        eight.c = col;
                    case 9:
                        nine.c = col;
                }
            }
        }

        ArrayList<Portal>[][] portalGrid;
        ArrayList<Portal> portals = new ArrayList<Portal>();

        public Board() {
            endScreen = new EndScreen();
            settings = new Settings();
            tileInfo = new TileInfo();
            colorizer = new FlagColorizer();
            boardPos.x = 100;
            boardPos.y = 100;
        }

        public void start() {
            CameraManager.addToDraw(this, 1);
            KeyDetect.instance.addH(this);
            KeyDetect.instance.addP(this);
            addToUpdate(this);
            MouseDetect.instance.addP(this);
            MouseDetect.instance.addR(this);
            gameState = GameState.pre;
            generateBoard(20, 20);
            flagCounts = new int[10];
            if (Portal.generatePortals) generatePortals(5);
            else {
                for (Portal p : portals) {
                    p.start();
                    p.calcTileColl();
                    //System.out.println(p.coll.l.altToString());
                    //System.out.println(p.o.coll.l.altToString());
                }
            }
        }

        public void destroy() {
            CameraManager.removeToDraw(this, 1);
            KeyDetect.instance.removeH(this);
            KeyDetect.instance.removeP(this);
            removeToUpdate(this);
            MouseDetect.instance.removeP(this);
            MouseDetect.instance.removeR(this);
            endScreen.destroy();
            tileInfo.destroy();
            for (Portal p : portals) {
                p.destroy();
            }
            if (Portal.generatePortals) portals.clear();
            System.out.println("\n\n\n\n");
        }

        public void unfreeze() {
            KeyDetect.instance.addH(this);
            KeyDetect.instance.addP(this);
            addToUpdate(this);
            MouseDetect.instance.addP(this);
            MouseDetect.instance.addR(this);
        }

        public void freeze() {
            KeyDetect.instance.removeH(this);
            KeyDetect.instance.removeP(this);
            removeToUpdate(this);
            MouseDetect.instance.removeP(this);
            MouseDetect.instance.removeR(this);
        }

        public void update() {
            if (!lockPos) {
                Coord mousePos = MouseDetect.instance.mousePos();
                mousePos.x -= boardPos.x * boardScale;
                mousePos.y -= boardPos.y * boardScale;
                mousePos.x = (int) (mousePos.x / (tileSize * boardScale));
                mousePos.y = (int) (mousePos.y / (tileSize * boardScale));
                tileSelected = false;
                if (isOnBoard(mousePos)) {
                    selectedTile.copy(mousePos);
                    tileSelected = true;
                }
            }
        }

        public void mousePressed(MouseEvent e) {
            if (gameState == GameState.end) return;
            if (tileSelected) {
                pressedTile.copy(selectedTile);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (gameState == GameState.end) {
                destroy();
                mainMenu.start();
                return;
            }
            if (selectedTile.equals(pressedTile)) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (boardDisplay[selectedTile.y()][selectedTile.x()].uncovered)
                        revealAdjacent(selectedTile.x(), selectedTile.y());
                    else
                        toggleFlag(pressedTile.x(), pressedTile.y());
                }
                else if (boardDisplay[pressedTile.y()][pressedTile.x()].f == FlagColor.none)
                    revealTile(pressedTile.x(), pressedTile.y());
            }
        }

        public void toggleFlag(int x, int y) {
            if (isOnBoard(x, y)) {
                boardDisplay[y][x].toggleFlag();
            }
        }

        public void revealTile(int x, int y) {
            if (isOnBoard(x,y)) {
                if (!boardDisplay[y][x].uncovered) {
                    if (gameState == GameState.pre) {
                        ArrayList<Coord> avoid = new ArrayList<Coord>();
                        for (int x2 = x - 1; x2 <= x + 1; x2++) {
                            for (int y2 = y - 1; y2 <= y + 1; y2++) {
                                avoid.add(new Coord(x2, y2));
                            }
                        }
                        generateMines(80, avoid);
                        gameState = GameState.run;
                    }
                    boardDisplay[y][x].uncovered = true;
                    if (board[y][x] == 1) {
                        endGame(false);
                    }
                    else {
                        boardDisplay[y][x].updateAdjMines();
                        nonmineLeft--;
                        if (boardDisplay[y][x].adjMines == 0) {
                            for (int x2 = x - 1; x2 <= x + 1; x2++) {
                                for (int y2 = y - 1; y2 <= y + 1; y2++) {
                                    if (x2 != x || y2 != y)
                                        revealTile(x2, y2);
                                }
                            }
                            if (portalGrid[y][x] != null) {
                                for (Portal p : portalGrid[y][x]) {
                                    for (Coord c : p.tileAltTiles.get(new Coord(x, y))) {
                                        if (c.x != x || c.y != y)
                                            revealTile(c.x(), c.y());
                                    }
                                }
                            }
                        }
                        checkWin();
                    }
                }
            }
        }

        public void revealAdjacent(int x, int y) {
            if (boardDisplay[y][x].adjMines - adjacentFlags(x, y) == 0) {
                for (int x2 = x - 1; x2 <= x + 1; x2++) {
                    for (int y2 = y - 1; y2 <= y + 1; y2++) {
                        if (x2 != x || y2 != y)
                            if (isOnBoard(x2, y2))
                                if (boardDisplay[y2][x2].f == FlagColor.none)
                                    revealTile(x2, y2);
                    }
                }
                if (portalGrid[y][x] != null) {
                    for (Portal p : portalGrid[y][x]) {
                        for (Coord c : p.tileAltTiles.get(new Coord(x, y))) {
                            if (c.x != x || c.y != y)
                                if (boardDisplay[c.y()][c.x()].f == FlagColor.none)
                                    revealTile(c.x(), c.y());
                        }
                    }
                }
            }
        }
        public void setMineVis(boolean t) {
            for (Coord c : minePos) {
                boardDisplay[c.y()][c.x()].uncovered = t;
            }
        }

        public void checkWin() {
            if (nonmineLeft == 0)
                endGame(true);
        }

        public int adjacentMines(int x, int y) {
            int ret = 0;
            for (int x2 = x - 1; x2 <= x + 1; x2++) {
                for (int y2 = y - 1; y2 <= y + 1; y2++) {
                    if (isOnBoard(x2,y2))
                        if (board[y2][x2] == 1)
                            ret++;
                }
            }
            if (portalGrid[y][x] != null) {
                for (Portal p : portalGrid[y][x])
                    if (p.tileAltTiles.get(new Coord(x, y)) != null)
                        for (Coord c : p.tileAltTiles.get(new Coord(x, y)))
                            if (board[c.y()][c.x()] == 1)
                                ret++;
            }
            return ret;
        }

        public int adjacentFlags(int x, int y) {
            int ret = 0;
            for (int x2 = x - 1; x2 <= x + 1; x2++) {
                for (int y2 = y - 1; y2 <= y + 1; y2++) {
                    if (isOnBoard(x2,y2))
                        if (boardDisplay[y2][x2].f != FlagColor.none && !boardDisplay[y2][x2].uncovered)
                            ret++;
                }
            }
            if (portalGrid[y][x] != null) {
                for (Portal p : portalGrid[y][x])
                    if (p.tileAltTiles.get(new Coord(x, y)) != null)
                        for (Coord c : p.tileAltTiles.get(new Coord(x, y)))
                            if (boardDisplay[c.y()][c.x()].f != FlagColor.none && !boardDisplay[c.y()][c.x()].uncovered)
                                ret++;
            }
            return ret;
        }

        public int adjacentFlagsOf(int x, int y, FlagColor col) {
            int ret = 0;
            for (int x2 = x - 1; x2 <= x + 1; x2++) {
                for (int y2 = y - 1; y2 <= y + 1; y2++) {
                    if (isOnBoard(x2,y2))
                        if (boardDisplay[y2][x2].f == col && !boardDisplay[y2][x2].uncovered)
                            ret++;
                }
            }
            if (portalGrid[y][x] != null) {
                for (Portal p : portalGrid[y][x])
                    if (p.tileAltTiles.get(new Coord(x, y)) != null)
                        for (Coord c : p.tileAltTiles.get(new Coord(x, y)))
                            if (boardDisplay[c.y()][c.x()].f == col && !boardDisplay[c.y()][c.x()].uncovered)
                                ret++;
            }
            return ret;
        }

        public int adjacentFlagsOf(int x, int y, ArrayList<FlagColor> cols) {
            int ret = 0;
            for (int x2 = x - 1; x2 <= x + 1; x2++) {
                for (int y2 = y - 1; y2 <= y + 1; y2++) {
                    if (isOnBoard(x2,y2))
                        if (cols.contains(boardDisplay[y2][x2].f) && !boardDisplay[y2][x2].uncovered)
                            ret++;
                }
            }
            if (portalGrid[y][x] != null) {
                for (Portal p : portalGrid[y][x])
                    if (p.tileAltTiles.get(new Coord(x, y)) != null)
                        for (Coord c : p.tileAltTiles.get(new Coord(x, y)))
                            if (cols.contains(boardDisplay[c.y()][c.x()].f) && !boardDisplay[c.y()][c.x()].uncovered)
                                ret++;
            }
            return ret;
        }

        public void keyHeld(int e) {
            if (e == KeyEvent.VK_LEFT) { boardPos.x+=4; }
            if (e == KeyEvent.VK_RIGHT) { boardPos.x-=4; }
            if (e == KeyEvent.VK_UP) { boardPos.y+=4; }
            if (e == KeyEvent.VK_DOWN) { boardPos.y-=4; }
            if (e == KeyEvent.VK_A) { boardPos.x+=.5f; }
            if (e == KeyEvent.VK_D) { boardPos.x-=.5f; }
            if (e == KeyEvent.VK_W) { boardPos.y+=.5f; }
            if (e == KeyEvent.VK_S) { boardPos.y-=.5f; }
            if (e == KeyEvent.VK_Q) { boardScale*=1.025f; }
            if (e == KeyEvent.VK_E) { boardScale/=1.025f; }
        }

        public void keyPressed(int e) {
            if (gameState != GameState.end) {
                if (e == KeyEvent.VK_ESCAPE) {
                    freeze();
                    settings.start();
                }
            }
            if (e == KeyEvent.VK_C) {Portal.generatePortals = !Portal.generatePortals;}
            if (e == KeyEvent.VK_X) altView = !altView;
            if (e == KeyEvent.VK_Z) tileInfo.toggle();
            if (e == KeyEvent.VK_V) lockPos = !lockPos;
            switch (e) {
                case KeyEvent.VK_0:
                    flag = FlagColor.zero; break;
                case KeyEvent.VK_1:
                    flag = FlagColor.one; break;
                case KeyEvent.VK_2:
                    flag = FlagColor.two; break;
                case KeyEvent.VK_3:
                    flag = FlagColor.three; break;
                case KeyEvent.VK_4:
                    flag = FlagColor.four; break;
                case KeyEvent.VK_5:
                    flag = FlagColor.five; break;
                case KeyEvent.VK_6:
                    flag = FlagColor.six; break;
                case KeyEvent.VK_7:
                    flag = FlagColor.seven; break;
                case KeyEvent.VK_8:
                    flag = FlagColor.eight; break;
                case KeyEvent.VK_9:
                    flag = FlagColor.nine; break;
            }
        }

        public void generateBoard(int sizeX, int sizeY) {
            boardSize.x = sizeX;
            boardSize.y = sizeY;
            board = new int[sizeY][sizeX];
            boardDisplay = new Tile[sizeY][sizeX];
            portalGrid = new ArrayList[boardSize.y()][boardSize.x()];
            minePos = new ArrayList<Coord>();
            nonmineLeft = sizeX * sizeY;
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    boardDisplay[y][x] = new Tile(x, y);
                }
            }
        }

        public void generateBoard(int sizeX, int sizeY, int targetMines) {
            boardSize.x = sizeX;
            boardSize.y = sizeY;
            board = new int[sizeY][sizeX];
            boardDisplay = new Tile[sizeY][sizeX];
            portalGrid = new ArrayList[boardSize.y()][boardSize.x()];
            minePos = new ArrayList<Coord>();
            ArrayList<Coord> minePool = new ArrayList<Coord>();
            nonmineLeft = sizeX * sizeY;
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    minePool.add(new Coord(x,y));
                    boardDisplay[y][x] = new Tile(x, y);
                }
            }
            Coord genPos;
            for (int x = 0; x < targetMines; x++) {
                if (minePool.isEmpty()) break;
                genPos = minePool.remove(randomIntGen.nextInt(0,minePool.size()));
                minePos.add(genPos);
                board[genPos.y()][genPos.x()] = 1;
                nonmineLeft--;
            }
        }

        public void generateBoard(int sizeX, int sizeY, int targetMines, ArrayList<Coord> avoid) {
            boardSize.x = sizeX;
            boardSize.y = sizeY;
            board = new int[sizeY][sizeX];
            boardDisplay = new Tile[sizeY][sizeX];
            portalGrid = new ArrayList[boardSize.y()][boardSize.x()];
            minePos = new ArrayList<Coord>();
            ArrayList<Coord> minePool = new ArrayList<Coord>();
            Coord genPos;
            nonmineLeft = sizeX * sizeY;
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    genPos = new Coord(x,y);
                    if (!avoid.contains(genPos)) minePool.add(genPos);
                    boardDisplay[y][x] = new Tile(x, y);
                }
            }
            for (int x = 0; x < targetMines; x++) {
                if (minePool.isEmpty()) break;
                genPos = minePool.remove(randomIntGen.nextInt(0,minePool.size()));
                minePos.add(genPos);
                board[genPos.y()][genPos.x()] = 1;
                nonmineLeft--;
            }
        }

        public void generateMines(int targetMines, ArrayList<Coord> avoid) {
            minePos = new ArrayList<Coord>();
            ArrayList<Coord> minePool = new ArrayList<Coord>();
            Coord genPos;
            boolean canGen = true;
            for (int x = 0; x < board[0].length; x++) {
                for (int y = 0; y < board.length; y++) {
                    /* avoid revealed?
                    if (boardDisplay[y][x] == -1) break; */
                    genPos = new Coord(x,y);
                    for (Coord c : avoid) { if (genPos.equals(c)) { canGen = false; break; } }
                    if (canGen) minePool.add(genPos);
                    canGen = true;
                }
            }
            for (int x = 0; x < targetMines; x++) {
                if (minePool.isEmpty()) break;
                genPos = minePool.remove(randomIntGen.nextInt(0,minePool.size()));
                minePos.add(genPos);
                board[genPos.y()][genPos.x()] = 1;
                nonmineLeft--;
            }
            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[0].length; x++) {
                    //System.out.print(board[y][x] + " ");
                }
                //System.out.println("\n");
            }
        }

        public void generatePortals(int numPortals) {
            for (int x = 0; x < numPortals; x++) {
                new Portal();
            }
        }

        public boolean isOnBoard(int x, int y) { return x >= 0 && x < boardSize.x && y >= 0 && y < boardSize.y; }
        public boolean isOnBoard(Coord pos) { return pos.x >= 0 && pos.x < boardSize.x && pos.y >= 0 && pos.y < boardSize.y; }

        public void endGame(boolean w) {
            setMineVis(true);
            gameState = GameState.end;
            endScreen.start(w);
        }

        public void render(Graphics g) {
            g.setColor(Color.blue);
            g.fillRect(0,0,1100,1100);
            for (int x = 0; x < boardDisplay[0].length; x++) {
                for (int y = 0; y < boardDisplay.length; y++) {
                    if (!boardDisplay[y][x].uncovered) {
                        g.drawImage(tileImg, (int) ((boardPos.x + x * tileSize)*boardScale), (int) ((boardPos.y + y * tileSize)*boardScale), (int) (tileSize*boardScale), (int) (tileSize*boardScale), null);
                        if (boardDisplay[y][x].f != FlagColor.none) {
                            g.setColor(boardDisplay[y][x].f.c);
                            g.fillRect((int) ((boardPos.x + (x + .2f) * tileSize)*boardScale), (int) ((boardPos.y + (y + .5f) * tileSize)*boardScale), (int) (tileSize*boardScale*.1f), (int) (tileSize*boardScale*.3f));
                            g.fillPolygon(new int[]{(int) ((boardPos.x + (x + .2f) * tileSize)*boardScale),(int) ((boardPos.x + (x + .8f) * tileSize)*boardScale),(int) ((boardPos.x + (x + .2f) * tileSize)*boardScale)}, new int[]{(int) ((boardPos.y + (y + .2f) * tileSize)*boardScale), (int) ((boardPos.y + (y + .4f) * tileSize)*boardScale), (int) ((boardPos.y + (y + .6f) * tileSize)*boardScale)}, 3);
                        }
                    }
                    else if (board[y][x] == 1) {
                        g.drawImage(mineImg, (int) ((boardPos.x + x * tileSize)*boardScale), (int) ((boardPos.y + y * tileSize)*boardScale), (int) (tileSize*boardScale), (int) (tileSize*boardScale), null);
                    }
                    else
                    {
                        if (boardDisplay[y][x].adjMines > 0) {
                            g.setColor(Color.darkGray);
                            g.drawRect((int) ((boardPos.x + x * tileSize)*boardScale), (int) ((boardPos.y + y * tileSize)*boardScale), (int) (tileSize*boardScale), (int) (tileSize*boardScale));
                        }
                        if (boardDisplay[y][x].adjMines > 0) {
                            if (altView) {
                                if (boardDisplay[y][x].adjMines > adjacentFlags(x, y))
                                    g.setColor(Color.white);
                                else if (boardDisplay[y][x].adjMines == adjacentFlags(x, y))
                                    g.setColor(new Color(20,155,0));
                                else
                                    g.setColor(Color.red);
                                g.drawString(boardDisplay[y][x].adjMines - adjacentFlags(x, y) + "", (int) ((boardPos.x + x * tileSize + (int) (tileSize / 4)) * boardScale), (int) ((boardPos.y + y * tileSize + (int) (tileSize / 4)) * boardScale));
                            }
                            else {
                                g.setColor(Color.white);
                                g.drawString("" + boardDisplay[y][x].adjMines, (int) ((boardPos.x + x * tileSize + (int) (tileSize / 4)) * boardScale), (int) ((boardPos.y + y * tileSize + (int) (tileSize / 4)) * boardScale));
                            }
                        }
                    }
                }
            }
            g.setColor(new Color(255, 255, 255));
            //g.setColor(Color.orange);
            g.drawRect((int) ((boardPos.x + selectedTile.x * tileSize)*boardScale), (int) ((boardPos.y + selectedTile.y * tileSize)*boardScale), (int) (tileSize*boardScale), (int) (tileSize*boardScale));
            if (portalGrid[selectedTile.y()][selectedTile.x()] != null) {
                Geometry collGeo;
                Geometry altCollGeo;
                for (Portal p : portalGrid[selectedTile.y()][selectedTile.x()]) {
                    collGeo = p.tileColls.get(selectedTile);
                    altCollGeo = p.tileAltColls.get(selectedTile);
                    //System.out.println(collGeo);
                    g.setColor(new Color(140, 140, 130));
                    for (Coord c : p.tileAltTiles.get(selectedTile))
                        g.drawRect((int) ((boardPos.x + c.x * tileSize)*boardScale), (int) ((boardPos.y + c.y * tileSize)*boardScale), (int) (tileSize*boardScale), (int) (tileSize*boardScale));
                    if (altCollGeo != null) {
                        if (altCollGeo instanceof Coord)
                            altCollGeo.debugRender2().run(new Object[]{g, Color.white, boardPos.multiply(boardScale), new Coord(boardScale*tileSize, boardScale*tileSize), new Coord(8, 8)});
                        else
                            altCollGeo.debugRender2().run(new Object[]{g, Color.magenta, boardPos, new Coord(boardScale, boardScale), new Coord(tileSize, tileSize)});
                    }
                    //else System.out.println("{\nerror\nerror\nerror\n}");
                    if (collGeo != null) {
                        if (collGeo instanceof Coord)
                            collGeo.debugRender2().run(new Object[]{g, Color.white, boardPos.multiply(boardScale), new Coord(boardScale*tileSize, boardScale*tileSize), new Coord(8, 8)});
                        else
                            collGeo.debugRender2().run(new Object[]{g, Color.magenta, boardPos, new Coord(boardScale, boardScale), new Coord(tileSize, tileSize)});
                    }
                    //else System.out.println("{\nerror\nerror\nerror\n}");
                }
                //System.out.println("\n");
            }
        }

        public class Tile {
            public FlagColor f;
            public boolean uncovered;
            public int adjMines;
            public int posX;
            public int posY;

            public ArrayList<Geometry> portalColls = new ArrayList<Geometry>();

            public Tile(int x, int y) {
                uncovered = false;
                adjMines = 0;
                posX = x;
                posY = y;
                f = FlagColor.none;
            }

            public void toggleFlag() {
                if (f == FlagColor.none) {
                    flagCounts[flag.n]++;
                    f = flag;
                }
                else {
                    flagCounts[f.n]--;
                    f = FlagColor.none;
                }
            }

            public void updateAdjMines() { adjMines = adjacentMines(posX, posY); }

            public String description() {
                String ret = "";
                if (portalGrid[posY][posX] != null) {
                    ret = "\n" + portalGrid[posY][posX].size() + " portals leading to:\n";
                    for (Portal p : portalGrid[posY][posX]) {
                        for (Coord c : p.tileAltTiles.get(new Coord(posX, posY))) {
                            ret += c.toString() + "  ";
                        }
                        ret += "\n";
                    }
                }
                if (f != FlagColor.none && !uncovered) return "flagged with flag " + f.n + ret;
                if (!uncovered) return "Tile hasn't been revealed" + ret;
                if (board[posY][posX] == 1) return "This a mine" + ret;
                return adjMines + " adjacent mines" + ret;
            }
        }

        public class SplitTile extends Tile {
            public ArrayList<Tile> subTiles;

            public SplitTile(int x, int y) {
                super(x, y);
            }
        }

        public class EndScreen extends Script {

            boolean w;

            public void start() { CameraManager.addToDraw(this, 1); }
            public void start(boolean w) { this.w = w; CameraManager.addToDraw(this, 1); }

            public void destroy() { CameraManager.removeToDraw(this, 1); }

            public void render(Graphics g) {
                g.setColor(Color.white);
                g.fillRect(0, 0, 150, 50);
                g.setColor(Color.black);
                if (w)
                    g.drawString("Wowie, W", 25, 25);
                else
                    g.drawString("Lmao, L", 25, 25);
            }
        }

        public class Settings extends Script {
            ResumeButton resume;
            ExitButton exit;

            public Settings() {
                resume = new ResumeButton();
                exit = new ExitButton();
            }
            public void start() {
                CameraManager.addToDraw(this, 1);
                KeyDetect.instance.addP(this);
                resume.start();
                exit.start();
            }

            public void destroy() {
                CameraManager.removeToDraw(this, 1);
                KeyDetect.instance.removeP(this);
                resume.destroy();
                exit.destroy();
            }

            public void keyPressed(int e) {
                if (e == KeyEvent.VK_ESCAPE) {
                    destroy();
                    unfreeze();
                }
            }

            public void render(Graphics g) {

            }

            public void close() {
                destroy();
                unfreeze();
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
                public void mouseReleased(MouseEvent e) { if (isPressed) { close(); } }

                public void render(Graphics g) {
                    if (isSelected && isPressed)
                        g.setColor(MainMenu.StartButton.ColorState.press.c);
                    else if (isSelected)
                        g.setColor(MainMenu.StartButton.ColorState.hover.c);
                    else
                        g.setColor(MainMenu.StartButton.ColorState.rest.c);
                    g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                    g.setColor(Color.black);
                    g.drawString("Resume Game", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
                }
            }

            public class ExitButton extends Script {

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
                public void mouseReleased(MouseEvent e) { if (isPressed) { close(); endGame(false); } }

                public void render(Graphics g) {
                    if (isSelected && isPressed)
                        g.setColor(MainMenu.StartButton.ColorState.press.c);
                    else if (isSelected)
                        g.setColor(MainMenu.StartButton.ColorState.hover.c);
                    else
                        g.setColor(MainMenu.StartButton.ColorState.rest.c);
                    g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
                    g.setColor(Color.black);
                    g.drawString("Forfeit Game", coll.og.x() + 10, (int) (coll.og.y - coll.width) + 30);
                }
            }
        }

        public class TileInfo extends Script {
            public TileInfo() { sim = false; }

            public void toggle() {
                if (sim)
                    destroy();
                else
                    start();
            }

            public void start() {
                sim = true;
                CameraManager.addToDraw(this, 1);
            }

            public void destroy() {
                sim = false;
                CameraManager.removeToDraw(this, 1);
            }

            public void render(Graphics g) {
                Tile t = boardDisplay[selectedTile.y()][selectedTile.x()];
                String s = t.description();
                ArrayList<String> lines = new ArrayList<String>();
                int longestStr = 0;
                int nIndex;
                while (true) {
                    nIndex = s.indexOf("\n");
                    if (s.length() < 2)
                        break;
                    if (nIndex == -1) {
                        if (s.length() > longestStr)
                            longestStr = s.length();
                        lines.add(s);
                        break;
                    }
                    lines.add(s.substring(0, nIndex));
                    if (s.substring(0, nIndex).length() > longestStr)
                        longestStr = s.substring(0, nIndex).length();
                    s = s.substring(nIndex+1);
                }

                g.setColor(Color.white);
                g.fillRect(0,0,(int) (4.4 * longestStr) + 45, 20 + 15 * lines.size());
                g.setColor(Color.black);
                for (int x = 0; x < lines.size(); x++)
                    g.drawString(lines.get(x), 10, 15 * x + 15);
            }
        }

        public class FlagColorizer extends Script {

        }

        public class Portal extends Script {
            public static boolean generatePortals = true;
            LineCollider coll;
            Portal o;
            boolean og;

            public TreeMap<Coord,Geometry> tileColls = new TreeMap<Coord, Geometry>();
            public TreeMap<Coord,Geometry> tileAltColls = new TreeMap<Coord, Geometry>();
            public TreeMap<Coord,TreeSet<Coord>> tileAltTiles = new TreeMap<Coord, TreeSet<Coord>>();

            ArrayList<Coord> hitTiles = new ArrayList<Coord>();

            public Portal() {
                og = true;
                coll = new LineCollider(new Coord(), new Coord());
                start();
                portals.add(this);
                randomizePos();
                o = new Portal(this);
                calcTileColl();
            }

            public Portal(Portal alt) {
                og = false;
                o = alt;
                coll = new LineCollider(new Coord(), new Coord());
                start();
                portals.add(this);
                randomizePos();
                calcTileColl();
            }

            public void start() {
                CameraManager.addToDraw(this, 1);
            }

            public void destroy() {
                CameraManager.removeToDraw(this, 1);
            }

            public void randomizePos() {
                int round = 10;
                coll.l.s.x = randomIntGen.nextInt(boardSize.x()*round) / (double)round;
                coll.l.s.y = randomIntGen.nextInt(boardSize.y()*round) / (double)round;
                coll.l.e.x = randomIntGen.nextInt(boardSize.x()*round) / (double)round;
                coll.l.e.y = randomIntGen.nextInt(boardSize.y()*round) / (double)round;
                //coll.l.e.y = coll.l.s.y;
            }

            public void calcTileColl() {
                hitTiles.clear();
                tileColls.clear();
                tileAltColls.clear();
                tileAltTiles.clear();
                if (coll.l.s.x == coll.l.e.x) {
                    for (int x = (int) Math.ceil(coll.l.s.x - 1); x <= Math.floor(coll.l.s.x); x++) {
                        for (int y = (int) Math.ceil(coll.l.downP().y - 1); y < Math.floor(coll.l.upP().y + 1); y++) {
                            hitTiles.add(new Coord(x, y));
                            //portalGrid[y][x].add(this);
                        }
                    }
                }
                else {
                    double edgeX = Math.min(Math.ceil(coll.l.leftP().x - 1) + 1, coll.l.rightP().x);
                    if (coll.l.leftP().y <= coll.l.rightP().y) {
                        for (int y = (int) Math.ceil(coll.l.leftP().y - 1); y < (int) Math.floor((edgeX - coll.l.s.x) * coll.l.slope() + coll.l.s.y + 1); y++) {
                            hitTiles.add(new Coord(Math.ceil(coll.l.leftP().x - 1), y));
                        }
                    }
                    else {
                        for (int y = (int) Math.ceil((edgeX - coll.l.s.x) * coll.l.slope() + coll.l.s.y - 1); y < (int) Math.floor(coll.l.leftP().y + 1); y++) {
                            hitTiles.add(new Coord(Math.ceil(coll.l.leftP().x - 1), y));
                        }
                    }
                    //System.out.println("start: "+hitTiles);
                    if (edgeX != coll.l.rightP().x) {
                        for (int x = (int) Math.ceil(coll.l.leftP().x); x < Math.floor(coll.l.rightP().x); x++) {
                            if (coll.l.leftP().y <= coll.l.rightP().y) {
                                for (int y = (int) Math.ceil((x - coll.l.s.x) * coll.l.slope() + coll.l.s.y - 1); y < Math.floor((x - coll.l.s.x + 1) * coll.l.slope() + coll.l.s.y + 1); y++) {
                                    hitTiles.add(new Coord(x, y));
                                }
                            } else {
                                for (int y = (int) Math.ceil((x - coll.l.s.x + 1) * coll.l.slope() + coll.l.s.y - 1); y < Math.floor((x - coll.l.s.x) * coll.l.slope() + coll.l.s.y + 1); y++) {
                                    hitTiles.add(new Coord(x, y));
                                }
                            }
                        }
                        //System.out.println("mid: "+hitTiles);
                        edgeX = Math.floor(coll.l.rightP().x);
                        if (coll.l.leftP().y <= coll.l.rightP().y) {
                            for (int y = (int) Math.ceil((edgeX - coll.l.s.x) * coll.l.slope() + coll.l.s.y - 1); y < (int) Math.floor(coll.l.rightP().y + 1); y++) {
                                hitTiles.add(new Coord(edgeX, y));
                            }
                        } else {
                            for (int y = (int) Math.ceil(coll.l.rightP().y - 1); y < (int) Math.floor((edgeX - coll.l.s.x) * coll.l.slope() + coll.l.s.y + 1); y++) {
                                hitTiles.add(new Coord(edgeX, y));
                            }
                        }
                        //System.out.println("end: "+hitTiles);
                    }
                }
                Coord c;
                ArrayList<Geometry> tColl;
                /*
                Can miss a tip point in certain scenarios
                whole line selection shenanigans when line tip at edge
                incorrect single point labeling on vertical line
                corner shots can break;
                */
                for (int x = 0; x < hitTiles.size(); x++) {
                    c = hitTiles.get(x);
                    if (!isOnBoard(c)) {hitTiles.remove(x); x--; continue;}
                    if (portalGrid[c.y()][c.x()] == null) portalGrid[c.y()][c.x()] = new ArrayList<Portal>();
                    portalGrid[c.y()][c.x()].add(this);
                    tColl = coll.getEdgeContacts(new RectCollider(new Coord(c.x, c.y+.5f), new PCoord(0, 1), .5f));
                    /*tColl = new ArrayList<Geometry>();
                    while (true) {
                        tcoll.add()
                        break;
                    }*/
                    //System.out.println(c + " : " + tColl);
                    for (Geometry g : tColl) {
                        if (g instanceof Line) {
                            tileColls.put(c, g);
                            tColl = null;
                            //System.out.println("line detected\n");
                            break;
                        }
                    }
                    if (tColl != null) {
                        if (tColl.size() == 1) {
                            //System.out.println("Single point of collision at " + c + ", start at " + coll.l.s + ", end at " + coll.l.e);
                            if ((coll.l.s.x <= c.x+1 && coll.l.s.x >= c.x) && (coll.l.s.y <= c.y+1 && coll.l.s.y >= c.y)) {
                                //System.out.println(c + " matches start: " + coll.l.s+"\n");
                                //if (coll.l.s.x != ((Coord) tColl.get(0)).x && coll.l.s.y != ((Coord) tColl.get(0)).y) {
                                if (coll.l.s.x != coll.l.s.x() && coll.l.s.y != coll.l.s.y()) {
                                    tileColls.put(c, new Line(coll.l.s, (Coord) tColl.get(0)));
                                    continue;
                                }
                                else {
                                    tileColls.put(c, coll.l.s);
                                    continue;
                                }
                            }
                            else if ((coll.l.e.x <= c.x+1 && coll.l.e.x >= c.x) && (coll.l.e.y <= c.y+1 && coll.l.e.y >= c.y)) {
                                //System.out.println(c + " matches end: " + coll.l.e+"\n");
                                if (coll.l.e.x != ((Coord) tColl.get(0)).x && coll.l.e.y != ((Coord) tColl.get(0)).y) {
                                    tileColls.put(c, new Line(coll.l.e, (Coord) tColl.get(0)));
                                    continue;
                                }
                                else {
                                    tileColls.put(c, coll.l.e);
                                    continue;
                                }
                            }
                            else
                                tileColls.put(c, tColl.get(0));
                        }
                        else if (tColl.size() == 2){
                            tileColls.put(c, new Line((Coord) tColl.get(0), (Coord) tColl.get(1)));
                        }
                        else {
                            //if ()
                            tileColls.put(c, coll.l);
                        }
                    }
                    else {
                        tileColls.put(c, coll.l);
                    }
                    //System.out.println();
                }
                for (Coord tcs : hitTiles) {
                    if (tileColls.get(tcs) instanceof Coord temp)
                        tileAltColls.put(tcs, coll.l.pointProjectedTo(temp, o.coll.l));
                    else if (tileColls.get(tcs) instanceof Line temp)
                        tileAltColls.put(tcs, coll.l.lineProjectedTo(temp, o.coll.l));
                }
                /*
                can go out of bounds????
                tips occasionally dont fully account
                horizontal line shenanigans?
                */
                if (o.coll.l.s.x == o.coll.l.e.x) {
                    for (Coord tcs : hitTiles) {
                        tileAltTiles.put(tcs, new TreeSet<Coord>());
                        if (tileAltColls.get(tcs) instanceof Coord temp) {
                            for (int x = (int) Math.ceil(temp.x - 1); x <= Math.floor(temp.x); x++) {
                                for (int y = (int) Math.ceil(temp.y - 1); y <= Math.floor(temp.y); y++) {
                                    if (isOnBoard(x, y))
                                        tileAltTiles.get(tcs).add(new Coord(x, y));
                                }
                            }
                        }
                        else if (tileAltColls.get(tcs) instanceof Line temp) {
                            for (int x = (int) Math.ceil(temp.s.x - 1); x <= Math.floor(temp.s.x); x++) {
                                for (int y = (int) Math.ceil(temp.downP().y - 1); y < Math.floor(temp.upP().y + 1); y++) {
                                    if (isOnBoard(x, y))
                                        tileAltTiles.get(tcs).add(new Coord(x, y));
                                }
                            }
                        }
                    }
                }
                else {
                    for (Coord tcs : hitTiles) {
                        tileAltTiles.put(tcs, new TreeSet<Coord>());
                        if (tileAltColls.get(tcs) instanceof Coord temp) {
                            for (int x = (int) Math.ceil(temp.x - 1); x <= Math.floor(temp.x); x++) {
                                for (int y = (int) Math.ceil(temp.y - 1); y <= Math.floor(temp.y); y++) {
                                    tileAltTiles.get(tcs).add(new Coord(x, y));
                                }
                            }
                        }
                        else if (tileAltColls.get(tcs) instanceof Line temp) {
                            double edgeX = Math.min(Math.ceil(temp.leftP().x - 1) + 1, temp.rightP().x);
                            if (temp.leftP().y <= temp.rightP().y) {
                                for (int y = (int) Math.ceil(temp.leftP().y - 1); y < (int) Math.floor((edgeX - temp.s.x) * temp.slope() + temp.s.y + 1); y++) {
                                    if (isOnBoard((int) Math.ceil(temp.leftP().x - 1), y))
                                        tileAltTiles.get(tcs).add(new Coord(Math.ceil(temp.leftP().x - 1), y));
                                }
                            } else {
                                for (int y = (int) Math.ceil((edgeX - temp.s.x) * temp.slope() + temp.s.y - 1); y < (int) Math.floor(temp.leftP().y + 1); y++) {
                                    if (isOnBoard((int) Math.ceil(temp.leftP().x - 1), y))
                                        tileAltTiles.get(tcs).add(new Coord(Math.ceil(temp.leftP().x - 1), y));
                                }
                            }
                            //System.out.println("start: "+hitTiles);
                            if (edgeX != temp.rightP().x) {
                                for (int x = (int) Math.ceil(temp.leftP().x); x < Math.floor(temp.rightP().x); x++) {
                                    if (temp.leftP().y <= temp.rightP().y) {
                                        for (int y = (int) Math.ceil((x - temp.s.x) * temp.slope() + temp.s.y - 1); y < Math.floor((x - temp.s.x + 1) * temp.slope() + temp.s.y + 1); y++) {
                                            if (isOnBoard(x, y))
                                                tileAltTiles.get(tcs).add(new Coord(x, y));
                                        }
                                    } else {
                                        for (int y = (int) Math.ceil((x - temp.s.x + 1) * temp.slope() + temp.s.y - 1); y < Math.floor((x - temp.s.x) * temp.slope() + temp.s.y + 1); y++) {
                                            if (isOnBoard(x, y))
                                                tileAltTiles.get(tcs).add(new Coord(x, y));
                                        }
                                    }
                                }
                                //System.out.println("mid: "+hitTiles);
                                edgeX = Math.floor(temp.rightP().x);
                                if (temp.leftP().y <= temp.rightP().y) {
                                    for (int y = (int) Math.ceil((edgeX - temp.s.x) * temp.slope() + temp.s.y - 1); y < (int) Math.floor(temp.rightP().y + 1); y++) {
                                        if (isOnBoard((int) edgeX, y))
                                            tileAltTiles.get(tcs).add(new Coord(edgeX, y));
                                    }
                                } else {
                                    for (int y = (int) Math.ceil(temp.rightP().y - 1); y < (int) Math.floor((edgeX - temp.s.x) * temp.slope() + temp.s.y + 1); y++) {
                                        if (isOnBoard((int) edgeX, y))
                                            tileAltTiles.get(tcs).add(new Coord(edgeX, y));
                                    }
                                }
                                //System.out.println("end: "+hitTiles);
                            }
                        }
                    }
                }
            }

            public void render(Graphics g) {
                if (altView) return;
                /*if (og)
                    g.setColor(Color.cyan);
                else
                    g.setColor(Color.orange);
                g.drawLine((int) ((boardPos.x + coll.l.s.x * tileSize)*boardScale), (int) ((boardPos.y + coll.l.s.y * tileSize)*boardScale), (int) ((boardPos.x + coll.l.e.x * tileSize)*boardScale), (int) ((boardPos.y + coll.l.e.y * tileSize)*boardScale));*/
                coll.l.debugRender2().run(new Object[]{g,og?Color.cyan:Color.orange,boardPos, new Coord(boardScale,boardScale), new Coord(tileSize,tileSize)});
                coll.l.s.debugRender2().run(new Object[]{g,og?Color.cyan:Color.orange,boardPos.multiply(boardScale), new Coord(boardScale*tileSize,boardScale*tileSize), new Coord(6,6)});
            }
        }
    }
}
