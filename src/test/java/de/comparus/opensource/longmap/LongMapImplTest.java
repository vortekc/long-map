package de.comparus.opensource.longmap;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LongMapImplTest {

    @Test
    public void put_value() {
        LongMap<String> map = new LongMapImpl<>();


        for (int i=0; i<=24; i++) {
            map.put(i, String.valueOf(i));
        }

        String val = map.get(16);
        System.out.println("dd");
    }

    @Test
    public void put_same_key_with_different_value() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(1, "TWO");

        Assert.assertEquals("TWO", map.get(1));
    }

    @Test
    public void put_with_collisions() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(4, "ONE");
        map.put(100, "TWO");

        Assert.assertEquals("ONE", map.get(4));
        Assert.assertEquals("TWO", map.get(100));
    }

    @Test
    public void remove() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(2, "TWO");
        map.put(3, "THREE");

        String removedValue = map.remove(2);

        Assert.assertEquals("TWO", removedValue);
        Assert.assertEquals(2, map.size());
    }

    @Test
    public void remove_not_exist_key() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(2, "TWO");
        map.put(3, "THREE");

        String removedValue = map.remove(12);

        Assert.assertNull(removedValue);
        Assert.assertEquals(3, map.size());
    }

    @Test
    public void containsKey_positive() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(2, "TWO");
        map.put(3, "THREE");

        boolean isContains = map.containsKey(2);

        Assert.assertTrue(isContains);
    }

    @Test
    public void containsKey_negative() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(2, "TWO");
        map.put(3, "THREE");

        boolean isContains = map.containsKey(21);

        Assert.assertFalse(isContains);
    }

    @Test
    public void containsKey_positive_with_collisions() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(2, "TWO");
        map.put(3, "THREE");

        // In case when key have value 100 and 4, they will be placed in the same bucket
        map.put(100, "ONE");
        map.put(4, "FOUR");

        boolean isContains = map.containsKey(4);
        Assert.assertTrue(isContains);

        isContains = map.containsKey(100);
        Assert.assertTrue(isContains);
    }

    @Test
    public void containsValue_positive_with_collisions() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(2, "TWO");
        map.put(3, "THREE");

        // In case when key have value 100 and 4, they will be placed in the same bucket
        map.put(100, "HUNDRED");
        map.put(4, "FOUR");

        boolean isContains = map.containsValue("FOUR");
        Assert.assertTrue(isContains);

        isContains = map.containsValue("HUNDRED");
        Assert.assertTrue(isContains);
    }

    @Test
    public void containsValue_negative() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(2, "TWO");
        map.put(3, "THREE");

        boolean isContains = map.containsValue("FIVE");
        Assert.assertFalse(isContains);
    }

    @Test
    public void containsValue_after_remove_negative() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(2, "TWO");
        map.put(4, "FOUR");
        map.put(100, "HUNDRED");

        map.remove(100);

        boolean isContains = map.containsValue("HUNDRED");
        Assert.assertFalse(isContains);
    }

    @Test
    public void size() {
        LongMap<String> map = new LongMapImpl<>();

        for (int i=0; i<=14; i++) {
            map.put(i, String.valueOf(i));
        }

        Assert.assertEquals(15, map.size());
    }

    @Test
    public void clear_all() {
        LongMap<String> map = new LongMapImpl<>();

        for (int i=0; i<=24; i++) {
            map.put(i, String.valueOf(i));
        }

        map.clear();

        Assert.assertEquals(0, map.size());

        for (int i=0; i<=24; i++) {
            Assert.assertNull(map.get(i));
        }
    }

    @Test
    public void keys() {
        LongMap<String> map = new LongMapImpl<>();

        for (int i=0; i<=14; i++) {
            map.put(i, String.valueOf(i));
        }

        long[] keys = map.keys();
        Assert.assertEquals(15, keys.length);
    }

    @Test
    public void keys_not_ordered() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(12, "TWO");
        map.put(100, "HUNDRED");
        map.put(41, "FOUR");
        map.put(101, "HUNDRED ONE");

        long[] keys = map.keys();

        Assert.assertArrayEquals(new long[] {1, 100, 101, 41, 12}, keys);
    }

    @Test
    public void values_not_ordered() {
        LongMap<String> map = new LongMapImpl<>();

        map.put(1, "ONE");
        map.put(12, "TWO");
        map.put(100, "HUNDRED");
        map.put(41, "FOUR");
        map.put(101, "HUNDRED ONE");

        List<String> expected = Stream.of("ONE", "TWO", "HUNDRED", "FOUR", "HUNDRED ONE")
                .sorted()
                .collect(Collectors.toList());

        List<String> values = map.values();
        Collections.sort(values);

        Assert.assertEquals(5, values.size());
        Assert.assertEquals(expected, values);
    }
}