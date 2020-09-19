//
// MIT License
//
// Copyright (c) 2020 Alexander Söderberg
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package com.intellectualsites.commands.meta;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

/**
 * Object that is associated with a {@link com.intellectualsites.commands.Command}.
 * Command meta should not be mutable, as one fixed instance will be used per command.
 * <p>
 * Appropriate use for command meta would be fixed state, such as command descriptions.
 */
public abstract class CommandMeta {

    /**
     * Create a new simple command meta builder
     *
     * @return Builder instance
     */
    @Nonnull
    public static SimpleCommandMeta.Builder simple() {
        return SimpleCommandMeta.builder();
    }

    @Nonnull
    @Override
    public final String toString() {
        return "";
    }

    /**
     * Get the value associated with a key
     *
     * @param key Key
     * @return Optional that may contain the associated value
     */
    @Nonnull
    public abstract Optional<String> getValue(@Nonnull String key);

    /**
     * Get the value if it exists, else return the default value
     *
     * @param key          Key
     * @param defaultValue Default value
     * @return Value, or default value
     */
    @Nonnull
    public abstract String getOrDefault(@Nonnull String key, @Nonnull String defaultValue);

    /**
     * Get a copy of the meta map
     *
     * @return Copy of meta map
     */
    @Nonnull
    public abstract Map<String, String> getAll();

}
