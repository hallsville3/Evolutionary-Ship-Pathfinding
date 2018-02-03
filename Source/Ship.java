/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author chasehanson
 */
public class Ship implements Comparable<Ship> {
    //Each Ship object holds many Boosters that direct it in various directions

    double mass, score, rate;
    int n_boosters, size, radius, punishment;
    Booster[] boosters;
    boolean alive;
    double x, y, vx, vy, ax, ay;

    /**
     *
     * @param mass
     * @param boosters
     * @param size
     * @param radius
     */
    public Ship(double mass, Booster[] boosters, int size, int radius, double rate) {
        //Constructor used for creating a pre-defined ship
        
        this.mass = mass;
        this.boosters = boosters;
        n_boosters = boosters.length;
        score = 0;
        alive = true;
        this.rate = rate;

        reset_pva(size, radius);
    }

    /**
     *
     * @param size
     * @param radius
     */
    public Ship(int size, int radius, double rate) {
        //Constructor used for creating a random ship
        
        n_boosters = (int) (6 * Math.random());
        this.mass = 5 * Math.random();
        this.boosters = new Booster[n_boosters];
        score = 0;
        alive = true;

        populate_boosters(); //Get random boosters
        reset_pva(size, radius);
        this.rate = rate;
    }

    /**
     *
     * @param size
     * @param radius
     */
    public void reset_pva(int size, int radius) {
        //Sets position to bottom center, and velocity/acceleration to 0
        //Also notes screen size and radius of ship
        
        this.size = size;
        this.radius = radius;
        x = size / 2;
        y = size - 20;
        vx = 0;
        vy = 0;
        ax = 0;
        ay = 0;
    }

    public int compareTo(Ship other) {
        //Returns comparison of ship scores for purpose of sorting ships
        
        Double s = new Double(score);
        return s.compareTo(other.score);
    }

    /**
     *
     * @return
     */
    public Ship copy() {
        //Creates an exact copy of a ship with no shared pointers
        
        Booster[] new_boosters = new Booster[this.boosters.length];
        for (int i = 0; i < new_boosters.length; i++) {
            new_boosters[i] = this.boosters[i].copy();
        }
        return new Ship(this.mass, this.boosters, size, radius, rate);
    }

    /**
     *
     */
    public void populate_boosters() {
        //Creates n_boosters random boosters in the ship
        
        for (int i = 0; i < n_boosters; i++) {
            boosters[i] = new Booster();
        }
    }

    /**
     *
     */
    public void sort_boosters() {
        //Sorts boosters by angle for better breeding. Currently disabled
        
        return;
        //Arrays.sort(boosters);
    }

    /**
     *
     * @param other the other ship to be mated with
     * @param variance a measure of how much the population improved during the last simulation 
     * @return a new ship from the two parent ships
     */
    public Ship mate(Ship other, double variance) {
        /*
        Creates a new "offspring" ship from two parent ships.
        
        If variance > a certain value, new ships will be more varied 
            for increased mutation potential
         */

        //sort_boosters();
        //other.sort_boosters();
        double new_mass = Math.random() < .5 ? mass : other.mass;

        Booster[] new_boosters = new Booster[Math.random() < .5 ? boosters.length : other.boosters.length];

        int l1 = new_boosters.length;
        int length = Math.min(boosters.length, other.boosters.length);

        //Get new boosters from offspring of parents' boosters
        for (int i = 0; i < length; i++) {
            new_boosters[i] = boosters[i].mate(other.boosters[i]);
        }

        //Fill in the booster "gaps" with parent boosters
        for (int i = 0; i < new_boosters.length-length; i++) {
            if (i + length < other.boosters.length) {
                new_boosters[i + length] = other.boosters[i + length];
            } else if (i + length < boosters.length) {
                new_boosters[i + length] = boosters[i + length];
            }
        }

        //Create a new ship with the new mass and boosters
        Ship s = new Ship(new_mass, new_boosters, size, radius, rate);

        //Mutate the ship to ensure genetic variation, accounting for variance
        s.mutate(variance);

        return s;

    }

