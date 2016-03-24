/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Used to test reloading of the index page from file system when the page gets modified.
 */
public final class IndexPageBundleServingFromFileSystemTest {

    @Rule
    public final DropwizardAppRule<TestApp.TestConfiguration> rule =
            new DropwizardAppRule<TestApp.TestConfiguration>(TestApp.class,
                    this.getClass().getResource("/example-reload-page.yml").getPath());

    private final File file = new File("./src/test/resources/service/web/reloadIndex.html");

    private String originalContent;

    @Before
    public void init() throws IOException {
        originalContent = Resources.toString(file.toURI().toURL(), Charsets.UTF_8);
    }

    @Test
    public void testGetIndexPageWithDynamicContent() throws IOException {
        Client client = ClientBuilder.newClient();
        Response response = client.target(String.format("http://localhost:%d/example/", rule.getLocalPort()))
                .request()
                .get();
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("Hello World!"));

        String tempContent = originalContent.replace("Hello", "Cruel");

        // modify the file and let the bundle reload it
        try (FileOutputStream out = new FileOutputStream(file.getPath())) {
            out.write(tempContent.getBytes(Charsets.UTF_8));
        }

        response = client.target(String.format("http://localhost:%d/example/", rule.getLocalPort()))
                .request()
                .get();
        assertTrue(response.readEntity(String.class).contains("Cruel World!"));

        // delete the file and check if the servlet returns proper response
        boolean deleteSucceeded = file.delete();

        if (deleteSucceeded) {
            response = client.target(String.format("http://localhost:%d/example/", rule.getLocalPort()))
                    .request()
                    .get();
            assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        }
    }

    @After
    public void revertChanges() throws IOException {
        FileOutputStream out = new FileOutputStream(file.getPath());
        try {
            out.write(originalContent.getBytes(Charsets.UTF_8));
        } finally {
            out.close();
        }
    }
}
