import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.function.*;
import java.lang.Math;
import java.awt.Polygon;

import enjine.GeoMath.*;
import enjine.util.*;
import enjine.Prog;

public class Shooter extends Prog {

    public static void main(String[] args) {
        new Shooter();
    }

    int subLevel;

    int width = 600;
    int height = 300;
    
    int uiHeight = 50;

    boolean isPaused = false;

    ArrayList<Level> levels = new ArrayList<Level>();
    int level = 1;
    int maxLevel = 1;

    public Shooter() {
        super("Shooter", 600, 350);
        System.out.println("155");
        new Camera(0, uiHeight, width, height);
        new Camera(0, 0, width, uiHeight);
        new Camera(0, 0, width, uiHeight + height);
        /*SwingUtilities.invokeLater(() -> new Background());
        SwingUtilities.invokeLater(() -> new Menu());
        //SwingUtilities.invokeLater(() -> new Asteroid(256));
        SwingUtilities.invokeLater(() -> new Ship());
        //SwingUtilities.invokeLater(() -> new Enemy());
        SwingUtilities.invokeLater(() -> new AsteroidSpawner());
        SwingUtilities.invokeLater(() -> new AsteroidParticle());
        //SwingUtilities.invokeLater(() -> new LevelTitle());*/
        new EscListener();
        new MainMap();
        new Level(maxLevel);
        MainMap.instance.start();
    }

    public class EscListener extends Script {
        boolean confirm = false;
        public EscListener() {
            KeyDetect.instance.addP(this);
        }

        public void keyPressed(int e) {
            if (e == KeyEvent.VK_ESCAPE) {
                if (level != 0) {
                    if (confirm) {
                        curLevel().close();
                        MainMap.instance.start();
                        level = 0;
                        confirm = false;
                    }
                    else
                        confirm = true;
                }
            }
            else
                confirm = false;
        }
    }

    public Level curLevel() { return levels.get(level - 1); }

    public class Level extends Script {
        int i;
        Group objG = new Group();
        boolean isComplete = false;
        //ArrayList<Script> activateOnOpen = new ArrayList<Script>();
        public Level(int i) {
            levels.add(this);
            //start();
            this.i = i;
            MainMap.instance.addNode(new Coord(45*i, 45 + 10*i), this);
        }

        public void start() {
            /*activateOnOpen.add(new Background());
            activateOnOpen.add(new Menu());
            activateOnOpen.add(new Ship());
            activateOnOpen.add(new AsteroidSpawner());
            activateOnOpen.add(new AsteroidParticle());*/
            new Background();
            new Menu();
            new Ship();
            new AsteroidSpawner();
            new AsteroidParticle();
        }

        public void add(Script s) {
            objG.items.add(s);
        }

        public void remove(Script s) {
            objG.items.remove(s);
        }

        public void close() {
            for (Script s : objG.items) {
                s.destroy();
            }
            objG.items.clear();
            toAddU.clear();
            //toRemU.clear();
        }
    }
    
    public class Ship extends Script {
    
        public static Ship instance;
        
        double dx = 0;
        double dy = 0;
        
        //int[] shapeX = new int[] {0, 20, 0};
        //int[] shapeY = new int[] {0, 10, 20};
        Polygon shape = new Polygon(new int[] {0, 20, 0}, new int[] {0, 10, 20}, 3);
        
        Color color = Color.orange;
        
        Coord pos = new Coord();

        //frame-dependant
        //double speed = 3;
        double speed = 1.5f;

        ArrayList<Weapon> weapons = new ArrayList<Weapon>();
        Weapon weapon;
        int weaponIndex = 0;

        Coord inputMove = new Coord();

        int maxHp = 200;
        int hp = 0;
        
        public Ship() {
            weapons.add(new LaserCannon(pos, Bullet.Owner.player));
            weapons.add(new MachineGun(pos, Bullet.Owner.player));
            weapons.add(new RocketLauncher(pos, Bullet.Owner.player));
            weapons.add(new Shotgun(pos, Bullet.Owner.player));
            weapons.add(new FlameThrower(pos, Bullet.Owner.player));
            weapons.add(new LightningGun(pos, Bullet.Owner.player));
            weapons.add(new RailGun(pos, Bullet.Owner.player));
            weapons.add(new MineLauncher(pos, Bullet.Owner.player));
            weapons.add(new ForceField(pos, Bullet.Owner.player));
            weapon = weapons.get(weaponIndex);
            instance = this;
            pos.x = 20;
            pos.y = height / 2 + uiHeight;
            KeyDetect.instance.addH(this);
            KeyDetect.instance.addP(this);
            CameraManager.addToDraw(this, 1);
            addToUpdate(this);
            curLevel().add(this);
            hp = maxHp;
        }

        public void destroy() {
            KeyDetect.instance.removeH(this);
            KeyDetect.instance.removeP(this);
            CameraManager.removeToDraw(this, 1);
            removeToUpdate(this);
            
        }

        public void keyPressed(int e) {
            if (isPaused) return;
            /*if (e == KeyEvent.VK_UP) { inputMove.y += -1; }
            if (e == KeyEvent.VK_DOWN) { inputMove.y += 1; }
            if (e == KeyEvent.VK_LEFT) { inputMove.x += -1; }
            if (e == KeyEvent.VK_RIGHT) { inputMove.x += 1; }
            if (e == KeyEvent.VK_SPACE && weapon.canShoot) { shoot(); }*/
            if (e == KeyEvent.VK_N) { heal(10); }
            if (e == KeyEvent.VK_M) { damage(10); }
            if (e == KeyEvent.VK_Z) { weaponIndex = (weaponIndex + 1) % weapons.size(); weapons.get(weaponIndex).coolCur = weapon.coolCur; weapons.get(weaponIndex).canShoot = weapon.canShoot; weapon = weapons.get(weaponIndex); }
        }
        
        public void keyHeld(int e) {
            if (isPaused) return;
            /*if (e == KeyEvent.VK_UP && y - 10 > 0) { dy -= speed; }
            if (e == KeyEvent.VK_DOWN && y + 10 < height) { dy += speed; }
            if (e == KeyEvent.VK_LEFT && x > 0) { dx -= speed; }
            if (e == KeyEvent.VK_RIGHT && x + 20 < width) { dx += speed; }*/
            if (e == KeyEvent.VK_UP) { inputMove.y += -1; }
            if (e == KeyEvent.VK_DOWN) { inputMove.y += 1; }
            if (e == KeyEvent.VK_LEFT) { inputMove.x += -1; }
            if (e == KeyEvent.VK_RIGHT) { inputMove.x += 1; }
            System.out.println(inputMove);
            if (e == KeyEvent.VK_SPACE && weapon.canShoot) { shoot(); }
        }
        
        public void update() {
            if (isPaused) return;
            inputMove.reduce();
            dx += inputMove.x * speed;
            dy += inputMove.y * speed;
            inputMove.x = 0;
            inputMove.y = 0;
            if (weapon != null) {
                weapon.coolCur--;
                if (weapon.coolCur <= 0) {
                    weapon.canShoot = true;
                }
            }
            for (Weapon w : weapons) {
                w.update();
            }
            if (pos.x + dx < 0) {
                pos.x = 0;
            }
            else if (pos.x + dx > width - 20) {
                pos.x = width - 20;
            }
            else {
                pos.x += dx;
            }
            if (pos.y + dy < uiHeight + 10) {
                pos.y = uiHeight + 10;
            }
            else if (pos.y + dy > height + uiHeight - 10) {
                pos.y = height + uiHeight - 10;
            }
            else {
                pos.y += dy;
            }
            dx = (double) Math.log(Math.abs(dx) + 1) * Math.signum(dx);
            dy = (double) Math.log(Math.abs(dy) + 1) * Math.signum(dy);
        }
        
        public void shoot() {
            weapon.fire();
            //new LaserSweep(pos, new Coord(14, 0), 12, -12, (double) Math.PI / 40, Bullet.Owner.player);
            //new LaserSweep(pos, new Coord(14, 0), 12, 12, -(double) Math.PI / 40, Bullet.Owner.player);
            //new Laser(pos, new Coord(14, 0), 12, 0, Bullet.Owner.player);
            //new Laser(pos, new Coord(14, 0), 12, -6, Bullet.Owner.player);
            //new Missile(pos.x + 14, pos.y, 16, 0, Bullet.Owner.player);
        }

