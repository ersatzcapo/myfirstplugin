package javamag.forge.myfirstplugin;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Test;

public class JUnitPluginTest extends AbstractShellTest
{
    
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractShellTest.getDeployment().addPackages(true, JUnitPlugin.class.getPackage());
   }

   @Test
   public void testSetup() throws Exception
   {
      queueInputLines("\n","\n");
      getShell().execute("new-project --named test");  
      getShell().execute("jut setup");
   }
   
   @After
   public void afterTest() throws IOException {
       super.afterTest();
       FileUtils.deleteDirectory(new File("test"));
   }
}
