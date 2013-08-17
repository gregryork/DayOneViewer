package uk.co.gregreynolds.dayone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;

public class Entry implements EntryInterface
{  
  private File file;
  private NSDictionary rootDict;
    
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
  }

  /* 
   ** <PRE>
   ** Release   Date     By  Proj   Ref      Description
   ** --------- -------- --- ------ -------- --------------------------------------
   ** </PRE>
   * (non-Javadoc)
   * @see uk.co.gregreynolds.dayone.EntryInterface#getCreationDate()
   */
  @Override
  public Date getCreationDate()
  {
    return (Date) rootDict.objectForKey("Creation Date").toJavaObject();
  }

  /* 
   ** <PRE>
   ** Release   Date     By  Proj   Ref      Description
   ** --------- -------- --- ------ -------- --------------------------------------
   ** </PRE>
   * (non-Javadoc)
   * @see uk.co.gregreynolds.dayone.EntryInterface#getEntryText()
   */
  @Override
  public String getEntryText()
  {
    return rootDict.objectForKey("Entry Text").toString();
  }

  /* 
   ** <PRE>
   ** Release   Date     By  Proj   Ref      Description
   ** --------- -------- --- ------ -------- --------------------------------------
   ** </PRE>
   * (non-Javadoc)
   * @see uk.co.gregreynolds.dayone.EntryInterface#getUUID()
   */
  @Override
  public String getUUID()
  {
    return rootDict.objectForKey("UUID").toString();
  }

  /* 
   ** <PRE>
   ** Release   Date     By  Proj   Ref      Description
   ** --------- -------- --- ------ -------- --------------------------------------
   ** </PRE>
   * (non-Javadoc)
   * @see uk.co.gregreynolds.dayone.EntryInterface#compareTo(uk.co.gregreynolds.dayone.Entry)
   */
  @Override
  public int compareTo(EntryInterface o)
  {
    return getCreationDate().compareTo(o.getCreationDate());
  }

  /* 
   ** <PRE>
   ** Release   Date     By  Proj   Ref      Description
   ** --------- -------- --- ------ -------- --------------------------------------
   ** </PRE>
   * (non-Javadoc)
   * @see uk.co.gregreynolds.dayone.EntryInterface#save()
   */
  @Override
  public void save() throws IOException
  {
    PropertyListParser.saveAsXML(rootDict, file);
  }

  /* 
   ** <PRE>
   ** Release   Date     By  Proj   Ref      Description
   ** --------- -------- --- ------ -------- --------------------------------------
   ** </PRE>
   * (non-Javadoc)
   * @see uk.co.gregreynolds.dayone.EntryInterface#setEntryText(java.lang.String)
   */
  @Override
  public void setEntryText(String entryText)
  {
    rootDict.put("Entry Text", entryText);    
  }

  /* 
   ** <PRE>
   ** Release   Date     By  Proj   Ref      Description
   ** --------- -------- --- ------ -------- --------------------------------------
   ** </PRE>
   * (non-Javadoc)
   * @see uk.co.gregreynolds.dayone.EntryInterface#getFile()
   */
  @Override
  public File getFile()
  {
    return file;
  }


}
