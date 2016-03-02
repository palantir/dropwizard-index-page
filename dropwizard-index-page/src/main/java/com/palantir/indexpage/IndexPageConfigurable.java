/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import com.google.common.base.Optional;

/**
 * Used by the application's {@link io.dropwizard.Configuration} to specify the index page path.
 */
public interface IndexPageConfigurable {

    Optional<String> getIndexPagePath();
}
