/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.Set;

/**
 * Applies {@link IndexPageServlet} to the application.
 */
public final class IndexPageBundle implements ConfiguredBundle<IndexPageConfigurable> {

    public static final String INDEX_PAGE_NAME = "index.html";

    private static final Set<String> DEFAULT_MAPPING = ImmutableSet.of("");
    private static final String DEFAULT_PATH = "service/web/index.html";

    private final String indexPagePath;
    private final ImmutableSet<String> mappings;

    /**
     * Creates a new {@link IndexPageBundle} which serves up the index page from either the file system or the class
     * path using {@code ./service/web/index.html} as the default path.
     *
     * @param mappings
     *        the mappings for the {@link IndexPageServlet} which serves up the index page
     */
    public IndexPageBundle(Set<String> mappings) {
        this(DEFAULT_PATH, mappings);
    }

    /**
     * Creates a new {@link IndexPageBundle} which serves up the index page from either the file system or the class
     * path specified by the {@code indexPagePath}.
     *
     * @param indexPagePath
     *        the path of the index page
     * @param mappings
     *        the mappings for the {@link IndexPageServlet} which serves up the index page
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

    @Override
    public void run(IndexPageConfigurable configuration, Environment environment) throws Exception {
        checkNotNull(configuration);
        checkNotNull(environment);

        String overriddenPath = indexPagePath;
        String maybeIndexPagePath = configuration.getIndexPagePath();
        if (!Strings.isNullOrEmpty(maybeIndexPagePath)) {
            overriddenPath = maybeIndexPagePath;
        }

        addIndexPageServlet(configuration, environment, overriddenPath, mappings);
    }

    private static void addIndexPageServlet(
            IndexPageConfigurable configuration, Environment environment, String indexPagePath, Set<String> mappings) {
        String contextPath = getContextPath(configuration);
        environment.servlets()
                .addServlet(INDEX_PAGE_NAME, new IndexPageServlet(contextPath, indexPagePath))
                .addMapping(mappings.toArray(new String[mappings.size()]));
    }

    private static String getContextPath(IndexPageConfigurable configuration) {
        String contextPath = "/";

        if (configuration instanceof Configuration) {
            Configuration dwConfig = (Configuration) configuration;
            ServerFactory serverFactory = dwConfig.getServerFactory();
            if (serverFactory instanceof DefaultServerFactory) {
                DefaultServerFactory defaultServerFactory = (DefaultServerFactory) serverFactory;
                contextPath = defaultServerFactory.getApplicationContextPath();
            } else if (serverFactory instanceof SimpleServerFactory) {
                SimpleServerFactory simpleServerFactory = (SimpleServerFactory) serverFactory;
                contextPath = simpleServerFactory.getApplicationContextPath();
            }
        }

        return contextPath;
    }
}
