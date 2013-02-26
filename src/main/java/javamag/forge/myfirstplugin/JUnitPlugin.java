package javamag.forge.myfirstplugin;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.template.CompiledTemplateResource;

/**
 *
 */
@Alias("jut")
@RequiresFacet({ JUnitFacet.class })
public class JUnitPlugin implements Plugin {
	@Inject
	private ShellPrompt prompt;
	
    @Inject
    private Event<InstallFacets> installFaEvent;
	
	@Inject
	private Project project;

	@Inject
	@Current
	private JavaResource resource;

	private CompiledTemplateResource unitTestTemplate;

	@Inject
	public JUnitPlugin(TemplateCompiler compiler) {
		unitTestTemplate = compiler.compileResource(JUnitPlugin.class
				.getResourceAsStream("/templates/UnitTest.java.jv"));
	}
	
    @SetupCommand
    public void setup(PipeOut out) throws Exception {
        if (!project.hasFacet(JUnitFacet.class)) {
            // ask for junit version
            DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
            List<Dependency> versions = dependencyFacet.resolveAvailableVersions("junit:junit:[4.10,):test");
            Dependency dependency = prompt.promptChoiceTyped("Select version: ", versions, JUnitFacet.JUNIT);
            project.setAttribute(JUnitFacet.JUNIT_ATTR, dependency);

            installFaEvent.fire(new InstallFacets(JUnitFacet.class));
        } else {
            ShellMessages.info(out, "is installed");
        }
    }

	@Command
	public void createTest(PipeOut out,	@Option(required = false) JavaResource clazzUnderTest)
			throws FileNotFoundException {
		if (clazzUnderTest == null) {
			clazzUnderTest = resource;
		}
		JavaSource<?> javaSource = clazzUnderTest.getJavaSource();
		
		HashMap<Object, Object> context = new HashMap<Object, Object>();
		context.put("package", javaSource.getPackage());
		context.put("classname", javaSource.getName());
		String renderedContent = unitTestTemplate.render(context);
		JavaSource<?> javaTestSource = JavaParser.parse(renderedContent);
		
		JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
		javaSourceFacet.saveTestJavaSource(javaTestSource);
	}
	
	@Command
	public void createTestMethod(PipeOut out,
			@Option(required = false) JavaResource clazzUnderTest, @Option(name="all", flagOnly=true) boolean all,
			@Option(name = "methodName", completer = PropertyCompleter.class) String methodName)
			throws FileNotFoundException {
		JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
		if (clazzUnderTest == null) {
			clazzUnderTest = resource;
		}
		
		JavaClass javaSource = (JavaClass) clazzUnderTest.getJavaSource();
		JavaResource testJavaResource = javaSourceFacet.getTestJavaResource(javaSource.getQualifiedName() + "Test");
		JavaClass testSource = (JavaClass) testJavaResource.getJavaSource();
		
		if (all){
			createAllTestMethods(javaSource, testSource);
		} else if (methodName != null) {
			createMethod(testSource, methodName);
		}
		javaSourceFacet.saveTestJavaSource(testSource);
	}

	private void createAllTestMethods(JavaClass javaSource, JavaClass testSource) {
		List<Method<JavaClass>> methods = javaSource.getMethods();
		for (Method<JavaClass> method : methods) {
			if (method.isPublic()) {
				String sourceMethodName = method.getName();
				createMethod(testSource, sourceMethodName);
			}
		}
	}

	private void createMethod(JavaClass testSource,	String sourceMethodName) {
		String methodName = "test" + sourceMethodName.substring(0, 1).toUpperCase() + sourceMethodName.substring(1);
		if (!testSource.hasMethodSignature(methodName)) {
			testSource.addMethod("@Test public void " + methodName + "() { fail(\"Not yet implemented\"); }");
		}
	}
}
