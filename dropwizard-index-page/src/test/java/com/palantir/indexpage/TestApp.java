/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.palantir.indexpage.TestApp.TestConfiguration;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Test app for {@link IndexPageBundle}.
 */
public final class TestApp extends Application<TestConfiguration> {

    @Override
    public void initialize(Bootstrap<TestConfiguration> bootstrap) {
        bootstrap.addBundle(new IndexPageBundle(ImmutableSet.of("/home")));
    }

    @Override
    public void run(TestConfiguration configuration, Environment environment) throws Exception {
        // intentionally left blank
    }

    public static final class TestConfiguration extends Configuration implements IndexPageConfigurable {

        private final String indexPagePath;

        @JsonCreator
        public TestConfiguration(@JsonProperty("indexPagePath") Optional<String> indexPagePath) {
            this.indexPagePath = indexPagePath.or("./service/web/index.html");
        }

        @Override
        public String getIndexPagePath() {
            return this.indexPagePath;
        }
    }
}
