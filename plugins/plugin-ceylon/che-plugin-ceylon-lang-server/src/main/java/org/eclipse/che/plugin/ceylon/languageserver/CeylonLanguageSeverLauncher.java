/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.ceylon.languageserver;

import org.eclipse.che.api.languageserver.exception.LanguageServerException;
import org.eclipse.che.api.languageserver.launcher.LanguageServerLauncherTemplate;
import org.eclipse.che.api.languageserver.shared.model.LanguageDescription;
import org.eclipse.che.plugin.ceylon.shared.ProjectAttributes;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static java.util.Arrays.asList;

/**
 * Launches language server for Ceylon
 */
@Singleton
public class CeylonLanguageSeverLauncher extends LanguageServerLauncherTemplate {

    private static final String[] EXTENSIONS = new String[]{ProjectAttributes.CEYLON_EXT};
    private static final String[] MIME_TYPES = new String[]{"text/x-ceylon"};
    private static final LanguageDescription description;

    private final Path launchScript;

    public CeylonLanguageSeverLauncher() {
        launchScript = Paths.get(System.getenv("HOME"), "che/ls-ceylon/launch.sh");
    }

    @Override
    public LanguageDescription getLanguageDescription() {
        return description;
    }

    @Override
    public boolean isAbleToLaunch() {
        return launchScript.toFile().exists();
    }

    @Override
    protected Process startLanguageServerProcess(String projectPath) throws LanguageServerException {
        ProcessBuilder processBuilder = new ProcessBuilder(launchScript.toString());
        processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);

        try {
            return processBuilder.start();
        } catch (IOException e) {
            throw new LanguageServerException("Can't start Ceylon language server", e);
        }
    }

    @Override
    protected LanguageServer connectToLanguageServer(final Process languageServerProcess, LanguageClient client) {
        Launcher<LanguageServer> launcher = Launcher.createLauncher(client, LanguageServer.class,
                                                                    languageServerProcess.getInputStream(),
                                                                    languageServerProcess.getOutputStream());
        launcher.startListening();
        return launcher.getRemoteProxy();
    }

    static {
        description = new LanguageDescription();
        description.setFileExtensions(asList(EXTENSIONS));
        description.setLanguageId(ProjectAttributes.CEYLON_ID);
        description.setMimeTypes(Arrays.asList(MIME_TYPES));
    }
}
