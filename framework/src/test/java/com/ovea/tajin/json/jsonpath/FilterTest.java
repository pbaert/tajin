package com.ovea.tajin.json.jsonpath;

import com.ovea.tajin.json.*;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.ovea.tajin.json.JSON.filter;
import static com.ovea.tajin.json.JSON.where;
import static java.util.Arrays.asList;
import static junit.framework.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/5/12
 * Time: 12:27 PM
 */
public class FilterTest {

    @Test
    public void is_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("foo", "foo");
        check.put("bar", null);

        assertTrue(filter(where("bar").is(null)).accept(new JSONObject(check)));
        assertTrue(filter(where("foo").is("foo")).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").is("xxx")).accept(new JSONObject(check)));
        assertFalse(filter(where("bar").is("xxx")).accept(new JSONObject(check)));
    }

    @Test
    public void ne_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("foo", "foo");
        check.put("bar", null);

        assertTrue(filter(where("foo").ne(null)).accept(new JSONObject(check)));
        assertTrue(filter(where("foo").ne("not foo")).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").ne("foo")).accept(new JSONObject(check)));
        assertFalse(filter(where("bar").ne(null)).accept(new JSONObject(check)));
    }

    @Test
    public void gt_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("foo", 12.5D);
        check.put("foo_null", null);

        assertTrue(filter(where("foo").gt(12D)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").gt(null)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").gt(20D)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo_null").gt(20D)).accept(new JSONObject(check)));
    }

    @Test
    public void gte_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("foo", 12.5D);
        check.put("foo_null", null);

        assertTrue(filter(where("foo").gte(12D)).accept(new JSONObject(check)));
        assertTrue(filter(where("foo").gte(12.5D)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").gte(null)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").gte(20D)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo_null").gte(20D)).accept(new JSONObject(check)));
    }

    @Test
    public void lt_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("foo", 10.5D);
        check.put("foo_null", null);

        assertTrue(filter(where("foo").lt(12D)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").lt(null)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").lt(5D)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo_null").lt(5D)).accept(new JSONObject(check)));
    }

    @Test
    public void lte_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("foo", 12.5D);
        check.put("foo_null", null);

        assertTrue(filter(where("foo").lte(13D)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").lte(null)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").lte(5D)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo_null").lte(5D)).accept(new JSONObject(check)));
    }

    @Test
    public void in_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("item", 3);
        check.put("null_item", null);

        assertTrue(filter(where("item").in(1, 2, 3)).accept(new JSONObject(check)));
        assertTrue(filter(where("item").in(asList(1, 2, 3))).accept(new JSONObject(check)));
        assertFalse(filter(where("item").in(4, 5, 6)).accept(new JSONObject(check)));
        assertFalse(filter(where("item").in(asList(4, 5, 6))).accept(new JSONObject(check)));
        assertFalse(filter(where("item").in(asList('A'))).accept(new JSONObject(check)));
        assertFalse(filter(where("item").in(asList((Object) null))).accept(new JSONObject(check)));

