package javamag.forge.myfirstplugin;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

public class JUnitPluginTest extends AbstractShellTest {
	@Deployment
	public static JavaArchive getDeployment() {
		return AbstractShellTest.getDeployment().addPackages(true,
				JUnitPlugin.class.getPackage());
	}

	@Test
	public void testCreateTest() throws Exception {
		Project p = initializeProject(PackagingType.JAR);
		JavaSource<?> classUnderTest = JavaParser.parse("package test; public class ForgeIt { public void doit(){} }");
		p.getFacet(JavaSourceFacet.class).saveJavaSource(classUnderTest);
		
		getShell().execute("jut createtest test.ForgeIt.java");
		
		Assert.assertTrue(p.getFacet(JavaSourceFacet.class).getTestJavaResource("test.ForgeItTest.java").exists());
	}

}
