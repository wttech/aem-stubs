package com.wttech.aem.stubs.core.util;

import org.apache.sling.api.resource.Resource;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.Stack;

public class ResourceTreeSpliterator implements Spliterator<Resource> {

    private final Stack<Resource> stack = new Stack<>();

    public ResourceTreeSpliterator(Resource root) {
        stack.push(root);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Resource> action) {
        while (!stack.isEmpty()) {
            Resource current = stack.pop();
            if (current.isResourceType(JcrUtils.NT_FILE)) {
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
