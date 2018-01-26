/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

/**
 *
 * @author chasehanson
 */
public class Ship implements Comparable<Ship> {

    double mass, score;
    int n_boosters, size, radius, punishment;
    Booster[] boosters;
    boolean was_scored, alive;
    double x, y, vx, vy, ax, ay;

    public Ship(double mass, Booster[] boosters, int size, int radius) {
        //Constructor used for creating a pre-defined ship
        this.mass = mass;
        this.boosters = boosters;
        n_boosters = boosters.length;
        was_scored = false;
        score = 0;
        alive = true;

        reset_pva(size, radius);
    }

    public Ship(int size, int radius) {
        //Constructor used for creating a random ship
        n_boosters = (int) (6 * Math.random());
        this.mass = 5 * Math.random();
        this.boosters = new Booster[n_boosters];
        score = 0;
        alive = true;

        populate_boosters(); //Get random boosters
        reset_pva(size, radius);
    }

    public void reset_pva(int size, int radius) {
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
        Double s = new Double(score);
        return s.compareTo(other.score);
    }

    public Ship copy() {
        Booster[] new_boosters = new Booster[this.boosters.length];
        for (int i = 0; i < new_boosters.length; i++) {
            new_boosters[i] = this.boosters[i].copy();
        }
        return new Ship(this.mass, this.boosters, size, radius);
    }

    public void populate_boosters() {
        for (int i = 0; i < n_boosters; i++) {
            boosters[i] = new Booster();
        }
    }

    public void sort_boosters() {
        return;
        //Arrays.sort(boosters);
    }

    public Ship mate(Ship other, double variance) {
        sort_boosters();
        other.sort_boosters();

        double new_mass = Math.random() < .5 ? mass : other.mass;
        Booster[] new_boosters = new Booster[Math.random() < .5 ? boosters.length : other.boosters.length];
        int l1 = new_boosters.length;
        int length = Math.min(boosters.length, other.boosters.length);

        for (int i = 0; i < length; i++) {
            new_boosters[i] = boosters[i].mate(other.boosters[i]);
        }

        for (int i = 0; i < l1 - length; i++) {
            if (i + length < boosters.length && i + length < other.boosters.length) {
                new_boosters[i + length] = Math.random() < .5 ? boosters[i + length] : other.boosters[i + length];
            } else if (i + length < other.boosters.length) {
                new_boosters[i + length] = other.boosters[i + length];
            } else if (i + length < boosters.length) {
                new_boosters[i + length] = boosters[i + length];
            }
        }

        Ship s = new Ship(new_mass, new_boosters, size, radius);
        s.mutate(variance); //As if by random mutation

        return s;

    }

    public void mutate(double variance) {
        for (Booster b : boosters) {
            b.mutate(variance);
        }

        if (variance < 1000) {
            mass += (Math.random() - .5) * .20; //MAKE SURE IS POSITVE
        } else {
            mass += (Math.random() - .5) * .05; //MAKE SURE IS POSITVE
        }

        if (mass <= 0) {
            mass = .25 * Math.random();
        }

        int multiplier = 1;
        if (variance < 5000) {
            multiplier = 20;
        }
        //Chance of adding new booster
        if (Math.random() < .01 * multiplier && boosters.length < 10) {
            add_booster();
        }

        //Chance of losing a booster
        if (Math.random() < .01 * multiplier && boosters.length > 1) {
            lose_booster();
        }

    }

    public void add_booster() {
        Booster[] new_boosters = new Booster[boosters.length + 1];
        for (int i = 0; i < boosters.length; i++) {
            new_boosters[i] = boosters[i];
        }
        new_boosters[boosters.length] = new Booster();
        boosters = new_boosters;
    }

    public void lose_booster() {
        Booster[] new_boosters = new Booster[boosters.length - 1];
        int remove = (int) (Math.random() * boosters.length);
        int offset = 0;
        for (int i = 0; i < boosters.length - 1; i++) {
            if (i == remove) {
                offset = 1;
            }
            new_boosters[i] = boosters[i + offset];
        }

        boosters = new_boosters;
    }

    public boolean still_alive(int[] target, Obstacle[] obstacles) {
        punishment = 0;
        if (x - radius < 0) {
            //punishment = 50000;
            return false;
        }

        if (x + radius > size) {
            //punishment = 50000;
            return false;
        }

        if (y - radius < 0) {
            //punishment = 50000;
            return false;
        }

        if (y + radius > size) {
            //punishment = 50000;
            return false;
        }

        if (Math.pow(Math.pow((target[0] - x), 2) + Math.pow((target[1] - y), 2), .5) <= radius) {
            return false;
        }

        for (Obstacle o : obstacles) {
            if (o.intersects(this)) {
                punishment = 50000;
                return false;
            }
        }

        return true;
    }

    public double[] get_forces(double time, double dt) {
        double[] force = new double[]{0, 0};

        for (Booster b : boosters) {
            if (time > b.delay && time < b.end) { //Is activated
                force[0] += Math.cos(b.angle) * b.force;
                force[1] += Math.sin(b.angle) * b.force;
            }
        }

        return force;
    }

    public void update(double time, double dt) {
        double[] force = get_forces(time, dt);

        ax = force[0] / mass;
        ay = force[1] / mass;
        vx += ax * dt;
        vy += ay * dt;

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

    public double calculate_score(int[] target, Obstacle[] obstacles) {
        score = Math.pow((target[0] - x), 2) + Math.pow((target[1] - y), 2) + punishment;
        for (Obstacle o : obstacles) {
            if (!o.cleared(this)) {
                score += 10000;
            }
        }
        was_scored = true;
        return score;
    }

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
