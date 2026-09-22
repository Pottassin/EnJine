import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.function.Function;

import enjine.GeoMath.*;
import enjine.util.*;
import enjine.Prog;

public class Gamblinator extends Prog {

    public static void main(String[] args) { new Gamblinator(); }

    public int startHandSize = 5;
    public int endHandSize = 8;
    public int sampleSize = 20000;
    //public int sampleSize = 5;
    public boolean replace = false;
    public boolean withJokers = false;
    public ArrayList<Coord> pool;
    //public TreeMap<Integer,boolean[][]> simHands;
    public TreeMap<Integer, int[]> handCountBySize;
    public TreeMap<Integer, int[]> handHighestBySize;
    public TreeMap<Integer, int[]> handCountBySizeWithJs;
    public TreeMap<Integer, int[]> handHighestBySizeWithJs;

    public enum Hand {
        high(1, (Coord[] hand) -> {
            return hand.length > 0;
        }),
        pair(2, (Coord[] hand) -> {
            if (hand.length < 2) return false;
            TreeSet<Integer> cards = new TreeSet<Integer>();
            int js = jokerCount(hand);
            if (js >= 1) return true;
            for (Coord card : hand) {
                if (!cards.add(card.x())) return true;
            }
            return false;
        }),
        twoPair(4, (Coord[] hand) -> {
            if (hand.length < 4) return false;
            TreeMap<Integer, Integer> cards = new TreeMap<Integer, Integer>();
            int numPairs = 0;
            int js = jokerCount(hand);
            if (js >= 3) return true;
            for (Coord card : hand) {
                if (!cards.containsKey(card.x())) cards.put(card.x(), 1);
                else { cards.put(card.x(), cards.get(card.x()) + 1);
                    numPairs++;
                    if (cards.get(card.x()) > 2) numPairs--;
                    if (numPairs > 1) return true;
                }
            }
            if (js == 1) return numPairs >= 1 && cards.keySet().size() >= 2;
            if (js == 2) return numPairs >= 1 || cards.keySet().size() >= 2;
            return false;
        }),
        triple(5, (Coord[] hand) -> {
            if (hand.length < 3) return false;
            TreeMap<Integer, Integer> cards = new TreeMap<Integer, Integer>();
            int js = jokerCount(hand);
            if (js >= 2) return true;
            for (Coord card : hand) {
                if (!cards.containsKey(card.x())) cards.put(card.x(), 1);
                else { cards.put(card.x(), cards.get(card.x()) + 1);
                    if (cards.get(card.x()) > 2 || js == 1) return true;
                }
            }
            return false;
        }),
        straight(6, (Coord[] hand) -> {
            if (hand.length < 5) return false;
            int consecutive = 0;
            int js = jokerCount(hand);
            if (js >= 4) return true;
            if (js == 0) {
                int prev = 0;
                for (Coord card : hand) {
                    if (card.y == 0) continue;
                    if (consecutive == 0) {
                        consecutive = 1;
                        prev = card.x();
                        continue;
                    }
                    if (prev == card.x) continue;
                    if (prev + 1 == card.x) {
                        consecutive++;
                        prev = card.x();
                        if (consecutive == 5) return true;
                    } else consecutive = 0;
                }
                if (prev == 13 && consecutive == 4) {
                    for (Coord c : hand) {
                        if (c.x == 1) return true;
                        if (c.x > 1) return false;
                    }
                }
            }
            else {
                boolean[] ranks = new boolean[13];
                for (Coord card : hand) {
                    if (card.y == 0) continue;
                    ranks[card.x() - 1] = true;
                }
                for (int x = 0; x < 10; x++) {
                    consecutive = js;
                    for (int y = 0; y < 5; y++) {
                        if (x+y >= 13) {
                            return ranks[0] || consecutive > 0;
                        }
                        if (ranks[x+y]) { if (y == 4) return true; else continue; }
                        if (consecutive > 0) { consecutive--; if (y == 4) return true; else continue; }
                        break;
                    }
                }
            }
            return false;
        }),
        flush(7, (Coord[] hand) -> {
            int[] suitNums = new int[4];
            int js = jokerCount(hand);
            if (js >= 4) return hand.length >= 5;
            for (Coord card : hand) {
                if (card.y == 0) continue;
                suitNums[card.y() - 1]++;
                if (suitNums[card.y() - 1] > 4 - js) return true;
            }
            return false;
        }),
        fullHouse(8, (Coord[] hand) -> {
            TreeMap<Integer, Integer> cards = new TreeMap<Integer, Integer>();
            int numPairs = 0;
            int numTriples = 0;
            int js = jokerCount(hand);
            if (js >= 4) return hand.length >= 5;
            for (Coord card : hand) {
                if (card.y == 0) continue;
                if (!cards.containsKey(card.x())) cards.put(card.x(), 1);
                else {
                    cards.put(card.x(), cards.get(card.x()) + 1);
                    if (cards.get(card.x()) == 2)
                        numPairs++;
                    if (cards.get(card.x()) == 3) {
                        numPairs--;
                        numTriples++;
                    }
                    if (numTriples > 1) return true;
                    else if (numTriples == 1 && numPairs >= 1) return true;
                }
            }
            if (js == 1) return (numPairs >= 2) || (numTriples >= 1 && cards.keySet().size() >= 2);
            if (js == 2) return (numPairs >= 1 && cards.keySet().size() >= 2) || (numTriples >= 1);
            if (js == 3) return (numPairs >= 1) || (cards.keySet().size() >= 2);
            return false;
        }),
        quad(9, (Coord[] hand) -> {
            if (hand.length < 4) return false;
            TreeMap<Integer, Integer> cards = new TreeMap<Integer, Integer>();
            int js = jokerCount(hand);
            if (js >= 3) return true;
            for (Coord card : hand) {
                if (!cards.containsKey(card.x())) cards.put(card.x(), 1);
                else {
                    cards.put(card.x(), cards.get(card.x()) + 1);
                    if (cards.get(card.x()) > 3 - js) return true;
                }
            }
            return false;
        }),
        straightFlush(10, (Coord[] hand) -> {
            if (hand.length < 5) return false;
            int[] consecs = new int[4];
            int js = jokerCount(hand);
            if (js >= 4) return true;
            if (js == 0) {
                int[] prevs = new int[4];
                for (Coord card : hand) {
                    if (card.y == 0) continue;
                    if (consecs[card.y() - 1] == 0) {
                        consecs[card.y() - 1] = 1;
                        prevs[card.y() - 1] = card.x();
                        continue;
                    }
                    if (prevs[card.y() - 1] + 1 == card.x) {
                        consecs[card.y() - 1]++;
                        prevs[card.y() - 1] = card.x();
                        if (consecs[card.y() - 1] == 5) return true;
                    } else if (prevs[card.y() - 1] != card.x) consecs[card.y() - 1] = 0;
                }
                for (int x = 0; x < 4; x++) {
                    if (prevs[x] == 13 && consecs[x] == 4) {
                        for (Coord c : hand) {
                            if (c.x == 1 && c.y == x + 1) return true;
                            if (c.x > 1) return false;
                        }
                    }
                }
            }
            else {
                boolean[][] ranks = new boolean[13][4];
                for (Coord card : hand) {
                    if (card.y == 0) continue;
                    ranks[card.x() - 1][card.y() - 1] = true;
                }
                for (int suit = 0; suit < 4; suit++) {
                    for (int rank = 0; rank < 10; rank++) {
                        consecs[suit] = js;
                        for (int x = 0; x < 5; x++) {
                            if (x+rank >= 13) {
                                if (consecs[suit] > 0 || ranks[0][suit]) return true;
                                break;
                            }
                            if (ranks[rank+x][suit]) {
                                if (x == 4) return true;
                            }
                            else if (consecs[suit] > 0) {
                                consecs[suit]--;
                                if (x == 4) return true;
                            }
                            else break;
                        }
                    }
                }
            }
            return false;
        }),
        five(11, (Coord[] hand) -> {
            if (hand.length < 5) return false;
            TreeMap<Integer, Integer> cards = new TreeMap<Integer, Integer>();
            int js = jokerCount(hand);
            if (js >= 4) return true;
            for (Coord card : hand) {
                if (!cards.containsKey(card.x())) cards.put(card.x(), 1);
                else {
                    cards.put(card.x(), cards.get(card.x()) + 1);
                    if (cards.get(card.x()) > 4 - js) return true;
                }
            }
            return false;
        }),
        six(12, (Coord[] hand) -> {
            if (hand.length < 6) return false;
            TreeMap<Integer, Integer> cards = new TreeMap<Integer, Integer>();
            int js = jokerCount(hand);
            if (js >= 5) return true;
            for (Coord card : hand) {
                if (!cards.containsKey(card.x())) cards.put(card.x(), 1);
                else {
                    cards.put(card.x(), cards.get(card.x()) + 1);
                    if (cards.get(card.x()) > 5 - js) return true;
                }
            }
            return false;
        }),
        crap(3, (Coord[] hand) -> {
            if (hand.length < 4) return false;
            TreeSet<Integer>[] cards = new TreeSet[4];
            for (int x = 0; x < 4; x++)
                cards[x] = new TreeSet<Integer>();
            int js = jokerCount(hand);
            if (js >= 3) return true;
            for (Coord card : hand) {
                if (card.y == 0) continue;
                cards[card.y() - 1].add(card.x());
            }
            int check = 0;
            TreeSet<Integer> unDump;
            for (int x = 0; x < 4; x++)
                if (cards[x].size() > 0) check++;
            if (check+js < 4) return false;
            for (int x = 0; x < 3; x++) {
                for (int y = x + 1; y < 4; y++) {
                    unDump = cards[x]; unDump.addAll(cards[y]);
                    if (unDump.size() > 1) check++;
                }
            }
            if (check+js < 4) return false;
            for (int x = 0; x < 2; x++) {
                for (int y = x + 1; y < 3; y++) {
                    for (int z = x + y + 1; z < 4; z++) {
                        unDump = cards[x]; unDump.addAll(cards[y]); unDump.addAll(cards[z]);
                        if (unDump.size() > 2) check++;
                    }
                }
            }
            if (check+js < 4) return false;
            unDump = cards[0]; unDump.addAll(cards[1]); unDump.addAll(cards[2]); unDump.addAll(cards[3]);
            if (unDump.size()+js > 3) return true;
            return false;
        });
        public int priority;
        public Function<Coord[], Boolean> condition;

