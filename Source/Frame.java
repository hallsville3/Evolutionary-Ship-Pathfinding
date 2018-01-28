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
import javax.swing.JPanel;

/**
 *
 * @author chasehanson
 */
class Frame extends JPanel {
    //The window that the ships, obstacles and the target are drawn into

    ShipPool p;
    Obstacle[] obstacles;
    boolean toggle;

    public Frame(ShipPool p, Obstacle[] obstacles) {
        //Contruct a frame with a given ship pool and obstacles

        super();
        this.p = p;
        this.obstacles = obstacles;

        toggle = true; //True if simulation is active
    }

    public void set(ShipPool p, Obstacle[] obstacles) {
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
    }
}
