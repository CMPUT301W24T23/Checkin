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

import androidx.fragment.app.FragmentManager;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;


// https://developer.android.com/training/testing/espresso/idling-resource
// https://stackoverflow.com/a/41638243
@RunWith(AndroidJUnit4.class)
@LargeTest

// need to comment out line 129 in Database class before testing
// as it causes the app to close due to unique id generated not being set yet
public class OrganizerSideTest {

    @Rule
    public GrantPermissionRule permissionRule2 = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<OrganizerView> scenario = new
            ActivityScenarioRule<OrganizerView>(OrganizerView.class);
    private ViewIdlingResource idlingResource = new ViewIdlingResource(R.id.progress);


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
        mockevent.setQrcodeid("12345abc");
        events.add(mockevent);
        EventArrayAdapter arrayadapter = new EventArrayAdapter(InstrumentationRegistry.getInstrumentation().getTargetContext(), events);
        scenario.getScenario().onActivity(activity -> {

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            OrganizerFragment1 fragment = (OrganizerFragment1) fragmentManager.findFragmentByTag("organizer_fragment_tag");
            // if (fragment != null && fragment.getView() != null) {

            ListView listView = fragment.getView().findViewById(R.id.events);
            listView.setAdapter(arrayadapter);
            //}
        });


    }


}
