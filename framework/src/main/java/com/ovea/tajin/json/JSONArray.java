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
/*
Copyright (c) 2002 JSON.org

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.ovea.tajin.json;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * A JSONArray is an ordered sequence of values. Its external text form is a
 * string wrapped in square brackets with commas separating the values. The
 * internal form is an object having <code>get</code> and <code>opt</code>
 * methods for accessing the values by index, and <code>put</code> methods for
 * adding or replacing values. The values can be any of these types:
 * <code>Boolean</code>, <code>JSONArray</code>, <code>JSONObject</code>,
 * <code>Number</code>, <code>String</code>, or the
 * <code>JSON.NULL object</code>.
 * <p/>
 * The constructor can convert a JSON text into a Java object. The
 * <code>toString</code> method converts to JSON text.
 * <p/>
 * A <code>get</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p/>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coersion for you.
 * <p/>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * JSON syntax rules. The constructors are more forgiving in the texts they will
 * accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing bracket.</li>
 * <li>The <code>null</code> value will be inserted when there
 * is <code>,</code>&nbsp;<small>(comma)</small> elision.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote
 * or single quote, and if they do not contain leading or trailing spaces,
 * and if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers
 * and if they are not the reserved words <code>true</code>,
 * <code>false</code>, or <code>null</code>.</li>
 * <li>Values can be separated by <code>;</code> <small>(semicolon)</small> as
 * well as by <code>,</code> <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0-</code> <small>(octal)</small> or
 * <code>0x-</code> <small>(hex)</small> prefix.</li>
 * <li>Comments written in the slashshlash, slashstar, and hash conventions
 * will be ignored.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2
 */
public class JSONArray extends JSONType implements Iterable<JSONType> {

    /**
     * The arrayList where the JSONArray's properties are kept.
     */
    private final ArrayList<JSONType> myArrayList = new ArrayList<>();

    /**
     * Construct an empty JSONArray.
     */
    public JSONArray() {
    }

