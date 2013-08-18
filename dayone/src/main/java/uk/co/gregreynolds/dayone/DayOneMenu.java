package uk.co.gregreynolds.dayone;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


public class DayOneMenu extends JMenuBar 
{
  private JMenu fileMenu;
  private JMenu helpMenu;
  private JMenuItem directoryLocationItem;
  
  
  public DayOneMenu(final Component parent)
  {
    super();
    
    fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    
    directoryLocationItem = new JMenuItem("Set Directory");
    directoryLocationItem.setMnemonic(KeyEvent.VK_D);
    directoryLocationItem.setToolTipText("Set the Journal Directory");
    
    JMenuItem eMenuItem = new JMenuItem("Exit");
    eMenuItem.setMnemonic(KeyEvent.VK_E);
    eMenuItem.setToolTipText("Exit application");
    eMenuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    });
    
    fileMenu.add(directoryLocationItem);
    fileMenu.addSeparator();
    fileMenu.add(eMenuItem);
    
    helpMenu = new JMenu("Help");
    helpMenu.setMnemonic(KeyEvent.VK_H);    
    JMenuItem aboutItem = new JMenuItem("About");
    aboutItem.getAccessibleContext().setAccessibleDescription(
        "Display the about text");
    aboutItem.addActionListener(new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Version version = new Version();
        String message = "DayOneViewer\n© Greg Reynolds\nVersion: " + version.getPomVersion() +
            "\nBuild time: " + version.getBuildTime() +
            "\nBuild number: " + version.getBuildNumber();
        JOptionPane.showMessageDialog(parent, message,"About",JOptionPane.INFORMATION_MESSAGE);
      }
    });
    helpMenu.add(aboutItem);
    
    
    add(fileMenu);
    
    
    add(helpMenu);
  }
  
  JMenuItem getDirectoryLocationItem()
  {
    return directoryLocationItem;
  }
  
}
