package com.example.checkin;

import android.Manifest;

import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapActivityTest {

    @Rule
    public ActivityScenarioRule<MapActivity> scenario = new ActivityScenarioRule<>(MapActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    );

    @Test
    public void testMapDisplay() throws InterruptedException {
        // Initialize UI Automator
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Wait for the map to load
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        // Handle the permission dialog
        Thread.sleep(2000);
        UiSelector selector = new UiSelector().className("android.widget.Button");
        if (device.findObject(selector).exists()) {
            try {
                device.findObject(selector).click();
            } catch (Exception e) {
                // Log or handle the exception if needed
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testBackButton() {
        scenario.getScenario().onActivity(activity -> {
            assertThat(activity, notNullValue());
        });

        // Click the back button
        onView(withId(R.id.btnBack)).perform(click());
    }

    @Test
    public void testMapZoomFunctionality() {
        scenario.getScenario().onActivity(activity -> {
            assertThat(activity, notNullValue());
        });

        // Wait for the map to load before performing actions
        try {
            Thread.sleep(10000); // Wait for 10 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Perform zoom in and zoom out gestures on the map
        onView(withId(R.id.map)).perform(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.BOTTOM_RIGHT,
                GeneralLocation.CENTER, Press.FINGER));
        onView(withId(R.id.map)).perform(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER,
                GeneralLocation.BOTTOM_RIGHT, Press.FINGER));
    }

    @Test
    public void testMapMovements() {
        scenario.getScenario().onActivity(activity -> {
            assertThat(activity, notNullValue());
        });

        // Wait for the map to load before performing actions
        try {
            Thread.sleep(5000); // Wait for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Swipe left and up to test map movements
        onView(withId(R.id.map)).perform(swipeLeft());
        onView(withId(R.id.map)).perform(swipeUp());
    }
}
