/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author chasehanson
 */
public class ShipPool {

    int n;
    double variance, last_score;
    int size, radius;
    boolean working;
    Ship[] ships;
    int[] target;

    public ShipPool(int n, int size, int ship_radius) {
        /*Constructor for creating a ship pool of size n, 
        with a screen size of "size" and a ship radius of ship_radius
        */
        
        this.n = n;
        this.size = size;
        this.radius = ship_radius;
        generate_ships();
        working = false;
        target = new int[]{250, 100};
        variance = 0;
    }

    public void generate_ships() {
        //Populages the ship[] with n random ships
        
        ships = new Ship[n];
        for (int i = 0; i < n; i++) {
            ships[i] = new Ship(size, radius);
        }
    }

    public Ship[] get_n_best(int count) {
        //Returns a Ship[] of the 'count' best ships
        
        Arrays.sort(ships);
        Ship[] best = new Ship[count];
        for (int i = 0; i < count; i++) {
            best[i] = ships[i];
        }
        return best;
    }

    public Ship[] remove_random_ship(Ship[] s) {
        //Removes a random Ship from the given Ship[] s
        
        Ship[] new_ships = new Ship[s.length-1];
        int remove = (int)(Math.random()*s.length);
        int offset = 0;
        for (int i = 0; i<s.length-1; i++) {
            if (i == remove) {
                offset = 1;
            }
            
            new_ships[i] = s[i+offset];

        }
        return new_ships;
    }
    
    public Ship[] kill_n_randomly(Ship[] s, int count) {
        //Algorithm to remove 'count' elements from an array
        
        Ship[] old_ships = s;
        for (int i = 0; i<count; i ++) {
            Ship[] new_ships = new Ship[s.length-i];
            new_ships = remove_random_ship(old_ships);
            old_ships = new_ships;
        }
        
        //Remove the nulls from the array by counting how many nulls there are
        int null_count = 0;
        for (Ship sh: old_ships) {
            if (sh == null) {
                null_count ++;
            }
        }
        
        //And then by creating a properly sized array
        Ship[] return_ships = new Ship[old_ships.length-null_count];
        
        //And populating it with the ships, ignoring the nulls
        int offset = 0;
        for (int i = 0; i<old_ships.length-null_count; i++) {
            while (old_ships[i+offset] == null) {
                offset++;
            }
            return_ships[i] = old_ships[i+offset];
        }
        
        return return_ships;
    }

    public Ship[] get_offspring(Ship[] parents, int survived) {
        //Get a new Ship[] of length n by mating parents

        Ship[] new_generation = new Ship[n];

        final int[] ints = new Random().ints(0, parents.length).distinct().limit(survived).toArray();
        
        for (int i = 0; i < survived; i++) {
            new_generation[i] = parents[ints[i]].copy();
            if (Math.random() < .05) {
                /*
                Change this parent a bit, to prevent stagnation
                This way we ensure no parents live on forever unchanged
                */
                new_generation[i].mutate(variance); 
                
            }
        }

        int required_children = n - survived;
        final int[] parents1 = new Random().ints(0, parents.length).limit(required_children).toArray();
        final int[] parents2 = new Random().ints(0, parents.length).limit(required_children).toArray();

        //Fill the rest of the array with children of the selected parents
        for (int i = survived; i < new_generation.length; i++) {
            new_generation[i] = parents[parents1[i-survived]].mate(parents[parents2[i-survived]], variance);
        }
        
        return new_generation;

    }

    public int score_ships(Frame f, Obstacle[] obstacles, double end_time, double dt, double speed, boolean display) throws InterruptedException {
        //Scores all the ships in a simulation, optionally displaying it
        
        double time = 0;
        
        if (display) {
            f.set(this, obstacles);
            f.repaint();
            Thread.sleep((long)(speed*dt));
        }
        
        //Run the generation, updating the ships and checking if they die
        int alive_count = ships.length;
        while (time < end_time && alive_count>0) { 
            
            for (int i = 0; i < ships.length; i++) {
                if (ships[i].alive) {
                    ships[i].update(time, dt);
                    ships[i].alive = ships[i].still_alive(target, obstacles);
                    if (!ships[i].alive) {
                        alive_count--;
                    }
                }
            }
            
            if (display) {
                f.set(this, obstacles);
                f.repaint();
                Thread.sleep((long)(speed*dt));
            }
            
            time += dt;
        }
        
        double best = 1000000;
        double score = 0;
        
        //Determine the best score of the ships
        for (Ship s : ships) {
            score = s.calculate_score(target, obstacles);
            if (score < best) {
                best = score;
            }
        }
        
        //Calculate the variation for the next frame
        variance = Math.abs(last_score-best);
        if (best < 1000) {
            variance = 1000000;
        }
        
        last_score = best;
        
        //Do the next generation, accounting for the scores of this generation
        do_generation();
        
        return (int)best;
    }
    
    public void draw(Graphics G) {
        //Draw the target, and each ship and its boosters
        G.setColor(Color.RED);
        G.fillOval(target[0] - 3, target[1] - 3, 6, 6);

        for (Ship s: ships) {
            s.draw(G);
        }
    }

    public void do_generation() {
        //Do one generation of mating, accounting for the scores received
        
        int best_count = 10;
        int kill_count = 0;
        int leave_count = 0;
        
        //Get top best_count ships from the array of n
        Ship[] best = get_n_best(best_count);
        
        //Kill "kill_count" of these parents
        Ship[] parents = kill_n_randomly(best, kill_count);

        //Generate the offspring, leaving leave_count parents.
        Ship[] offspring = get_offspring(parents, leave_count);
        
        //Overwrite the old ships with the new offspring
        ships = offspring;
    }

}
