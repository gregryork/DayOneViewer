package uk.co.gregreynolds.dayone;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.TransferHandler;

public class PhotoTransferHandler extends TransferHandler
{
  private class PhotoObservable extends Observable
  {
    public void update(File file)
    {
      setChanged();
      notifyObservers(file);
    }
    
    public void update(Image image)
    {
      setChanged();
      notifyObservers(image);
    }
  }
  
  PhotoObservable transferObserver = new PhotoObservable();

  @Override
  public boolean importData(TransferSupport support)
  {
    if (!canImport(support)) {
        return false;
    }
    
    try
    {
      
      if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
      {
        List data = (List)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
      
        for (Object elt : data) {
          File file = (File)elt;
          transferObserver.update(file);        
        }
      }
      else if (support.isDataFlavorSupported(DataFlavor.imageFlavor))
      {
        Image image = (Image)support.getTransferable().getTransferData(DataFlavor.imageFlavor);
        transferObserver.update(image);
      }
    }
    catch (UnsupportedFlavorException | IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public boolean canImport(TransferSupport support)
  {
    return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) ||
        support.isDataFlavorSupported(DataFlavor.imageFlavor);
  }
  
  public void addfileTransferAction(Observer action)
  {
    transferObserver.addObserver(action);
  }

}
