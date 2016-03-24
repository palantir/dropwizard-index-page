/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static org.junit.Assert.assertEquals;

import io.dropwizard.testing.junit.DropwizardAppRule;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;

/**
 * Using default index page path to test the {@link IndexPageBundle}.
 */
public final class IndexPageBundleServingFromClasspathTest {
    @Rule
    public final DropwizardAppRule<TestApp.TestConfiguration> rule =
            new DropwizardAppRule<TestApp.TestConfiguration>(TestApp.class,
                    TestApp.class.getClassLoader().getResource("example-default.yml").getPath());

    @Test
    public void testGetIndexPage() {
        // the app will not be able to find the page using the default path
        Client client = ClientBuilder.newClient();
        Response response = client.target(String.format("http://localhost:%d/example/", rule.getLocalPort()))
                .request()
                .get();
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }
}
