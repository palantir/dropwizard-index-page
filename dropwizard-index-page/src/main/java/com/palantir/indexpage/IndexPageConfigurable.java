/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;


/**
 * Used by the application's {@link io.dropwizard.Configuration} to specify the index page path.
 */
public interface IndexPageConfigurable {

    String getIndexPagePath();
}
