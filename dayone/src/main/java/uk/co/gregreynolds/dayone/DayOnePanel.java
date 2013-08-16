package uk.co.gregreynolds.dayone;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

import org.jdesktop.swingx.JXList;

import com.jhlabs.image.FourColorFilter;


public class DayOnePanel extends JPanel implements ListSelectionListener
{
  private static final String JOURNAL_DIRECTORY = "JOURNAL_DIRECTORY";
  
  private JSplitPane splitPane;
  private JXList list;
  private JTextArea text;
  private List<Entry> entries;
  private JPanel contentPanel;
  private JLabel photoLabel = null;
  private UndoManager undoManager = new UndoManager();
  private JButton undoButton = new JButton("Undo");
  private JButton redoButton = new JButton("Redo");
  private JButton saveButton = new JButton("Save");
  private JButton newButton = new JButton("New");
  private File parentDirectory;
  
  private UndoableEditListener undoListener = new UndoableEditListener() {

    public void undoableEditHappened(UndoableEditEvent e)
    {
      undoManager.addEdit(e.getEdit());
      updateButtons();

    }
  };
  
  private File getEntriesDirectory()
  {
    return new File(parentDirectory,"entries");
  }
  
  private File getPhotosDirectory()
  {
    return new File(parentDirectory,"photos");
  }
  
  public void changeParentDirectory(File pd) throws FileNotFoundException
  {
    if (!pd.isDirectory())
    {
      throw new FileNotFoundException();
    }
    parentDirectory = pd;
    getEntriesDirectory().mkdir();
    getPhotosDirectory().mkdir();
    
    this.parentDirectory = pd;
    entries = new ArrayList<Entry>();

    final File[] listOfFiles = getEntriesDirectory().listFiles();
    for (File file : listOfFiles) {      
      entries.add(new Entry(file));
    }
    
    if (list != null)
    {
      list.clearSelection();
      list.setModel(getModelFromEntries());
    }
    
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    prefs.put(JOURNAL_DIRECTORY, parentDirectory.toString());
  }

  private EntryDataModel getModelFromEntries()
  {
    EntryDataModel model = new EntryDataModel();
    for (Entry entry : entries) {
      model.addElement(entry);
    }
    return model;
  }