        Hand(int p, Function<Coord[], Boolean> c) {
            priority = p;
            condition = c;
        }

        public static Hand[] allHands() {
            return new Hand[]{high, pair, twoPair, triple, straight, flush, fullHouse, quad, straightFlush, five, six, crap};
        }
    }

    public Gamblinator() {
        super("Gamblinator", 625, 700);
        new Camera(0,0,625,700);
        new Graph();
        buildSim();
        for (int c : handCountBySize.get(6)) {
            System.out.println(c);
        }
        System.out.println(Hand.straight.condition.apply(new Coord[]{new Coord(2,1),new Coord(3,1),new Coord(4,1),new Coord(5,1),new Coord(6,1)}));
        /*setPool();
        Coord[] hand = drawHand(5);
        for (Coord c : hand) System.out.println(cardNames(c));
        System.out.println();
        hand = sortHand(hand);
        for (Coord c : hand) System.out.println(cardNames(c));*/

    }

    public void buildSim() {
        //simHands = new TreeMap<Integer, boolean[][]>();
        handCountBySize = new TreeMap<Integer, int[]>();
        handHighestBySize = new TreeMap<Integer, int[]>();
        handCountBySizeWithJs = new TreeMap<Integer, int[]>();
        handHighestBySizeWithJs = new TreeMap<Integer, int[]>();
        for (int x = startHandSize; x <= endHandSize; x++) {
            fullSim(x);
        }
    }

