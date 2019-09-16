/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author chasehanson
 */
class Frame extends JPanel {
    //The window that the ships, obstacles and the target are drawn into

    ShipPool p;
    ArrayList<Obstacle> obstacles;
    Scorer s;
    boolean toggle;
    ArrayList<int[]> line;
    int SCALE;

    public Frame(ShipPool p, ArrayList<Obstacle> obstacles) {
        //Contruct a frame with a given ship pool and obstacles

        super();
        this.p = p;
        s = null;
        this.obstacles = obstacles;
        SCALE = 1;
        toggle = true; //True if simulation is active
    }
    
    public void set_line(ArrayList<int[]> line, int sc) {
        SCALE = sc;
        this.line = line;
    }
    
    public void set(ShipPool p, ArrayList<Obstacle> obstacles) {
        //Update the stored ship pool and obstacles

        this.p = p;
        this.obstacles = obstacles;
    }

    public void pause() {
        //Toggle the state off

        toggle = false;
    }

    public void resume() {
        //Toggle the state on

        toggle = true;
    }

    @Override
    public void paintComponent(Graphics G) {
        //Draws each frame of the simulation

        super.paintComponent(G);
        G.setColor(Color.white);
        G.fillRect(0, 0, 10000, 10000);

        for (Obstacle o : obstacles) {
            o.draw(G);
        }

        if (!toggle) { //If not active, return before drawing the ships
            return;
        }

        p.draw(G);
        G.setColor(Color.black);
        if (line != null) {
            for (int i = 0; i<line.size()-1; i++) {
                G.drawLine(SCALE*line.get(i)[0], SCALE*line.get(i)[1], SCALE*line.get(i+1)[0], SCALE*line.get(i+1)[1]);
            }
        }
    }
}
