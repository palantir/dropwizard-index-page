/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.github.mustachejava.MustacheException;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link FileSystemIndexPage}.
 */
public final class FileSystemIndexPageTests {

    private static final Map<String, String> CONTEXT = ImmutableMap.of(IndexPageServlet.BASE_URL, "/example/");

    @Rule
    public final TemporaryFile indexFile = new TemporaryFile()
            .suffix(".html")
            .initialize(file -> IndexPageResources.update(file, IndexPageResources.INDEX_PAGE));

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreateFileSystemAsset() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("file");
        new FileSystemIndexPage(CONTEXT, null);
    }

    @Test
    public void testCreateFileSystemWithInvalidFile() throws IOException {
        IndexPageResources.update(indexFile.get(), IndexPageResources.INDEX_PAGE_INVALID);
        FileSystemIndexPage page = new FileSystemIndexPage(CONTEXT, indexFile.get());
        thrown.expect(MustacheException.class);
        page.getContent();
    }

    @Test
    public void testCreateFileSystemWithAbsentFile() throws IOException {
        assertTrue(indexFile.get().delete());
        FileSystemIndexPage page = new FileSystemIndexPage(CONTEXT, indexFile.get());
        assertFalse(page.getContent().isPresent());
    }

    @Test
    public void testCreateFileSystemWithAbsentThenPresentFile() throws IOException {
        assertTrue(indexFile.get().delete());
        FileSystemIndexPage page = new FileSystemIndexPage(CONTEXT, indexFile.get());
        assertFalse(page.getContent().isPresent());
        IndexPageResources.update(indexFile.get(), IndexPageResources.INDEX_PAGE);
        assertTrue(page.getContent().isPresent());
    }

    @Test
    public void testFileSystemAssetWithChange() throws IOException, InterruptedException {
        FileSystemIndexPage indexPage = new FileSystemIndexPage(CONTEXT, indexFile.get());
        String content1 = indexPage.getContent().get();
        String content2 = indexPage.getContent().get();

        // template should be the same if the file is not changed
        assertEquals(content1, content2);

        // modify the file and let FileSystemAsset reload the file
        IndexPageResources.update(indexFile.get(), IndexPageResources.INDEX_PAGE_2);

        String content3 = indexPage.getContent().get();
        assertNotEquals(content1, content3);
    }
}
