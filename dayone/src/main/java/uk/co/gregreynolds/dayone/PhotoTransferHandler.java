package uk.co.gregreynolds.dayone;

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
    public void updateFile(File file)
    {
      setChanged();
      notifyObservers(file);
    }
  }
  
  PhotoObservable fileTransferActions = new PhotoObservable();

  @Override
  public boolean importData(TransferSupport support)
  {
    if (!canImport(support)) {
        return false;
    }
    try
    {
      List data = (List)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
      
      for (Object elt : data) {
        File file = (File)elt;
        fileTransferActions.updateFile(file);
        break;
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
    return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
  }
  
  public void addfileTransferAction(Observer action)
  {
    fileTransferActions.addObserver(action);
  }

}
