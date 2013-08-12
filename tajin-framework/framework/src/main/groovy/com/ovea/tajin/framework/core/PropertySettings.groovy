package com.ovea.tajin.framework.core

import com.google.common.base.Function
import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.google.common.collect.Multimap
import com.google.common.collect.TreeMultimap
import com.ovea.tajin.framework.util.MissingPropertySettingException
import com.ovea.tajin.framework.util.PropertyPlaceholderResolver
import com.ovea.tajin.framework.util.SystemPropertiesMode

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-08-12
 */
final class PropertySettings {

    private final PropertyPlaceholderResolver resolver = new PropertyPlaceholderResolver();
    private final Properties properties;

    public PropertySettings() {
        this(new Properties());
    }

    public PropertySettings(Map map) {
        this()
        map?.each { k, v -> properties.setProperty(k as String, v as String) }
    }

    public PropertySettings(Properties properties) {
        this.properties = properties;
        this.resolver.setSystemPropertiesMode(SystemPropertiesMode.OVERRIDE);
    }

    public PropertySettings(Resource resource) {
        if (!resource.isExist())
            throw new IllegalArgumentException("Inexisting resource: " + resource);
        InputStream is = resource.getInput();
        this.properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unreadable resource: " + resource);
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
        this.resolver.setSystemPropertiesMode(SystemPropertiesMode.OVERRIDE);
    }

    public PropertySettings plus(Map<String, String> m) {
        properties.putAll(m);
        return this;
    }

    public File getPath(String key) {
        return new File(getRequired(key));
    }

    public File getPath(String key, File

    def) {
        String v = resolve(key);
        return v == null ? def: new File(v);
    }

    public String getString(String key) {
        return getRequired(key);
    }

    public String getString(String key, String

    def) {
        String v = resolve(key);
        return v == null ? def: v;
    }

    public List<String> getStrings(String key) {
        return split(getRequired(key));
    }

    public List<String> getStrings(String key, String...

    def) {
        String v = resolve(key);
        return v == null ? Arrays.asList(def) : split(v);
    }

    public List<String> getStrings(String key, List<String>

    def) {
        String v = resolve(key);
        return v == null ? def: split(v);
    }

    private List<String> split(String s) {
        return Lists.newArrayList(Iterables.transform(Lists.newArrayList(s.split(",|;")), new Function<String, String>() {
            @Override
            public String apply(java.lang.String input) {
                return input.trim();
            }
        }));
    }

    public Resource getResource(String key) {
        return Resource.resource(getRequired(key));
    }

    public Resource getResource(String key, String

    def) {
        String v = resolve(key);
        return Resource.resource(v == null ? def: v);
    }

    private String getRequired(String key) throws MissingPropertySettingException {
        String v = resolve(key);
        if (v == null)
            throw new MissingPropertySettingException(key);
        return v;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    private String resolve(String key) {
        return resolver.resolve(key, properties);
    }

    public long getLong(String key) {
        return Long.parseLong(getRequired(key));
    }

    public long getLong(String key, long

    def) {
        String v = resolve(key);
        return v == null ? def: Long.parseLong(v);
    }

    public int getInt(String key) {
        return Integer.parseInt(getRequired(key));
    }

    public int getInt(String key, int

    def) {
        String v = resolve(key);
        return v == null ? def: Integer.parseInt(v);
    }

    public boolean getBoolean(String key) {
        return Boolean.valueOf(getRequired(key));
    }

    public boolean getBoolean(String key, boolean

    def) {
        String v = resolve(key);
        return v == null ? def: Boolean.valueOf(v);
    }

    public <E extends Enum<E>> E getEnum(Class<E> type, String key) {
        return Enum.valueOf(type, getRequired(key));
    }

    public <E extends Enum<E>> E getEnum(Class<E> type, String key, E

    def) {
        String v = resolve(key);
        return v == null ? def: Enum.valueOf(type, v);
    }

    public boolean has(String key) {
        return resolve(key) != null;
    }

    public List<Map<String, String>> getList(String prefix) {
        Multimap<Integer, String> map = TreeMultimap.create();
        prefix = prefix + '.';
        for (String s : properties.stringPropertyNames()) {
            if (s.startsWith(prefix)) {
                int end = s.indexOf('.', prefix.length());
                map.put(Integer.parseInt(s.substring(prefix.length(), end)), s.substring(end + 1));
            }
        }
        List<Map<String, String>> list = new ArrayList<>(map.size());
        for (Integer i : new TreeSet<>(map.keySet())) {
            Map<String, String> o = new HashMap<>();
            for (String prop : map.get(i)) {
                String v = resolve(prefix + i + '.' + prop);
                o.put(prop, v == null ? null : v.trim());
            }
            list.add(o);
        }
        return list;
    }

}