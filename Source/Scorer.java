/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author chasehanson
 */
public class Scorer {

    Obstacle[] obstacles;
    Ship[] ships;
    double[] scores;
    int[] target;
    
    int SCALE = 20; //Down-Scaling factor for A-Star
    
    int[] lastCheckedNode, start, end, current;
    
    double[][] f_values, g_values, h_values;
    boolean[][] wall_values;
    
    ArrayList<int[]> openSet, closedSet;
    int[][][] came_from_values;
    ArrayList<int[]>[][] neighbor_values;
    ArrayList<int[]> finished_line;
    private final int width;
    private final int height;
    double best_score;

    public Scorer(Obstacle[] obstacles, Ship[] ships, int[] target, int width, int height) throws InterruptedException {
        this.obstacles = obstacles;
        this.ships = ships;
        this.target = target;
        this.width = width;
        this.height = height;
        
        finished_line = null;
        
        f_values = new double[width/SCALE][height/SCALE];
        g_values = new double[width/SCALE][height/SCALE];
        h_values = new double[width/SCALE][height/SCALE];
        wall_values = new boolean[width/SCALE][height/SCALE];
        came_from_values = new int[width/SCALE][height/SCALE][2];
        
        for (int i = 0; i<width/SCALE; i++) {
            for (int j = 0; j<height/SCALE; j++) {
                f_values[i][j] = 100000000;
                g_values[i][j] = 100000000;
                h_values[i][j] = 100000000;
                boolean wall = false;
                for (Obstacle o: obstacles) {
                    if (o.contains(i*SCALE,j*SCALE)) {
                        wall = true;
                    }
                }
                wall_values[i][j] = wall;
                came_from_values[i][j] = null;
            }
        }
        
        best_score = 10000000;
        
        generate_neighbors();
        

    }
    
