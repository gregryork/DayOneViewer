package uk.co.gregreynolds.dayone;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
    File journalFile = new File(dayoneFile,"Journal.dayone");
    final File entriesFile = new File(journalFile,"entries");
    
    final File[] listOfFiles = entriesFile.listFiles();

    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try
        {
          createAndShowGUI(listOfFiles, entriesFile);
        }
        catch (FileNotFoundException e)
        {
          e.printStackTrace();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }

  private static void createAndShowGUI(File[] listOfFiles, File parentDirectory) throws Exception
  {
    //Create and set up the window.
    JFrame frame = new JFrame("Day One");

    List<Entry> entries = new ArrayList<Entry>();

    for (File file : listOfFiles) {      
      entries.add(new Entry(file));
    }

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    DayOnePanel panel = new DayOnePanel(entries,parentDirectory);
    frame.getContentPane().add(panel.getInterfacePane());

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }


}
