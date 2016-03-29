/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Used to represent the index page loaded from the file system.
 */
public final class FileSystemIndexPage implements IndexPage {

    private final Map<String, String> context;
    private final DefaultMustacheFactory factory;
    private final File file;

    private Optional<String> content;
    private long lastModified;

    public FileSystemIndexPage(Map<String, String> context, File file) {
        checkNotNull(context);
        checkArgument(file != null && file.exists());

        this.context = ImmutableMap.copyOf(context);
        this.factory = new DefaultMustacheFactory();
        this.file = file;

        try {
            loadFile();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Index page cannot be found at " + file.toString(), e);
        }
    }

    @Override
    public Optional<String> getContent() {
        try {
            checkAndReloadFile();
        } catch (FileNotFoundException e) {

            // the file could be deleted during run time, so return absent in order to handle the behavior gracefully
            return Optional.absent();
        }

        return content;
    }

    private synchronized void checkAndReloadFile() throws FileNotFoundException {

        // If file has been changed, we need to reload the file and resolve the template.
        if (lastModified != file.lastModified()) {
            loadFile();
        }
    }

    private synchronized void loadFile() throws FileNotFoundException {
        Mustache mustacheCompiler =
                factory.compile(Files.newReader(file, Charsets.UTF_8), IndexPageBundle.INDEX_PAGE_NAME);
        lastModified = file.lastModified();
        StringWriter writer = new StringWriter();
        mustacheCompiler.execute(writer, context);
        content = Optional.of(writer.toString());
    }
}
