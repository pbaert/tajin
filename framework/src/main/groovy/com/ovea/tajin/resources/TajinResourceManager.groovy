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
package com.ovea.tajin.resources

import com.ovea.tajin.TajinConfig
import groovy.json.JsonBuilder

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2012-11-26
 */
class TajinResourceManager {

    final TajinConfig config

    TajinResourceManager(TajinConfig config) {
        this.config = config
    }

    Collection<File> getResources() {
        //TODO MATHIEU - find dynami resources
        return []
    }

    void buid() {
        println 'Building: all...'
        File output = new File(config.webapp, config.json.client?.output as String ?: 'tajin-client.json')
        output.text = new JsonBuilder(config.json.client?.modules ?: [:])
    }

    void buid(File resource) {
        println 'Building: ' + resource
    }

}