        public void heal(int amount) {
            hp += amount;
            if (hp > maxHp) hp = maxHp;
        }

        public void damage(int amount) {
            hp -= amount;
            if (hp <= 0) {
                curLevel().close();
                MainMap.instance.start();
                level = 0;
            }
        }
        
        public void render(Graphics g) {
          g = g.create((int) pos.x, (int) pos.y - 10, 100, 100);
          g.setColor(color);
          g.fillPolygon(shape);
        }

    }
    
    public class Enemy extends Script {
    
        public static HashSet<Enemy> instances = new HashSet<Enemy>();
        
        double dx = 0;
        double dy = 0;
        
        //int[] shapeX = new int[] {0, 20, 0};
        //int[] shapeY = new int[] {0, 10, 20};
        Polygon shape = new Polygon(new int[] {20, 0, 20}, new int[] {0, 10, 20}, 3);
        
        Color color = Color.red;
        
        Coord pos = new Coord();

        //frame-dependant
        //double speed = 3;
        double speed = 1.5f;
        
        //frame-dependant
        //int shootCooldown = 500;
        int shootCooldown = 1000;

        Weapon weapon = new RocketLauncher(pos, Bullet.Owner.enemy);
        
        public boolean canShoot = true;
        
        public Enemy() {
            instances.add(this);
            pos.x = 400;
            pos.y = 150;
            CameraManager.addToDraw(this, 1);
            addToUpdate(this);
            curLevel().add(this);
        }
        
        public void update() {
            if (isPaused) return;
            move();
            pos.x += dx;
            pos.y += dy;
            dx = 0;
            dy = 0;
        }
        
        public void move() {
            if (weapon != null) {
                weapon.coolCur--;
                if (weapon.coolCur <= 0) {
                    weapon.canShoot = true;
                }
            }
            if (pos.y + 10 >= height + uiHeight && speed > 0) { speed = -speed;}
            if (pos.y - 10 <= uiHeight && speed < 0) { speed = -speed;}
            dy = speed;
            if (weapon.canShoot) { weapon.fire(); }
        }
        
        public void shoot() {
          weapon.fire();
        }
        
        public void render(Graphics g) {
          g = g.create((int) pos.x, (int) pos.y - 10, 100, 100);
          g.setColor(color);
          g.fillPolygon(shape);
        }
    }

    public class Weapon extends Script {

        Coord pos;

        int coolCur;
        int shootCooldown;
        
        public boolean canShoot = true;

        Bullet.Owner o;

        int uL = 5;

        public Weapon(Coord p, Bullet.Owner owner) {
            pos = p;
            o = owner;
        }
        
        public void fire() {
            coolCur = shootCooldown;
            canShoot = false;
        }

        public void upgrade() {
            uL++;
            //System.out.println(uL);
        }
    }

    public class MachineGun extends Weapon {
        public MachineGun(Coord p, Bullet.Owner owner) {
            super(p, owner);
            
            //frame-dependant
            //shootCooldown = 5;
            shootCooldown = 10;
        }

        public void fire() {
            super.fire();
            APBullet b;
            if (o == Bullet.Owner.enemy)
                b = new APBullet(pos.x + 6, pos.y, -12, 0, o);
            else 
                b = new APBullet(pos.x + 14, pos.y, 12, Ship.instance.dy, o);
            b.pierce = 0;
            b.damage = 8 + uL;
            b.source = this;
        }
    }

    public class RocketLauncher extends Weapon {
        public RocketLauncher(Coord p, Bullet.Owner owner) {
            super(p, owner);
            
            //frame-dependant
            //shootCooldown = 40;
            shootCooldown = 80;
        }

        public void fire() {
            super.fire();
            Missile b;
            if (o == Bullet.Owner.enemy)
                b = new Missile(pos.x + 6, pos.y, -16, 0, o);
            else 
                b = new Missile(pos.x + 14, pos.y, 16, 0, o);
            b.expRad += uL * 5;
            b.source = this;
        }
    }

    public class LaserCannon extends Weapon {

        boolean dir = true;

        public int heat = 0;
        public int maxHeat = 121;

        public LaserCannon(Coord p, Bullet.Owner owner) {
            super(p, owner);
            
            //frame-dependant
            //shootCooldown = 20;
            shootCooldown = 40;
            //shootCooldown = 3;
        }

        public void update() {
            if (heat < 0) {
                heat++;
                if (heat >= -80)
                    heat = -heat;
            }
            if (heat > 0 && canShoot) {
                heat--;
            }
        }

        //frame-dependant
        public void fire() {
            if (heat < 0) {
                return;
            }
            else if (heat < maxHeat) {
                heat += 41;
            }
            else {
                heat = -heat;
            }
            super.fire();
            Laser ls;
            if (o == Bullet.Owner.enemy) {
                //ls = new Laser(pos, new Coord(6, 0), -16, 0, o);
                if (dir) {
                    ls = new LaserSweep(pos, new Coord(6, 0), -16, 0, (double) Math.PI/80 /*40*/, o);
                    ls.dir.a = (double) Math.PI / 4 * 3;
                }
                else {
                    ls = new LaserSweep(pos, new Coord(6, 0), -16, 0, (double) -Math.PI/80 /*40*/, o);
                    ls.dir.a = (double) -Math.PI / 4 * 3;
                }
            }
            else
            {
                //ls = new Laser(pos, new Coord(6, 0), 16, 0, o);
                if (dir) {
                    ls = new LaserSweep(pos, new Coord(14, 0), 16, 0, (double) Math.PI/80/(uL+1) /*40*/, o);
                    ls.dir.a = (double) -Math.PI / 4 / (uL+1);
                }
                else {
                    ls = new LaserSweep(pos, new Coord(14, 0), 16, 0, (double) -Math.PI/80/(uL+1) /*40*/, o);
                    ls.dir.a = (double) Math.PI / 4 / (uL+1);
                }
            }
            ls.source = this;
            dir = !dir;
        }
    }

    public class Shotgun extends Weapon {

        double spread = (double) Math.PI / 4;

        int bullets = 8;
        
        public Shotgun(Coord p, Bullet.Owner owner) {
            super(p, owner);
            
            //frame-dependant
            //shootCooldown = 30;
            shootCooldown = 60;
        }

        public void fire() {
            super.fire();
            Bullet b;
            double dy;
            if (o == Bullet.Owner.enemy) {
                for (int x = 0; x < bullets + uL; x++) {
                    dy = (double) Math.sin(randomIntGen.nextDouble(-spread / 2, spread / 2));
                    b = new Bullet(pos.x + 6, pos.y, - (double) Math.sqrt(1 - dy * dy) * 18, dy * 18, o);
                    b.pierce = 3;
                    b.damage = 6;
                    b.kb = 1.25f;
                    b.source = this;
                }
            }
            else
            {
                for (int x = 0; x < bullets + uL; x++) {
                    dy = (double) Math.sin(randomIntGen.nextDouble(-spread / 2, spread / 2));
                    b = new Bullet(pos.x + 14, pos.y, (double) Math.sqrt(1 - dy * dy) * 18, dy * 18, o);
                    b.pierce = 3;
                    b.damage = 4;
                    b.kb = 1.25f;
                    Ship.instance.dx -= 5;
                    b.source = this;
                }
            }
        }
    }

    public class LightningGun extends Weapon {
        public LightningGun(Coord p, Bullet.Owner owner) {
            super(p, owner);
            
            //frame-dependant
            //shootCooldown = 30;
            shootCooldown = 50;
            //shootCooldown = 3;
        }

        public void fire() {
            super.fire();
            Lightning b;
            if (o == Bullet.Owner.enemy)
                b = new Lightning(pos, new Coord(6, 0), -16, 0, o);
            else 
                b = new Lightning(pos, new Coord(14, 0), 16, 0, o);
            b.pierce = 5 + uL;
            b.maxPierce = 25 + uL * 5;
            b.damage = 8;
            b.kb = .5f;
            b.source = this;
        }
    }

    public class FlameThrower extends Weapon {

