/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.workspace.infrastructure.openshift.project;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import io.fabric8.kubernetes.api.model.DoneableServiceAccount;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.openshift.api.model.DoneableProjectRequest;
import io.fabric8.openshift.api.model.Project;
import io.fabric8.openshift.api.model.ProjectRequestFluent.MetadataNested;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.dsl.ProjectRequestOperation;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesConfigsMaps;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesDeployments;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesIngresses;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesPersistentVolumeClaims;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesSecrets;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesServices;
import org.eclipse.che.workspace.infrastructure.openshift.OpenShiftClientFactory;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests {@link OpenShiftProject}
 *
 * @author Sergii Leshchenko
 */
@Listeners(MockitoTestNGListener.class)
public class OpenShiftProjectTest {

  public static final String PROJECT_NAME = "testProject";
  public static final String WORKSPACE_ID = "workspace123";

  @Mock private KubernetesDeployments deployments;
  @Mock private KubernetesServices services;
  @Mock private OpenShiftRoutes routes;
  @Mock private KubernetesPersistentVolumeClaims pvcs;
  @Mock private KubernetesIngresses ingresses;
  @Mock private KubernetesSecrets secrets;
  @Mock private KubernetesConfigsMaps configsMaps;
  @Mock private OpenShiftClientFactory clientFactory;
  @Mock private OpenShiftClient openShiftClient;
  @Mock private KubernetesClient kubernetesClient;
  @Mock private Resource<ServiceAccount, DoneableServiceAccount> serviceAccountResource;

  private OpenShiftProject openShiftProject;

  @BeforeMethod
  public void setUp() throws Exception {
    lenient().when(clientFactory.create(anyString())).thenReturn(kubernetesClient);
    lenient().when(clientFactory.createOC()).thenReturn(openShiftClient);
    lenient().when(clientFactory.createOC(anyString())).thenReturn(openShiftClient);
    lenient().when(openShiftClient.adapt(OpenShiftClient.class)).thenReturn(openShiftClient);

    final MixedOperation mixedOperation = mock(MixedOperation.class);
    final NonNamespaceOperation namespaceOperation = mock(NonNamespaceOperation.class);
    lenient().doReturn(mixedOperation).when(kubernetesClient).serviceAccounts();
    lenient().when(mixedOperation.inNamespace(anyString())).thenReturn(namespaceOperation);
    lenient().when(namespaceOperation.withName(anyString())).thenReturn(serviceAccountResource);
    lenient().when(serviceAccountResource.get()).thenReturn(mock(ServiceAccount.class));

    openShiftProject =
        new OpenShiftProject(
            clientFactory,
            WORKSPACE_ID,
            PROJECT_NAME,
            deployments,
            services,
            routes,
            pvcs,
            ingresses,
            secrets,
            configsMaps);
  }

  @Test
  public void testOpenShiftProjectPreparingWhenProjectExists() throws Exception {
    // given
    prepareProject(PROJECT_NAME);
    OpenShiftProject openShiftProject =
        new OpenShiftProject(clientFactory, PROJECT_NAME, WORKSPACE_ID);

    // when
    openShiftProject.prepare();
  }

  @Test
  public void testOpenShiftProjectPreparingWhenProjectDoesNotExist() throws Exception {
    // given
    MetadataNested projectMetadata = prepareProjectRequest();

    Resource resource = prepareProjectResource(PROJECT_NAME);
    doThrow(new KubernetesClientException("error", 403, null)).when(resource).get();
    OpenShiftProject openShiftProject =
        new OpenShiftProject(clientFactory, PROJECT_NAME, WORKSPACE_ID);

    // when
    openShiftProject.prepare();

    // then
    verify(projectMetadata).withName(PROJECT_NAME);
  }

  @Test
  public void testOpenShiftProjectCleaningUp() throws Exception {
    // when
    openShiftProject.cleanUp();

    verify(routes).delete();
    verify(services).delete();
    verify(deployments).delete();
    verify(secrets).delete();
    verify(configsMaps).delete();
  }

  @Test
  public void testOpenShiftProjectCleaningUpIfExceptionsOccurs() throws Exception {
    doThrow(new InfrastructureException("err1.")).when(services).delete();
    doThrow(new InfrastructureException("err2.")).when(deployments).delete();

    InfrastructureException error = null;
    // when
    try {
      openShiftProject.cleanUp();

    } catch (InfrastructureException e) {
      error = e;
    }

    // then
    assertNotNull(error);
    String message = error.getMessage();
    assertEquals(message, "Error(s) occurs while cleaning up the namespace. err1. err2.");
    verify(routes).delete();
  }

  @Test
  public void testDeletesExistingProject() throws Exception {
    // given
    OpenShiftProject project = new OpenShiftProject(clientFactory, PROJECT_NAME, WORKSPACE_ID);
    Resource resource = prepareProjectResource(PROJECT_NAME);

    // when
    project.delete();

    // then
    verify(resource).delete();
  }

  @Test
  public void testDoesntFailIfDeletedProjectDoesntExist() throws Exception {
    // given
    OpenShiftProject project = new OpenShiftProject(clientFactory, PROJECT_NAME, WORKSPACE_ID);
    Resource resource = prepareProjectResource(PROJECT_NAME);
    when(resource.delete()).thenThrow(new KubernetesClientException("err", 404, null));

    // when
    project.delete();

    // then
    verify(resource).delete();
    // and no exception is thrown
  }

  @Test
  public void testDoesntFailIfDeletedProjectIsBeingDeleted() throws Exception {
    // given
    OpenShiftProject project = new OpenShiftProject(clientFactory, PROJECT_NAME, WORKSPACE_ID);
    Resource resource = prepareProjectResource(PROJECT_NAME);
    when(resource.delete()).thenThrow(new KubernetesClientException("err", 409, null));

    // when
    project.delete();

    // then
    verify(resource).delete();
    // and no exception is thrown
  }

  private MetadataNested prepareProjectRequest() {
    ProjectRequestOperation projectRequestOperation = mock(ProjectRequestOperation.class);
    DoneableProjectRequest projectRequest = mock(DoneableProjectRequest.class);
    MetadataNested metadataNested = mock(MetadataNested.class);

    doReturn(projectRequestOperation).when(openShiftClient).projectrequests();
    doReturn(projectRequest).when(projectRequestOperation).createNew();
    doReturn(metadataNested).when(projectRequest).withNewMetadata();
    doReturn(metadataNested).when(metadataNested).withName(anyString());
    doReturn(projectRequest).when(metadataNested).endMetadata();
    return metadataNested;
  }

  private Resource prepareProjectResource(String projectName) {
    Resource projectResource = mock(Resource.class);

    NonNamespaceOperation projectOperation = mock(NonNamespaceOperation.class);
    doReturn(projectResource).when(projectOperation).withName(projectName);
    doReturn(projectOperation).when(openShiftClient).projects();
    openShiftClient.projects().withName(projectName).get();
    return projectResource;
  }

  private void prepareProject(String projectName) {
    Project project = mock(Project.class);
    Resource projectResource = prepareProjectResource(projectName);
    doReturn(project).when(projectResource).get();
  }
}
