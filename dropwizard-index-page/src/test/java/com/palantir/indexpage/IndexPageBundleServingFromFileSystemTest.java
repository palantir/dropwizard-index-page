/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.io.IOException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * Used to test reloading of the index page from file system when the page gets modified.
 */
public final class IndexPageBundleServingFromFileSystemTest {

    private final TemporaryFile indexFile = new TemporaryFile()
            .suffix(".html")
            .initialize(file -> IndexPageResources.update(file, IndexPageResources.INDEX_PAGE));

    private final DropwizardAppRule<TestApp.TestConfiguration> rule =
            new DropwizardAppRule<>(TestApp.class,
                    this.getClass().getResource("/example-default.yml").getPath(),
                    ConfigOverride.config("indexPagePath",
                            () -> indexFile.get().getAbsolutePath()));

    @Rule
    public final RuleChain rules = RuleChain.outerRule(indexFile)
            .around(rule);

    @Test
    public void testGetIndexPageWithDynamicContent() throws IOException {
        Client client = ClientBuilder.newClient();
        Response response = client.target(String.format("http://localhost:%d/example/", rule.getLocalPort()))
                .request()
                .get();
        assertEquals(HttpStatus.OK_200, response.getStatus());
        String responseContent = response.readEntity(String.class);
        assertTrue(responseContent.contains("Hello World!"));
        assertTrue(responseContent.contains("<base href=\"/example/\">"));


        // modify the file and let the bundle reload it
        IndexPageResources.update(indexFile.get(), IndexPageResources.INDEX_PAGE_2);

        response = client.target(String.format("http://localhost:%d/example/", rule.getLocalPort()))
                .request()
                .get();
        assertTrue(response.readEntity(String.class).contains("Cruel World!"));

        // delete the file and check if the servlet returns proper response
        assertTrue(indexFile.get().delete());
        response = client.target(String.format("http://localhost:%d/example/", rule.getLocalPort()))
                .request()
                .get();
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }
}
