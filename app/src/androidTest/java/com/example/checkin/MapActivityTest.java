package com.example.checkin;

import android.Manifest;

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

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
        onView(withId(R.id.map)).check(matches(ViewMatchers.isDisplayed()));

        // Handle the permission dialog
        Thread.sleep(2000); // Wait for the dialog to appear
        UiSelector selector = new UiSelector().className("android.widget.Button");
        if (device.findObject(selector).exists()) {
            try {
                device.findObject(selector).click();
            } catch (UiObjectNotFoundException e) {
                // Log or handle the exception if needed
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testBackButton() {
        // Click the back button
        onView(withId(R.id.btnBack)).perform(click());
    }
}
