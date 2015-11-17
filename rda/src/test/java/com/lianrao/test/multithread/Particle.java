/*******************************************************************************
 * rda
 ******************************************************************************/
package com.lianrao.test.multithread;

import java.awt.Graphics;
import java.util.Random;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class Particle {
  protected int x;
  protected int y;
  protected final Random rng = new Random();
 
  public Particle(int initialX, int initialY) { 
    x = initialX;
    y = initialY;
  }

  public synchronized void move() {
    x += rng.nextInt(10) - 5;
    y += rng.nextInt(20) - 10;
  }

  public void draw(Graphics g) {
    int lx, ly;
    synchronized (this) { lx = x; ly = y; }
    g.drawRect(lx, ly, 10, 10);
  }
}