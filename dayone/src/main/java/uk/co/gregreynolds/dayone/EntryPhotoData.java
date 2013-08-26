package uk.co.gregreynolds.dayone;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXList;

public class EntryPhotoData
{
  public class IconAction
  {

  }


  public EntryInterface entry;
  public Image thumbnailImage;
  public File file;
  public int thumbnailWidth = 50;
  public int thumbnailHeight = 50;
  public JXList thumbnailList;
  private File photoDirectory;
  
  EntryPhotoData(EntryInterface entry, File photoDirectory)
  {
    this.entry = entry;
    this.photoDirectory = photoDirectory;
    loader.execute();
  }
  
  public SwingWorker<Void, IconAction> loader = new SwingWorker<Void, IconAction>(){
  
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
        EntryDataModel model = (EntryDataModel)thumbnailList.getModel();
        int index = model.indexOf(entry);
        if (index > 0)
        {
          index = thumbnailList.convertIndexToView(index);        
          thumbnailList.repaint(thumbnailList.getCellBounds(index, index));
          thumbnailList.repaint();
        }
      }  
      return null;
    }
  };

  public Icon getThumbnail(JList list)  
  {
    thumbnailList = (JXList) list;
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
  
  public Image getPhotoScaledToHeight(int height) throws IOException
  {
    Image photo = getPhoto();
    if (photo == null)
    {
      return photo;
    }
    double aspectRatio = (double)photo.getWidth(null)
        / (double)photo.getHeight(null);
    int width = (int)(aspectRatio * height);

    Image resizedImage =
        photo.getScaledInstance(width, height,Image.SCALE_DEFAULT);
    return resizedImage;
  }
  
  public File getPhotoFile()
  {
    File returnValue = null;    
    File[] photoList = photoDirectory.listFiles(new FilenameFilter()
    {      
      @Override
      public boolean accept(File dir,
          String name)
      {
        return entry.photoFilenameAccept(name);
      }
    });
    if (photoList.length > 0)
    {
      returnValue = photoList[0];
    }
    return returnValue;
  }
}