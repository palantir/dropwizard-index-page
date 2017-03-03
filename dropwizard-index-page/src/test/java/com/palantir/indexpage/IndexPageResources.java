/*
 * Copyright 2017 Palantir Technologies, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.indexpage;

import static org.junit.Assert.assertTrue;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

public final class IndexPageResources {

    public static final URL INDEX_PAGE = Resources.getResource("service/web/index.html");
    public static final URL INDEX_PAGE_2 = Resources.getResource("service/web/index2.html");
    public static final URL INDEX_PAGE_INVALID = Resources.getResource("service/web/indexInvalid.html");

    // Java only guarantees 1 second modification time precision
    private static final long MINIMUM_MODIFY_TIME_INCREMENT = 1000L;

    private IndexPageResources() {
        // utils
    }

    public static void update(File file, URL resource) throws IOException {
        long originalLastModified = file.lastModified();
        try (InputStream input = resource.openStream();
                OutputStream output = Files.newOutputStream(file.toPath())) {
            ByteStreams.copy(input, output);
        }
        // Kludge: Unit tests are too fast, and modification time precision may not reflect change.
        // Force modification time to update at least as much as minimum precision specifies.
        if (originalLastModified != 0L) {
            long minimumNewModifiedTime = originalLastModified + MINIMUM_MODIFY_TIME_INCREMENT;
            if (file.lastModified() < minimumNewModifiedTime) {
                assertTrue(file.setLastModified(minimumNewModifiedTime));
            }
        }
    }
}
