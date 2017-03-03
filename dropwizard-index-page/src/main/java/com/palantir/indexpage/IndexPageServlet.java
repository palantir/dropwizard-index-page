/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.servlet.DefaultServlet;

/**
 * Used to serve the index page and set disable cache headers in the HTTP response.
 */
public final class IndexPageServlet extends DefaultServlet {

    public static final String BASE_URL = "baseUrl";

    private static final long serialVersionUID = 1L;
    private static final String CACHE_CONTROL = "no-cache, no-store, max-age=0, must-revalidate";

    @SuppressFBWarnings("SE_BAD_FIELD")
    private final IndexPage indexPage;

    /**
     * Creates a new servlet that will try to serve the index page from the local file path. If the page doesn't exist
     * on the disk, the servlet will try to load it from the classpath.
     *
     * @param contextPath the context path used as the base url for the index page
     * @param indexPagePath
     *        the path to the index path and it can either be the classpath or the local file path
     * @throws IllegalArgumentException if the index page is not found
     */
    public IndexPageServlet(String contextPath, String indexPagePath) {
        checkArgument(contextPath != null);
        checkArgument(!Strings.isNullOrEmpty(indexPagePath));

        String slashedContextPath = contextPath;
        if (!contextPath.endsWith("/")) {
            slashedContextPath += "/";
        }

        ImmutableMap<String, String> templateContext = ImmutableMap.of(BASE_URL, slashedContextPath);
        Optional<URL> indexPageResource = tryGetResource(indexPagePath);
        if (indexPageResource.isPresent()) {
            indexPage = new ClasspathIndexPage(templateContext, indexPageResource.get());
        } else {
            indexPage = new FileSystemIndexPage(templateContext, new File(indexPagePath));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Optional<String> maybeContent = indexPage.getContent();
        if (!maybeContent.isPresent()) {
            response.sendError(HttpStatus.NOT_FOUND_404, "Index page file not found.");
            return;
        }

        response.setHeader(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.HTML_UTF_8.toString());
        try (PrintWriter writer = response.getWriter()) {
            writer.write(maybeContent.get());
        }
    }

    private static Optional<URL> tryGetResource(String resourcePath) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = IndexPageServlet.class.getClassLoader();
        }
        return Optional.fromNullable(loader.getResource(resourcePath));
    }
}
