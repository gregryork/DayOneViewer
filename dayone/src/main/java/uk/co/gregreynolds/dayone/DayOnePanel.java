package uk.co.gregreynolds.dayone;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

import org.jdesktop.swingx.JXList;


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

  private JButton removePhotoButton = new JButton("Remove Photo");
  private JButton changePhotoButton = new JButton("Change Photo");
  private JButton pastePhotoButton = new JButton("Paste Photo");

  private File parentDirectory;

  private UndoableEditListener undoListener = new UndoableEditListener() {

    @Override
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
    EntryDataModel model = new EntryDataModel(getPhotosDirectory());
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
    JPanel photoPanel = new JPanel(new BorderLayout());
    photoLabel = new JLabel();
    photoPanel.add(photoLabel,BorderLayout.CENTER);
    contentPanel.add(photoPanel,BorderLayout.NORTH);
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
        EntryInterface entry = getCurrentEntry();
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

    JPanel east = new JPanel(new GridBagLayout());
    JPanel photoButtonPanel = new JPanel(new GridLayout(3,1));
    photoButtonPanel.add(removePhotoButton);
    photoButtonPanel.add(changePhotoButton);
    photoButtonPanel.add(pastePhotoButton);
    east.add(photoButtonPanel);
    photoPanel.add(east,BorderLayout.EAST);

    changePhotoButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          changePhoto();
        }
        catch (IOException e1)
        {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    });

    removePhotoButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        removePhoto();        
      }
    });

    pastePhotoButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        TransferHandler th = getTransferHandler();
        Action action = th.getPasteAction();
        action.actionPerformed(new ActionEvent(DayOnePanel.this,e.getID() , e.getActionCommand()));
      }
    });

    undoButton.addActionListener(new ActionListener() {
      @Override
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
      @Override
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
        EntryInterface entry = getCurrentEntry();
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

  protected void changePhoto() throws IOException
  {
    EntryInterface entry = getCurrentEntry();
    JFileChooser chooser = new JFileChooser();
    ImagePreviewPanel preview = new ImagePreviewPanel();
    chooser.setAccessory(preview);
    chooser.addPropertyChangeListener(preview);
    File newPhoto = null;
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      newPhoto = chooser.getSelectedFile();
    }
    updatePhoto(entry, newPhoto);
  }

  public void updateCurrentEntryPhoto(File newPhoto) throws IOException
  {
    EntryInterface entry = getCurrentEntry();
    updatePhoto(entry, newPhoto);
  }

  public void updateCurrentEntryPhoto(Image newPhoto) throws IOException
  {
    EntryInterface entry = getCurrentEntry();
    updatePhoto(entry, newPhoto);
  }
  
  private void updatePhoto(EntryInterface entry, File file) throws IOException
  {
    Image photo = ImageIO.read(file);
    updatePhoto(entry, photo);
  }

  private void updatePhoto(EntryInterface entry, Image newPhoto) throws IOException
  {
    if (newPhoto == null)
    {
      return;
    }
    String uuid = entry.getUUID();
    if (uuid.equals(""))
    {
      return;
    }
    File destPhoto = new File (getPhotosDirectory(),uuid + ".jpg");
    int w = newPhoto.getWidth(null);
    int h = newPhoto.getHeight(null);
    int type = BufferedImage.TYPE_INT_RGB;  // other options
    BufferedImage dest = new BufferedImage(w, h, type);
    Graphics2D g2 = dest.createGraphics();
    g2.drawImage(newPhoto, 0, 0, null);
    g2.dispose();
    removePhoto();
    ImageIO.write(dest, "jpg", destPhoto);
    photoChanged(entry);
  }

  protected void removePhoto()

  {
    EntryInterface entry = getCurrentEntry();
    EntryDataModel model = (EntryDataModel)list.getModel();
    File photoFile = model.getEntryPhotoData(entry).getPhotoFile();
    
    if (photoFile == null)
    {
      return;
    }

    if (photoFile.isFile())
    {
      photoFile.delete();
      photoChanged(entry);
    }
  }

  private void photoChanged(EntryInterface entry)
  {
    EntryDataModel model = (EntryDataModel)list.getModel();
    model.removeEntryPhotoData(entry);
    list.repaint();
    updatePhoto(entry);
  }

  protected void updateButtons()
  {
    undoButton.setText(undoManager.getUndoPresentationName());
    redoButton.setText(undoManager.getRedoPresentationName());
    undoButton.setEnabled(undoManager.canUndo());
    redoButton.setEnabled(undoManager.canRedo());
  }


  @Override
  public void valueChanged(ListSelectionEvent e)
  {
    JXList list = (JXList)e.getSource();
    EntryInterface entry = getCurrentEntry();

    undoManager.end();
    text.getDocument().removeUndoableEditListener(undoListener);
    text.setText(entry.getEntryText());
    text.getDocument().addUndoableEditListener(undoListener);

    undoManager = new UndoManager();
    updateButtons();

    updatePhoto(entry);

  }

  private void updatePhoto(EntryInterface entry)
  {
    Image photo = null;
    try
    {
      EntryPhotoData photoData = new EntryPhotoData(entry,getPhotosDirectory());
      photo = photoData.getPhotoScaledToHeight(300);
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

  private EntryInterface getCurrentEntry()
  {
    EntryInterface entry = (EntryInterface)(list.getSelectedValue());
    if (entry == null)
    {
      entry = new NullEntry();
    }
    return entry;    
  }

  protected void insertEntry(Entry entry)
  {
    list.clearSelection();
    EntryDataModel entries = (EntryDataModel)list.getModel();
    entries.addElement(entry);
  }

  private void saveEntry(EntryInterface entry)
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

  public Observer getPhotoObserver()
  {
    return new Observer(){

      @Override
      public void update(Observable o,
          Object arg)
      {
        if (arg instanceof File)
        {
          File file = (File)arg;
          try
          {
            updateCurrentEntryPhoto(file);
          }
          catch (IOException e)
          {
            JOptionPane.showMessageDialog(DayOnePanel.this, 
                "Could not import photo.");
          }
        }
        if (arg instanceof Image)
        {
          Image image = (Image)arg;
          try
          {
            updateCurrentEntryPhoto(image);
          }
          catch (IOException e)
          {
            JOptionPane.showMessageDialog(DayOnePanel.this, 
                "Could not import photo.");
          }
        }
      }   
    };
  }
}
