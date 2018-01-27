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

    ShipPool p;
    Obstacle[] obstacles;
    boolean toggle;

    public Frame(ShipPool p, Obstacle[] obstacles) {
        super();
        this.p = p;
        this.obstacles = obstacles;
        toggle = true;
    }

    public void set(ShipPool p, Obstacle[] obstacles) {
        this.p = p;
        this.obstacles = obstacles;
    }
    
    public void pause() {
        toggle = false;
    }
    
    public void resume() {
        toggle = true;
    }
    
    @Override
    public void paintComponent(Graphics G) {
        super.paintComponent(G);
        G.setColor(Color.white);
        G.fillRect(0, 0, 10000, 10000);
        
        for (Obstacle o: obstacles) {
            o.draw(G);
        }
        
        if (!toggle) {
            return;
        }
        
        
        p.draw(G);
    }
}