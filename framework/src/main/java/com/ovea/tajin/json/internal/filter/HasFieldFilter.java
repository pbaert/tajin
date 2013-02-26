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
package com.ovea.tajin.json.internal.filter;

import com.ovea.tajin.json.JSONArray;
import com.ovea.tajin.json.JSONObject;
import com.ovea.tajin.json.JSONType;

/**
 * @author Kalle Stenflo
 */
public class HasFieldFilter extends PathTokenFilter {

    public HasFieldFilter(String condition) {
        super(condition);
    }

    @Override
    public JSONType filter(JSONType obj) {

        //[?(@.isbn)]
        JSONArray src = obj.asArray();
        JSONArray result = new JSONArray();

        String trimmedCondition = condition;

        if (condition.contains("['")) {
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }

        trimmedCondition = trim(trimmedCondition, 5, 2);

        for (JSONType item : src) {
            if (item.isObject()) {
                JSONObject map = item.asObject();
                if (map.has(trimmedCondition)) {
                    result.add(map);
                }
            }
        }
        return result;
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
