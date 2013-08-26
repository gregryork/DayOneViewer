package uk.co.gregreynolds.dayone;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class NullEntry implements EntryInterface
{

  @Override
  public Date getCreationDate()
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public String getEntryText()
  {
    return "";
  }


  @Override
  public String getUUID()
  {
    return "";
  }


  @Override
  public int compareTo(EntryInterface o)
  {
    return 0;
  }


  @Override
  public void save() throws IOException
  {
    // TODO Auto-generated method stub

  }


  @Override
  public void setEntryText(String entryText)
  {
    // TODO Auto-generated method stub

  }


  @Override
  public File getFile()
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public boolean photoFilenameAccept(String name)
  {
    return false;
  }

}
