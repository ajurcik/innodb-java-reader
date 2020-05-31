package com.alibaba.innodb.java.reader.util;

import com.google.common.collect.ImmutableList;

import com.alibaba.innodb.java.reader.schema.Column;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.alibaba.innodb.java.reader.Constants.MAX_VAL;
import static com.alibaba.innodb.java.reader.Constants.MIN_VAL;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author xu.zx
 */
public class UtilsTest {

  @Test(expected = IllegalStateException.class)
  public void testCastCompareLenNotMatch() {
    List<Object> o1 = new ArrayList<>();
    o1.add("abc");
    o1.add("bcd");
    List<Object> o2 = new ArrayList<>();
    o2.add("xyz");
    Utils.castCompare(o1, o2);
  }

  @Test(expected = IllegalStateException.class)
  public void testCastCompareLenNotMatch2() {
    List<Object> o1 = new ArrayList<>();
    o1.add("abc");
    o1.add("bcd");
    List<Object> o2 = new ArrayList<>();
    o2.add(Utils.constructMinRecord(1));
    Utils.castCompare(o1, o2);
  }

  @Test(expected = ClassCastException.class)
  public void testCastCompareLenClassMatch() {
    List<Object> o1 = new ArrayList<>();
    o1.add("abc");
    List<Object> o2 = new ArrayList<>();
    o2.add(1);
    Utils.castCompare(o1, o2);
  }

  @Test(expected = ClassCastException.class)
  public void testCastCompareLenClassMatch2() {
    List<Object> o1 = new ArrayList<>();
    o1.add(12.45);
    List<Object> o2 = new ArrayList<>();
    o2.add(10L);
    Utils.castCompare(o1, o2);
  }

  @Test(expected = ClassCastException.class)
  public void testCastCompareLenClassMatch3() {
    List<Object> o1 = new ArrayList<>();
    o1.add(10);
    List<Object> o2 = new ArrayList<>();
    o2.add(10L);
    Utils.castCompare(o1, o2);
  }

  @Test
  public void testCastCompareOneColumn() {
    List<Object> o1 = new ArrayList<>();
    List<Object> o2 = new ArrayList<>();
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of();
    o2 = ImmutableList.of();
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = new ArrayList<>();
    o2 = ImmutableList.of();
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of(1);
    o2 = ImmutableList.of(2);
    assertThat(Utils.castCompare(o1, o2), is(-1));

    o1 = ImmutableList.of(100);
    o2 = ImmutableList.of(100);
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of(100);
    o2 = ImmutableList.of(50);
    assertThat(Utils.castCompare(o1, o2), is(1));

    o1 = ImmutableList.of("1");
    o2 = ImmutableList.of("2");
    assertThat(Utils.castCompare(o1, o2), is(-1));

    o1 = ImmutableList.of("100");
    o2 = ImmutableList.of("100");
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of("100");
    o2 = ImmutableList.of("50");
    assertThat(Utils.castCompare(o1, o2), is(-4));

    o1 = ImmutableList.of("100");
    o2 = Utils.constructMaxRecord(1);
    assertThat(Utils.castCompare(o1, o2), is(-1));

    o1 = ImmutableList.of("100");
    o2 = Utils.constructMinRecord(1);
    assertThat(Utils.castCompare(o1, o2), is(1));

    o1 = Utils.constructMaxRecord(1);
    o2 = ImmutableList.of("100");
    assertThat(Utils.castCompare(o1, o2), is(1));

    o1 = Utils.constructMinRecord(1);
    o2 = ImmutableList.of("100");
    assertThat(Utils.castCompare(o1, o2), is(-1));

    o1 = ImmutableList.of(true);
    o2 = ImmutableList.of(false);
    assertThat(Utils.castCompare(o1, o2), is(1));
  }

  @Test
  public void testCastCompareOneColumnSting() {
    List<Object> o1 = ImmutableList.of("A");
    List<Object> o2 = ImmutableList.of("A");
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of("A");
    o2 = ImmutableList.of("B");
    assertThat(Utils.castCompare(o1, o2), is(-1));

    o1 = ImmutableList.of("C");
    o2 = ImmutableList.of("B");
    assertThat(Utils.castCompare(o1, o2), is(1));

    o1 = ImmutableList.of("Hello");
    o2 = ImmutableList.of("world");
    assertThat(Utils.castCompare(o1, o2), lessThan(0));

    o1 = ImmutableList.of("Hello");
    o2 = ImmutableList.of("World");
    assertThat(Utils.castCompare(o1, o2), lessThan(0));

    // case insensitive
    o1 = ImmutableList.of("Hello");
    o2 = ImmutableList.of("hello");
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of("HELLO");
    o2 = ImmutableList.of("HELLO");
    assertThat(Utils.castCompare(o1, o2), is(0));

    // case sensitive
    o1 = ImmutableList.of("Hello");
    o2 = ImmutableList.of("hello");
    List<Column> columnList = ImmutableList.of(new Column().setCollation("utf8_bin"));
    assertThat(Utils.castCompare(o1, o2, columnList), lessThan(0));

    o1 = ImmutableList.of("HELLO");
    o2 = ImmutableList.of("hello");
    assertThat(Utils.castCompare(o1, o2, columnList), lessThan(0));

    o1 = ImmutableList.of("hello");
    o2 = ImmutableList.of("Hello");
    assertThat(Utils.castCompare(o1, o2, columnList), greaterThan(0));

    o1 = ImmutableList.of("HELLo");
    o2 = ImmutableList.of("HELLO");
    assertThat(Utils.castCompare(o1, o2, columnList), greaterThan(0));
  }

