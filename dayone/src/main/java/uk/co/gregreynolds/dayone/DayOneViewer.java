package uk.co.gregreynolds.dayone;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;


public class DayOneViewer
{
  
  public static void main(String[] args) throws FileNotFoundException
  {
    
    String userHome = System.getProperty( "user.home" );
    
    File userHomeFile = new File(userHome);
    File dropboxFile = new File(userHomeFile,"Dropbox");
    File appsFile = new File(dropboxFile,"Apps");
    File dayoneFile = new File(appsFile,"Day One");
    final File journalFile = new File(dayoneFile,"Journal.dayone");
    
    
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try
        {
          createAndShowGUI(journalFile);
        }
        catch (FileNotFoundException e)
        {
          System.exit(0);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }

  private static void createAndShowGUI(File parentDirectory) throws Exception
  {
    //Create and set up the window.
    JFrame frame = new JFrame("Day One");
    DayOneMenu menu = new DayOneMenu(frame);
    frame.setJMenuBar(menu);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    final DayOnePanel panel = new DayOnePanel(parentDirectory);
    frame.getContentPane().add(panel.getInterfacePane());
    
    menu.getDirectoryLocationItem().addActionListener(new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          panel.chooseNewJournalDirectory();
        }
        catch (Exception e1)
        {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        
      }
    });

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }


}
