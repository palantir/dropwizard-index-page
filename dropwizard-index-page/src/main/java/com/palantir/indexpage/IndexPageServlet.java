/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static com.google.common.base.Preconditions.checkArgument;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.servlet.DefaultServlet;

/**
 * Used to serve to the index page and set disable cache headers in the HTTP response.
 */
public final class IndexPageServlet extends DefaultServlet {

    private static final long serialVersionUID = 1L;
    private static final String BASE_URL = "baseUrl";
    private static final String CACHE_CONTROL = "no-cache, no-store, max-age=0, must-revalidate";

    @SuppressFBWarnings("SE_BAD_FIELD")
    private final DefaultMustacheFactory factory;
    private final File indexPage;

    public IndexPageServlet(String indexPagePath) {
        checkArgument(!Strings.isNullOrEmpty(indexPagePath));

        this.factory = new DefaultMustacheFactory();
        this.indexPage = new File(indexPagePath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.HTML_UTF_8.toString());

        Map<String, String> context = ImmutableMap.of(BASE_URL, request.getContextPath() + "/");
        BufferedReader reader;
        try {
            reader = Files.newReader(indexPage, Charsets.UTF_8);
        } catch (FileNotFoundException e) {
            response.sendError(HttpStatus.NOT_FOUND_404, "Index page file not found.");
            return;
        }

        Mustache mustache = factory.compile(reader, IndexPageBundle.INDEX_PAGE_NAME);
        PrintWriter writer = response.getWriter();
        mustache.execute(writer, context);
        writer.close();
    }
}
