package com.ucombinator.needle.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TestActivity extends Activity {
	private Integer a,b,c;
    @Override
    protected void onCreate(Bundle b) {
    	test(b.getInt("myKey"));
    }
    
    private int test(int a) {
        if(a == 1) {
        	b = a;
        	c = 1;
        } else {
        	b = 2;
        	c = 2;
        }
        return b + c;
    }
}