  @Test
  public void testCastCompareCompositeColumn() {
    List<Object> o1 = new ArrayList<>();
    o1.add(1);
    o1.add(2);
    List<Object> o2 = new ArrayList<>();
    o2.add(1);
    o2.add(3);
    assertThat(Utils.castCompare(o1, o2), is(-1));

    List<Column> columnList = ImmutableList.of(
        new Column().setCollation("utf8_bin"),
        new Column()
    );

    o1 = ImmutableList.of(100, 200);
    o2 = ImmutableList.of(1000, 2000);
    assertThat(Utils.castCompare(o1, o2), is(-1));

    o1 = ImmutableList.of("hello", 200);
    o2 = ImmutableList.of("Hello", 2000);
    assertThat(Utils.castCompare(o1, o2, columnList), greaterThan(0));

    o1 = ImmutableList.of("hello", 200);
    o2 = ImmutableList.of("Hello", 2000);
    assertThat(Utils.castCompare(o1, o2), lessThan(0));

    o1 = ImmutableList.of("hello", 200);
    o2 = ImmutableList.of("hello", 2000);
    assertThat(Utils.castCompare(o1, o2), lessThan(0));

    o1 = ImmutableList.of(100, 200);
    o2 = ImmutableList.of(100, 200);
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of("zhang", "xu");
    o2 = ImmutableList.of("zhang", "xu");
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of("ZHANG", "xu");
    o2 = ImmutableList.of("zhang", "xu");
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of("zhang", "xu");
    o2 = ImmutableList.of("zhang", "XU");
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of(1000, 2000);
    o2 = ImmutableList.of(100, 200);
    assertThat(Utils.castCompare(o1, o2), is(1));

    o1 = ImmutableList.of("Hello", 2000);
    o2 = ImmutableList.of("hello", 200);
    assertThat(Utils.castCompare(o1, o2), greaterThan(0));

    o1 = ImmutableList.of("Hello", 2000);
    o2 = ImmutableList.of("hello", 200);
    assertThat(Utils.castCompare(o1, o2, columnList), lessThan(0));

    o1 = ImmutableList.of("hello", 2000);
    o2 = ImmutableList.of("hello", 200);
    assertThat(Utils.castCompare(o1, o2), greaterThan(0));

    o1 = ImmutableList.of("hello", "world", "abc");
    o2 = ImmutableList.of("hello", "world", "bcd");
    assertThat(Utils.castCompare(o1, o2), lessThan(0));

    o1 = ImmutableList.of("hello", "world", "abc");
    o2 = ImmutableList.of("hello", "world", "abc");
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of("hello", "world", "bcd");
    o2 = ImmutableList.of("hello", "world", "abc");
    assertThat(Utils.castCompare(o1, o2), greaterThan(0));

    o1 = ImmutableList.of(100, 12.45);
    o2 = ImmutableList.of(100, 99d);
    assertThat(Utils.castCompare(o1, o2), lessThan(0));

    o1 = ImmutableList.of(100, 12.45);
    o2 = ImmutableList.of(100, 12.45);
    assertThat(Utils.castCompare(o1, o2), is(0));

    o1 = ImmutableList.of(100, 99.0);
    o2 = ImmutableList.of(100, 12.45);
    assertThat(Utils.castCompare(o1, o2), greaterThan(0));

    o1 = ImmutableList.of(100, new Integer(5));
    o2 = ImmutableList.of(100, 6);
    assertThat(Utils.castCompare(o1, o2), lessThan(0));

    o1 = ImmutableList.of(100, new Long(900L));
    o2 = ImmutableList.of(100, new Long(1000L));
    assertThat(Utils.castCompare(o1, o2), lessThan(0));

    o1 = ImmutableList.of(100, new Long(900L));
    o2 = ImmutableList.of(100, new Long(1000L));
    assertThat(Utils.castCompare(o1, o2), lessThan(0));

    o1 = ImmutableList.of(100, new Double(0.0));
    o2 = ImmutableList.of(100, new Double(0.0));
    assertThat(Utils.castCompare(o1, o2), is(0));

    // who comes first is bigger
    o1 = ImmutableList.of(100, MAX_VAL);
    o2 = ImmutableList.of(100, MAX_VAL);
    assertThat(Utils.castCompare(o1, o2), is(1));

    o1 = ImmutableList.of(100, MAX_VAL);
    o2 = ImmutableList.of(100, 20);
    assertThat(Utils.castCompare(o1, o2), is(1));

    o1 = ImmutableList.of(100, MAX_VAL);
    o2 = ImmutableList.of(100, Long.MAX_VALUE);
    assertThat(Utils.castCompare(o1, o2), is(1));

    o1 = ImmutableList.of(100, MAX_VAL);
    o2 = ImmutableList.of(100, MIN_VAL);
    assertThat(Utils.castCompare(o1, o2), is(1));

    // who comes first is smaller
    o1 = ImmutableList.of(100, MIN_VAL);
    o2 = ImmutableList.of(100, MIN_VAL);
    assertThat(Utils.castCompare(o1, o2), is(-1));

    o1 = ImmutableList.of(100, 20);
    o2 = ImmutableList.of(100, MIN_VAL);
    assertThat(Utils.castCompare(o1, o2), is(1));

    o1 = ImmutableList.of(100, Integer.MIN_VALUE);
    o2 = ImmutableList.of(100, MIN_VAL);
    assertThat(Utils.castCompare(o1, o2), is(1));

    o1 = ImmutableList.of(100, MIN_VAL);
    o2 = ImmutableList.of(100, MAX_VAL);
    assertThat(Utils.castCompare(o1, o2), is(-1));
  }

