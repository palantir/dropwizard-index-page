/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.palantir.indexpage.IndexPageBundle;
import com.palantir.indexpage.IndexPageConfigurable;
import com.palantir.indexpage.example.ExampleApplication.ExampleConfig;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Example application that consumes {@link IndexPageBundle}.
 */
public final class ExampleApplication extends Application<ExampleConfig> {

    public static void main(String[] args) throws Exception {
        new ExampleApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<ExampleConfig> bootstrap) {
        bootstrap.addBundle(new IndexPageBundle(ImmutableSet.of("/hello/*", "/goodbye/*", "/surprise/*")));
    }

    @Override
    public void run(ExampleConfig configuration, Environment environment) throws Exception {
        // intentionally left blank
    }

    static class ExampleConfig extends Configuration implements IndexPageConfigurable {

        private final String indexPagePath;

        @JsonCreator
        ExampleConfig(@JsonProperty("indexPagePath") Optional<String> indexPagePath) {
            this.indexPagePath = indexPagePath.or("./service/web/index.html");
        }

        @Override
        public String getIndexPagePath() {
            return this.indexPagePath;
        }
    }
}

