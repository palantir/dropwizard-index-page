/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;

/**
 * Tests for {@link ClasspathIndexPage}.
 */
public final class ClassPathIndexPageTests {

    private static final ImmutableMap<String, String> CONTEXT = ImmutableMap.of(IndexPageServlet.BASE_URL, "/test/");

    @Test(expected = NullPointerException.class)
    public void testCreateClasspathAssetWithInvalidContext() {
        new ClasspathIndexPage(null, Resources.getResource("service/web/index.html"));
    }


    @Test(expected = NullPointerException.class)
    public void testCreateClasspathAssetWithInvalidResource() {
        new ClasspathIndexPage(CONTEXT, null);
    }

    @Test
    public void testLoadResource() {
        ClasspathIndexPage indexPage = new ClasspathIndexPage(CONTEXT, Resources.getResource("service/web/index.html"));
        Optional<String> maybeContent1 = indexPage.getContent();
        Optional<String> maybeContent2 = indexPage.getContent();

        assertEquals(maybeContent1.get(), maybeContent2.get());
    }

    @Test
    public void testLoadResourceWithWrongPath() throws MalformedURLException {
        String wrongPath = Resources.getResource("service/web/index.html").toString().replace("index.html", "abc.html");
        URL url = new URL(wrongPath);

        try {
            new ClasspathIndexPage(CONTEXT, url);
            fail();
        } catch (RuntimeException e) {
            assertEquals("Index page cannot be found at " + url.getPath(), e.getMessage());
        }
    }
}
