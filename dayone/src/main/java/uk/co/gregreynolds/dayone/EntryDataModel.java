package uk.co.gregreynolds.dayone;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;


public class EntryDataModel extends DefaultListModel<Entry>
{
  Map<Entry,EntryPhotoData> photoDataMap =
      new HashMap<Entry,EntryPhotoData>();
  private File photoDirectory;
  
  public EntryDataModel(File photoDirectory)
  {
    super();
    this.photoDirectory = photoDirectory;
  }

  public EntryPhotoData getEntryPhotoData(Entry entry)
  {
    EntryPhotoData entryPhotoData = photoDataMap.get(entry);
    if (entryPhotoData == null)
    {
      entryPhotoData = new EntryPhotoData(entry,photoDirectory);
      photoDataMap.put(entry, entryPhotoData);
    }
    return entryPhotoData;
  }
  
  public void removeEntryPhotoData(Entry entry)
  {
    photoDataMap.remove(entry);
  }
}
