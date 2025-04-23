package com.sunkenpotato.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MethodArgumentType implements ArgumentType<MethodArgumentType.Method> {
    private static final Collection<String> EXAMPLES = Arrays.stream(Method.values()).map(Enum::name).collect(Collectors.toList());

    public static MethodArgumentType method() {
        return new MethodArgumentType();
    }

    public static <S> Method getMethod(CommandContext<S> context, String name) {
        return context.getArgument(name, Method.class);
    }

    @Override
    public Method parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if(!reader.canRead()) reader.skip();

        while (reader.canRead() && Character.isAlphabetic(reader.peek())) reader.skip();

        String unparsedString = reader.getString().substring(argBeginning, reader.getCursor());
        try {
            return Method.valueOf(unparsedString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (Method m : Method.values()) {
            builder.suggest(m.name());
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public enum Method {
        GET(true),
        HEAD(true),
        OPTIONS(true),
        POST(true),
        PUT(true),
        DELETE(true),
        TRACE(false),
        PATCH(true);

        final boolean canHaveBody;

        Method(boolean canHaveBody) {
            this.canHaveBody = canHaveBody;
        }
    }
}