    public void sim(int size) {
        Coord[] hand;
        //simHands.put(size, new boolean[sampleSize][Hand.allHands().length]);
        if (withJokers) {
            handCountBySizeWithJs.put(size, new int[Hand.allHands().length]);
            handHighestBySizeWithJs.put(size, new int[Hand.allHands().length]);
        }
        else {
            handCountBySize.put(size, new int[Hand.allHands().length]);
            handHighestBySize.put(size, new int[Hand.allHands().length]);
        }
        boolean[] handsPresent;
        for (int x = 0; x < sampleSize; x++) {
            setPool(withJokers);
            hand = drawHand(size);
            hand = sortHand(hand);
            handsPresent = checkCards(hand);
            //simHands.get(size)[x] = handsPresent;
            for (int y = 0; y < handsPresent.length; y++) {
                if (handsPresent[y]) {
                    if (withJokers)
                        handCountBySizeWithJs.get(size)[y]++;
                    else
                        handCountBySize.get(size)[y]++;
                }
            }
            for (int y = handsPresent.length - 1; y >= 0; y--) {
                if (handsPresent[y]) {
                    if (withJokers)
                        handHighestBySizeWithJs.get(size)[y]++;
                    else
                        handHighestBySize.get(size)[y]++;
                    break;
                }
            }
        }
    }

