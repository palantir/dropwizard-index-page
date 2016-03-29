/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link FileSystemIndexPage}.
 */
public final class FileSystemIndexPageTests {

    private static final Map<String, String> CONTEXT = ImmutableMap.of(IndexPageServlet.BASE_URL, "/example/");

    private final File file = new File("./src/test/resources/service/web/reloadIndex.html");

    private String originalContent;

    @Before
    public void init() throws IOException {
        originalContent = Resources.toString(file.toURI().toURL(), Charsets.UTF_8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFileSystemAsset() {
        new FileSystemIndexPage(CONTEXT, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFileSystemWithInvalidFilePath() {
        new FileSystemIndexPage(CONTEXT, new File("./src/test/resources/abc"));
    }

    @Test
    public void testFileSystemAssetWithChange() throws IOException {
        FileSystemIndexPage indexPage = new FileSystemIndexPage(CONTEXT, file);
        Optional<String> content1 = indexPage.getContent();
        Optional<String> content2 = indexPage.getContent();

        // template should be the same if the file is not changed
        assertEquals(content1.get(), content2.get());

        String tempContent = originalContent.replace("Hello", "Cruel");

        // modify the file and let FileSystemAsset reload the file
        try (FileOutputStream out = new FileOutputStream(file.getPath())) {
            out.write(tempContent.getBytes(Charsets.UTF_8));
        }

        Optional<String> content3 = indexPage.getContent();
        assertNotEquals(content1.get(), content3.get());
    }

    @After
    public void revertChanges() throws IOException {
        FileOutputStream out = new FileOutputStream(file.getPath());
        try {
            out.write(originalContent.getBytes(Charsets.UTF_8));
        } finally {
            out.close();
        }
    }
}
