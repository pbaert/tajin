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
package com.ovea.tajin.framework.support.jersey

import com.ovea.tajin.framework.util.Uuid

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-09-20
 */
class APIToken {

    static final String TOKEN_PARAM = 'token'
    static final String TOKEN_HEADER = 'X-Token'

    static final long RATE_LIMITING_NO_LIMIT = -1
    static final String RATE_LIMITING_PERIOD_HOURLY = 'hour'
    static final String RATE_LIMITING_PERIOD_DAILY = 'day'
    static final String RATE_LIMITING_PERIOD_MONTHLY = 'month'

    String value = Uuid.generate()
    String alias
    String account
    boolean production = true

    List<String> ipRestrictions = []
    List<String> apiRestrictions = []
    Map<String, String> headerRestrictions = [:]

    Map<String, ?> options = [:]

    long rateLimitingLimit = RATE_LIMITING_NO_LIMIT
    String rateLimitingPeriod = RATE_LIMITING_PERIOD_HOURLY

    boolean isApiRestricted() { !apiRestrictions.empty }
    boolean isIpRestricted() { !ipRestrictions.empty }
    boolean isRateLimited() { rateLimitingLimit >= 0 }
    boolean isHeaderRestricted() { !headerRestrictions.isEmpty() }

    @Override
    String toString() { "Token: ${value} (${alias})" }
}