    /**
     * Construct a JSONArray from a JSONTokener.
     *
     * @param x A JSONTokener
     * @throws JSONException If there is a syntax error.
     */
    JSONArray(JSONTokener x) throws JSONException {
        if (x.nextClean() != '[') {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        if (x.nextClean() == ']') {
            return;
        }
        x.back();
        for (; ; ) {
            if (x.nextClean() == ',') {
                x.back();
                put((Object) null);
            } else {
                x.back();
                put(x.nextValue());
            }
            switch (x.nextClean()) {
                case ';':
                case ',':
                    if (x.nextClean() == ']') {
                        return;
                    }
                    x.back();
                    break;
                case ']':
                    return;
                default:
                    throw x.syntaxError("Expected a ',' or ']'");
            }
        }
    }

    /**
     * Construct a JSONArray from a source sJSON text.
     *
     * @param string A string that begins with
     *               <code>[</code>&nbsp;<small>(left bracket)</small>
     *               and ends with <code>]</code>&nbsp;<small>(right bracket)</small>.
     * @throws JSONException If there is a syntax error.
     */
    public JSONArray(String string) throws JSONException {
        this(new JSONTokener(string));
    }

    public JSONArray(final Object... values) {
        if (values != null) {
            for (Object value : values) {
                put(value);
            }
        }
    }

    /**
     * Construct a JSONArray from a Collection.
     *
     * @param collection A Collection.
     */
    public JSONArray(Iterable<?> collection) {
        if (collection != null) {
            for (Object o : collection) {
                put(o);
            }
        }
    }

    public JSONArray(JSONArray array) {
        if (array != null) {
            for (JSONType o : array) {
                put(o);
            }
        }
    }

    /**
     * Get the object value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return An object value.
     * @throws JSONException If there is no value for the index.
     */

    public JSONType get(int index) throws JSONException {
        JSONType o = opt(index);
        if (o == null) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        return o;
    }

    /**
     * Get the boolean value associated with an index.
     * The string values "true" and "false" are converted to boolean.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The truth.
     * @throws JSONException If there is no value for the index or if the
     *                       value is not convertable to boolean.
     */
    public boolean getBoolean(int index) throws ConversionException {
        try {
            return get(index).asBoolean();
        } catch (ConversionException e) {
            throw new JSONException("JSONArray[" + index + "] is not a boolean.");
        }
    }

    /**
     * Get the double value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException If the key is not found or if the value cannot
     *                       be converted to a number.
     */
    public double getDouble(int index) throws JSONException {
        try {
            return get(index).asDouble();
        } catch (ConversionException e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    /**
     * Get the int value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException If the key is not found or if the value cannot
     *                       be converted to a number.
     *                       if the value cannot be converted to a number.
     */
    public int getInt(int index) throws JSONException {
        try {
            return get(index).asInt();
        } catch (ConversionException e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    /**
     * Get the JSONArray associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A JSONArray value.
     * @throws JSONException If there is no value for the index. or if the
     *                       value is not a JSONArray
     */
    public JSONArray getArray(int index) throws JSONException {
        JSONType o = get(index);
        if (o.isArray()) {
            return o.asArray();
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
    }

    /**
     * Get the JSONObject associated with an index.
     *
     * @param index subscript
     * @return A JSONObject value.
     * @throws JSONException If there is no value for the index or if the
     *                       value is not a JSONObject
     */
    public JSONObject getObject(int index) throws JSONException {
        JSONType o = get(index);
        if (o.isObject()) {
            return o.asObject();
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
    }

    /**
     * Get the long value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException If the key is not found or if the value cannot
     *                       be converted to a number.
     */
    public long getLong(int index) throws JSONException {
        try {
            return get(index).asLong();
        } catch (ConversionException e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    /**
     * Get the string associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A string value.
     * @throws JSONException If there is no value for the index.
     */
    public String getString(int index) throws JSONException {
        return get(index).asString();
    }

    /**
     * Determine if the value is null.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return true if the value at the index is null, or if there is no value.
     */
    public boolean isNull(int index) {
        return JSON.NULL.equals(opt(index));
    }

    /**
     * Make a string from the contents of this JSONArray. The
     * <code>separator</code> string is inserted between each element.
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param separator A string that will be inserted between the elements.
     * @return a string.
     * @throws JSONException If the array contains an invalid number.
     */
    public String join(String separator) throws JSONException {
        int len = length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i += 1) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(this.myArrayList.get(i));
        }
        return sb.toString();
    }

    /**
     * Get the number of elements in the JSONArray, included nulls.
     *
     * @return The length (or size).
     */
    public int length() {
        return this.myArrayList.size();
    }

    public int size() {
        return length();
    }

    public boolean isEmpty() {
        return this.myArrayList.isEmpty();
    }

    @Override
    public Iterator<JSONType> iterator() {
        return myArrayList.iterator();
    }

    /**
     * Get the optional object value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return An object value, or null if there is no
     *         object at that index.
     */
    public JSONType opt(int index) {
        return (index < 0 || index >= length()) ? null : this.myArrayList.get(index);
    }

    /**
     * Get the optional boolean value associated with an index.
     * It returns false if there is no value at that index,
     * or if the value is not Boolean.TRUE or the String "true".
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The truth.
     */
    public boolean optBoolean(int index) {
        return optBoolean(index, false);
    }

    /**
     * Get the optional boolean value associated with an index.
     * It returns the defaultValue if there is no value at that index or if
     * it is not a Boolean or the String "true" or "false" (case insensitive).
     *
     * @param index        The index must be between 0 and length() - 1.
     * @param defaultValue A boolean default.
     * @return The truth.
     */
    public boolean optBoolean(int index, boolean defaultValue) {
        try {
            return getBoolean(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional double value associated with an index.
     * NaN is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     */
    public double optDouble(int index) {
        return optDouble(index, Double.NaN);
    }

    /**
     * Get the optional double value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index        subscript
     * @param defaultValue The default value.
     * @return The value.
     */
    public double optDouble(int index, double defaultValue) {
        try {
            return getDouble(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional int value associated with an index.
     * Zero is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     */
    public int optInt(int index) {
        return optInt(index, 0);
    }

    /**
     * Get the optional int value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index        The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return The value.
     */
    public int optInt(int index, int defaultValue) {
        try {
            return getInt(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional JSONArray associated with an index.
     *
     * @param index subscript
     * @return A JSONArray value, or null if the index has no value,
     *         or if the value is not a JSONArray.
     */
    public JSONArray optArray(int index) {
        JSONType o = opt(index);
        return o instanceof JSONArray ? (JSONArray) o : null;
    }

    /**
     * Get the optional JSONObject associated with an index.
     * Null is returned if the key is not found, or null if the index has
     * no value, or if the value is not a JSONObject.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A JSONObject value.
     */
    public JSONObject optObject(int index) {
        JSONType o = opt(index);
        return o instanceof JSONObject ? (JSONObject) o : null;
    }

    /**
     * Get the optional long value associated with an index.
     * Zero is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     */
    public long optLong(int index) {
        return optLong(index, 0);
    }

    /**
     * Get the optional long value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index        The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return The value.
     */
    public long optLong(int index, long defaultValue) {
        try {
            return getLong(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional string value associated with an index. It returns an
     * empty string if there is no value at that index. If the value
     * is not a string and is not null, then it is coverted to a string.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A String value.
     */
    public String optString(int index) {
        return optString(index, "");
    }

    /**
     * Get the optional string associated with an index.
     * The defaultValue is returned if the key is not found.
     *
     * @param index        The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return A String value.
     */
    public String optString(int index, String defaultValue) {
        JSONType o = opt(index);
        return o != null ? o.asString() : defaultValue;
    }

    public JSONArray putNull() {
        return put(JSON.NULL);
    }

    /**
     * Append a boolean value. This increases the array's length by one.
     *
     * @param value A boolean value.
     * @return this.
     */
    public JSONArray put(boolean value) {
        return put(value ? Boolean.TRUE : Boolean.FALSE);
    }

    public JSONArray add(boolean value) {
        return put(value);
    }

    /**
     * Put a value in the JSONArray, where the value will be a
     * JSONArray which is produced from a Collection.
     *
     * @param value A Collection value.
     * @return this.
     */
    public JSONArray put(Collection<?> value) {
        return put(new JSONArray(value));
    }

    public JSONArray add(Iterable<?> value) {
        return put(value);
    }

    /**
     * Append a double value. This increases the array's length by one.
     *
     * @param value A double value.
     * @return this.
     * @throws JSONException if the value is not finite.
     */
    public JSONArray put(double value) throws JSONException {
        return put(Double.valueOf(value));
    }

    public JSONArray add(double value) {
        return put(value);
    }

    public JSONArray put(float value) throws JSONException {
        return put(Float.valueOf(value));
    }

    public JSONArray add(float value) {
        return put(value);
    }

    /**
     * Append an int value. This increases the array's length by one.
     *
     * @param value An int value.
     * @return this.
     */
    public JSONArray put(int value) {
        return put(new Integer(value));
    }

    public JSONArray add(int value) {
        return put(value);
    }

    /**
     * Append an long value. This increases the array's length by one.
     *
     * @param value A long value.
     * @return this.
     */
    public JSONArray put(long value) {
        return put(new Long(value));
    }

    public JSONArray add(long value) {
        return put(value);
    }

    /**
     * Put a value in the JSONArray, where the value will be a
     * JSONObject which is produced from a Map.
     *
     * @param value A Map value.
     * @return this.
     */
    public JSONArray put(Map<String, ?> value) {
        return put(new JSONObject(value));
    }

    public JSONArray add(Map<String, ?> value) {
        return put(value);
    }

    /**
     * Append an object value. This increases the array's length by one.
     *
     * @param value An object value.  The value should be a
     *              Boolean, Double, Integer, JSONArray, JSONObject, Long, or String, or the
     *              JSON.NULL object.
     * @return this.
     */
    public JSONArray put(Object value) {
        this.myArrayList.add(JSON.valueOf(value));
        return this;
    }

    public JSONArray add(Object value) {
        return put(value);
    }

    /**
     * Put or replace a boolean value in the JSONArray. If the index is greater
     * than the length of the JSONArray, then null elements will be added as
     * necessary to pad it out.
     *
     * @param index The subscript.
     * @param value A boolean value.
     * @return this.
     * @throws JSONException If the index is negative.
     */
    public JSONArray put(int index, boolean value) throws JSONException {
        return put(index, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public JSONArray add(int index, boolean value) {
        return put(index, value);
    }

    public JSONArray putNull(int index) throws JSONException {
        return put(index, JSON.NULL);
    }

    public JSONArray addNull(int index) {
        return putNull(index);
    }

    /**
     * Put a value in the JSONArray, where the value will be a
     * JSONArray which is produced from a Collection.
     *
     * @param index The subscript.
     * @param value A Collection value.
     * @return this.
     * @throws JSONException If the index is negative or if the value is
     *                       not finite.
     */
    public JSONArray put(int index, Collection<?> value) throws JSONException {
        return put(index, new JSONArray(value));
    }

    public JSONArray add(int index, Iterable<?> value) {
        return put(index, value);
    }

    /**
     * Put or replace a double value. If the index is greater than the length of
     * the JSONArray, then null elements will be added as necessary to pad
     * it out.
     *
     * @param index The subscript.
     * @param value A double value.
     * @return this.
     * @throws JSONException If the index is negative or if the value is
     *                       not finite.
     */
    public JSONArray put(int index, double value) throws JSONException {
        return put(index, Double.valueOf(value));
    }

    public JSONArray add(int index, double value) {
        return put(index, value);
    }

    public JSONArray put(int index, float value) throws JSONException {
        return put(index, Float.valueOf(value));
    }

    public JSONArray add(int index, float value) {
        return put(index, value);
    }

    /**
     * Put or replace an int value. If the index is greater than the length of
     * the JSONArray, then null elements will be added as necessary to pad
     * it out.
     *
     * @param index The subscript.
     * @param value An int value.
     * @return this.
     * @throws JSONException If the index is negative.
     */
    public JSONArray put(int index, int value) throws JSONException {
        return put(index, new Integer(value));
    }

    public JSONArray add(int index, int value) {
        return put(index, value);
    }

    /**
     * Put or replace a long value. If the index is greater than the length of
     * the JSONArray, then null elements will be added as necessary to pad
     * it out.
     *
     * @param index The subscript.
     * @param value A long value.
     * @return this.
     * @throws JSONException If the index is negative.
     */
    public JSONArray put(int index, long value) throws JSONException {
        return put(index, new Long(value));
    }

    public JSONArray add(int index, long value) {
        return put(index, value);
    }

    /**
     * Put a value in the JSONArray, where the value will be a
     * JSONObject which is produced from a Map.
     *
     * @param index The subscript.
     * @param value The Map value.
     * @return this.
     * @throws JSONException If the index is negative or if the the value is
     *                       an invalid number.
     */
    public JSONArray put(int index, Map<String, ?> value) throws JSONException {
        return put(index, new JSONObject(value));
    }

    public JSONArray add(int index, Map<String, ?> value) {
        return put(index, value);
    }

    /**
     * Put or replace an object value in the JSONArray. If the index is greater
     * than the length of the JSONArray, then null elements will be added as
     * necessary to pad it out.
     *
     * @param index The subscript.
     * @param value The value to put into the array. The value should be a
     *              Boolean, Double, Integer, JSONArray, JSONObject, Long, or String, or the
     *              JSON.NULL object.
     * @return this.
     * @throws JSONException If the index is negative or if the the value is
     *                       an invalid number.
     */
    public JSONArray put(int index, Object value) throws JSONException {
        if (index < 0) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        if (index < length()) {
            this.myArrayList.set(index, JSON.valueOf(value));
        } else {
            while (index != length()) {
                put(JSON.NULL);
            }
            put(value);
        }
        return this;
    }

    public JSONArray add(int index, Object value) {
        return put(index, value);
    }

    /**
     * Produce a JSONObject by combining a JSONArray of names with the values
     * of this JSONArray.
     *
     * @param names A JSONArray containing a list of key strings. These will be
     *              paired with the values.
     * @return A JSONObject, or null if there are no names or if this JSONArray
     *         has no values.
     * @throws JSONException If any of the names are null.
     */
    public JSONObject toObject(JSONArray names) throws JSONException {
        JSONObject jo = new JSONObject();
        if (names == null || names.length() == 0 || length() == 0) {
            return jo;
        }
        for (int i = 0; i < names.length(); i += 1) {
            jo.put(names.getString(i), this.opt(i));
        }
        return jo;
    }

    public JSONObject toObject(String... keys) throws JSONException {
        return toObject(new JSONArray(Arrays.asList(keys)));
    }

    public JSONObject toObject(Iterable<String> keys) throws JSONException {
        return toObject(new JSONArray(keys));
    }

    public JSONType remove(int index) {
        return (index < 0 || index >= length()) ? null : this.myArrayList.remove(index);
    }

    public JSONArray removeAll(int... indexes) {
        for (int index : indexes) {
            remove(index);
        }
        return this;
    }

    /**
     * Make a JSON text of this JSONArray. For compactness, no
     * unnecessary whitespace is added. If it is not possible to produce a
     * syntactically correct JSON text then '' will be returned instead. This
     * could occur if the array contains an invalid number.
     * <p/>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, transmittable
     *         representation of the array.
     */
    @Override
    public String toString() {
        try {
            return '[' + join(",") + ']';
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Make a prettyprinted JSON text of this JSONArray.
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor The number of spaces to add to each level of
     *                     indentation.
     * @param indent       The indention of the top level.
     * @return a printable, displayable, transmittable
     *         representation of the array.
     * @throws JSONException
     */
    @Override
    public String toString(int indentFactor, int indent) throws JSONException {
        int len = length();
        if (len == 0) {
            return "[]";
        }
        int i;
        StringBuilder sb = new StringBuilder("[");
        if (len == 1) {
            sb.append(this.myArrayList.get(0).toString(indentFactor, indent));
        } else {
            int newindent = indent + indentFactor;
            sb.append('\n');
            for (i = 0; i < len; i += 1) {
                if (i > 0) {
                    sb.append(",\n");
                }
                for (int j = 0; j < newindent; j += 1) {
                    sb.append(' ');
                }
                sb.append(this.myArrayList.get(i).toString(indentFactor, newindent));
            }
            sb.append('\n');
            for (i = 0; i < indent; i += 1) {
                sb.append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Write the contents of the JSONArray as JSON text to a writer.
     * For compactness, no whitespace is added.
     * <p/>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JSONException
     */
    @Override
    public Writer write(Writer writer) throws JSONException {
        try {
            boolean b = false;
            int len = length();

            writer.write('[');

            for (int i = 0; i < len; i += 1) {
                if (b) {
                    writer.write(',');
                }
                JSONType v = this.myArrayList.get(i);
                if (v.isArray() || v.isObject()) {
                    v.write(writer);
                } else {
                    writer.write(v.toString());
                }
                b = true;
            }
            writer.write(']');
            return writer;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    public JSONArray addAll(JSONArray array) {
        for (JSONType jsonType : array) {
            add(jsonType);
        }
        return this;
    }

    public List<JSONType> toList() {
        return new ArrayList<>(myArrayList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONArray array = (JSONArray) o;
        return myArrayList.equals(array.myArrayList);
    }

    @Override
    public int hashCode() {
        return myArrayList.hashCode();
    }
}