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

import com.ovea.tajin.framework.core.Uuid
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-06-06
 */
@ToString(includeNames = true)
@EqualsAndHashCode(includes = ['id'])
class ScheduledJobTriggeredEvent {

    /**
     * ID, auto-generated if not set
     */
    String id = Uuid.newUUID

    ScheduledJobEvent source

    /**
     * Current retry count
     */
    int currentRetry

    /**
     * Date of last retry
     */
    Date lastRetried

    Date nextTry

    boolean completed

    Throwable failure

    transient Map<String, Closure<?>> callbacks = [:]

    void completed() {
        completed = true
        callbacks?.oncomplete?.call()
    }

    void failed(Throwable e) {
        failure = e
        callbacks?.onerror?.call(e)
    }
}