    public void fullSim(int size) {
        Coord[] hand;
        //simHands.put(size, new boolean[sampleSize][Hand.allHands().length]);
        handCountBySizeWithJs.put(size, new int[Hand.allHands().length]);
        handHighestBySizeWithJs.put(size, new int[Hand.allHands().length]);
        handCountBySize.put(size, new int[Hand.allHands().length]);
        handHighestBySize.put(size, new int[Hand.allHands().length]);
        boolean[] handsPresent;
        for (int x = 0; x < sampleSize; x++) {
            setPool(false);
            hand = drawHand(size);
            hand = sortHand(hand);
            handsPresent = checkCards(hand);
            //simHands.get(size)[x] = handsPresent;
            for (int y = 0; y < handsPresent.length; y++) {
                if (handsPresent[y]) {
                    handCountBySize.get(size)[y]++;
                }
            }
            for (int y = handsPresent.length - 1; y >= 0; y--) {
                if (handsPresent[y]) {
                    handHighestBySize.get(size)[y]++;
                    break;
                }
            }
            setPool(true);
            hand = drawHand(size);
            hand = sortHand(hand);
            handsPresent = checkCards(hand);
            //simHands.get(size)[x] = handsPresent;
            for (int y = 0; y < handsPresent.length; y++) {
                if (handsPresent[y]) {
                    handCountBySizeWithJs.get(size)[y]++;
                }
            }
            for (int y = handsPresent.length - 1; y >= 0; y--) {
                if (handsPresent[y]) {
                    handHighestBySizeWithJs.get(size)[y]++;
                    break;
                }
            }
        }
    }

    /*public void setPool() {
        pool = new ArrayList<Coord>();
        for (int rank = 1; rank <= 13; rank++) {
            for (int suit = 1; suit <= 4; suit++) {
                pool.add(new Coord(rank, suit));
            }
        }
    }*/

    public void setPool(boolean js) {
        pool = new ArrayList<Coord>();
        for (int rank = 1; rank <= 13; rank++) {
            for (int suit = 1; suit <= 4; suit++) {
                pool.add(new Coord(rank, suit));
            }
        }
        if (js) {
            pool.add(new Coord(1, 0));
            pool.add(new Coord(2, 0));
        }
    }

    public Coord[] drawHand(int size) {
        Coord[] ret = new Coord[size];
        int i;
        for (int x = 0; x < size; x++) {
            if (pool.isEmpty()) { ret[x] = new Coord(0,0); continue; }
            i = randomIntGen.nextInt(0,pool.size());
            ret[x] = pool.get(i);
            if (!replace) pool.remove(i);
        }
        return ret;
    }

    public Coord[] sortHand(Coord[] hand) {
        int[][] cardCounts = new int[13][4];
        Coord[] ret = new Coord[hand.length];
        int sortStart = 0;
        for (int i = 0; i < hand.length; i++) {
            if (hand[i].y == 0) {
                if (hand[i].x != 0) {
                    ret[sortStart] = new Coord(0, 0);
                    for (int pos = 0; pos <= sortStart; pos++) {
                        if (ret[pos].x == 0) { ret[pos] = hand[i]; }
                        /*if (ret[pos].x < hand[i].x) {
                            if (ret[pos].x == 0)

                        }*/
                    }
                }
                else
                    ret[sortStart] = hand[i];
                sortStart++;
            }
            else {
                cardCounts[hand[i].x() - 1][hand[i].y() - 1]++;
            }
        }
        for (int x = 0; x < 13; x++) {
            for (int y = 0; y < 4; y++) {
                for (int c = cardCounts[x][y]; c > 0; c--) {
                    ret[sortStart] = new Coord(x+1, y+1);
                    sortStart++;
                }
            }
        }
        return ret;
    }

    public static boolean[] checkCards(Coord[] hand) {
        boolean[] ret = new boolean[Hand.allHands().length];
        for (Hand h : Hand.allHands()) {
            ret[h.priority-1] = h.condition.apply(hand);
        }
        return ret;
    }

