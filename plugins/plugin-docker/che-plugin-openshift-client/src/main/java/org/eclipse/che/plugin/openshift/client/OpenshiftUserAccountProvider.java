/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.openshift.client;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.keycloak.token.provider.service.KeycloakTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;

@Singleton
public class OpenshiftUserAccountProvider {
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftUserAccountProvider.class);
    
    @Inject
    private KeycloakTokenProvider keycloakTokenProvider;

    private String workspacesMasterURL;
    private String openShiftCheProjectName;

    @Inject
    public OpenshiftUserAccountProvider(@Nullable @Named("che.openshift.workspaces.master_url") String workspacesMasterURL,
                                        @Named("che.openshift.project") String openShiftCheProjectName) {
        this.workspacesMasterURL = workspacesMasterURL;
        this.openShiftCheProjectName = openShiftCheProjectName;
    }

    public Config getUserNameSpaceOpenshiftConfig() {
        if (workspacesMasterURL == null) {
            LOG.info("Connecting to Openshift with default config");
            // workspaces are on the same cluster / namespace as the workspace master.
            return new OpenShiftConfigBuilder().build();
        } else {
            String osoToken = getOpenShiftTokenForUser();
            if (osoToken == null) {
                LOG.info("OSO token is null => Connecting to Openshift with default config");
                return new OpenShiftConfigBuilder().build();
            }
            
//            LOG.info("Connecting to Openshift on: " + workspacesMasterURL);
            // workspaces are on a different cluster / namespace as the workspace master.
            // In the case, the namespace is per-user.
            return new ConfigBuilder()
                .withMasterUrl(workspacesMasterURL) //"http://console.starter-us-east-2.openshift.com"
                .withOauthToken(getOpenShiftTokenForUser())
                .withNamespace(getOpenShiftCheProjectNameForUser())
                .build();
        }
  }

  private String getOpenShiftTokenForUser() {
      String keycloakToken = EnvironmentContext.getCurrent().getSubject().getToken();
      if (keycloakToken == null) {
          return null;
      }
      try {
        return keycloakTokenProvider.obtainOsoToken("Bearer " + keycloakToken);
    } catch (ServerException | UnauthorizedException | ForbiddenException | NotFoundException | ConflictException | BadRequestException
        | IOException e) {
        LOG.error("Cound not retrieve OSO token from Keycloak token: " + keycloakToken, e);
        return "zKlZDgTntjmwnMmzHzIjR86JwGn5CUk82snB0mfllOY";
    }
  }
  
  public String getOpenShiftCheProjectNameForUser() {
      if (workspacesMasterURL == null) {
          return openShiftCheProjectName;
      } else {
          String userName = EnvironmentContext.getCurrent().getSubject().getUserName();
          return userName + "-che";
      }
  }
}
