/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.dropwizard.index;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.google.common.collect.ImmutableSet;
import com.palantir.dropwizard.index.IndexPageBundleTests.BundleConfiguredPath;
import com.palantir.dropwizard.index.IndexPageBundleTests.BundleDefaultPath;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.DropwizardAppRule;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests for {@link IndexPageBundle}.
 */
@RunWith(Suite.class)
@SuiteClasses({
        BundleConfiguredPath.class,
        BundleDefaultPath.class
        })
public final class IndexPageBundleTests {

    public static final class BundleConfiguredPath {
        @Rule
        public final DropwizardAppRule<TestApp.TestConfiguration> rule =
                new DropwizardAppRule<TestApp.TestConfiguration>(TestApp.class,
                        TestApp.class.getClassLoader().getResource("example.yml").getPath());

        @Test(expected = NullPointerException.class)
        public void testNewWithInvalidMappings() {
            new IndexPageBundle(null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testNewWithEmptyPath() {
            new IndexPageBundle("", ImmutableSet.<String>of());
        }

        @Test(expected = IllegalArgumentException.class)
        public void testNewWithNullPath() {
            new IndexPageBundle(null, ImmutableSet.<String>of());
        }

        @Test(expected = NullPointerException.class)
        public void testRunWithInvalidEnvironment() throws Exception {
            IndexPageBundle bundle = new IndexPageBundle(ImmutableSet.of("/views/*"));
            bundle.run(mock(IndexPageConfigurable.class), null);
        }

        @Test(expected = NullPointerException.class)
        public void testRunWithInvalidConfiguration() throws Exception {
            IndexPageBundle bundle = new IndexPageBundle(ImmutableSet.of("/views/*"));
            bundle.run(null, mock(Environment.class));
        }

        @Test
        public void testGetIndexPage() {
            Client client = ClientBuilder.newClient();
            Response response = client.target(String.format("http://localhost:%d/example/", rule.getLocalPort()))
                    .request()
                    .get();
            assertEquals(HttpStatus.OK_200, response.getStatus());
        }
    }

    public static final class BundleDefaultPath {
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
            assertEquals("Index page file not found.", response.getStatusInfo().getReasonPhrase());
        }
    }
}