  public DayOnePanel(File pd) throws FileNotFoundException
  {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    String journalFromPrefs = prefs.get(JOURNAL_DIRECTORY, pd.toString());

    File journalFile = new File(journalFromPrefs);
    
    if (!journalFile.isDirectory())
    {
      chooseNewJournalDirectory();
    }
    else
    {
      changeParentDirectory(journalFile);      
    }
    
    EntryDataModel model = getModelFromEntries();
    list = new JXList(model);
    list.setComparator(new EntryDateComparator());
    list.setAutoCreateRowSorter(true);
    list.setSortOrder(SortOrder.DESCENDING);
    list.setSortsOnUpdates(true);
    
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setCellRenderer(new EntryCellRenderer(model));

    list.addListSelectionListener(this);

    JScrollPane listScrollPane = new JScrollPane(list);

    contentPanel = new JPanel(new BorderLayout());
    JPanel textPanel = new JPanel(new BorderLayout());
    text = new JTextArea();
    text.setWrapStyleWord(true);
    text.setLineWrap(true);
    JScrollPane textScrollPane = new JScrollPane(text);
    photoLabel = new JLabel();
    contentPanel.add(photoLabel,BorderLayout.NORTH);
    textPanel.add(textScrollPane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    undoButton.setEnabled(false);
    redoButton.setEnabled(false);
    saveButton.setEnabled(true);
    newButton.setEnabled(true);
    buttonPanel.add(newButton);
    buttonPanel.add(saveButton);
    buttonPanel.add(undoButton);
    buttonPanel.add(redoButton);
    textPanel.add(buttonPanel,BorderLayout.NORTH);

    text.getDocument().addUndoableEditListener(undoListener);
    text.getDocument().addDocumentListener(new DocumentListener()
    {
      
      @Override
      public void removeUpdate(DocumentEvent e)
      {
        updateEntry();
      }


      private void updateEntry()
      {
        Entry entry = getCurrentEntry();
        String entryText = text.getText();
        entry.setEntryText(entryText);
      }
      
      
      @Override
      public void insertUpdate(DocumentEvent e)
      {
        updateEntry();
      }
      
      
      @Override
      public void changedUpdate(DocumentEvent e)
      {
        updateEntry();
      }
    });
        
    undoButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          undoManager.undo();
        } catch (CannotRedoException cre) {
          cre.printStackTrace();
        }
        updateButtons();
      }
    });

    redoButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          undoManager.redo();
        } catch (CannotRedoException cre) {
          cre.printStackTrace();
        }
        updateButtons();
      }
    });
    
    saveButton.addActionListener(new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Entry entry = getCurrentEntry();
        saveEntry(entry);        
      }

    });
    
    newButton.addActionListener(new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Entry entry = Entry.createNewEntry(getEntriesDirectory());
        insertEntry(entry);
        saveEntry(entry);  
        list.setSelectedValue(entry, true);
        
      }
    });

    contentPanel.add(textPanel,BorderLayout.CENTER);
    

    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, contentPanel);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(250);

    //Provide minimum sizes for the two components in the split pane.
    Dimension minimumSize = new Dimension(150, 50);
    listScrollPane.setMinimumSize(minimumSize);
    textScrollPane.setMinimumSize(minimumSize);

    //Provide a preferred size for the split pane.
    splitPane.setPreferredSize(new Dimension(800, 600));    
    list.setSelectedIndex(0);
  }




  protected void updateButtons()
  {
    undoButton.setText(undoManager.getUndoPresentationName());
    redoButton.setText(undoManager.getRedoPresentationName());
    undoButton.setEnabled(undoManager.canUndo());
    redoButton.setEnabled(undoManager.canRedo());
  }


  public void valueChanged(ListSelectionEvent e)
  {
    JXList list = (JXList)e.getSource();
    Entry entry = (Entry)(list.getSelectedValue());
    undoManager.end();
    if (entry == null)
    {
      return;
    }
    text.getDocument().removeUndoableEditListener(undoListener);
    text.setText(entry.getEntryText());
    text.getDocument().addUndoableEditListener(undoListener);
    undoManager = new UndoManager();
    updateButtons();

    Image photo = null;
    try
    {
      photo = entry.getPhotoScaledToHeight(300);
    }
    catch (IOException e1)
    {
      // do nothing
    }
    
    if (photo == null)
    {
      photo = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
    }
    

    if (photo != null)
    {
      photoLabel.setIcon(new ImageIcon(photo));      
    }
    
  }




  public Component getSplitPane()
  {
    return splitPane;
  }


  public Component getInterfacePane()
  {
    return getSplitPane();
  }

  private Entry getCurrentEntry()
  {
    return (Entry)(list.getSelectedValue());    
  }

  protected void insertEntry(Entry entry)
  {
    list.clearSelection();
    EntryDataModel entries = (EntryDataModel)list.getModel();
    entries.addElement(entry);
  }

  private void saveEntry(Entry entry)
  {
    try
    {
      entry.save();
    }
    catch (IOException e1)
    {
      JOptionPane.showMessageDialog(contentPanel, 
          "Could not save entry.");
    }
  }
  
  public void chooseNewJournalDirectory() throws FileNotFoundException
  {
    File newParent = chooseJournalDirectory(this, parentDirectory);
    if (newParent != null && !newParent.equals(parentDirectory)) {
      changeParentDirectory(newParent);
    }
  }
  
  public static File chooseJournalDirectory(Component parent, File parentDirectory)
  {
    File returnValue = null;
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(parentDirectory);
    chooser.setDialogTitle("Day One Journal Location");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
    chooser.setAcceptAllFileFilterUsed(false);
    //    
    if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
      returnValue = chooser.getSelectedFile();
    }
    return returnValue;
  }
  
  
}
