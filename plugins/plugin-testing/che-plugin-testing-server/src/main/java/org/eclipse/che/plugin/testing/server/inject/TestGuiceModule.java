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
package org.eclipse.che.plugin.testing.server.inject;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

import org.eclipse.che.plugin.testing.server.TestingService;
import org.eclipse.che.plugin.testing.server.framework.TestRunner;
import org.eclipse.che.inject.DynaModule;

import com.google.inject.AbstractModule;

/**
 * @author Mirage Abeysekara
 */
@DynaModule
public class TestGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        newSetBinder(binder(), TestRunner.class);
        bind(TestingService.class);
    }
}
