//
// MIT License
//
// Copyright (c) 2020 Alexander Söderberg & Contributors
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
package cloud.commandframework.arguments.standard;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NumberParseException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class LongArgument<C> extends CommandArgument<C, Long> {

    private final long min;
    private final long max;

    private LongArgument(
            final boolean required,
            final @NonNull String name,
            final long min,
            final long max,
            final String defaultValue,
            final @Nullable BiFunction<@NonNull CommandContext<C>, @NonNull String,
                    @NonNull List<@NonNull String>> suggestionsProvider
    ) {
        super(required, name, new LongParser<>(min, max), defaultValue, Long.class, suggestionsProvider);
        this.min = min;
        this.max = max;
    }

    /**
     * Create a new builder
     *
     * @param name Name of the argument
     * @param <C>  Command sender type
     * @return Created builder
     */
    public static <C> LongArgument.@NonNull Builder<C> newBuilder(final @NonNull String name) {
        return new Builder<>(name);
    }

    /**
     * Create a new required command argument
     *
     * @param name Argument name
     * @param <C>  Command sender type
     * @return Created argument
     */
    public static <C> @NonNull CommandArgument<C, Long> of(final @NonNull String name) {
        return LongArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Create a new optional command argument
     *
     * @param name Argument name
     * @param <C>  Command sender type
     * @return Created argument
     */
    public static <C> @NonNull CommandArgument<C, Long> optional(final @NonNull String name) {
        return LongArgument.<C>newBuilder(name).asOptional().build();
    }

    /**
     * Create a new required command argument with a default value
     *
     * @param name       Argument name
     * @param defaultNum Default num
     * @param <C>        Command sender type
     * @return Created argument
     */
    public static <C> @NonNull CommandArgument<C, Long> optional(
            final @NonNull String name,
            final long defaultNum
    ) {
        return LongArgument.<C>newBuilder(name).asOptionalWithDefault(Long.toString(defaultNum)).build();
    }

    /**
     * Get the minimum accepted long that could have been parsed
     *
     * @return Minimum long
     */
    public long getMin() {
        return this.min;
    }

    /**
     * Get the maximum accepted long that could have been parsed
     *
     * @return Maximum long
     */
    public long getMax() {
        return this.max;
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, Long> {

        private long min = Long.MIN_VALUE;
        private long max = Long.MAX_VALUE;

        protected Builder(final @NonNull String name) {
            super(Long.class, name);
        }

        /**
         * Set a minimum value
         *
         * @param min Minimum value
         * @return Builder instance
         */
        public @NonNull Builder<C> withMin(final long min) {
            this.min = min;
            return this;
        }

        /**
         * Set a maximum value
         *
         * @param max Maximum value
         * @return Builder instance
         */
        public @NonNull Builder<C> withMax(final long max) {
            this.max = max;
            return this;
        }

        /**
         * Builder a new long argument
         *
         * @return Constructed argument
         */
        @Override
        public @NonNull LongArgument<C> build() {
            return new LongArgument<>(this.isRequired(), this.getName(), this.min,
                    this.max, this.getDefaultValue(), this.getSuggestionsProvider()
            );
        }

    }

    private static final class LongParser<C> implements ArgumentParser<C, Long> {

        private final long min;
        private final long max;

        private LongParser(final long min, final long max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public @NonNull ArgumentParseResult<Long> parse(
                final @NonNull CommandContext<C> commandContext,
                final @NonNull Queue<@NonNull String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NullPointerException("No input was provided"));
            }
            try {
                final long value = Long.parseLong(input);
                if (value < this.min || value > this.max) {
                    return ArgumentParseResult.failure(new LongParseException(input, this.min, this.max));
                }
                inputQueue.remove();
                return ArgumentParseResult.success(value);
            } catch (final Exception e) {
                return ArgumentParseResult.failure(new LongParseException(input, this.min, this.max));
            }
        }

        @Override
        public boolean isContextFree() {
            return true;
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(
                final @NonNull CommandContext<C> commandContext,
                final @NonNull String input
        ) {
            return IntegerArgument.IntegerParser.getSuggestions(this.min, this.max, input);
        }

    }


    public static final class LongParseException extends NumberParseException {

        /**
         * Construct a new long parse exception
         *
         * @param input String input
         * @param min   Minimum value
         * @param max   Maximum value
         */
        public LongParseException(final @NonNull String input, final long min, final long max) {
            super(input, min, max);
        }

        @Override
        public boolean hasMin() {
            return this.getMin().longValue() != Long.MIN_VALUE;
        }

        @Override
        public boolean hasMax() {
            return this.getMax().longValue() != Long.MAX_VALUE;
        }

        @Override
        public @NonNull String getNumberType() {
            return "long";
        }

    }

}
