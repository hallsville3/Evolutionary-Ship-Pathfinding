/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author chasehanson
 */
public class ShipPool {
    //Data structure for managing ships and their scoring/ simulation

    int n;
    double variance, last_score;
    int size, radius;
    boolean working;
    ArrayList<Ship> ships;
    int[] target;
    double rate;

    public ShipPool(int n, int size, int ship_radius, double rate, int[] target) {
        /*Constructor for creating a ship pool of size n, 
        with a screen size of "size" and a ship radius of ship_radius
         */

        this.n = n;
        this.size = size;
        this.radius = ship_radius;
        generate_ships();
        working = false;
        this.target = target;
        variance = 0;
        this.rate = rate;
    }

    public void generate_ships() {
        //Populages the ship[] with n random ships

        ships = new ArrayList<Ship>();
        for (int i = 0; i < n; i++) {
            ships.add(new Ship(size, radius, rate));
        }
    }

    public ArrayList<Ship> get_n_best(int count) {
        //Returns a Ship[] of the 'count' best ships

        Collections.sort(ships);
        ArrayList<Ship> best = new ArrayList<Ship>();
        for (int i = 0; i < count; i++) {
            best.add(ships.get(i));
        }
        return best;
    }

    public ArrayList<Ship> remove_random_ship(ArrayList<Ship> s) {
        //Removes a random Ship from the given ArrayList<Ship> s

        int remove = (int) (Math.random() * s.size());
        s.remove(remove);

        return s;
    }

    public ArrayList<Ship> kill_n_randomly(ArrayList<Ship> s, int count) {
        //Remove count ships from s
        for (int i = 0; i < count; i++) {
            s = remove_random_ship(s);
        }

        return s;
    }

    public ArrayList<Ship> get_offspring(ArrayList<Ship> parents, int survived) {
        //Get a new ArrayList<Ship> of length n by mating parents

        ArrayList<Ship> new_generation = new ArrayList<Ship>();

        final int[] ints = new Random().ints(0, parents.size()).distinct().limit(survived).toArray();

        for (int i = 0; i < survived; i++) {
            new_generation.add(parents.get(ints[i]).copy());
        }

        int required_children = n - survived;
        final int[] parents1 = new Random().ints(0, parents.size()).limit(required_children).toArray();
        final int[] parents2 = new Random().ints(0, parents.size()).limit(required_children).toArray();

        //Fill the rest of the arraylist with children of the selected parents
        for (int i = survived; i < n; i++) {
            new_generation.add(parents.get(parents1[i - survived]).mate(parents.get(parents2[i - survived]), variance));
        }

        return new_generation;

    }

    public double score_ships(Frame f, ArrayList<Obstacle> obstacles, double end_time, double dt, double speed, boolean display) throws InterruptedException {
        //Scores all the ships in a simulation, optionally displaying it

        double time = 0;

        if (display) {
            f.set(this, obstacles);
            f.repaint();
            Thread.sleep((long) (speed * dt));
        }

        //Run the generation, updating the ships and checking if they die
        int alive_count = ships.size();
        while (time < end_time && alive_count > 0) {

            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).alive) {
                    ships.get(i).update(time, dt);
                    ships.get(i).alive = ships.get(i).still_alive(target, obstacles);
                    if (!ships.get(i).alive) {
                        alive_count--;
                    }
                }
            }

            if (display) {
                f.set(this, obstacles);
                f.repaint();
                Thread.sleep((long) (speed * dt));
            }

            time += dt;
        }

        double best = 1000000;
        double score = 0;

        //Determine the best score of the ships
        Scorer scorer = new Scorer(obstacles, ships, target, size, size);
        scorer.run();
        f.set_line(scorer.finished_line, scorer.SCALE);
        f.repaint();
        Thread.sleep(1000);
        f.set_line(null, scorer.SCALE);
        
        for (int i = 0; i<ships.size(); i++) {
            score = scorer.scores[i];
            ships.get(i).score = score;
            if (score < best) {
                best = score;
            }
        }

        //Calculate the variation for the next frame
        variance = Math.abs(last_score - best);
        if (best < 30) {
            variance = 1000000;
        }

        last_score = best;

        //Do the next generation, accounting for the scores of this generation
        do_generation();

        return best;
    }

    public void draw(Graphics G) {
        //Draw the target, and each ship and its boosters
        G.setColor(Color.RED);
        G.fillOval(target[0] - 3, target[1] - 3, 6, 6);

        for (Ship s : ships) {
            s.draw(G);
        }
    }

    public void do_generation() {
        //Do one generation of mating, accounting for the scores received

        int best_count = 10;
        int kill_count = 0;
        int leave_count = 10;

        //Get top best_count ships from the array of n
        ArrayList<Ship> best = get_n_best(best_count);

        //Kill "kill_count" of these parents
        ArrayList<Ship> parents = kill_n_randomly(best, kill_count);

        //Generate the offspring, leaving leave_count parents.
        ArrayList<Ship> offspring = get_offspring(parents, leave_count);

        for (Ship s: offspring) {
            s.mutate(variance);
        }

        //Overwrite the old ships with the new offspring
        ships = offspring;
    }

}
