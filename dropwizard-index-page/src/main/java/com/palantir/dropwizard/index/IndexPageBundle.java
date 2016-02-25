/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.dropwizard.index;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.Set;

/**
 * Applies {@link IndexPageServlet} to the application.
 */
public final class IndexPageBundle implements Bundle {

    public static final String INDEX_PAGE_NAME = "index.html";

    private final ImmutableSet<String> mappings;
    private final String indexPagePath;

    public IndexPageBundle(Set<String> mappings) {
        this(INDEX_PAGE_NAME, mappings);
    }

    public IndexPageBundle(String indexPagePath, Set<String> mappings) {
        checkArgument(!Strings.isNullOrEmpty(indexPagePath));
        checkNotNull(mappings);

        this.mappings = ImmutableSet.<String>builder().add("/").addAll(mappings).build();
        this.indexPagePath = trimSlash(indexPagePath);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // intentionally left blank
    }

    @Override
    public void run(Environment environment) {
        checkNotNull(environment);

        addIndexPageServlet(environment, indexPagePath, mappings);
    }

    private static void addIndexPageServlet(Environment environment, String indexPagePath, Set<String> mappings) {
        environment.servlets()
                .addServlet(INDEX_PAGE_NAME, new IndexPageServlet(indexPagePath))
                .addMapping(mappings.toArray(new String[mappings.size()]));
    }

    private static String trimSlash(String input) {
        if (input.startsWith("/")) {
            return input.substring(1);
        }

        return input;
    }
}