        double spread = (double) Math.PI / 6;
        int bullets = 2;
        
        public FlameThrower(Coord p, Bullet.Owner owner) {
            super(p, owner);
            
            //frame-dependant
            //shootCooldown = 1;
            shootCooldown = 1;
        }

        public void fire() {
            super.fire();
            Flame b;
            double dy;
            if (o == Bullet.Owner.enemy) {
                for (int x = 0; x < bullets; x++) {
                    dy = (double) Math.sin(randomIntGen.nextDouble(-spread / 2, spread / 2));
                    b = new Flame(pos.x + 6, pos.y, - (double) Math.sqrt(1 - dy * dy) * randomIntGen.nextDouble(5.75f, 6.25f) * 1.5f, dy * randomIntGen.nextDouble(5.75f, 6.25f) * 1.5f, o);
                    b.pierce += uL;
                    b.source = this;
                }
            }
            else
            {
                for (int x = 0; x < bullets; x++) {
                    dy = (double) Math.sin(randomIntGen.nextDouble(-spread / 2, spread / 2));
                    b = new Flame(pos.x + 14, pos.y, (double) Math.sqrt(1 - dy * dy) * randomIntGen.nextDouble(5.75f, 6.25f) * 1.5f + Ship.instance.dx, dy * randomIntGen.nextDouble(5.75f, 6.25f) * 1.5f + Ship.instance.dy, o);
                    b.pierce += uL;
                    b.source = this;
                }
            }
        }
    }

    public class RailGun extends Weapon {
        public RailGun(Coord p, Bullet.Owner owner) {
            super(p, owner);

            //frame-dependant
            //shootCooldown = 60;
            shootCooldown = 120;
        }

        public void fire() {
            super.fire();
            Bullet b;
            for (int x = 0; x < 2; x++) {
                if (o == Bullet.Owner.enemy)
                    b = new APBullet(pos.x + 6, pos.y, -25, 0, o);
                else
                    b = new APBullet(pos.x + 14, pos.y, 25, Ship.instance.dy, o);
                b.pierce = 4;
                b.damage = 20 + uL * 3;
                b.source = this;
            }
        }
    }

    public class MineLauncher extends Weapon {
        public MineLauncher(Coord p, Bullet.Owner owner) {
            super(p, owner);

            //frame-dependant
            //shootCooldown = 60;
            shootCooldown = 60;
        }

        public void fire() {
            super.fire();
            Bullet b;
            if (o == Bullet.Owner.enemy)
                b = new Mine(pos.x + 6, pos.y, -10, 0, o);
            else
                b = new Mine(pos.x + 14, pos.y, 10, Ship.instance.dy, o);
            b.pierce = 0;
            b.damage = 0;
            b.source = this;
        }
    }

    public class ForceField extends Weapon {

        boolean isActive = false;

        Explosion field = null;

        public ForceField(Coord p, Bullet.Owner owner) {
            super(p, owner);

            //frame-dependant
            //shootCooldown = 60;
            shootCooldown = 0;
        }

        public void update() {
            if (field != null) {
                if (!isActive)
                    field.destroy();
                else {
                    field.curFrames = 0;
                    isActive = false;
                }
            }
        }
        public void fire() {
            super.fire();
            isActive = true;
            Explosion e;
            if (o == Bullet.Owner.enemy)
                e = new Explosion(pos, 25, 0, 2);
            else
                e = new Explosion(pos, 25, 0, 2);
        }
    }
    
    public class Bullet extends Script {
    
        Coord pos = new Coord();
        
        double spdX;
        double spdY;
        
        int pierce = 2;

        int damage = 6;

        double kb = 1.0f;
        
        Color c = new Color(100, 150, 255);
        Color subC = new Color(180, 200, 255);

        int maxFrames = -1;
        int framesActive = 0;
        
        public Owner o;

        public Weapon source;
        
        public HashSet<Asteroid> ignores = new HashSet<Asteroid>();
        
        public enum Owner {
            player(1),
            enemy(2),
            none(0);
            int owner;
            Owner(int o) { owner = o; }
        }
        
        public Bullet(double x, double y, double spdX, double spdY, Owner o) {
            this.o = o;
            if (o != Owner.player) {
                c = Color.red;
                subC = new Color(0, 230, 230);
            }
            pos.x = x;
            pos.y = y;
            
            //frame-dependant
            this.spdX = spdX/2;
            this.spdY = spdY/2;
            addToUpdate(this);
            curLevel().add(this);
            CameraManager.addToDraw(this, 1);
        }

        public void destroy() {
            removeToUpdate(this);
            CameraManager.removeToDraw(this, 1);
            
        }
        
        public void render(Graphics g) {
            g.setColor(c);
            g.fillOval((int) pos.x - 3, (int) pos.y - 2, 6, 4);
        }
        
        public void update() {
            if (isPaused) return;
            pos.x += spdX;
            pos.y += spdY;
            if (pos.x >= width + 10 || pos.x <= -10 || pos.y >= height + uiHeight + 10 || pos.y <= uiHeight - 10) {
                destroy();
                curLevel().remove(this);
            }
            for (Asteroid a : Asteroid.instances) {
            if (!a.sim) { continue; }
            if (pos.distanceTo(a.pos) <= a.size + 2) {
                if (ignores.contains(a)) continue;
                a.damage(damage, pos.x, pos.y, kb);
                // push the asteroid a little
                if (pierce == 0) {
                    destroy();
                    curLevel().remove(this);
                }
                else {
                    ignores.add(a);
                    pierce--;
                }
            }
            else if (ignores.contains(a)) ignores.remove(a);
            }
        }
    }

    public class APBullet extends Bullet {
        public APBullet(double x, double y, double spdX, double spdY, Owner o) {
            super(x, y, spdX, spdY, o);
        }

        public void update() {
            if (isPaused) return;
            pos.x += spdX;
            pos.y += spdY;
            if (pos.x >= width + 10 || pos.x <= -10 || pos.y >= height + uiHeight + 10 || pos.y <= uiHeight - 10) {
                destroy();
                curLevel().remove(this);
            }
            for (Asteroid a : Asteroid.instances) {
            if (!a.sim) { continue; }
            if (pos.distanceTo(a.pos) <= a.size + 2) {
                if (ignores.contains(a)) continue;
                a.damage(damage, pos.x, pos.y, kb);
                // push the asteroid a little
                if (damage <= 0) {
                    destroy();
                    curLevel().remove(this);
                    return;
                }
                else {
                    ignores.add(a);
                    if (pierce != 0) {
                        pierce--;
                    }
                    else {
                        damage = (damage - a.hp) / 2;
                    }
                    if (damage <= 0) {
                        destroy();
                        curLevel().remove(this);
                        return;
                    }
                }
            }
            else if (ignores.contains(a)) ignores.remove(a);
            }
        }
    }

    public class Missile extends Bullet {

        double expRad = 50f;

        CircleCollider coll;

        //int expDur = 8;
        int expDur = 16;

        //frame-dependant

        //int iFrames = 6;
        int iFrames = 12;
        int iFramesCur = 0;

        public Missile(double x, double y, double spdX, double spdY, Owner o) {
            super(x, y, spdX, spdY, o);
            coll = new CircleCollider(pos, 3);
            pierce = 2;
            damage = 0;
        }

        public void update() {
            if (isPaused) return;
            pos.x += spdX;
            pos.y += spdY;
            if (pos.x >= width + 10 || pos.x <= -10 || pos.y >= height + uiHeight + 10 || pos.y <= uiHeight - 10) {
                destroy();
                curLevel().remove(this);
            }
            if (iFramesCur >= 0) {
                iFramesCur--;
                return;
            }
            else {
                ignores.clear();
            }
            for (Asteroid a : Asteroid.instances) {
                if (!a.sim) { continue; }
                if (coll.isColliding(a.coll)) {
                    if (ignores.contains(a)) continue;
                    ignores.add(a);
                    pierce--;
                    SwingUtilities.invokeLater(() -> new Explosion(new Coord(pos), expRad, 20, expDur));
                    if (pierce == 0) {
                        destroy();
                        curLevel().remove(this);
                    }
                    iFramesCur = iFrames;
                    break;
                }
            }
        }

