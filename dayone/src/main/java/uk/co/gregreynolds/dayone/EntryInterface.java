package uk.co.gregreynolds.dayone;

import java.io.File;
import java.io.IOException;
import java.util.Date;


public interface EntryInterface
{

  public abstract Date getCreationDate();


  public abstract String getEntryText();


  public abstract String getUUID();


  public abstract int compareTo(EntryInterface o);


  public abstract void save() throws IOException;


  public abstract void setEntryText(String entryText);


  public abstract File getFile();


  public abstract boolean photoFilenameAccept(String name);

}