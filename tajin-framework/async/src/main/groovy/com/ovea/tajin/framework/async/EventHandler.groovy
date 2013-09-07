/**
 * Copyright (C) 2011 Ovea <dev@ovea.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ovea.tajin.framework.async

import com.google.common.base.Preconditions

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

import static com.google.common.base.Preconditions.checkNotNull

/**
 * Wraps a single-argument 'handler' method on a specific object.
 *
 * <p>This class only verifies the suitability of the method and event type if
 * something fails.  Callers are expected to verify their uses of this class.
 *
 * <p>Two EventHandlers are equivalent when they refer to the same method on the
 * same object (not class).   This property is used to ensure that no handler
 * method is registered more than once.
 *
 * @author Cliff Biffle
 */
class EventHandler {

    /** Object sporting the handler method. */
    private final Object target;
    /** Handler method. */
    private final Method method;

    /**
     * Creates a new EventHandler to wrap {@code method} on @{code target}.
     *
     * @param target object to which the method applies.
     * @param method handler method.
     */
    EventHandler(Object target, Method method) {
        Preconditions.checkNotNull(target,
            "EventHandler target cannot be null.");
        Preconditions.checkNotNull(method, "EventHandler method cannot be null.");

        this.target = target;
        this.method = method;
        method.setAccessible(true);
    }

    /**
     * Invokes the wrapped handler method to handle {@code event}.
     *
     * @param event event to handle
     * @throws InvocationTargetException  if the wrapped method throws any
     * {@link Throwable} that is not an {@link Error} ({@code Error} instances are
     *     propagated as-is).
     */
    public void handleEvent(Object event) throws InvocationTargetException {
        checkNotNull(event);
        try {
            method.invoke(target, [event] as Object[]);
        } catch (InvocationTargetException e) {
            throw e.targetException
        }
    }

    @Override
    public String toString() {
        return "[wrapper " + method + "]";
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        return (PRIME + method.hashCode()) * PRIME + System.identityHashCode(target);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventHandler) {
            EventHandler that = (EventHandler) obj;
            // Use == so that different equal instances will still receive events.
            // We only guard against the case that the same object is registered
            // multiple times
            return target == that.target && method.equals(that.method);
        }
        return false;
    }
}
