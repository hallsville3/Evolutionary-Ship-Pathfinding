/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author chasehanson
 */
public class Obstacle {
    int x, y, size_x, size_y;
    
    public Obstacle(int x, int y, int size_x, int size_y) {
        this.x = x;
        this.y = y;
        this.size_x = size_x;
        this.size_y = size_y;
    }
    
    public void draw(Graphics G) {
        G.setColor(Color.black);
        G.fillRect(x, y, size_x, size_y);
    }
    
    public boolean intersects(Ship s) {
        double delta_x = s.x - Math.max(x, Math.min(s.x, x + size_x));
        double delta_y = s.y - Math.max(y, Math.min(s.y, y + size_y));
        return (delta_x * delta_x + delta_y * delta_y) < (s.radius * s.radius);
    }
    
    public boolean cleared(Ship s) {
        return (s.y < y);
    }
}