  @Test
  public void testProcessFileWithDelimiter() {
    String filePath = "src/test/resources/test.sql";
    String delimiter = ";";
    List<String> list = new ArrayList<>();
    Utils.processFileWithDelimiter(filePath, "UTF-8", s -> {
      if (s.startsWith("CREATE TABLE")) {
        list.add(s.trim());
      }
    }, delimiter);
    assertThat(list.isEmpty(), is(false));
    for (String s : list) {
      System.out.println(s);
      assertThat(s.startsWith("CREATE TABLE"), is(true));
      assertThat(s.endsWith(";"), is(true));
    }
  }

  @Test
  public void testParseDateTimeText() {
    String s = "2020-05-01 12:15:59";
    LocalDateTime dt = Utils.parseDateTimeText(s);
    assertThat(dt.toString(), is("2020-05-01T12:15:59"));

    s = "2020-05-01 12:15:59.5";
    dt = Utils.parseDateTimeText(s, 1);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.500"));

    s = "2020-05-01 12:15:59.50";
    dt = Utils.parseDateTimeText(s, 2);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.500"));

    s = "2020-05-01 12:15:59.25";
    dt = Utils.parseDateTimeText(s, 2);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.250"));

    s = "2020-05-01 12:15:59.000";
    dt = Utils.parseDateTimeText(s, 3);
    assertThat(dt.toString(), is("2020-05-01T12:15:59"));

    s = "2020-05-01 12:15:59.001";
    dt = Utils.parseDateTimeText(s, 3);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.001"));

    s = "2020-05-01 12:15:59.091";
    dt = Utils.parseDateTimeText(s, 3);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.091"));

    s = "2020-05-01 12:15:59.500";
    dt = Utils.parseDateTimeText(s, 3);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.500"));

    s = "2020-05-01 12:15:59.2500";
    dt = Utils.parseDateTimeText(s, 4);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.250"));

    s = "2020-05-01 12:15:59.25000";
    dt = Utils.parseDateTimeText(s, 5);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.250"));

    s = "2020-05-01 12:15:59.250000";
    dt = Utils.parseDateTimeText(s, 6);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.250"));

    s = "2020-05-01 12:15:59.250001";
    dt = Utils.parseDateTimeText(s, 6);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.250001"));

    s = "2020-05-01 12:15:59.050001";
    dt = Utils.parseDateTimeText(s, 6);
    assertThat(dt.toString(), is("2020-05-01T12:15:59.050001"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseDateTimeTextNegate2() {
    String s = "2020-05-01 12:15:59.456789";
    Utils.parseDateTimeText(s, 9);
  }

  @Test
  public void testParseTimeText() {
    String s = "12:15:59";
    LocalTime time = Utils.parseTimeText(s);
    assertThat(time.toString(), is("12:15:59"));

    s = "12:15:59.5";
    time = Utils.parseTimeText(s, 1);
    assertThat(time.toString(), is("12:15:59.500"));

    s = "12:15:59.50";
    time = Utils.parseTimeText(s, 2);
    assertThat(time.toString(), is("12:15:59.500"));

    s = "12:15:59.500";
    time = Utils.parseTimeText(s, 3);
    assertThat(time.toString(), is("12:15:59.500"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseTimeTextNegate2() {
    String s = "12:15:59.456789";
    Utils.parseTimeText(s, 9);
  }

}
