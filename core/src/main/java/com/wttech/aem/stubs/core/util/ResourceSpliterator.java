package com.wttech.aem.stubs.core.util;

import java.util.Spliterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sling.api.resource.Resource;

public class ResourceSpliterator implements Spliterator<Resource> {

    private final Stack<Resource> stack = new Stack<>();

    private final Predicate<Resource> filter;

    public ResourceSpliterator(Resource root, Predicate<Resource> filter) {
        this.filter = filter;
        stack.push(root);
    }

    public static Stream<Resource> stream(Resource root, Predicate<Resource> filter) {
        return StreamSupport.stream(new ResourceSpliterator(root, filter), false);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Resource> action) {
        while (!stack.isEmpty()) {
            Resource current = stack.pop();
            if (filter.test(current)) {
                action.accept(current);
                return true;
            }
            current.listChildren().forEachRemaining(stack::push);
        }
        return false;
    }

    @Override
    public Spliterator<Resource> trySplit() {
        return null; // parallel processing is not supported
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE; // size is unknown
    }

    @Override
    public int characteristics() {
        return DISTINCT | NONNULL | IMMUTABLE;
    }
}
