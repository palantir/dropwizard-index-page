/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to represent the index page loaded from the file system.
 */
public final class FileSystemIndexPage implements IndexPage {
    private static final Logger log = LoggerFactory.getLogger(FileSystemIndexPage.class);

    private final Map<String, String> context;
    private final DefaultMustacheFactory factory;
    private final File file;

    private Optional<IndexPageState> state = Optional.absent();

    public FileSystemIndexPage(Map<String, String> context, File file) {
        checkNotNull(context, "context");
        checkNotNull(file, "file");

        this.context = ImmutableMap.copyOf(context);
        this.factory = new DefaultMustacheFactory();
        this.file = file;

        try {
            refresh();
        } catch (Exception e) {
            log.error("Failed to preload index page {}", file, e);
        }
    }

    @Override
    public Optional<String> getContent() {
        refresh();
        return state.transform(input -> input.content);
    }

    private synchronized void refresh() {
        if (!state.isPresent() || state.get().lastModified != file.lastModified()) {
            log.debug("Reloading index page {}", file.lastModified());
            state = loadFile();
        }
    }

    private Optional<IndexPageState> loadFile() {
        // Guarantee that the modification time did not change between start and end of compilation
        for (;;) {
            long preLastModified = file.lastModified();
            Optional<String> optContent = compileContent();
            if (preLastModified == file.lastModified()) {
                return optContent.transform(content -> new IndexPageState(content, preLastModified));
            }
        }
    }

    private Optional<String> compileContent() {
        StringWriter writer = new StringWriter();

        try (Reader reader = Files.newReader(file, Charsets.UTF_8)) {
            Mustache mustacheCompiler = factory.compile(reader, IndexPageBundle.INDEX_PAGE_NAME);
            mustacheCompiler.execute(writer, context);
        } catch (FileNotFoundException e) {
            // the file could be deleted during run time, so return absent in order to handle the behavior gracefully
            log.warn("Index page {} does not exist, deferring load", file);
            log.debug("Index page {} does not exist, deferring load", file, e);
            return Optional.absent();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load index page " + file, e);
        }

        return Optional.of(writer.toString());
    }


    private static final class IndexPageState {
        private final String content;
        private final long lastModified;

        private IndexPageState(String content, long lastModified) {
            this.content = content;
            this.lastModified = lastModified;
        }
    }
}
