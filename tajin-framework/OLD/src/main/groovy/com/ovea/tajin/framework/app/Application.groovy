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
package com.ovea.tajin.framework.app

import com.ovea.tajin.framework.support.guice.WebBinder
import com.ovea.tajin.framework.util.PropertySettings

public interface Application {
    /**
     * Called at initialization time
     */
    void onInit(WebBinder binder, PropertySettings settings)

    /**
     * Called when web application is started and injections are completed
     */
    void onStart()

    /**
     * Called when application is stopped
     */
    void onstop()
}
