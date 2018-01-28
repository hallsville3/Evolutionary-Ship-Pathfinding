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
    //Represents a rectangular obstacle on the screen

    int x, y, width, height;

    public Obstacle(int x, int y, int width, int height) {
        //Constructs an obstacle with a given x, y, width and height

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics G) {
        //Draws the obstacle into the given Graphics object

        G.setColor(Color.black);
        G.fillRect(x, y, width, height);
    }

    public boolean intersects(Ship s) {
        //Returns true if the ship intersects the obstacle, otherwise false

        double delta_x = s.x - Math.max(x, Math.min(s.x, x + width));
        double delta_y = s.y - Math.max(y, Math.min(s.y, y + height));
        return (delta_x * delta_x + delta_y * delta_y) < (s.radius * s.radius);
    }

    public boolean cleared(Ship s) {
        //Returns true if a ship is above the obstacle, otherwise false

        return (s.y < y);
    }
}