    /**
     *
     * @param variance a measure of how much the population improved during the last simulation
     */
    public void mutate(double variance) {
        //Mutate a ship by changing its boosters and mass
        
        int multiplier = 1;
        if (variance < 50) { //If variance is too low, crank up the variation
            multiplier = 50;
        }
        
        
        for (Booster b : boosters) {
            b.mutate(multiplier, rate);
        }

        mass += (Math.random() - .5) * rate * multiplier;

        if (mass <= 0) { //No negative masses allowed, messes up F = ma
            mass = .25 * Math.random();
        }

        

        //Chance of adding new booster
        if (Math.random() < rate/5.0 * multiplier) {
            add_booster();
        }

        //Chance of losing a booster
        if (Math.random() < rate/5.0 * multiplier && boosters.length > 1) {
            lose_booster();
        }

    }

    /**
     * Adds a new random booster to the ship
     */
    public void add_booster() {
        //Add a new random booster to the ship

        Booster[] new_boosters = new Booster[boosters.length + 1];

        for (int i = 0; i < boosters.length; i++) {
            new_boosters[i] = boosters[i];
        }

        new_boosters[boosters.length] = new Booster();
        boosters = new_boosters;
    }

    /**
     * Removes a random booster from the ship
     */
    public void lose_booster() {
        //Remove a random booster from the ship

        Booster[] new_boosters = new Booster[boosters.length - 1];
        int remove = (int) (Math.random() * boosters.length);
        int offset = 0;
        for (int i = 0; i < boosters.length - 1; i++) {
            if (i == remove) {
                offset = 1;
            }
            new_boosters[i] = boosters[i + offset];
        }

        boosters = new_boosters; //Overwrite old boosters
    }

    /**
     *
     * @param target an array storing the x and y coordinates of a target
     * @param obstacles an array of obstacles which need to be avoided
     * @return true if the ship hasn't struck anything, otherwise false
     */
    public boolean still_alive(int[] target, Obstacle[] obstacles) {
        //Returns true if ship is alive, otherwise false

        //Keep track of a "punishment" to aid in accurate scoring of ships
        punishment = 0;

        if (x - radius < 0) {
            punishment = 0;
            return false;
        }

        if (x + radius > size) {
            punishment = 0;
            return false;
        }

        if (y - radius < 0) {
            punishment = 0;
            return false;
        }

        if (y + radius > size) {
            punishment = 0;
            return false;
        }

        //If the ship nears the target
        if (Math.pow(Math.pow((target[0] - x), 2) + Math.pow((target[1] - y), 2), .5) <= radius) {
            return false;
        }

        //Punish the ship for hitting an obstacle
        for (Obstacle o : obstacles) {
            if (o.intersects(this)) {
                punishment = 0;
                return false;
            }
        }

        //Ship is still in free space
        return true;
    }

    /**
     *
     * @param time the amount of time that passed since the simulation started
     * @param dt the time that will pass during this frame
     * @return the force vector of the boosters acting on the ship during this frame
     */
    public double[] get_forces(double time, double dt) {
        //Sum the forces of the active boosters and return a force vector

        double[] force = new double[]{0, 0};

        for (Booster b : boosters) {
            if (b.is_activated(time)) {
                force[0] += Math.cos(b.angle) * b.force;
                force[1] += Math.sin(b.angle) * b.force;
            }
        }

        return force;
    }

    /**
     *
     * @param time the amount of time that passed since the simulation started
     * @param dt the time that will pass during this frame
     */
    public void update(double time, double dt) {
        //Update a ship's PVA, as well as restricting its velocity

        double[] force = get_forces(time, dt);

        ax = force[0] / mass;
        ay = force[1] / mass;
        vx += ax * dt;
        vy += ay * dt;

        //Check velocity BEFORE adjusting position
        double[] vel = {vx, vy};
        double mag = Math.sqrt(vel[0] * vel[0] + vel[1] * vel[1]);

        double max = 50;
        if (mag > max) {
            double m = max / mag;
            vx *= m;
            vy *= m;
        }

        x += vx * dt;
        y += vy * dt;

    }

    /**
     *
     * @param G a reference to a Graphics object to draw into
     */
    public void draw(Graphics G) {
        //Every ship is a circle of radius 4, with boosters sticking out of length 6 at various angles
        //x and y are assumed to be the center of the ship

        G.setColor(Color.black);
        for (Booster b : boosters) {
            G.fillPolygon(b.get_polygon(x, y, 16));
        }
        G.setColor(alive ? Color.green : Color.red);
        G.fillOval((int) x - radius, (int) y - radius, 2 * radius, 2 * radius);

    }
}
