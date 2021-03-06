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
package com.ovea.tajin.framework.templating

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Stage
import com.mycila.guice.ext.service.ServiceModule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-09-09
 */
@RunWith(JUnit4)
class TemplatingTest {

    @Template('classpath:template.txt')
    TemplateMerger merger

    @Test
    void test() {
        Guice.createInjector(Stage.PRODUCTION, new ServiceModule(), new AbstractModule() {
            @Override
            protected void configure() {
                requestInjection(TemplatingTest.this)
            }
        })
        assert merger
        assert merger.merge(Locale.US, [liked: 'you']) == 'i like you'
        assert merger.merge(Locale.FRANCE, [liked: 'toi']) == 'j\'aime toi'
        assert merger.merge(Locale.CANADA_FRENCH, [liked: 'ti']) == 'je capote sur ti'
    }

}
