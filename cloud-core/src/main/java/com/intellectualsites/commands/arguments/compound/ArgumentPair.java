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
package com.intellectualsites.commands.arguments.compound;

import com.google.common.reflect.TypeToken;
import com.intellectualsites.commands.CommandManager;
import com.intellectualsites.commands.arguments.parser.ArgumentParser;
import com.intellectualsites.commands.arguments.parser.ParserParameters;
import com.intellectualsites.commands.arguments.parser.ParserRegistry;
import com.intellectualsites.commands.types.tuples.Pair;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * A compound argument consisting of two inner arguments
 *
 * @param <C> Command sender type
 * @param <U> First argument type
 * @param <V> Second argument type
 * @param <O> Output type
 */
public final class ArgumentPair<C, U, V, O> extends CompoundArgument<Pair<U, V>, C, O> {

    private ArgumentPair(final boolean required,
                         @Nonnull final String name,
                         @Nonnull final Pair<String, String> names,
                         @Nonnull final Pair<Class<U>, Class<V>> types,
                         @Nonnull final Pair<ArgumentParser<C, U>, ArgumentParser<C, V>> parserPair,
                         @Nonnull final Function<Pair<U, V>, O> mapper,
                         @Nonnull final TypeToken<O> valueType) {
        super(required, name, names, parserPair, types, mapper, o -> Pair.of((U) o[0], (V) o[1]), valueType);
    }

    /**
     * Construct a builder for an argument pair
     *
     * @param manager Command manager
     * @param name    Argument name
     * @param names   Sub-argument names
     * @param types   Pair containing the types of the sub-arguments. There must be parsers for these types registered
     *                in the {@link com.intellectualsites.commands.arguments.parser.ParserRegistry} used by the
     *                {@link CommandManager}
     * @param <C>     Command sender type
     * @param <U>     First parsed type
     * @param <V>     Second parsed type
     * @return Intermediary builder
     */
    @Nonnull
    public static <C, U, V> ArgumentPairIntermediaryBuilder<C, U, V> required(@Nonnull final CommandManager<C> manager,
                                                                              @Nonnull final String name,
                                                                              @Nonnull final Pair<String, String> names,
                                                                              @Nonnull final Pair<Class<U>, Class<V>> types) {
        final ParserRegistry<C> parserRegistry = manager.getParserRegistry();
        final ArgumentParser<C, U> firstParser = parserRegistry.createParser(TypeToken.of(types.getFirst()),
                                                                             ParserParameters.empty()).orElseThrow(() ->
                                                                           new IllegalArgumentException(
                                                                                   "Could not create parser for primary type"));
        final ArgumentParser<C, V> secondaryParser = parserRegistry.createParser(TypeToken.of(types.getSecond()),
                                                                                 ParserParameters.empty()).orElseThrow(() ->
                                                                       new IllegalArgumentException(
                                                                               "Could not create parser for secondary type"));
        return new ArgumentPairIntermediaryBuilder<>(true, name, names, Pair.of(firstParser, secondaryParser), types);
    }

    @SuppressWarnings("ALL")
    public static final class ArgumentPairIntermediaryBuilder<C, U, V> {

        private final boolean required;
        private final String name;
        private final Pair<ArgumentParser<C, U>, ArgumentParser<C, V>> parserPair;
        private final Pair<String, String> names;
        private final Pair<Class<U>, Class<V>> types;

        private ArgumentPairIntermediaryBuilder(final boolean required,
                                                @Nonnull final String name,
                                                @Nonnull final Pair<String, String> names,
                                                @Nonnull final Pair<ArgumentParser<C, U>, ArgumentParser<C, V>> parserPair,
                                                @Nonnull final Pair<Class<U>, Class<V>> types) {
            this.required = required;
            this.name = name;
            this.names = names;
            this.parserPair = parserPair;
            this.types = types;
        }

        /**
         * Create a simple argument pair that maps to a pair
         *
         * @return Argument pair
         */
        @Nonnull
        public ArgumentPair<C, U, V, Pair<U, V>> simple() {
            return new ArgumentPair<C, U, V, Pair<U, V>>(this.required,
                                                         this.name,
                                                         this.names,
                                                         this.types,
                                                         this.parserPair,
                                                         Function.identity(),
                                                         new TypeToken<Pair<U, V>>() {
                                                         });
        }

        /**
         * Create an argument pair that maps to a specific type
         *
         * @param clazz  Output class
         * @param mapper Output mapper
         * @param <O>    Output type
         * @return Created pair
         */
        @Nonnull
        public <O> ArgumentPair<C, U, V, O> withMapper(@Nonnull final TypeToken<O> clazz,
                                                       @Nonnull final Function<Pair<U, V>, O> mapper) {
            return new ArgumentPair<C, U, V, O>(this.required, this.name, this.names, this.types, this.parserPair, mapper, clazz);
        }

        /**
         * Create an argument pair that maps to a specific type
         *
         * @param clazz  Output class
         * @param mapper Output mapper
         * @param <O>    Output type
         * @return Created pair
         */
        @Nonnull
        public <O> ArgumentPair<C, U, V, O> withMapper(@Nonnull final Class<O> clazz,
                                                       @Nonnull final Function<Pair<U, V>, O> mapper) {
            return this.withMapper(TypeToken.of(clazz), mapper);
        }

    }

}