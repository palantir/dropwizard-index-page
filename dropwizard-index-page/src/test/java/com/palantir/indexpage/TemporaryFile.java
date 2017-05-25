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

import static com.google.common.base.Preconditions.checkNotNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import org.junit.rules.ExternalResource;

public final class TemporaryFile extends ExternalResource implements Supplier<File> {
    private String prefix = "junit";
    private String suffix = "";
    private Initializer initializer = dummy -> {};

    private File file;

    public TemporaryFile prefix(String prefixParam) {
        this.prefix = prefixParam;
        return this;
    }

    public TemporaryFile suffix(String suffixParam) {
        this.suffix = suffixParam;
        return this;
    }

    public TemporaryFile initialize(Initializer initializerParam) {
        this.initializer = initializerParam;
        return this;
    }

    @Override
    public File get() {
        return checkNotNull(file, "Cannot retrieve temp file before initialization");
    }

    @Override
    protected void before() throws Throwable {
        file = File.createTempFile(prefix, suffix);
        initializer.initialize(file);
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    @Override
    protected void after() {
        file.delete();
        file = null;
    }

    @FunctionalInterface
    public interface Initializer {
        void initialize(File file) throws IOException;
    }
}
