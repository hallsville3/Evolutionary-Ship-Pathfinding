/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.awt.Color;
import javax.swing.JFrame;

/**
 *
 * @author chasehanson
 */
public class Genetic {
    //Base Class for Pathfinding Genetic Algorithm
    
    public static void main(String[] args) throws InterruptedException {
        int n = 100;
        double dt = .05;
        int size = 500;
        int radius = 6;
        ShipPool pool = new ShipPool(n, size, radius);
        
        Obstacle[] obstacles = new Obstacle[4]; //Array of black Obstacle bars
        
        obstacles[0] = new Obstacle(0, 200, 350, 20);
        obstacles[1] = new Obstacle(350, 300, 150, 20);
        obstacles[2] = new Obstacle(120, 400, 190, 20);
        obstacles[3] = new Obstacle(100, 200, 20, 300);

        //Frame setup
        Frame frame = new Frame(pool, obstacles);
        JFrame screen = new JFrame("Ships");
        frame.setSize(size, size+22);
        screen.setSize(size, size+22);
        screen.add(frame);
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screen.setVisible(true);
        frame.setVisible(true);
        
        double end_time = 200;
        int gen = 1;
        double speed = 50;
        
        int gen_skip = 500; //Generations between each displayed generation
        int score = 0;
        score = pool.score_ships(frame, obstacles, end_time, dt, speed, true);
        while (true) {
            gen += gen_skip + 1;
            frame.pause();
            for (int i = 0; i<gen_skip; i++) {
               pool.score_ships(frame, obstacles, end_time, dt, speed, false);
               Thread.sleep(1);
            }
            frame.resume();
            screen.setTitle("Generation: "+gen + " Best: " + score);
            score = pool.score_ships(frame, obstacles, end_time, dt, speed, true);
            //System.out.println(pool.ships[0].boosters.length);
        }
        
        
    }
    
}