        public void render(Graphics g) {
            g.setColor(c);
            g.fillOval((int) pos.x - 3, (int) pos.y - 3, 3 * 2, 3 * 2);
        }

    }

    public class Mine extends Bullet {

        double expRad = 50f;

        CircleCollider coll;

        //int expDur = 8;
        int expDur = 16;

        //frame-dependant

        //int iFrames = 6;
        int iFrames = 12;
        int iFramesCur = 0;

        public Mine(double x, double y, double spdX, double spdY, Owner o) {
            super(x, y, spdX, spdY, o);
            coll = new CircleCollider(pos, 25);
            pierce = 0;
            damage = 0;
        }

        public void update() {
            if (isPaused) return;
            pos.x += spdX;
            pos.y += spdY;
            spdX /= 1.03f;
            spdY /= 1.03f;
            if (pos.x >= width + 10 || pos.x <= -10 || pos.y >= height + uiHeight + 10 || pos.y <= uiHeight - 10) {
                destroy();
                curLevel().remove(this);
            }
            if (iFramesCur >= 0) {
                iFramesCur--;
                return;
            }
            else {
                ignores.clear();
            }
            for (Asteroid a : Asteroid.instances) {
                if (!a.sim) { continue; }
                if (coll.isColliding(a.coll)) {
                    if (ignores.contains(a)) continue;
                    ignores.add(a);
                    pierce--;
                    SwingUtilities.invokeLater(() -> new Explosion(new Coord(pos), expRad, 20, expDur));
                    if (pierce <= 0) {
                        destroy();
                        curLevel().remove(this);
                    }
                    iFramesCur = iFrames;
                    break;
                }
            }
        }

        public void render(Graphics g) {
            g.setColor(c);
            g.fillOval((int) pos.x - 3, (int) pos.y - 3, 3 * 2, 3 * 2);
        }

    }

    public class Explosion extends Script {

        public HashSet<Asteroid> ignores = new HashSet<Asteroid>();

        public int d;
        public Coord p;
        public double r;

        public int maxFrames = 40;
        public int curFrames = 0;

        CircleCollider coll;
        
        public Explosion(Coord pos, double rad, int damage, int dur) {
            p = pos;
            r = rad;
            d = damage;
            maxFrames = dur;
            coll = new CircleCollider(p, r);
            addToUpdate(this);
            curLevel().add(this);
            CameraManager.addToDraw(this, 1);
            //System.out.println("new explode");
        }

        public void destroy() {
            removeToUpdate(this);
            CameraManager.removeToDraw(this, 1);
            
        }

        public void update() {
            if (isPaused) return;
            curFrames++;
            if (curFrames >= maxFrames) {
                destroy();
                curLevel().remove(this);
            }
            for (Asteroid a : Asteroid.instances) {
                if (coll.isColliding(a.coll)) {
                    if (ignores.contains(a)) continue;
                    ignores.add(a);
                    a.damage(d, p.x, p.y, 5f);
                }
                else if (ignores.contains(a)) ignores.remove(a);
            }
        }

        public void render(Graphics g) {
            g.setColor(Color.red);
            g.fillOval((int) (p.x - r), (int) (p.y - r), (int) r * 2, (int) r * 2);
        }
    }
    
    public class Laser extends Bullet {
    
        Coord offs = new Coord();
        PCoord dir = new PCoord();
        
        //frame-dependant
        //int maxFrames = 3;
        int maxFrames = 6;
        int width = 5;
        
        RectCollider coll;
        
        public Laser(Coord og, Coord offs, double spdX, double spdY, Owner o) {
            super(og.x, og.y, spdX, spdY, o);
            pos = og;
            this.offs = offs;
            dir.a = (double) Math.atan2(spdY, spdX);
            dir.d = 600;
            coll = new RectCollider(new Coord(pos.x + offs.x, pos.y + offs.y), dir, width);
            pierce = -1;
            damage = 1;
            kb = .15f;
        }

        public void destroy() {
            removeToUpdate(this);
            SwingUtilities.invokeLater(() -> CameraManager.removeToDraw(this, 1));
            
        }
        
        public void update() {
            if (isPaused) return;
            dir.d = 650;
            coll.og.x = pos.x + offs.x;
            coll.og.y = pos.y + offs.y;
            framesActive++;
            if (framesActive >= maxFrames) {
                destroy();
                curLevel().remove(this);
                return;
            }
            for (Asteroid a : Asteroid.instances) {
                if (!a.sim) { continue; }
                if (coll.isColliding(a.coll)) {
                    //if (ignores.contains(a)) continue;
                    a.damage(damage, pos.x, pos.y, kb);
                    // push the asteroid a little
                    if (pierce == 0) {
                        destroy();
                        curLevel().remove(this);
                    }
                    else {
                        //ignores.add(a);
                        pierce--;
                    }
                }
                else if (ignores.contains(a)) ignores.remove(a);
            }
        }
        
        public void render(Graphics g) {
            g.setColor(c);
            double sa = (double) Math.sin(dir.a);
            double ca = (double) Math.cos(dir.a);
            
            //frame-dependant
            double miniLaser = Math.abs(8 - (frame) % 17) / 10.0f + .1f;
            g.fillPolygon(new int[] {(int) (pos.x + offs.x + sa * width), (int) (pos.x + offs.x + ca * dir.d + sa * width), (int) (pos.x + offs.x + ca * dir.d - sa * width), (int) (pos.x + offs.x - sa * width)}
                      , new int[] {(int) (pos.y + offs.y - ca * width), (int) (pos.y + offs.y + sa * dir.d - ca * width), (int) (pos.y + offs.y + sa * dir.d + ca * width), (int) (pos.y + offs.y + ca * width)}, 4);
            
            g.setColor(subC);
            g.fillPolygon(new int[] {(int) (pos.x + offs.x + sa * width * miniLaser), (int) (pos.x + offs.x + ca * dir.d + sa * width * miniLaser), (int) (pos.x + offs.x + ca * dir.d - sa * width * miniLaser), (int) (pos.x + offs.x - sa * width * miniLaser)}
                      , new int[] {(int) (pos.y + offs.y - ca * width * miniLaser), (int) (pos.y + offs.y + sa * dir.d - ca * width * miniLaser), (int) (pos.y + offs.y + sa * dir.d + ca * width * miniLaser), (int) (pos.y + offs.y + ca * width * miniLaser)}, 4);
        }
        
    }
        
    public class LaserSweep extends Laser {
    
        Coord offs = new Coord();
        
        //frame-dependant
        //int maxFrames = 20;
        int maxFrames = 40;
        
        double da;
        
        
        public LaserSweep(Coord og, Coord offs, double spdX, double spdY, double speedA, Owner o) {
            super(og, offs, spdX, spdY, o);
            da = speedA;
            pos = og;
            this.offs = offs;
            dir.a = (double) Math.atan2(spdY, spdX);
            dir.d = 650;
            coll = new RectCollider(new Coord(pos.x + offs.x, pos.y + offs.y), dir, width);
            pierce = -1;
            damage = 1;
        }
        
        public void update() {
            if (isPaused) return;
            coll.og.x = pos.x + offs.x;
            coll.og.y = pos.y + offs.y;
            framesActive++;
            dir.a += da;
            if (framesActive >= maxFrames) {
                destroy();
                curLevel().remove(this);
                return;
            }
            for (Asteroid a : Asteroid.instances) {
              if (!a.sim) { continue; }
              if (coll.isColliding(a.coll)) {
                  //if (ignores.contains(a)) continue;
                  a.damage(damage, pos.x, pos.y, kb);
                  // push the asteroid a little
                  if (pierce == 0) {
                      destroy();
                      curLevel().remove(this);
                  }
                  else {
                      //ignores.add(a);
                      pierce--;
                  }
              }
              else if (ignores.contains(a)) ignores.remove(a);
            }
        }
        
        /*public void render(Graphics g) {
            g.setColor(c);
            double sa = (double) Math.sin(dir.a);
            double ca = (double) Math.cos(dir.a);
            g.fillPolygon(new int[] {(int) (pos.x + offs.x + sa * width), (int) (pos.x + offs.x + ca * dir.d + sa * width), (int) (pos.x + offs.x + ca * dir.d - sa * width), (int) (pos.x + offs.x - sa * width)}
                      , new int[] {(int) (pos.y + offs.y - ca * width), (int) (pos.y + offs.y + sa * dir.d - ca * width), (int) (pos.y + offs.y + sa * dir.d + ca * width), (int) (pos.y + offs.y + ca * width)}, 4);
        }*/
    
    }

