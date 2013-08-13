package uk.co.gregreynolds.dayone;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.SwingWorker;

public class BackGroundLoadingIcon implements Icon
{

  private int width;
  private int height;
  private BasicStroke stroke = new BasicStroke(4);

  public BackGroundLoadingIcon(int width,
      int height)
  {
    this.width = width;
    this.height = height;
  }


  public int getIconHeight()
  {
    return height;
  }


  public int getIconWidth()
  {
    return width;
  }


  public void paintIcon(Component arg0,
      Graphics g,
      int x,
      int y)
  {
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setColor(Color.WHITE);
    g2d.fillRect(x +1 ,y + 1,width -2 ,height -2);



    g2d.setColor(Color.BLACK);
    g2d.drawRect(x +1 ,y + 1,width -2 ,height -2);

    g2d.setColor(Color.RED);

    g2d.setStroke(stroke);
    g2d.drawLine(x +10, y + 10, x + width -10, y + height -10);
    g2d.drawLine(x +10, y + height -10, x + width -10, y + 10);

    g2d.dispose();
  }
}