    public static int jokerCount(Coord[] hand) {
        for (int x = 0; x < hand.length; x++) {
            if (hand[x].y != 0 || hand[x].x == 0)
                return x;
        }
        return hand.length;
    }

    public static String cardNames(Coord card) {
        String ret = "" + card.x();
        if (card.x == 0) { ret = "null"; return ret; }
        if (card.y == 0) { ret = "Joker"; return ret; }
        if (card.x == 1) ret = "Ace";
        else if (card.x == 11) ret = "Jack";
        else if (card.x == 12) ret = "Queen";
        else if (card.x == 13) ret = "King";
        ret += " of ";
        if (card.y == 1) ret += "Spades";
        else if (card.y == 2) ret += "Clubs";
        else if (card.y == 3) ret += "Hearts";
        else if (card.y == 4) ret += "Diamonds";
        return ret;
    }

    public class Graph extends Script {

        int selectedSize = startHandSize;

        boolean single = false;
        boolean collapse = false;

        int range = 1;
        int origin = 5;

        public Graph() {
            CameraManager.addToDraw(this);
            KeyDetect.instance.addP(this);
            KeyDetect.instance.addH(this);
        }

        public void keyPressed(int e) {
            if (collapse) {
                if (e == KeyEvent.VK_RIGHT) { origin++; if (origin + range > endHandSize) { endHandSize++; fullSim(origin + range); } }
                if (e == KeyEvent.VK_LEFT && origin > 0) { origin--; if (origin < startHandSize) { startHandSize--; fullSim(origin); } }
                if (e == KeyEvent.VK_UP) { range++; if (origin + range > endHandSize) { endHandSize++; fullSim(origin + range); } }
                if (e == KeyEvent.VK_DOWN && range > 0) { range--; }
            }
            else {
                if (e == KeyEvent.VK_RIGHT) { selectedSize++; if (selectedSize > endHandSize) { endHandSize++; fullSim(selectedSize); } }
                if (e == KeyEvent.VK_LEFT && selectedSize > 0) { selectedSize--; if (selectedSize < startHandSize) { startHandSize--; fullSim(selectedSize); } }
            }
            if (e == KeyEvent.VK_R) sim(selectedSize);
            if (e == KeyEvent.VK_E) single = !single;
            if (e == KeyEvent.VK_Q) collapse = !collapse;
            if (e == KeyEvent.VK_J) { withJokers = !withJokers; }
            System.out.println(selectedSize);
        }

        public void render(Graphics g) {
            g.setColor(Color.white);
            g.fillRect(0,0,625,700);
            g.setColor(Color.gray);
            for (int x = 1; x <= 10; x++) {
                g.drawLine(25, 600 - x*60, 625,600 - x*60);
            }
            for (int x = 1; x <= Hand.allHands().length; x++) {
                g.drawLine((x*600/Hand.allHands().length) + 25, 0, (x*600/Hand.allHands().length) + 25, 600);
            }
            g.setColor(Color.darkGray);
            g.fillRect(24,0,3,600);
            g.fillRect(25,600,600,3);
            g.setColor(Color.black);
            if (collapse) {
                renderLinePlot(g);
            }
            else {
                renderDotPlot(g);
            }
        }