    public class Lightning extends Bullet {
    
        Coord offs = new Coord();
        ArrayList<Coord> targs = new ArrayList<Coord>();
        CircleCollider targetSpace;
        //frame-dependant
        //int maxFrames = 3;
        int maxFrames = 6;
        int width = 3;
        double targetRad = 100;
        int delay = -2;
        int maxPierce = 20;
        //RectCollider coll;
        
        public Lightning(Coord og, Coord offs, double spdX, double spdY, Owner o) {
            super(og.x, og.y, spdX, spdY, o);
            pos = og;
            this.offs = offs;
            //coll = new RectCollider(new Coord(pos.x + offs.x, pos.y + offs.y), dir, width);
            pierce = 10;
            damage = 10;
            kb = .5f;
            targetSpace = new CircleCollider(new Coord(pos.x + offs.x, pos.y + offs.y), targetRad);
        }

        public void destroy() {
            removeToUpdate(this);
            SwingUtilities.invokeLater(() -> CameraManager.removeToDraw(this, 1));
            
        }
        
        public void update() {
            if (isPaused) return;
            if (targs.isEmpty()) {
                targetSpace.og.x = pos.x + offs.x;
                targetSpace.og.y = pos.y + offs.y;
            }
            framesActive++;
            if (framesActive >= maxFrames) {
                if (targs.isEmpty()) { source.canShoot = true; source.coolCur = 0; }
                destroy();
                curLevel().remove(this);
                return;
            }
            if (pierce <= 0 || maxPierce <= 0) { return; }
            boolean didHit = false;
            Asteroid targ;
            for (int x = delay; x <= 0; x++) {
                targ = null;
                for (Asteroid a : Asteroid.instances) {
                    if (!a.sim) { continue; }
                    if (targetSpace.isColliding(a.coll)) {
                        if (ignores.contains(a)) continue;
                        //a.damage(damage, pos.x, pos.y, kb);
                        if (targ == null) { targ = a; }
                        else {
                            if (targetSpace.og.distanceTo(a.pos) - a.size < targetSpace.og.distanceTo(targ.pos) - targ.size) {
                                targ = a;
                            }
                        }
                    }
                }
                if (targ != null) {
                    didHit = true;
                    ignores.add(targ);
                    if (targs.isEmpty())
                        targ.damage(damage, pos.x, pos.y, kb);
                    else 
                        targ.damage(damage, targs.get(targs.size() - 1).x, targs.get(targs.size() - 1).y, kb);
                    targs.add(targ.pos);
                    targetSpace.og = targ.pos;
                    if (targ.hp > 0) {
                        pierce--;
                    }
                    maxPierce--;
                }
                else {
                    pierce = 0;
                    return;
                }
            }
            //if (didHit) { maxFrames++; }
            if (didHit) { framesActive = 0; }
        }
        
        public void render(Graphics g) {
            g.setColor(c);
            if (!targs.isEmpty()) {
                double sa = (double) Math.sin(Coord.angleBetween(new Coord(pos.x + offs.x, pos.y + offs.y), targs.get(0)));
                double ca = (double) Math.cos(Coord.angleBetween(new Coord(pos.x + offs.x, pos.y + offs.y), targs.get(0)));
                g.fillPolygon(new int[] {(int) (pos.x + offs.x + sa * width), (int) (targs.get(0).x + sa * width), (int) (targs.get(0).x - sa * width), (int) (pos.x + offs.x - sa * width)}
                  , new int[] {(int) (pos.y + offs.y - ca * width), (int) (targs.get(0).y - ca * width), (int) (targs.get(0).y + ca * width), (int) (pos.y + offs.y + ca * width)}, 4);
                for (int x = 0; x < targs.size() - 1; x++) {
                    sa = (double) Math.sin(Coord.angleBetween(targs.get(x), targs.get(x + 1)));
                    ca = (double) Math.cos(Coord.angleBetween(targs.get(x), targs.get(x + 1)));
                    g.fillPolygon(new int[] {(int) (targs.get(x).x + sa * width), (int) (targs.get(x + 1).x + sa * width), (int) (targs.get(x + 1).x - sa * width), (int) (targs.get(x).x - sa * width)}
                      , new int[] {(int) (targs.get(x).y - ca * width), (int) (targs.get(x + 1).y - ca * width), (int) (targs.get(x + 1).y + ca * width), (int) (targs.get(x).y + ca * width)}, 4);
                }
            }
        }
    }

    public class Flame extends Bullet {

        PointCollider coll;

        //frame-dependant
        //int maxFrames = 15;
        int maxFrames = 30;

        public Flame(double x, double y, double spdX, double spdY, Owner o) {
            super(x, y, spdX, spdY, o);
            coll = new PointCollider(pos);
            pierce = 7;
            damage = 1;
            kb = 0.04f;
            c = Color.yellow;
        }

        public void update() {
            if (isPaused) return;
            c = new Color(1f, 1f * (1 - (float) framesActive / maxFrames), 0f);
            framesActive++;
            if (framesActive > maxFrames) {
                destroy();
                curLevel().remove(this);
            }
            pos.x += spdX;
            pos.y += spdY;
            spdX /= 1.02f;
            spdY *= 1.02f;
            if (pos.x > width || pos.x < 0 || pos.y > height + uiHeight || pos.y < uiHeight) {
                destroy();
                curLevel().remove(this);
            }
            for (Asteroid a : Asteroid.instances) {
                if (!a.sim) { continue; }
                if (coll.isColliding(a.coll)) {
                    if (ignores.contains(a)) continue;
                    ignores.add(a);
                    pierce--;
                    a.damage(damage, pos.x, pos.y, kb);
                    if (pierce == 0) {
                        destroy();
                        curLevel().remove(this);
                    }
                    break;
                }
            }
        }

        public void render(Graphics g) {
            g.setColor(c);
            g.fillOval((int) pos.x - 1, (int) pos.y - 1, 2, 2);
        }
    }
    
    public class AsteroidSpawner extends Script {
    
        public static AsteroidSpawner instance;
        
        public boolean doSpawn = false;
        
        /*//frame-dependant
        //int waveDelay = 32000;
        int waveDelay = 64000;
        int waveCount = 4;
        //int spawnDelay = 10800;
        int spawnDelay = 21600;
        int spawnCount = 3;*/
        //int initDelay = 200;
        int initDelay = 800;

        WaveType wave;

        int numWaves = -1;

        Spawn si;
        
        public AsteroidSpawner() {
            curLevel().add(this);
            instance = this;
            wave = WaveType.randomType();
            si = new Spawn();
        }

        public void destroy() {
            removeToUpdate(this);
            
        }

        public void update() {
            if (isPaused) return;
            if (Asteroid.instances.isEmpty()) {
                if (level == maxLevel) {
                    for (Weapon w : Ship.instance.weapons) {
                        w.upgrade();
                    }
                    maxLevel++;
                    new Level(maxLevel);
                }
                curLevel().isComplete = true;
                curLevel().close();
                MainMap.instance.start();
                /*removeToUpdate(this);
                si.m = Spawn.Mode.init;
                si.initF = initDelay;
                si.waveF = wave.waveDelay;
                si.runIF = true;
                LevelTitle.instance.trigger();*/
            }
        }

        public enum WaveType {
            swarm(24000, 36, 750, 2, 10, 25, 1, 7, 13),
            avg(24000, 4, 10000, 5, 30, 60, 2, 10, 25),
            giants(32000, 2, 12000, 3, 84, 112, 0, 10, 25);

            int waveDelay;
            int waveCount;
            int spawnDelay;
            int spawnCount;
            int asteroidSizeMin;
            int asteroidSizeMax;
            int cometCount;
            int cometSizeMin;
            int cometSizeMax;

