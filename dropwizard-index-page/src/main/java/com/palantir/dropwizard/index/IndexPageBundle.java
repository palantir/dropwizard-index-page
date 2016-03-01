/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.dropwizard.index;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.Set;

/**
 * Applies {@link IndexPageServlet} to the application.
 */
public final class IndexPageBundle implements ConfiguredBundle<IndexPageConfigurable> {

    public static final String INDEX_PAGE_NAME = "index.html";

    private static final Set<String> DEFAULT_MAPPING = ImmutableSet.of("/");
    private static final String DEFAULT_PATH = "./service/web/index.html";

    private final String indexPagePath;
    private final ImmutableSet<String> mappings;

    /**
     * Creates a new {@link IndexPageBundle} which serves up the index page from the file system using
     * {@code ./service/web/index.html} as the default file path.
     *
     * @param mappings      the mappings for the {@link IndexPageServlet} which serves up the index page
     */
    public IndexPageBundle(Set<String> mappings) {
        this(DEFAULT_PATH, mappings);
    }

    /**
     * Creates a new {@link IndexPageBundle} which serves up the index page from the file system specified by the
     * {@code indexPagePath}.
     *
     * @param indexPagePath the path of the index page
     * @param mappings      the mappings for the {@link IndexPageServlet} which serves up the index page
     */
    public IndexPageBundle(String indexPagePath, Set<String> mappings) {
        checkArgument(!Strings.isNullOrEmpty(indexPagePath));
        checkNotNull(mappings);

        this.indexPagePath = indexPagePath;
        this.mappings = ImmutableSet.<String>builder().addAll(DEFAULT_MAPPING).addAll(mappings).build();
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // intentionally left blank
    }

    private static void addIndexPageServlet(Environment environment, String indexPagePath, Set<String> mappings) {
        environment.servlets()
                .addServlet(INDEX_PAGE_NAME, new IndexPageServlet(indexPagePath))
                .addMapping(mappings.toArray(new String[mappings.size()]));
    }

    @Override
    public void run(IndexPageConfigurable configuration, Environment environment) throws Exception {
        checkNotNull(configuration);
        checkNotNull(environment);

        String overriddenPath = indexPagePath;
        Optional<String> maybeConfiguration = configuration.getIndexPagePath();
        if (maybeConfiguration.isPresent()) {
            overriddenPath = maybeConfiguration.get();
        }

        addIndexPageServlet(environment, overriddenPath, mappings);
    }
}
