package uk.co.gregreynolds.dayone;

import java.awt.Component;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class EntryCellRenderer extends JLabel implements ListCellRenderer
{
  EntryDataModel model; 

  public EntryCellRenderer(EntryDataModel model)
  {
    this.model = model;
    this.setOpaque(true);
  }

  @Override
  public Component getListCellRendererComponent(JList list,
      Object value,
      int index,
      boolean isSelected,
      boolean cellHasFocus)
  {
    Entry selectedEntry = (Entry)value;

    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    SimpleDateFormat format = new SimpleDateFormat();
    setText(format.format(selectedEntry.getCreationDate().getTime()));
    
    setIcon((model.getEntryPhotoData(selectedEntry)).getThumbnail(list));
    return this;
  }
}