            WaveType(int wd, int wc, int sd, int sc, int asMin, int asMax, int cc, int cMin, int cMax) {
                waveDelay = wd;
                waveCount = wc;
                spawnDelay = sd;
                spawnCount = sc;
                asteroidSizeMin = asMin;
                asteroidSizeMax = asMax;
                cometCount = sc;
                cometSizeMin = cMin;
                cometSizeMax = cMax;
            }

            public static WaveType randomType() {
                int i = randomIntGen.nextInt(0, 3);
                switch (i) {
                    case 1:
                        return swarm;
                    case 2:
                        return giants;
                    default:
                        return avg;
                }
            }

            public int genASize() {
                return randomIntGen.nextInt(asteroidSizeMin, asteroidSizeMax);
            }

            public int genCSize() { return randomIntGen.nextInt(cometSizeMin, cometSizeMax); }
        }
        
        public class Wave extends Script {
            int fs;
            int spawnsRemain;
            Spawn s;
            public Wave(Spawn s) {
                this.s = s;
                fs = wave.spawnDelay;
                //start();
                sim = false;
                addToUpdate(this);
                curLevel().add(this);
            }
  
            public void destroy() {
                removeToUpdate(this);
            }
  
            public void update() {
                //System.out.println(); //////////////////////////////////////////////////////////////////////////////
                if (isPaused) return;
                fs -= frameRate;
                if (fs <= 0) { actionPerformed(); }
            }
          
            public void startWave() { spawnsRemain = wave.waveCount; sim = true; }
          
            public void stop() {
                fs = wave.spawnDelay;
                sim = false;
            }
          
            public void actionPerformed() {
                if (level == 0) return;
                if (spawnsRemain == 0) {
                    stop();
                    s.m = Spawn.Mode.wait;
                    s.resume();
                    return;
                }
                fs = wave.spawnDelay;
                spawnsRemain--;
                for (int x = 0; x < wave.spawnCount; x++) {
                    //SwingUtilities.invokeLater(() -> new Asteroid(wave.genASize()));
                    new Asteroid(wave.genASize());
                }
                for (int x = 0; x < wave.cometCount; x++) {
                    //SwingUtilities.invokeLater(() -> new Asteroid(wave.genCSize()));
                    new Comet(wave.genCSize());
                }
            }
        }
        
        public class Spawn extends Script {
          
            int wavesRemain;
            
            int initF;
            int waveF;
            boolean runIF = false;
            boolean runWF = false;
            
            public Mode m;
            
            public Wave w;
            
            public Spawn() {
                m = Mode.init;
                initF = initDelay;
                waveF = wave.waveDelay;
                runIF = true;
                w = new Wave(this);
                addToUpdate(this);
                curLevel().add(this);
            }
            
            public void destroy() {
                removeToUpdate(this);
              
            }
            
            public void update() {
                if (isPaused) return;
                if (runIF) { initF -= frameRate; if (initF <= 0) actionPerformed(); }
                if (runWF) { waveF -= frameRate; if (waveF <= 0) actionPerformed(); }
            }
            
            public void pause() { runWF = false; }
            public void resume() { runWF = true; }
            
            public void actionPerformed() {
                if (level == 0) return;
                if (m == Mode.init) {
                    m = Mode.wait;
                    runIF = false;
                    initF = initDelay;
                    //resume();
                }
                if (m == Mode.wait) {
                    pause();
                    m = Mode.wave;
                    wave = WaveType.randomType();
                    waveF = wave.waveDelay;
                    numWaves++;
                    if (numWaves >= level) {
                        subLevel++;
                        numWaves = -1;
                        System.out.println("new subLevel");
                        m = Mode.end_level;
                        addToUpdate(AsteroidSpawner.instance);
                        return;
                    }
                    w.startWave();
                }
            }
        
            public enum Mode {
            stopped(-1), init(0), wait(1), wave(2), end_level(3);
            int m;
            Mode(int mode) { m = mode; }
            }
        }
    }
    
    public class Asteroid extends Script {

        int size;
        int hp;
        Coord pos = new Coord();
        double spdX;
        double spdY;
        
        //frame-dependant
        double maxSpd = 7.5f;
        
        public CircleCollider coll;
        
        public static HashSet<Asteroid> instances = new HashSet<Asteroid>();
        
        public Asteroid() {
            instances.add(this);
            
            //frame-dependant ???????
            //size = randomIntGen.nextInt(5, 9) * 6;
            size = randomIntGen.nextInt(5, 9) * 3;
            maxSpd = (double) maxSpd * 20f / (size + 20f);
            pos.x = width + size + 2;
            pos.y = randomIntGen.nextDouble(0, height) + uiHeight;
            hp = size;
            spdX = randomIntGen.nextDouble(-30f / (double) size, 60f / (double) size);
            spdY = randomIntGen.nextDouble(-30f / (double) size, 60f / (double) size);
            CameraManager.addToDraw(this, 1);
            addToUpdate(this);
            curLevel().add(this);
            coll = new CircleCollider(pos, size);
        }

        public Asteroid(int size) {
            instances.add(this);
            this.size = size;
            maxSpd = (double) maxSpd * 20f / (size + 20f);
            pos.x = width + size + 2;
            pos.y = randomIntGen.nextDouble(0, height) + uiHeight;
            hp = size;
            //frame-dependant
            //spdX = randomIntGen.nextDouble(-30f / (double) size, 60f / (double) size);
            //spdY = randomIntGen.nextDouble(-30f / (double) size, 60f / (double) size);
            spdX = randomIntGen.nextDouble(-15f / (double) size, 30f / (double) size);
            spdY = randomIntGen.nextDouble(-15f / (double) size, 30f / (double) size);
            CameraManager.addToDraw(this, 1);
            addToUpdate(this);
            curLevel().add(this);
            coll = new CircleCollider(pos, size);
        }
        
        public Asteroid(double x, double y) {
            instances.add(this);
            pos.x = x;
            pos.y = y;
            size = randomIntGen.nextInt(1, 8) * 5;
            maxSpd = (double) maxSpd * 20f / (size + 20f);
            hp = size;
            //frame-dependant
            //spdX = randomIntGen.nextDouble(5f / size, 20f / size) * (2 * randomIntGen.nextInt(0, 2) - 1);
            //spdY = randomIntGen.nextDouble(5f / size, 20f / size) * (2 * randomIntGen.nextInt(0, 2) - 1);
            spdX = randomIntGen.nextDouble(2.5f / size, 10f / size) * (2 * randomIntGen.nextInt(0, 2) - 1);
            spdY = randomIntGen.nextDouble(2.5f / size, 10f / size) * (2 * randomIntGen.nextInt(0, 2) - 1);
            CameraManager.addToDraw(this, 1);
            addToUpdate(this);
            curLevel().add(this);
            coll = new CircleCollider(pos, size);
        }
        
        public Asteroid(int size, double x, double y) {
            instances.add(this);
            pos.x = x;
            pos.y = y;
            this.size = size;
            maxSpd = (double) maxSpd * 20f / (size + 20f);
            hp = size;
            //frame-dependant
            //spdX = randomIntGen.nextDouble(5f / size, 20f / size) * (2 * randomIntGen.nextInt(0, 2) - 1);
            //spdY = randomIntGen.nextDouble(5f / size, 20f / size) * (2 * randomIntGen.nextInt(0, 2) - 1);
            spdX = randomIntGen.nextDouble(2.5f / size, 10f / size) * (2 * randomIntGen.nextInt(0, 2) - 1);
            spdY = randomIntGen.nextDouble(2.5f / size, 10f / size) * (2 * randomIntGen.nextInt(0, 2) - 1);
            CameraManager.addToDraw(this, 1);
            addToUpdate(this);
            curLevel().add(this);
            coll = new CircleCollider(pos, size);
        }

        public void destroy() {
            instances.remove(this);
            removeToUpdate(this);
            CameraManager.removeToDraw(this, 1);
            
        }
        
