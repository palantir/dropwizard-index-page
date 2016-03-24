/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

/**
 * Used to represent the index page loaded from the classpath.
 */
public final class ClasspathIndexPage implements IndexPage {

    private final Optional<String> content;

    public ClasspathIndexPage(Map<String, String> templatedContext, URL resource) {
        checkNotNull(templatedContext);
        checkNotNull(resource);

        content = loadResource(templatedContext, resource);
    }

    @Override
    public Optional<String> getContent() {
        return content;
    }

    private static Optional<String> loadResource(Map<String, String> templateContext, URL resource) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(resource.openStream(), Charsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Index page cannot be found at " + resource.getPath(), e);
        }

        MustacheFactory factory = new DefaultMustacheFactory();
        Mustache mustacheCompiler = factory.compile(reader, IndexPageBundle.INDEX_PAGE_NAME);
        StringWriter writer = new StringWriter();
        mustacheCompiler.execute(writer, templateContext);
        return Optional.of(writer.toString());
    }
}