        public void renderDotPlot(Graphics g) {
            if (!withJokers) {
                if (handCountBySize.get(selectedSize) == null) { sim(selectedSize); return; }
                if (single) {
                    for (int x = 0; x < handHighestBySize.get(selectedSize).length; x++) {
                        g.fillOval((int) ((600. / Hand.allHands().length) * x) + 25 - 2, 600 - (int) (600. * handHighestBySize.get(selectedSize)[x] / sampleSize) - 2, 5, 5);
                        g.drawString((100f * handHighestBySize.get(selectedSize)[x] / sampleSize) + "%", (int) ((600. / Hand.allHands().length) * x) + 25, 620);
                    }
                } else {
                    for (int x = 0; x < handCountBySize.get(selectedSize).length; x++) {
                        g.fillOval((int) ((600. / Hand.allHands().length) * x) + 25 - 2, 600 - (int) (600. * handCountBySize.get(selectedSize)[x] / sampleSize) - 2, 5, 5);
                        g.drawString((100f * handCountBySize.get(selectedSize)[x] / sampleSize) + "%", (int) ((600. / Hand.allHands().length) * x) + 25, 620);
                    }
                }
            }
            else {
                if (handCountBySizeWithJs.get(selectedSize) == null) { sim(selectedSize); return; }
                if (single) {
                    for (int x = 0; x < handHighestBySizeWithJs.get(selectedSize).length; x++) {
                        g.fillOval((int) ((600. / Hand.allHands().length) * x) + 25 - 2, 600 - (int) (600. * handHighestBySizeWithJs.get(selectedSize)[x] / sampleSize) - 2, 5, 5);
                        g.drawString((100f * handHighestBySizeWithJs.get(selectedSize)[x] / sampleSize) + "%", (int) ((600. / Hand.allHands().length) * x) + 25, 620);
                    }
                } else {
                    for (int x = 0; x < handCountBySizeWithJs.get(selectedSize).length; x++) {
                        g.fillOval((int) ((600. / Hand.allHands().length) * x) + 25 - 2, 600 - (int) (600. * handCountBySizeWithJs.get(selectedSize)[x] / sampleSize) - 2, 5, 5);
                        g.drawString((100f * handCountBySizeWithJs.get(selectedSize)[x] / sampleSize) + "%", (int) ((600. / Hand.allHands().length) * x) + 25, 620);
                    }
                }
            }
        }

        public void renderLinePlot(Graphics g) {
            Color c = new Color(0, 0, 0);
            if (!withJokers) {
                if (single) {
                    for (int x = origin; x < origin + range; x++) {
                        c = new Color((int) (255. * (x - origin) / range), 0, 0);
                        g.setColor(c);
                        if (handHighestBySize.get(x) == null) return;
                        for (int y = 0; y < handHighestBySize.get(selectedSize).length - 1; y++) {
                            g.drawLine((int) ((600. / Hand.allHands().length) * y) + 25, 600 - (int) (600. * handHighestBySize.get(x)[y] / sampleSize), (int) ((600. / Hand.allHands().length) * (y + 1)) + 25, 600 - (int) (600. * handHighestBySize.get(x)[y + 1] / sampleSize));
                        }
                    }
                } else {
                    for (int x = origin; x < origin + range; x++) {
                        c = new Color((int) (255. * (x - origin) / range), 0, 0);
                        g.setColor(c);
                        if (handCountBySize.get(x) == null) return;
                        for (int y = 0; y < handCountBySize.get(selectedSize).length - 1; y++) {
                            g.drawLine((int) ((600. / Hand.allHands().length) * y) + 25, 600 - (int) (600. * handCountBySize.get(x)[y] / sampleSize), (int) ((600. / Hand.allHands().length) * (y + 1)) + 25, 600 - (int) (600. * handCountBySize.get(x)[y + 1] / sampleSize));
                        }
                    }
                }
            }
            else {
                if (single) {
                    for (int x = origin; x < origin + range; x++) {
                        c = new Color((int) (255. * (x - origin) / range), 0, 0);
                        g.setColor(c);
                        if (handHighestBySizeWithJs.get(x) == null) return;
                        for (int y = 0; y < handHighestBySizeWithJs.get(selectedSize).length - 1; y++) {
                            g.drawLine((int) ((600. / Hand.allHands().length) * y) + 25, 600 - (int) (600. * handHighestBySizeWithJs.get(x)[y] / sampleSize), (int) ((600. / Hand.allHands().length) * (y + 1)) + 25, 600 - (int) (600. * handHighestBySizeWithJs.get(x)[y + 1] / sampleSize));
                        }
                    }
                } else {
                    for (int x = origin; x < origin + range; x++) {
                        c = new Color((int) (255. * (x - origin) / range), 0, 0);
                        g.setColor(c);
                        if (handCountBySizeWithJs.get(x) == null) return;
                        for (int y = 0; y < handCountBySizeWithJs.get(selectedSize).length - 1; y++) {
                            g.drawLine((int) ((600. / Hand.allHands().length) * y) + 25, 600 - (int) (600. * handCountBySizeWithJs.get(x)[y] / sampleSize), (int) ((600. / Hand.allHands().length) * (y + 1)) + 25, 600 - (int) (600. * handCountBySizeWithJs.get(x)[y + 1] / sampleSize));
                        }
                    }
                }
            }
        }
    }
}
