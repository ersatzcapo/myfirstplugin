package javamag.forge.myfirstplugin;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ DependencyFacet.class })
public class JUnitFacet extends BaseFacet {    
    public static final String JUNIT_ATTR = "JUnitFacet.JUNIT_ATTR";
    public static final Dependency JUNIT = DependencyBuilder.create("junit:junit:4.10:test");

    @Override
    public boolean install() {
        DependencyFacet dependencyFacet = getProject().getFacet(DependencyFacet.class);
        Dependency junitDep = (Dependency) getProject().getAttribute(JUNIT_ATTR);
        dependencyFacet.addDirectDependency(junitDep);
        return true;
    }

    @Override
    public boolean isInstalled() {
        DependencyFacet dependencyFacet = getProject().getFacet(DependencyFacet.class);
        Dependency junitDep = (Dependency) getProject().getAttribute(JUNIT_ATTR);
        Dependency dependency = junitDep != null ? junitDep : JUNIT;
        return dependencyFacet.hasDirectDependency(dependency);
    }
}
