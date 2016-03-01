/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.dropwizard.index;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Test app for {@link IndexPageBundle}.
 */
public final class TestApp extends Application<Configuration> {

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(new IndexPageBundle("/index.html", ImmutableSet.of("/")));
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
     // intentionally left blank
    }
}
