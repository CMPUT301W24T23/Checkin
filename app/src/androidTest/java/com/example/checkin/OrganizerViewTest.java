package com.example.checkin;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


// https://developer.android.com/training/testing/espresso/idling-resource
// https://stackoverflow.com/a/41638243
@RunWith(AndroidJUnit4.class)
@LargeTest

// need to comment out line 129 in Database class before testing
// as it causes the app to close due to unique id generated not being set yet
public class OrganizerViewTest {

    @Rule
    public GrantPermissionRule permissionRule2 = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    private ViewIdlingResource idlingResource = new ViewIdlingResource(R.id.progress);
    @Test
    public void testchangeorganizer(){
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());
        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));

        // start idling resource
        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // check if switches to organizer view
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));


    }

    @Test
    public void testbackbutton(){

        idlingResource.increment();

        // Perform click action to navigate to the organizer view
        onView(withId(R.id.organizerbtn)).perform(click());
        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));

        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // set up idling resource so data loads
        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on back button
        onView(withId(R.id.backbtn)).check(matches(isDisplayed())).perform(click());

        // check if it goes to homepage
        onView(withId(R.id.main_activity_page)).check(matches(isDisplayed()));

    }


    @Test
    public void testMessages() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on messages from nav bar
        onView(withId(R.id.messages)).perform(click());

        // Check if the announcements options fragment is displayed
        onView(withId(R.id.messageslisted_frag)).check(matches(isDisplayed()));

    }

    @Test
    public void testMilestones() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on messages from navbar
        onView(withId(R.id.messages)).perform(click());

        // wait for data to load
        idlingResource.increment();
        // Check if the announcements fragment is displayed
        onView(withId(R.id.messageslisted_frag)).check(matches(isDisplayed()));
        // click on view milestones button
        onView(withId(R.id.milestonebtn)).perform(click());
        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);
        // check if milestone fragment is displayed
        onView(withId(R.id.milestones_frag)).check(matches(isDisplayed()));

    }


    @Test
    public void addeventTest(){

        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // add event, and put in information
        onView(withId(R.id.addeventbtn)).perform(click());
        onView(withId(R.id.createfragment)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.etEventName)).perform(ViewActions.clearText()).perform(ViewActions.typeText("Soccer Game"));
        onView(ViewMatchers.withId(R.id.etEventDate)).perform(ViewActions.clearText()).perform(ViewActions.typeText("2024-06-08"));
        onView(ViewMatchers.withId(R.id.etEventTime)).perform(ViewActions.clearText()).perform(ViewActions.typeText("9:00"));
        onView(ViewMatchers.withId(R.id.etEventdetails)).perform(ViewActions.clearText()).perform(ViewActions.typeText("Come early."));
        onView(ViewMatchers.withId(R.id.etlocation)).perform(ViewActions.clearText()).perform(ViewActions.typeText("University Gym."));
        // close keyboard
        Espresso.closeSoftKeyboard();
        // click on generate qr code
        onView(withId(R.id.btnGenerateQR)).perform(click());
        //click on create event
        onView(withId(R.id.createeventbtn)).perform(click());

        // wait for data to load
        idlingResource.increment();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);
        // check if fragment that lists events is displayed
        onView(withId(R.id.org_frag1)).check(matches(isDisplayed()));

    }

    @Test
    public void testNotitificationFragment() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on messages from navbar
        onView(withId(R.id.messages)).perform(click());

        idlingResource.increment();
        // Check if the announcements fragment is displayed
        onView(withId(R.id.messageslisted_frag)).check(matches(isDisplayed()));
        // click on send notification button
        onView(withId(R.id.sendmssgbtn)).perform(click());
        // wait for event data to load
        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);
        // check if select event to send message to fragment is displayed
        onView(withId(R.id.selectmessagesfrag)).check(matches(isDisplayed()));

    }

    @Test
    public void testAttendees() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        //clcik on attendees button from nav bar
        onView(withId(R.id.attendees)).perform(click());

        // Check if the events for selecting attendee options fragment is displayed
        onView(withId(R.id.chooseeventfrag)).check(matches(isDisplayed()));
    }


    @Test
    public void testshareqrcode(){

        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on event that was added (called Event Name, previously added)
        onView(withText("Soccer Game")).perform(click());

        // check if it switches to event details page
        onView(withId(R.id.eventdet_org)).check(matches(isDisplayed()));
        // click on event qr code button
        onView(withId(R.id.codebtn)).perform(click());
        // check if share qr code fragment is displayed
        onView(withId(R.id.sharecode_frag)).check(matches(isDisplayed()));
        // check if qr code image is displayed
        onView(withId(R.id.imageCode)).check(matches(isDisplayed()));
    }

    @Test
    public void testseeattendees(){
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));
        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);


        // click on event name for event previously added
        onView(withText("Soccer Game")).perform(click());

        // check if it switches to event details page
        onView(withId(R.id.eventdet_org)).check(matches(isDisplayed()));

        // click on event attendees page
        onView(withId(R.id.attendeeslistbtn)).perform(click());
        // check if it shows attendee options
        onView(withId(R.id.attendeeslisted_frag)).check(matches(isDisplayed()));

    }


    @Test
    public void testGeoLocationTracking() {
        // click on the organizer button to navigate to the organizer view
        onView(withId(R.id.organizerbtn)).perform(click());

        // check if the organizer view is displayed
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // click on the CheckBox to enable GeoLocation tracking
        onView(withId(R.id.checkbox_geo_tracking)).perform(click());
        // assuming there is a button or switch for enabling GeoLocation tracking
        // click on the button/switch to enable GeoLocation tracking
        //onView(withId(R.id.checkbox_geo_tracking)).perform(click());

        // check if the CheckBox is checked
        onView(withId(R.id.checkbox_geo_tracking)).check(matches(isChecked()));
    }


    @Test
    public void testAddEventPoster() {
        // Click on the organizer button to navigate to the organizer view
        onView(withId(R.id.organizerbtn)).perform(click());

        // Check if the organizer view is displayed
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // Click on the button to add an event poster
        onView(withId(R.id.btnAddPoster)).perform(click());
        //onView(withId(R.id.ivEventPoster)).perform(click());

        // Check if the ImageView for the event poster is displayed
        onView(withId(R.id.ivEventPoster)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddEvent() {
        // Click on the organizer button to navigate to the organizer view
        onView(withId(R.id.organizerbtn)).perform(click());

        // Check if the organizer view is displayed
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // Click on the button to add an event poster
        onView(withId(R.id.addeventbtn)).perform(click());

        // Check if the add event is displayed
        onView(withId(R.id.createfragment)).check(matches(isDisplayed()));

    }

    @Test
    public void testsignedin(){
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        idlingResource.increment();
        // click on attendees button from nav bar
        onView(withId(R.id.attendees)).perform(click());

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on name for event previously added
        onView(withText("Soccer Game")).perform(click());

        // check if it shows attendees list fragment
        onView(withId(R.id.attendeeslisted_frag)).check(matches(isDisplayed()));
        // click on signed up attendees
        onView(withId(R.id.signedinbtn)).perform(click());
        // check to see signed up list of attendees
        onView(withId(R.id.signinlist_frag)).check(matches(isDisplayed()));
    }

    @Test
    public void testcheckedinattendees(){
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        idlingResource.increment();
        // click on attendee button from navbar
        onView(withId(R.id.attendees)).perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);
        // click on event that matched with one previously added
        onView(withText("Soccer Game")).perform(click());

        // check if it shows attendees list fragment
        onView(withId(R.id.attendeeslisted_frag)).check(matches(isDisplayed()));
        // click on checked in attendees button
        onView(withId(R.id.checkedinbtn)).perform(click());
        // check to see checked in list of attendees
        onView(withId(R.id.checkinlist_frag)).check(matches(isDisplayed()));
    }

}
