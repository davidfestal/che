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
package org.eclipse.che.plugin.ceylon.projecttype;

import com.google.inject.Inject;

import org.eclipse.che.api.project.server.type.ProjectTypeDef;

import static org.eclipse.che.plugin.ceylon.shared.ProjectAttributes.LANGUAGE;
import static org.eclipse.che.plugin.ceylon.shared.ProjectAttributes.CEYLON_ID;
import static org.eclipse.che.plugin.ceylon.shared.ProjectAttributes.CEYLON_NAME;


/**
 * Python  project type.
 *
 * @author David Festal
 */
public class CeylonProjectType extends ProjectTypeDef {
    @Inject
    public CeylonProjectType() {
        super(CEYLON_ID, CEYLON_NAME, true, false, true);
        addConstantDefinition(LANGUAGE, LANGUAGE, CEYLON_ID);
    }

}
