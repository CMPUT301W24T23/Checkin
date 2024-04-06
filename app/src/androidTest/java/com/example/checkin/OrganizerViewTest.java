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
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Or;

import java.util.ArrayList;


// https://developer.android.com/training/testing/espresso/idling-resource
// https://stackoverflow.com/a/41638243
@RunWith(AndroidJUnit4.class)
@LargeTest

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
       // onView(withId(R.id.progress)).check(matches(isDisplayed()));

        // start idling resource
        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // check if switches to organizer view
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));


    }

    @Test
    public void testingevent()  {

        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
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

        ArrayList<Event> events = new ArrayList<>();
        Event mockevent = new Event("1234");
        mockevent.setEventname("BasketBall Game");
        mockevent.setEventDetails("Starts early");
        mockevent.setEventDate("2024-09-08");
        mockevent.setLocation("Gym");
        mockevent.setEventTime("4:00");
        mockevent.setQrcodeid("12345abc");
        events.add(mockevent);


        // Launch OrganizerViewActivity
        ActivityScenario<OrganizerView> organizerActivityScenario = ActivityScenario.launch(OrganizerView.class);

        EventArrayAdapter arrayadapter = new EventArrayAdapter(InstrumentationRegistry.getInstrumentation().getTargetContext(), events);
        organizerActivityScenario.onActivity(activity -> {


            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            OrganizerFragment1 fragment = (OrganizerFragment1) fragmentManager.findFragmentByTag("organizer_fragment_tag");
           // if (fragment != null && fragment.getView() != null) {

                fragment.addEvent(mockevent);
                ListView listView = fragment.getView().findViewById(R.id.events);
                listView.setAdapter(arrayadapter);
            //}
        });
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

        onView(withText("BasketBall Game")).perform(click());


    }

    @Test
    public void testbackbutton(){

        idlingResource.increment();

        // Perform click action to navigate to the organizer view
        onView(withId(R.id.organizerbtn)).perform(click());
        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));

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
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
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
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
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


    /*@Test
    public void addeventTest(){

        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
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

    }*/

    @Test
    public void testNotitificationFragment() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
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
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
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

        //click on attendees button from nav bar
        onView(withId(R.id.attendees)).perform(click());

        // Check if the events for selecting attendee options fragment is displayed
        onView(withId(R.id.chooseeventfrag)).check(matches(isDisplayed()));
    }

    public void addevent(){
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
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

        ArrayList<Event> events = new ArrayList<>();
        Event mockevent = new Event("1234");
        mockevent.setEventname("BasketBall Game");
        mockevent.setEventDetails("Starts early");
        mockevent.setEventDate("2024-09-08");
        mockevent.setLocation("Gym");
        mockevent.setEventTime("4:00");
        mockevent.setQrcodeid("12345abc");
        events.add(mockevent);


        // Launch OrganizerViewActivity
        ActivityScenario<OrganizerView> organizerActivityScenario = ActivityScenario.launch(OrganizerView.class);

        EventArrayAdapter arrayadapter = new EventArrayAdapter(InstrumentationRegistry.getInstrumentation().getTargetContext(), events);
        organizerActivityScenario.onActivity(activity -> {



            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            OrganizerFragment1 fragment = (OrganizerFragment1) fragmentManager.findFragmentByTag("organizer_fragment_tag");
            // if (fragment != null && fragment.getView() != null) {

            fragment.addEvent(mockevent);
            ListView listView = fragment.getView().findViewById(R.id.events);
            listView.setAdapter(arrayadapter);
            //}
        });
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

        onView(withText("BasketBall Game")).perform(click());

    }




    @Test
    public void testshareqrcode(){


        addevent();
        // click on event that was added (called Event Name, previously added)
        //onView(withText("Basketball Game")).perform(click());

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


        addevent();
        // check if it switches to event details page
        onView(withId(R.id.eventdet_org)).check(matches(isDisplayed()));

        // click on event attendees page
        onView(withId(R.id.attendeeslistbtn)).perform(click());
        // check if it shows attendee options
        onView(withId(R.id.attendeeslisted_frag)).check(matches(isDisplayed()));

        // click on signed up attendees
        onView(withId(R.id.signedinbtn)).perform(click());
        // check to see signed up list of attendees
        onView(withId(R.id.signinlist_frag)).check(matches(isDisplayed()));

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
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
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

        // Click on the button to add an event poster
        onView(withId(R.id.addeventbtn)).perform(click());

        // Check if the add event is displayed
        onView(withId(R.id.createfragment)).check(matches(isDisplayed()));

    }

}
