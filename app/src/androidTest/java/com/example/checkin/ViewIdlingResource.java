package com.example.checkin;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static java.util.regex.Pattern.matches;

import android.view.View;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.TreeIterables;

import org.hamcrest.Matcher;

public class ViewIdlingResource implements IdlingResource {

    private final int viewId;
    private boolean isIdle = false;
    private int counter;
    private ResourceCallback resourceCallback;

    public ViewIdlingResource(int viewId) {
        this.viewId = viewId;
        this.counter = 1;

    }
    @Override
    public String getName() {
        return "ViewIdlingResource";
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    @Override
    public boolean isIdleNow() {
        return counter == 0;
    }

    public void increment() {
        counter++;
    }

    public void decrement() {
        counter--;
        if (isIdleNow() && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
    }

    public void reset() {
        counter = 0;
    }




}