        public void update() {
            if (isPaused) return;
            if (pos.y - size <= uiHeight) { spdY = Math.abs(spdY); }
            if (pos.x - size <= 0) { spdX = Math.abs(spdX); if (spdX < 0.5f) spdX = .5f; }
            if (pos.y + size >= height + uiHeight) { spdY = -Math.abs(spdY); }
            if (pos.x + size >= width) { spdX = -Math.abs(spdX); }
            if ((Math.sqrt(spdX*spdX + spdY*spdY)) > maxSpd) {
                pos.x += spdX * maxSpd / (Math.sqrt(spdX*spdX + spdY*spdY));
                pos.y += spdY * maxSpd / (Math.sqrt(spdX*spdX + spdY*spdY));
            }
            else {
                pos.x += spdX;
                pos.y += spdY;
            }
        }
        
        public void render(Graphics g) {
            g.setColor(Color.gray);
            g.fillOval((int) pos.x - size, (int) pos.y - size, size * 2, size * 2);
            g.setColor(Color.red);
            //g.drawOval((int) x - size - 1, (int) y - size - 1, size * 2 + 2, size * 2 + 2);
            g.drawOval((int) pos.x - size, (int) pos.y - size, size * 2, size * 2);
        }
        
        public void damage(int d) {
            hp -= d;
            if (hp <= 0) {
                sim = false;
                SwingUtilities.invokeLater(() -> frac(-hp));
            }
        }
        
        public void damage(int d, double sx, double sy, double push) {
            hp -= d;
            
            //frame-dependant
            spdX += Math.cos(Math.atan2(pos.y - sy, pos.x - sx)) * push / 2;
            spdY += Math.sin(Math.atan2(pos.y - sy, pos.x - sx)) * push / 2;
            /*AsteroidParticle.instance.addP(new Coord(pos.x, pos.y), (AsteroidParticle.Particle p) -> {
                if (p.framesActive >= p.lifetime) { p.stop(); return; }
                if (p.framesActive == 0) { p.dx = randomIntGen.nextDouble(-3, 3); p.dy = randomIntGen.nextDouble(-3, 3); }
                else { p.dx /= 1.01; p.dy /= 1.01; }
            }, (AsteroidParticle.Particle p, Graphics g) -> { 
                if (p.framesActive >= p.lifetime) { return; }
                g.setColor(Color.gray);
                g.fillPolygon(new int[] {(int) (p.pos.x + p.dx * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.x + p.dx * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.x + p.dx * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.x + p.dx * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2)}, new int[] {(int) (p.pos.y + p.dy * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.y + p.dy * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.y + p.dy * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.y + p.dy * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2)}, 4); }, 60 + randomIntGen.nextInt(-10, 10));*/
            AsteroidParticle.instance.addP(new Coord(pos.x, pos.y), (AsteroidParticle.Particle p) -> {
                if (p.framesActive >= p.lifetime) { p.stop(); return; }
                if (p.framesActive == 0) { p.dx = randomIntGen.nextDouble(-1.5f, 1.5f); p.dy = randomIntGen.nextDouble(-1.5f, 1.5f); }
                else { p.dx /= 1.005; p.dy /= 1.005; }
            }, (AsteroidParticle.Particle p, Graphics g) -> { 
                if (p.framesActive >= p.lifetime) { return; }
                g.setColor(Color.gray);
                g.fillPolygon(new int[] {(int) (p.pos.x + p.dx * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.x + p.dx * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.x + p.dx * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.x + p.dx * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2)}, new int[] {(int) (p.pos.y + p.dy * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.y + p.dy * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.y + p.dy * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.y + p.dy * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2)}, 4); }, 60 + randomIntGen.nextInt(-10, 10));
            if (hp <= 0) {
                sim = false;
                SwingUtilities.invokeLater(() -> frac(-hp));
            }
        }
        
        public void frac() {
            size = (int) (size / Math.sqrt(3));
            coll.rad /= 2;
            hp = size;
            if (size <= 4) {
                destroy();
                curLevel().remove(this);
            }
            else {
                sim = true;
                new Asteroid(size, pos.x, pos.y);
            }
        }
        
        public void frac(int overflow) {
            size = (int) (size / Math.sqrt(3));
            coll.rad /= 2;
            hp = size;
            if (size <= 4) {
                destroy();
                curLevel().remove(this);
            }
            else {
                sim = true;
                Asteroid newA = new Asteroid(size, pos.x, pos.y);
                if (overflow > 0) {
                    newA.damage(overflow);
                    damage(overflow);
                }
            }
        }
    }

    public class Comet extends Asteroid {

        //double maxSpd = 22.5f;

        int respawnTimerMax = 70;

        int respawnTimer = 0;

        boolean needsRespawn = false;

        public Comet() {
            super();
            randomizeSpd();
        }

        public Comet(int size) {
            super(size);
            randomizeSpd();
        }

        public Comet(double x, double y) {
            super(x, y);
            randomizeSpd();
        }

        public Comet(int size, double x, double y) {
            super(size, x, y);
            randomizeSpd();
        }

        public void randomizeSpd() {
            spdX = -randomIntGen.nextDouble(5f / size, 20f / size) - 15;
            spdY = randomIntGen.nextDouble(2.5f / size, 10f / size) * (2 * randomIntGen.nextInt(0, 2) - 1);
        }

        public void respawn() {
            needsRespawn = false;
            pos.x = width + size + 2;
            pos.y = randomIntGen.nextDouble(0, height) + uiHeight;
            randomizeSpd();
        }

        public void update() {
            if (isPaused) return;
            if (!needsRespawn) { if (pos.x + size <= 0) { respawnTimer = respawnTimerMax; needsRespawn = true; }}
            else if (respawnTimer <= 0) { respawn(); }
            else respawnTimer--;
            /*if ((Math.sqrt(spdX*spdX + spdY*spdY)) > maxSpd) {
                pos.x += spdX * maxSpd / (Math.sqrt(spdX*spdX + spdY*spdY));
                pos.y += spdY * maxSpd / (Math.sqrt(spdX*spdX + spdY*spdY));
            }*/
            pos.x += spdX;
            pos.y += spdY;
        }

        public void render(Graphics g) {
            g.setColor(Color.gray);
            g.fillOval((int) pos.x - size, (int) pos.y - size, size * 2, size * 2);
            g.setColor(Color.red);
            //g.drawOval((int) x - size - 1, (int) y - size - 1, size * 2 + 2, size * 2 + 2);
            g.drawOval((int) pos.x - size, (int) pos.y - size, size * 2, size * 2);
        }

        public void damage(int d, double sx, double sy, double push) {
            hp -= d;
            spdY += Math.sin(Math.atan2(pos.y - sy, pos.x - sx)) * push / 2;
            AsteroidParticle.instance.addP(new Coord(pos.x, pos.y), (AsteroidParticle.Particle p) -> {
                if (p.framesActive >= p.lifetime) { p.stop(); return; }
                if (p.framesActive == 0) { p.dx = randomIntGen.nextDouble(-1.5f, 1.5f); p.dy = randomIntGen.nextDouble(-1.5f, 1.5f); }
                else { p.dx /= 1.005; p.dy /= 1.005; }
            }, (AsteroidParticle.Particle p, Graphics g) -> {
                if (p.framesActive >= p.lifetime) { return; }
                g.setColor(Color.gray);
                g.fillPolygon(new int[] {(int) (p.pos.x + p.dx * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.x + p.dx * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.x + p.dx * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.x + p.dx * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2)}, new int[] {(int) (p.pos.y + p.dy * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.y + p.dy * p.framesActive - Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.y + p.dy * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2), (int) (p.pos.y + p.dy * p.framesActive + Math.log(p.lifetime - p.framesActive) / 2)}, 4); }, 60 + randomIntGen.nextInt(-10, 10));
            if (hp <= 0) {
                sim = false;
                SwingUtilities.invokeLater(() -> frac(-hp));
            }
        }

        public void frac() {
            size = (int) (size / Math.sqrt(3));
            coll.rad /= 2;
            hp = size;
            if (size <= 4) {
                destroy();
                curLevel().remove(this);
            }
            else {
                sim = true;
                new Comet(size, pos.x, pos.y);
            }
        }

        public void frac(int overflow) {
            size = (int) (size / Math.sqrt(3));
            coll.rad /= 2;
            hp = size;
            if (size <= 4) {
                destroy();
                curLevel().remove(this);
            }
            else {
                sim = true;
                Comet newA = new Comet(size, pos.x, pos.y);
                if (overflow > 0) {
                    newA.damage(overflow);
                    damage(overflow);
                }
            }
        }
    }
    
