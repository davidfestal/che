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
package org.eclipse.che.plugin.ceylon.shared;

/**
 * The utility class for constants.
 *
 * @author Valeriy Svydenko
 */
public final class ProjectAttributes {
    public static String LANGUAGE             = "language";
    public static String CEYLON_ID            = "ceylon";
    public static String CEYLON_NAME          = "Ceylon";
    public static String CEYLON_CATEGORY      = "Ceylon";
    public static String CEYLON_EXT           = "ceylon";

    private ProjectAttributes() {
        throw new UnsupportedOperationException("Unused constructor.");
    }

}
