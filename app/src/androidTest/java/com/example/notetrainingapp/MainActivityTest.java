package com.example.notetrainingapp;

import junit.framework.TestCase;

import org.junit.Test;

public class MainActivityTest extends TestCase {
    @Test
    public void test(){
        //MainActivity.stringCutting("1234567890",10);
        assertEquals("1234567890", MainActivity.stringCutting("1234567890",10));
    }
}