    public class AsteroidParticle extends Script {
    
        public static AsteroidParticle instance;
        
        public HashSet<Particle> ps = new HashSet<Particle>();
        
        public AsteroidParticle() {
            instance = this;
            CameraManager.addToDraw(this, 1);
            addToUpdate(this);
            curLevel().add(this);
        }

        public void destroy() {
            CameraManager.removeToDraw(this, 1);
            removeToUpdate(this);
            
        }
        
        public void addP(Coord p, Consumer<Particle> u, BiConsumer<Particle, Graphics> r, int l) {
            ps.add(new Particle(p, u, r, l));
        }
        
        public void update() {
            if (isPaused) return;
            for (Particle p : ps) {
                SwingUtilities.invokeLater(() -> p.tick());
            }
        }
        
        public void render(Graphics g) {
            for (Particle p : ps) {
                p.render(g);
            }
        }
        
        public class Particle {
          
            public int framesActive = -1;
            public int lifetime;
            public Coord pos;
            public double dx;
            public double dy;
            public double rot;
            public Consumer<Particle> updateFunc;
            public BiConsumer<Particle, Graphics> renderFunc;
            
            public Particle(Coord p, Consumer<Particle> u, BiConsumer<Particle, Graphics> r, int life) {
                pos = p; updateFunc = u; renderFunc = r; lifetime = life;
            }
            
            public void tick() {
                framesActive++;
                updateFunc.accept(this);
            }
            
            public void render(Graphics g) { renderFunc.accept(this, g); }
            
            public void stop() {
                SwingUtilities.invokeLater(() -> ps.remove(this));
            }
        }
    }
    
    public class Background extends Script {
    
        public Background() {
            CameraManager.addToDraw(this, 1);
            curLevel().add(this);
        }

        public void destroy() {
            CameraManager.removeToDraw(this, 1);
            
        }
        
        public void render(Graphics g) {
            g.setColor(Color.black);
            g.fillRect(0, uiHeight, width, height);
        }
    }

    public class Menu extends Script {

        public Menu() {
            new MenuBackground();
            new TestButton();
            new HPBar();
            curLevel().add(this);
        }
    
        public class MenuBackground extends Script {
        
            public MenuBackground() {
                CameraManager.addToDraw(this, 2);
                curLevel().add(this);
            }

            public void destroy() {
                CameraManager.removeToDraw(this, 2);
                
            }
            
            public void render(Graphics g) {
              g.setColor(Color.gray);
              g.fillRect(0, 0, width, uiHeight);
              g.setColor(Color.blue);
              g.drawRect(0, 0, width - 1, uiHeight - 1);
            }
        }

        public class TestButton extends Script {

            RectCollider coll = new RectCollider(new Coord(200, 25), new PCoord(0, 60), 15);

            public enum ColorState {
                rest(new Color(0, 255, 0)),
                hover(new Color(0, 225, 0)),
                press(new Color(60, 210, 0)),
                tRest(new Color(255, 0, 0)),
                tHover(new Color(225, 0, 0)),
                tPress(new Color(210, 60, 0));
                Color c;
                ColorState(Color c) { this.c = c; }
            }

            boolean isSelected = false;
            boolean isPressed = false;

            public TestButton() {
                CameraManager.addToDraw(this, 2);
                addToUpdate(this);
                curLevel().add(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                CameraManager.removeToDraw(this, 2);
                
            }

            public void update() {
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) { /*System.out.println("mouse click");*/ isPaused = !isPaused; } isPressed = false; }

            public void render(Graphics g) {
                if (!isPaused) {
                    if (isSelected && isPressed)
                        g.setColor(ColorState.press.c);
                    else if (isSelected)
                        g.setColor(ColorState.hover.c);
                    else
                        g.setColor(ColorState.rest.c);
                }
                else {
                    if (isSelected && isPressed)
                        g.setColor(ColorState.tPress.c);
                    else if (isSelected)
                        g.setColor(ColorState.tHover.c);
                    else
                        g.setColor(ColorState.tRest.c);
                }
                g.fillRect((int) coll.og.x, (int) (coll.og.y - coll.width), (int) coll.length.d, (int) coll.width * 2);
            }
        }

        public class HPBar extends Script {

            Coord topLeft = new Coord(15, 15);
            int width = 170;
            int length = 20;

            public HPBar() {
                CameraManager.addToDraw(this, 2);
                curLevel().add(this);
            }

            public void render(Graphics g) {
                if (Ship.instance.hp < Ship.instance.maxHp) {
                    g.setColor(Color.green);
                    g.fillRect(topLeft.x(), topLeft.y(), (int) ((double) Ship.instance.hp / Ship.instance.maxHp * width), length);
                    g.setColor(Color.red);
                    g.fillRect(topLeft.x() + (int) ((double) Ship.instance.hp / Ship.instance.maxHp * width), topLeft.y(), (int) ((1 - (double) Ship.instance.hp / Ship.instance.maxHp) * width), length);
                }
                else {
                    g.setColor(Color.green);
                    g.fillRect(topLeft.x(), topLeft.y(), width, length);
                }
            }
        }
    }

    public class MainMap extends Group {

        static MainMap instance;
        public Coord offset = new Coord();

        public MainMap() { instance = this; }

        public void start() {
            CameraManager.addToDraw(this, 4);
            addToUpdate(this);
            MouseDetect.instance.addP(this);
            MouseDetect.instance.addR(this);
            for (Script s : items) { s.start(); } 
        }

        public void destroy() {
            CameraManager.removeToDraw(this, 4);
            removeToUpdate(this);
            MouseDetect.instance.removeP(this);
            MouseDetect.instance.removeR(this);
            for (Script s : items) { s.destroy(); }
        }

        public void update() {
            if (MouseDetect.instance.isMouseDown) {
                offset.x += MouseDetect.instance.deltaMousePos().x;
                offset.y += MouseDetect.instance.deltaMousePos().y;
                if (offset.x > width) offset.x = -width;
                if (offset.x < -width) offset.x = width;
                if (offset.y > height + uiHeight) offset.y = -height - uiHeight;
                if (offset.y < -height - uiHeight) offset.y = height + uiHeight;
            }
        }

        public void render(Graphics g) {
            g.setColor(Color.black);
            g.fillRect(0, 0, width, height + uiHeight);
            g.setColor(Color.red);
            g.drawLine(0, (int) offset.y, width, (int) offset.y);
            g.drawLine((int) offset.x, 0, (int) offset.x, height + uiHeight);
        }

        public void addNode(Coord c, Level l) {
            items.add(new LevelNode(c, l));
        }

        public class LevelNode extends Script {
            Coord pos;
            Level l;

            boolean isSelected = false;
            boolean isPressed = false;
            
            RectCollider coll;

            public LevelNode(Coord p, Level l) {
                pos = p;
                this.l = l;
                coll = new RectCollider(new Coord(pos), new PCoord(0, 30), 15);
            }

            public void start() {
                addToUpdate(this);
                MouseDetect.instance.addP(this);
                MouseDetect.instance.addR(this);
                CameraManager.addToDraw(this, 4);
            }

            public void destroy() {
                removeToUpdate(this);
                MouseDetect.instance.removeP(this);
                MouseDetect.instance.removeR(this);
                CameraManager.removeToDraw(this, 4);
            }

            public void update() {
                coll.og.x = pos.x + offset.x;
                coll.og.y = pos.y + offset.y;
                isSelected = coll.isColliding(new PointCollider(MouseDetect.instance.mousePos()));
                if (!isSelected) isPressed = false;
            }

            public void render(Graphics g) {
                if (l.isComplete)
                    g.setColor(Color.green);
                else
                    g.setColor(Color.red);
                g.fillRect((int) (pos.x + offset.x), (int) (pos.y - coll.width + offset.y), (int) coll.length.d, (int) coll.width * 2);
            }

            public void mousePressed(MouseEvent e) { if (isSelected) isPressed = true; }
            public void mouseReleased(MouseEvent e) { if (isPressed) { /*System.out.println("mouse click");*/ level = l.i; MainMap.instance.destroy(); levels.get(l.i - 1).start(); } isPressed = false; }
            
        }
    }
}