/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 */

package com.palantir.indexpage;

import com.google.common.base.Optional;

/**
 * Simple interface used by both {@link ClasspathIndexPage} and {@link FileSystemIndexPage} to return the index page
 * asset.
 */
public interface IndexPage {

    Optional<String> getContent();
}
