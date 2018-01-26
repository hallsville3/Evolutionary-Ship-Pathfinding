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

    double angle, delay, end, force;

    public Booster(double angle, double delay, double end, double force) {
        this.angle = angle;
        this.delay = delay;
        this.end = end;
        this.force = force;
    }

    public int compareTo(Booster other) {
        Double d = new Double(angle);
        return d.compareTo(other.angle);
    }

    public Booster() {
        this.angle = 2 * Math.PI * Math.random();
        this.delay = 100 * Math.random();
        this.end = 200 * Math.random();
        this.force = 10 * Math.random();
    }

    public Booster copy() {
        return new Booster(this.angle, this.delay, this.force, this.end);
    }

    public Booster mate(Booster other) {
        double new_angle = Math.random() < .5 ? angle : other.angle;
        double new_delay = Math.random() < .5 ? delay : other.delay;
        double new_end = Math.random() < .5 ? end : other.end;
        double new_force = Math.random() < .5 ? force : other.force;

        return new Booster(new_angle, new_delay, new_end, new_force);
    }

    public void mutate(double variance) {
        int multiplier = 1;
        
        if (variance < 5000) {
            multiplier = 4;
        }
        angle += multiplier*.05 * (Math.random() - .5);
        delay += multiplier*(Math.random() - .5) * .05;
        end += multiplier*(Math.random() - .5) * .05;
        force += multiplier*(Math.random() - .5) * .05;
        /*if (force > 10) {
            force = 10;
        }*/
    }

    public Polygon get_polygon(double x, double y, double length) {
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
