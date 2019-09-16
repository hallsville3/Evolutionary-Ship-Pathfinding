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

    double angle, duration, delay, force, remainingDelay;

    public Booster(double angle, double duration, double delay, double force) {
        //Constructor used for creating a pre-defined booster

        this.angle = angle;
        this.duration = duration;
        this.delay = delay;
        this.force = force;
    }

    public Booster() {
        //Constructor used for creating a random booster;

        this.angle = 2 * Math.PI * Math.random();
        this.duration = 5 * Math.random();
        this.delay = Math.random()*50;
        this.force = 2 * Math.random();
        resetDelay();
    }

    public void resetDelay() {
        remainingDelay = delay;
    }

    public int compareTo(Booster other) {
        //Returns comparison of booster angles for purpose of sorting boosters

        Double d = angle;
        return d.compareTo(other.angle);
    }

    public Booster copy() {
        //Returns a copy of a booster with no shared pointers

        return new Booster(angle, duration, delay, force);
    }

    public Booster mate(Booster other) {
        //Returns a new "offspring" booster created from two booster parents

        double new_angle = (angle + other.angle)/2;
        double new_duration = (duration + other.duration)/2;
        double new_delay = (delay + other.delay)/2;
        double new_force = (force + other.force)/2;

        return new Booster(new_angle, new_duration, new_delay, new_force);
    }

    public void mutate(int multiplier, double rate) {
        //Randomly mutates a booster's angle, delay, end, and force

        angle += multiplier * (Math.random() - .5) * rate;
        duration += multiplier * (Math.random() - .5) * rate;
        delay += multiplier * (Math.random() -.5) * rate;
        force += multiplier * (Math.random() - .5) * rate;
        resetDelay();

    }

    public boolean is_activated(double relative_time) {
        //True if the booster is within its duration
        return (duration - relative_time > 0);
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