        assertTrue(filter(where("null_item").in((Object) null)).accept(new JSONObject(check)));
        assertFalse(filter(where("null_item").in(1, 2, 3)).accept(new JSONObject(check)));
    }

    @Test
    public void nin_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("item", 3);
        check.put("null_item", null);

        assertTrue(filter(where("item").nin(4, 5)).accept(new JSONObject(check)));
        assertTrue(filter(where("item").nin(asList(4, 5))).accept(new JSONObject(check)));
        assertTrue(filter(where("item").nin(asList('A'))).accept(new JSONObject(check)));
        assertTrue(filter(where("null_item").nin(1, 2, 3)).accept(new JSONObject(check)));
        assertTrue(filter(where("item").nin(asList((Object) null))).accept(new JSONObject(check)));

        assertFalse(filter(where("item").nin(3)).accept(new JSONObject(check)));
        assertFalse(filter(where("item").nin(asList(3))).accept(new JSONObject(check)));
    }

    @Test
    public void all_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("items", asList(1, 2, 3));

        assertTrue(filter(where("items").all(1, 2, 3)).accept(new JSONObject(check)));
        assertFalse(filter(where("items").all(1, 2, 3, 4)).accept(new JSONObject(check)));
    }

    @Test
    public void size_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("items", asList(1, 2, 3));
        check.put("items_empty", Collections.emptyList());

        assertTrue(filter(where("items").size(3)).accept(new JSONObject(check)));
        assertTrue(filter(where("items_empty").size(0)).accept(new JSONObject(check)));
        assertFalse(filter(where("items").size(2)).accept(new JSONObject(check)));
    }

    @Test
    public void exists_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("foo", "foo");
        check.put("foo_null", null);

        assertTrue(filter(where("foo").exists(true)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo").exists(false)).accept(new JSONObject(check)));

        assertTrue(filter(where("foo_null").exists(true)).accept(new JSONObject(check)));
        assertFalse(filter(where("foo_null").exists(false)).accept(new JSONObject(check)));

        assertTrue(filter(where("bar").exists(false)).accept(new JSONObject(check)));
        assertFalse(filter(where("bar").exists(true)).accept(new JSONObject(check)));
    }

    @Test
    public void type_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 1);
        check.put("long", 1L);
        check.put("double", 1.12D);

        assertFalse(filter(where("string_null").type(String.class)).accept(new JSONObject(check)));
        assertTrue(filter(where("string").type(String.class)).accept(new JSONObject(check)));
        assertFalse(filter(where("string").type(Number.class)).accept(new JSONObject(check)));

        assertTrue(filter(where("int").type(Integer.class)).accept(new JSONObject(check)));
        assertFalse(filter(where("int").type(Long.class)).accept(new JSONObject(check)));

        assertTrue(filter(where("long").type(Long.class)).accept(new JSONObject(check)));
        assertFalse(filter(where("long").type(Integer.class)).accept(new JSONObject(check)));

        assertTrue(filter(where("double").type(Double.class)).accept(new JSONObject(check)));
        assertFalse(filter(where("double").type(Integer.class)).accept(new JSONObject(check)));
    }

    @Test
    public void pattern_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<>();
        check.put("name", "kalle");
        check.put("name_null", null);


        assertFalse(filter(where("name_null").regex(Pattern.compile(".alle"))).accept(new JSONObject(check)));
        assertTrue(filter(where("name").regex(Pattern.compile(".alle"))).accept(new JSONObject(check)));
        assertFalse(filter(where("name").regex(Pattern.compile("KALLE"))).accept(new JSONObject(check)));
        assertTrue(filter(where("name").regex(Pattern.compile("KALLE", Pattern.CASE_INSENSITIVE))).accept(new JSONObject(check)));

    }

    //-------------------------------------------------
    //
    // Single filter tests
    //
    //-------------------------------------------------

    @Test
    public void filters_can_be_combined() throws Exception {

        Map<String, Object> check = new HashMap<>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12D);

        Filter shouldMarch = filter(where("string").is("foo").and("int").lt(11));
        Filter shouldNotMarch = filter(where("string").is("foo").and("int").gt(11));

        assertTrue(shouldMarch.accept(new JSONObject(check)));
        assertFalse(shouldNotMarch.accept(new JSONObject(check)));
    }

    @Test
    public void filters_can_be_extended_with_new_criteria() throws Exception {

        Map<String, Object> check = new HashMap<>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12D);

        Filter filter = filter(where("string").is("foo").and("int").lt(11));

        assertTrue(filter.accept(new JSONObject(check)));

        filter.addCriteria(where("long").ne(1L));

        assertFalse(filter.accept(new JSONObject(check)));

    }

    @Test
    public void filters_criteria_can_be_refined() throws Exception {

        Map<String, Object> check = new HashMap<>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12D);

        Filter filter = filter(where("string").is("foo"));

        assertTrue(filter.accept(new JSONObject(check)));

        Criteria criteria = where("string").is("not eq");

        filter.addCriteria(criteria);

        assertFalse(filter.accept(new JSONObject(check)));


        filter = filter(where("string").is("foo").and("string").is("not eq"));
        assertFalse(filter.accept(new JSONObject(check)));


        filter = filter(where("string").is("foo").and("string").is("foo"));
        assertTrue(filter.accept(new JSONObject(check)));

    }


    @Test
    public void arrays_of_maps_can_be_filtered() throws Exception {


        Map<String, Object> rootGrandChild_A = new HashMap<>();
        rootGrandChild_A.put("name", "rootGrandChild_A");

        Map<String, Object> rootGrandChild_B = new HashMap<>();
        rootGrandChild_B.put("name", "rootGrandChild_B");

        Map<String, Object> rootGrandChild_C = new HashMap<>();
        rootGrandChild_C.put("name", "rootGrandChild_C");


        Map<String, Object> rootChild_A = new HashMap<>();
        rootChild_A.put("name", "rootChild_A");
        rootChild_A.put("children", asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        Map<String, Object> rootChild_B = new HashMap<>();
        rootChild_B.put("name", "rootChild_B");
        rootChild_B.put("children", asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        Map<String, Object> rootChild_C = new HashMap<>();
        rootChild_C.put("name", "rootChild_C");
        rootChild_C.put("children", asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        Map<String, Object> root = new HashMap<>();
        root.put("children", asList(rootChild_A, rootChild_B, rootChild_C));


        Filter customFilter = new Filter.FilterAdapter() {
            @Override
            public boolean accept(JSONType map) {
                return map.asObject().get("name").asString().equals("rootGrandChild_A");
            }
        };

        Filter rootChildFilter = filter(where("name").regex(Pattern.compile("rootChild_[A|B]")));
        Filter rootGrandChildFilter = filter(where("name").regex(Pattern.compile("rootGrandChild_[A|B]")));

        JSONArray read = JSON.expr(new JSONObject(root), "children[?].children[?][?]", rootChildFilter, rootGrandChildFilter, customFilter).asArray();


        System.out.println(read.size());
    }


    @Test
    public void arrays_of_objects_can_be_filtered() throws Exception {
        Map<String, Object> doc = new HashMap<>();
        doc.put("items", asList(1, 2, 3));

        Filter customFilter = new Filter.FilterAdapter() {
            @Override
            public boolean accept(JSONType o) {
                return 1 == o.asInt();
            }
        };

        JSONArray res = JSON.expr(new JSONObject(doc), "$.items[?]", customFilter).asArray();

        assertEquals(1, res.get(0).asInt());
    }

}
