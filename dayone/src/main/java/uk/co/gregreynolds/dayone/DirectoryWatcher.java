package uk.co.gregreynolds.dayone;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatcher
{
  WatchService watcher;

  private class DirectoryObservable extends Observable
  {
    public void update(File file)
    {
      setChanged();
      notifyObservers(file);
    }    
  };

  Map<Path,DirectoryObservable> observableMap = 
      new HashMap<Path,DirectoryObservable>();
  Map<Path,WatchKey> watchKeyMap =
      new HashMap<Path,WatchKey>();

  public DirectoryWatcher() throws IOException
  {
    watcher = FileSystems.getDefault().newWatchService();
  }

  public void watchDirectory(File directory, Observer observer) throws IOException
  {
    Path dirPath = directory.toPath();
    WatchKey key = watchKeyMap.get(dirPath);
    
    if (key == null)
    {
      key = dirPath.register(watcher, 
          ENTRY_CREATE,
          ENTRY_DELETE,
          ENTRY_MODIFY);
      watchKeyMap.put(dirPath, key);
    }
    DirectoryObservable observable = observableMap.get(dirPath);
    if (observable == null)
    {
      observable = new DirectoryObservable();
      observableMap.put(dirPath, observable);
    }
    observable.addObserver(observer);
  }
  
  public void stopWatchingDirectory(File directory)
  {
    Path dirPath = directory.toPath();
    DirectoryObservable observable = observableMap.get(dirPath);
    if (observable != null)
    {
      observable.deleteObservers();
      observableMap.remove(dirPath);
    }
    WatchKey key = watchKeyMap.get(dirPath);
    if (key != null)
    {
      watchKeyMap.remove(key);
      key.cancel();
    }    
  }

  private void watchLoop()
  {
    for (;;)
    {
      WatchKey key;
      try
      {
        key = watcher.take();
      }
      catch (InterruptedException e)
      {
        return;
      }
      processWatchEvent(key);
      key.reset();
    }
  }

  private void processWatchEvent(WatchKey key)
  {
    for (WatchEvent<?> event: key.pollEvents()) {
      WatchEvent.Kind<?> kind = event.kind();

      WatchEvent<Path> ev = (WatchEvent<Path>)event;
      Path dir = (Path)key.watchable();
      Path fullPath = dir.resolve(ev.context());
      DirectoryObservable observable = observableMap.get(dir);
      if (observable != null)
      {
        observable.update(fullPath.toFile());
      }
    }
  }
  
  public void startWatching()
  {
    Thread thread = new Thread(new Runnable()
    {
      
      @Override
      public void run()
      {
        watchLoop();
      }
    });
    thread.start();
  }
}