    public void generate_neighbors() {
        neighbor_values = new ArrayList[width/SCALE][height/SCALE];
        
        for (int i = 0; i<width/SCALE; i++) {
            ArrayList<int[]>[] row = new ArrayList[height/SCALE];
            for (int j = 0; j<height/SCALE; j++) {
                ArrayList<int[]> neighbors = new ArrayList<int[]>();
                int[][] directions = {{-1,0},{1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
                for (int k = 0; k<directions.length; k++) {
                    if (i+directions[k][0] < 0 || i+directions[k][0] >= width/SCALE) {
                        continue;
                    } else {
                        if (j+directions[k][1] < 0 || j+directions[k][1] >= height/SCALE) {
                            continue;
                        } else { //Good
                            if (!wall_values[i+directions[k][0]][j+directions[k][1]]) {
                                neighbors.add(new int[] {i+directions[k][0], j+directions[k][1]});
                            }
                        }
                    }
                }
                row[j] = neighbors;
            }
            neighbor_values[i] = row;
        }
    }
    
    
    public void run() throws InterruptedException {
        for (int i = 0; i< width/SCALE; i++) {
            for (int j = 0; j<height/SCALE; j++) {
                boolean isWall = false; //IS IT IN THE OBSTACLE?
                for (Obstacle obstacle: obstacles) {
                    if (obstacle.contains(i*SCALE, j*SCALE)) {
                        isWall = true;
                    }
                }
                wall_values[i][j] = isWall;
            }
        }

        get_scores();
    }

    public void get_scores(){
        scores = new double[ships.length];
        for (int i = 0; i<scores.length; i++) {
            A_Star(new int[] {(int)ships[i].x/SCALE, (int)ships[i].y/SCALE}, new int[] {target[0]/SCALE, target[1]/SCALE});//?????
            scores[i] = calculate_score(ships[i]);
        }
    }

    public double distance(int[] a, int[] b) {
        return Math.pow(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2), .5);
    }

    public void A_Star(int[] start, int[] end) {
        g_values[start[0]][start[1]] = 0;
        f_values[start[0]][start[1]]  = heuristic(start, end);
        lastCheckedNode = start;
        
        openSet = new ArrayList<int[]>();
        
        // openSet starts with beginning node only
        openSet.add(start);
        closedSet = new ArrayList<int[]>();
        this.start = start;
        this.end = end;

        step();

        //At this point, current is the correct spot
        
        
    }
    
    public void set_line(ArrayList<int[]> line) {
        this.finished_line = line;
    }
    
    public double cost(int[] a, int[] b) {
        return distance(new int[] {SCALE*a[0], SCALE*a[1]}, new int[] {SCALE*b[0], SCALE*b[1]});
    }
    
    public double calculate_score(Ship s){

        double score = 0;
        ArrayList<int[]> line = new ArrayList<>();
        double the_cost = 0;
        int[] last = {0, 0};
        while (came_from_values[current[0]][current[1]] != null && score < 10000) {
            the_cost = cost(current, came_from_values[current[0]][current[1]]);
            score += the_cost;
            line.add(current);
            last = current;
            current = came_from_values[current[0]][current[1]];
        }
       
        line.add(current);
        score -= the_cost;
        score += cost(new int[] {(int)s.x/SCALE, (int)s.y/SCALE}, last);
        
        score += s.punishment;
        if (score < best_score) {
            set_line(line);
            best_score = score;
        }
        return score;
    }
    
    
    public double heuristic(int[] a, int[] b){
            double d = distance(a,b);
            return d;
    }

        //Run one finding step.
        //returns 0 if search ongoing
        //returns 1 if goal reached
        //returns -1 if no solution
    
    
    public int step(){
        if (start[0] == end[0] && start[1] == end[1]) {
            return 1;
        }

        while (openSet.size() > 0) {

            // Best next option
            int winner = 0;
            for (int i = 1; i < openSet.size(); i++) {
                if (f_values[openSet.get(i)[0]][openSet.get(i)[1]] < f_values[openSet.get(winner)[0]][openSet.get(winner)[1]]) {
                    winner = i;
                }
                
                //if we have a tie according to the standard heuristic
                if (f_values[openSet.get(i)[0]][openSet.get(i)[1]] == f_values[openSet.get(winner)[0]][openSet.get(winner)[1]]) {
                    //Prefer to explore options with longer known paths (closer to goal)
                    if (g_values[openSet.get(i)[0]][openSet.get(i)[1]] < g_values[openSet.get(winner)[0]][openSet.get(winner)[1]]) {
                        winner = i;
                    }
                }
            }
            
            current = openSet.get(winner);
            lastCheckedNode = current;

            // Did I finish?
            if (current[0] == end[0] && current[1] == end[1]) {
                return 1;
            }

            // Best option moves from openSet to closedSet
            openSet.remove(current);
            closedSet.add(current);

            // Check all the neighbors
            ArrayList<int[]> neighbors = neighbor_values[current[0]][current[1]];

            for (int i = 0; i < neighbors.size(); i++) {
                int[] neighbor = neighbors.get(i);

                // Valid next spot?
                boolean fl = false;
                
                for (int[] s: closedSet) {
                    if (s[0] == neighbor[0] && s[1] == neighbor[1]) {
                        fl = true;
                        break;
                    }
                }
                
                if (!fl) {
                    // Is this a better path than before?
                    double tempG = g_values[current[0]][current[1]] + heuristic(neighbor, current);

                    // Is this a better path than before?
                    fl = false;
                    
                    for (int[] s: openSet) {
                        if (s[0] == neighbor[0] && s[1] == neighbor[1]) {
                            fl = true;
                            break;
                        }
                    }
                    
                    if (!fl) {
                        openSet.add(neighbor);
                    } else if (tempG >= g_values[neighbor[0]][neighbor[1]]) {
                        // No, it's not a better path
                        continue;
                    }

                    g_values[neighbor[0]][neighbor[1]] = tempG;
                    h_values[neighbor[0]][neighbor[1]] = heuristic(neighbor, end);

                    f_values[neighbor[0]][neighbor[1]] = g_values[neighbor[0]][neighbor[1]] + h_values[neighbor[0]][neighbor[1]];
                    came_from_values[neighbor[0]][neighbor[1]] = current;
                }

            }
            // Uh oh, no solution yet
        }
        return -1;
    }
}