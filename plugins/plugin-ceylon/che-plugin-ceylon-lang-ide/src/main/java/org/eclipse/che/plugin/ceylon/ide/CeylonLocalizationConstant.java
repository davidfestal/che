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
package org.eclipse.che.plugin.ceylon.ide;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants defined in resource bundle:
 * 'CeylonLocalizationConstant.properties'.
 *
 * @author David Festal
 */
public interface CeylonLocalizationConstant extends Messages {
    @Key("ceylon.action.create.file.title")
    String createCeylonFileActionTitle();

    @Key("ceylon.action.create.file.description")
    String createCeylonFileActionDescription();

}
