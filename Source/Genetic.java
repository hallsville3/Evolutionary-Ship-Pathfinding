/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

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
        int radius = 5;
        double rate = .05;
        
        ShipPool pool = new ShipPool(n, size, radius, rate);

        Obstacle[] obstacles = new Obstacle[4]; //Array of black Obstacle bars

        obstacles[0] = new Obstacle(0, 200, 300, 20);
        obstacles[1] = new Obstacle(350, 300, 150, 20);
        obstacles[2] = new Obstacle(120, 400, 190, 20);
        obstacles[3] = new Obstacle(100, 200, 20, 300);

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

        int gen_skip = 20; //Generations between each displayed generation
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
                Thread.sleep(1);
            }
            gen++;
            frame.resume();

            screen.setTitle("Generation: " + gen + " Best: " + (double) Math.round(score * 1000d) / 1000d);
            score = pool.score_ships(frame, obstacles, end_time, dt, speed, true);
            //System.out.println(pool.ships[0].boosters.length);
        }

    }

}
