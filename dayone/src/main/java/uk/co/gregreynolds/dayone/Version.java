package uk.co.gregreynolds.dayone;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Version {
    String buildNumber = "N/A";
    String buildTime = "N/A";
    String pomVersion = "N/A";
    
    public Version()
    {
      super();
      
      Properties versionProperties = new Properties();
      String fileName = "uk/co/gregreynolds/dayone/Version.properties";
      
      InputStream properties = this.getClass().getClassLoader().getResourceAsStream(fileName);
      
      if (properties != null)
      {
        try
        {
          versionProperties.load(properties);
          buildNumber = versionProperties.getProperty("build_number", "N/A");
          pomVersion = versionProperties.getProperty("pomversion", "N/A");
          buildTime = versionProperties.getProperty("build_time", "N/A");

        }
        catch (IOException e)
        {
          // do nothing
        }
      }
    }

    public String getBuildNumber()
    {
      return buildNumber;
    }

    public String getBuildTime()
    {
      return buildTime;
    }

    public String getPomVersion()
    {
      return pomVersion;
    }
    
    

}