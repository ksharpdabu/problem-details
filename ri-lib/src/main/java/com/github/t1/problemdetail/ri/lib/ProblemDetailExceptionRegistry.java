package com.github.t1.problemdetail.ri.lib;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.github.t1.problemdetail.ri.lib.ProblemDetailBuilder.URN_PROBLEM_TYPE_PREFIX;

public class ProblemDetailExceptionRegistry {
    static final Map<String, Class<? extends Throwable>> REGISTRY = new HashMap<>();

    public static URI register(Class<? extends Throwable> exceptionType) {
        URI type = ProblemDetailBuilder.buildTypeUri(exceptionType);
        register(exceptionType, type);
        return type;
    }

    public static void register(Class<? extends Throwable> exceptionType, URI type) {
        REGISTRY.put(type.toString(), exceptionType);
    }

    static Class<? extends Throwable> computeFrom(String type) {
        if (type == null)
            return null;
        return computeFrom(type, "java.lang.", type.endsWith("-error") ? "" : "Exception");
    }

    public static Class<? extends Throwable> computeFrom(String type, String prefix, String suffix) {
        if (type.startsWith(URN_PROBLEM_TYPE_PREFIX)) {
            return computeFromUrn(type.substring(URN_PROBLEM_TYPE_PREFIX.length()), prefix, suffix);
        } else {
            return null;
        }
    }

    private static Class<? extends Throwable> computeFromUrn(String type, String prefix, String suffix) {
        return forName(prefix + kebapToCamel(type) + suffix);
    }

    private static String kebapToCamel(String string) {
        StringBuilder out = new StringBuilder();
        boolean upper = true;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '-') {
                upper = true;
            } else if (upper) {
                out.append(Character.toUpperCase(c));
                upper = false;
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private static Class<? extends Throwable> forName(String name) {
        try {
            Class<?> t = Class.forName(name);
            //noinspection unchecked
            return Throwable.class.isAssignableFrom(t)
                ? (Class<? extends Throwable>) t
                : null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
