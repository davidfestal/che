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
package org.eclipse.che.plugin.ceylon.ide.action;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.dialogs.DialogFactory;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.newresource.AbstractNewResourceAction;
import org.eclipse.che.plugin.ceylon.ide.CeylonLocalizationConstant;
import org.eclipse.che.plugin.ceylon.ide.CeylonResources;

import static org.eclipse.che.plugin.ceylon.shared.ProjectAttributes.CEYLON_EXT;

/**
 * Action to create new Python source file.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class CreateCeylonFileAction extends AbstractNewResourceAction {

    @Inject
    public CreateCeylonFileAction(CeylonLocalizationConstant localizationConstant,
                                  CeylonResources ceylonResources,
                                  DialogFactory dialogFactory,
                                  CoreLocalizationConstant coreLocalizationConstant,
                                  EventBus eventBus,
                                  AppContext appContext,
                                  NotificationManager notificationManager,
                                  Provider<EditorAgent> editorAgentProvider) {
        super(localizationConstant.createCeylonFileActionTitle(),
              localizationConstant.createCeylonFileActionDescription(),
              ceylonResources.ceylonFile(), dialogFactory, coreLocalizationConstant, eventBus, appContext, notificationManager, editorAgentProvider);
    }

    @Override
    protected String getExtension() {
        return CEYLON_EXT;
    }

    @Override
    protected String getDefaultContent() {
        return "";
    }

}
