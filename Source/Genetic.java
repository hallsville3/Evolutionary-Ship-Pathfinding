/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import javax.swing.JFrame;
import java.util.ArrayList;

/**
 *
 * @author chasehanson
 */
public class Genetic {
    //Base Class for Pathfinding Genetic Algorithm

    public static void main(String[] args) throws InterruptedException {
        int n = 1000;
        double dt = .05;
        int size = 500;
        int radius = 5;
        double rate = .2;
        int[] target = {250, 250};
        
        ShipPool pool = new ShipPool(n, size, radius, rate, target);

        ArrayList<Obstacle> obstacles = new ArrayList<>(); //Array of black Obstacle bars

        obstacles.add(new Obstacle(100, 300, 300, 20));
        obstacles.add(new Obstacle(100, 100, 20, 200));
        obstacles.add(new Obstacle(380, 100, 20, 200));

        //Frame setup
        Frame frame = new Frame(pool, obstacles);
        JFrame screen = new JFrame("Ships");
        frame.setSize(size, size + 22);
        screen.setSize(size, size + 22);
        screen.add(frame);
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screen.setVisible(true);
        frame.setVisible(true);

        double end_time = 200;
        int gen = 1;
        double speed = 25;

        int gen_skip = 5; //Generations between each displayed generation
        double score = 0;

        score = pool.score_ships(frame, obstacles, end_time, dt, speed, true);

        while (true) {

            //Do gen_skip generations between each displayed generation
            frame.pause();
            for (int i = 0; i < gen_skip; i++) {
                gen++;
                score = pool.score_ships(frame, obstacles, end_time, dt, speed, false);
                //screen.setTitle("Generation: " + gen + " Best: " + (double)Math.round(score * 1000d) / 1000d);
                //screen.repaint();
            }
            gen++;
            frame.resume();

            screen.setTitle("Generation: " + gen + " Best: " + (double) Math.round(score * 1000d) / 1000d);
            score = pool.score_ships(frame, obstacles, end_time, dt, speed, true);
            //System.out.println(pool.ships[0].boosters.length);
        }

    }

}
