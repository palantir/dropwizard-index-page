/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.base.Charsets;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link IndexPageServlet}.
 */
@SuppressWarnings("unchecked")
public final class IndexPageServletTests {

    private final ServletConfig mockConfig = mock(ServletConfig.class);
    private final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    private final HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    private final RequestDispatcher mockRequestDispatcher = mock(RequestDispatcher.class);

    @Before

    public void setUp() throws ServletException {
        ContextHandler.Context mockContext = mock(ContextHandler.Context.class);
        ContextHandler mockContextHandler = mock(ContextHandler.class);
        ServletHandler mockServletHandler = mock(ServletHandler.class);

        when(mockConfig.getServletContext()).thenReturn(mockContext);
        when(mockContext.getContextHandler()).thenReturn(mockContextHandler);
        when(mockContext.getRequestDispatcher(eq("/"))).thenReturn(mockRequestDispatcher);
        when(mockContextHandler.getChildHandlerByClass((Class<Handler>) any(Class.class)))
                .thenReturn(mockServletHandler);
        when(mockServletHandler.getServlets()).thenReturn(new ServletHolder[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewWithEmptyContextPath() {
        new IndexPageServlet(null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewWithEmptyIndexPagePath() {
        new IndexPageServlet("", "");
    }

    @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
    @Test(expected = IllegalArgumentException.class)
    public void testNewWithNullIndexPagePath() {
        new IndexPageServlet("", null);
    }

    @Test
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    public void testDoGetUsingPath() throws IOException, ServletException {
        String indexPagePath =
                IndexPageServletTests.class.getClassLoader().getResource("service/web/index.html").getPath();
        IndexPageServlet servlet = new IndexPageServlet("/testBaseUrl", indexPagePath);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        when(mockResponse.getWriter()).thenReturn(writer);
        servlet.init(mockConfig);
        servlet.doGet(mockRequest, mockResponse);

        String templatedPage = outputStream.toString(Charsets.UTF_8.name());
        assertTrue(templatedPage.contains("<base href=\"/testBaseUrl/\">"));
    }
}
