package uk.co.gregreynolds.dayone;

import java.util.Comparator;

public class EntryDateComparator implements Comparator<Entry>
{

  @Override
  public int compare(Entry o1,
      Entry o2)
  {
    return o1.getCreationDate().compareTo(o2.getCreationDate());
  }

}
