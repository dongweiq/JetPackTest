package com.honghe.jetpacktest;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void Testbool() {
        boolean bool = true;
        boolean bool2 = false;
        System.out.println(bool + "\t" + bool2);
    }
}