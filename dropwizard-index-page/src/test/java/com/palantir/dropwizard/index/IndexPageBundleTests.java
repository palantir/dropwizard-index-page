/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.dropwizard.index;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dropwizard.Configuration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * Tests for {@link IndexPageBundle}.
 */
public final class IndexPageBundleTests {

    @ClassRule
    public static final DropwizardAppRule<Configuration> RULE =
            new DropwizardAppRule<Configuration>(TestApp.class);

    @Test(expected = IllegalArgumentException.class)
    public void testNewWithEmptyIndexPage() {
        new IndexPageBundle("", ImmutableSet.<String>of());
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
    public void testNewWithNullIndexPage() {
        new IndexPageBundle(null, ImmutableSet.<String>of());
    }

    @Test(expected = NullPointerException.class)
    public void testNewWithInvalidMappings() {
        new IndexPageBundle("index.html", null);
    }

    @Test(expected = NullPointerException.class)
    public void testRunWithInvalidEnvironment() throws Exception {
        IndexPageBundle bundle = new IndexPageBundle(ImmutableSet.of("/views/*"));
        bundle.run(null);
    }
}
