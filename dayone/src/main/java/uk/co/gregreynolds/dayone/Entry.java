package uk.co.gregreynolds.dayone;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingWorker;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;



public class Entry implements Comparable<Entry>
{  
  public Image thumbnailImage = null;

  private File file;
  private NSDictionary rootDict;

  private int thumbnailWidth = 50;
  private int thumbnailHeight = 50;
  private JList thumbnailList;
    
  private Entry()
  {
    rootDict = new NSDictionary();
  }
  
  public static Entry createNewEntry(File parentDirectory)
  {
    Entry entry = new Entry();
    
    entry.rootDict = new NSDictionary();
    Date creationDate = new Date();
    entry.rootDict.put("Creation Date", creationDate);
    
    NSDictionary creatorDict = new NSDictionary();
    creatorDict.put("Device Agent","PC");
    creatorDict.put("Generation Date", creationDate);
    creatorDict.put("Host Name", "PC");
    creatorDict.put("OS Agent", "PC");
    creatorDict.put("Software Agent", "Day One Viewer");
    entry.rootDict.put("Creator", creatorDict);
    
    entry.rootDict.put("Entry Text", "");
    
//    NSDictionary locationDict = new NSDictionary();
//    locationDict.put("Administrative Area", "");
//    locationDict.put("Country", "");
//    locationDict.put("Latitude", 0.0);
//    locationDict.put("Locality", "");
//    locationDict.put("Longitude", 0.0);
//    locationDict.put("Place Name", "");
//    entry.rootDict.put("Location",locationDict);
    
    entry.rootDict.put("Starred",false);
    entry.rootDict.put("Time Zone", TimeZone.getDefault().getDisplayName());
    
    entry.rootDict.put("UUID", UUID.randomUUID().toString());
    entry.file = new File(parentDirectory,entry.getUUID() + ".doentry");
    
    return entry;
  }

  public Entry(File file) throws FileNotFoundException
  {
    this.file = file;
    try
    {
      rootDict = (NSDictionary)PropertyListParser.parse(file);
    }
    catch (Exception e)
    {
      throw new FileNotFoundException();
    }
    loader.execute();
  }

  public Date getCreationDate()
  {
    return (Date) rootDict.objectForKey("Creation Date").toJavaObject();
  }

  public String getEntryText()
  {
    return rootDict.objectForKey("Entry Text").toString();
  }

  public String getUUID()
  {
    return rootDict.objectForKey("UUID").toString();
  }

  public Image getPhoto() throws IOException
  {    
    File photoFile = getPhotoFile();
    Image photoImage = null;
    if (photoFile != null)
    {
      photoImage = ImageIO.read(photoFile);
    }
    return photoImage;
  }

  public File getPhotoFile()
  {
    File returnValue = null;
    File parentDir = file.getParentFile().getParentFile();
    File photoDir = new File(parentDir,"photos");
    File[] photoList = photoDir.listFiles(new FilenameFilter()
    {      
      public boolean accept(File dir,
          String name)
      {
        return name.startsWith(getUUID());
      }
    });
    if (photoList.length > 0)
    {
      returnValue = photoList[0];
    }
    return returnValue;
  }

  public Image getPhotoScaledToHeight(int height) throws IOException
  {
    Image photo = getPhoto();
    if (photo == null)
    {
      return photo;
    }
    double aspectRatio = (double)photo.getWidth(null)
        / (double)photo.getHeight(null);
    int width = (int)(aspectRatio * (double)height);

    Image resizedImage =
        photo.getScaledInstance(width, height,Image.SCALE_DEFAULT);
    return resizedImage;
  }

  public int compareTo(Entry o)
  {
    return getCreationDate().compareTo(o.getCreationDate());
  }

  public Icon getThumbnail(JList list)  
  {
    thumbnailList = list;
    Icon icon = null;
    if (thumbnailImage == null)
    {
      icon = new BackGroundLoadingIcon(thumbnailWidth, thumbnailHeight);
    }
    else
    {
      icon = new ImageIcon(thumbnailImage);
    }
    return icon;
  }

  public void save() throws IOException
  {
    PropertyListParser.saveAsXML(rootDict, file);
  }

  public void setEntryText(String entryText)
  {
    rootDict.put("Entry Text", entryText);    
  }
  
  public class IconAction extends AbstractAction
  {
    public void actionPerformed(ActionEvent e)
    {
    }
  }
  
  private SwingWorker<Void, IconAction> loader = new SwingWorker<Void, IconAction>(){

    @Override
    protected Void doInBackground() throws Exception
    {

      Image photoImage = ImageIO.read(getPhotoFile());

      BufferedImage resizedImg = new BufferedImage(thumbnailWidth, thumbnailHeight,
          BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = resizedImg.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.drawImage(photoImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
      g2.dispose();
      thumbnailImage = resizedImg;
      if (thumbnailList != null)
      {        
        thumbnailList.repaint();
      }

      //      publish(new IconAction(resizedImg));
      return null;
    }
  };

  public File getFile()
  {
    return file;
  }

}
