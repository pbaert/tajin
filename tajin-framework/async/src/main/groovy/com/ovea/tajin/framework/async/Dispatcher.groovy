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

/**
 * Dispatcher inyerface used to send events
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-09-05
 */
public interface Dispatcher {

    /**
     * Broadcast an event to all subscribers subscribed to this event.
     * Events are delivered in order but the order subscribers may be called is not necessary the order they registered.
     * Broadcasting is an in-memory process and delivery is not garanteed in case of crash. If there is no subscriber, the event is lost.
     *
     * @param event Event object to broadcast
     */
    void broadcast(Object event)

}
