/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.awt.Polygon;

/**
 *
 * @author chasehanson
 */
class Booster implements Comparable<Booster> {
    //Each booster has variables that determine its force, angle, and firing window

    double angle, delay, end, force;

    public Booster(double angle, double delay, double end, double force) {
        //Constructor used for creating a pre-defined booster

        this.angle = angle;
        this.delay = delay;
        this.end = end;
        this.force = force;
    }

    public Booster() {
        //Constructor used for creating a random booster;

        this.angle = 2 * Math.PI * Math.random();
        this.delay = 200 * Math.random();
        this.end = 200 * Math.random();
        this.force = 10 * Math.random();
    }

    public int compareTo(Booster other) {
        //Returns comparison of booster angles for purpose of sorting boosters

        Double d = new Double(angle);
        return d.compareTo(other.angle);
    }

    public Booster copy() {
        //Returns a copy of a booster with no shared pointers

        return new Booster(angle, delay, force, end);
    }

    public Booster mate(Booster other) {
        //Returns a new "offspring" booster created from two booster parents

        double new_angle = Math.random() < .5 ? angle : other.angle;
        double new_delay = Math.random() < .5 ? delay : other.delay;
        double new_end = Math.random() < .5 ? end : other.end;
        double new_force = Math.random() < .5 ? force : other.force;

        return new Booster(new_angle, new_delay, new_end, new_force);
    }

    public void mutate(int multiplier, double rate) {
        //Randomly mutates a booster's angle, delay, end, and force

        angle += multiplier * (Math.random() - .5) * rate;
        delay += multiplier * (Math.random() - .5) * rate;
        end += multiplier * (Math.random() - .5) * rate;
        force += multiplier * (Math.random() - .5) * rate;

    }

    public boolean is_activated(double time) {
        //True if the booster is within the critical range, false otherwise

        return time > delay && time < end;
    }

    public Polygon get_polygon(double x, double y, double length) {
        /*
        Returns a bounding polygon, 
        angle shifted to appear pointing away from where its force is applied, 
        like real life.
         */

        Polygon p = new Polygon();
        int l = 2;
        angle += Math.PI;
        p.addPoint((int) (x + l * Math.cos(angle + Math.PI / 2)), (int) (y + l * Math.sin(angle + Math.PI / 2)));
        p.addPoint((int) (x - l * Math.cos(angle + Math.PI / 2)), (int) (y - l * Math.sin(angle + Math.PI / 2)));
        p.addPoint((int) (x - l * Math.cos(angle + Math.PI / 2) + length * Math.cos(angle)), (int) (y - l * Math.sin(angle + Math.PI / 2) + length * Math.sin(angle)));
        p.addPoint((int) (x + l * Math.cos(angle + Math.PI / 2) + length * Math.cos(angle)), (int) (y + l * Math.sin(angle + Math.PI / 2) + length * Math.sin(angle)));
        angle -= Math.PI;

        return p;
    }

}
