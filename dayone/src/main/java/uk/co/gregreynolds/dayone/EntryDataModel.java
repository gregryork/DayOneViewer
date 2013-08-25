package uk.co.gregreynolds.dayone;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;


public class EntryDataModel extends DefaultListModel<EntryInterface>
{
  Map<EntryInterface,EntryPhotoData> photoDataMap =
      new HashMap<EntryInterface,EntryPhotoData>();
  private File photoDirectory;
  
  public EntryDataModel(File photoDirectory)
  {
    super();
    this.photoDirectory = photoDirectory;
  }

  public EntryPhotoData getEntryPhotoData(EntryInterface entry)
  {
    EntryPhotoData entryPhotoData = photoDataMap.get(entry);
    if (entryPhotoData == null)
    {
      entryPhotoData = new EntryPhotoData(entry,photoDirectory);
      photoDataMap.put(entry, entryPhotoData);
    }
    return entryPhotoData;
  }
  
  public void removeEntryPhotoData(EntryInterface entry)
  {
    photoDataMap.remove(entry);
  }

  public void setPhotoDirectory(File photoDirectory)
  {
    photoDataMap.clear();
    this.photoDirectory = photoDirectory;    
  }
  
  public EntryInterface getEntryByUUID(String uuid)
  {
    EntryInterface entry = new NullEntry();
    Enumeration<EntryInterface> elements = this.elements();
    while (elements.hasMoreElements())
    {
      EntryInterface elt = elements.nextElement();
      if (uuid.contains(elt.getUUID()))
      {
        entry = elt;
        break;
      }
    }
    return entry;
  }
}
