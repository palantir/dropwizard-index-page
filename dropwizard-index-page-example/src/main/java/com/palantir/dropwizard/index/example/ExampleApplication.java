/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.dropwizard.index.example;

import com.google.common.collect.ImmutableSet;
import com.palantir.dropwizard.index.IndexPageBundle;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Example application that consumes {@link AssetsBundle} and {@link IndexPageBundle}.
 */
public final class ExampleApplication extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new ExampleApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(new IndexPageBundle(
                "service/web/index.html",
                ImmutableSet.of("/hello/*", "/goodbye/*", "/surprise/*")));
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        // intentionally left blank
    }
}

