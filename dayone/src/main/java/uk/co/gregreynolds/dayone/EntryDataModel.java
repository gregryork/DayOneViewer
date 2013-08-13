package uk.co.gregreynolds.dayone;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;


public class EntryDataModel extends ArrayList<Entry> implements ListModel 
{
    
  public void addListDataListener(ListDataListener l)
  {
    // TODO Auto-generated method stub
    
  }

  public Object getElementAt(int index)
  {
    return get(index);
  }

  public int getSize()
  {
    return size();
  }

  public void removeListDataListener(ListDataListener l)
  {
    // TODO Auto-generated method stub
    
  }
  
  
}
