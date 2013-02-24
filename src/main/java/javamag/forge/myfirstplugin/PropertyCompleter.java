/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package javamag.forge.myfirstplugin;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

public class PropertyCompleter extends SimpleTokenCompleter {
	private final Shell shell;

	@Inject
	public PropertyCompleter(Shell shell) {
		this.shell = shell;
	}

	@Override
	public List<String> getCompletionTokens() {
		final List<String> tokens = new ArrayList<String>();
		try {
			final JavaClass currentResource = (JavaClass) ((JavaResource) shell
					.getCurrentResource()).getJavaSource();

			for (Method<JavaClass> method : currentResource.getMethods()) {
				tokens.add(method.getName());
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return tokens;
	}
}
