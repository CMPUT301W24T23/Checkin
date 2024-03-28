package com.example.checkin;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.Test;

public class AttendeeEventTest {

    /**
     * Test an attendee checking in
     */
    @Test
    public void CheckIn(){
        Event e = new Event("Event1", "ME");
        Attendee a = new Attendee();
        e.userCheckIn(a);
        assertTrue(e.IsCheckedIn(a));
        e.userCheckIn(a);   //check out
        assertFalse(e.IsCheckedIn(a));
    }

    /**
     * Test attendee subscribing and unsubscribing to notifications
     */
    @Test
    public void SubscriptionTest(){
        Event e = new Event("Event1", "ME");
        Attendee a = new Attendee();

        assertFalse(e.IsSubscribed(a));
        e.userSubs(a);
        assertTrue(e.IsSubscribed(a));
        e.userUnSubs(a);
        assertFalse(e.IsSubscribed(a));
    }


    /**
     * Test check in count for the AttendeeList class
     */
    @Test
    public void AttendeeCount(){
        AttendeeList a = new AttendeeList();
        Event e = new Event("Event1", "ME");
        Attendee new1 = new Attendee();
        Attendee new2 = new Attendee();
        Attendee new3 = new Attendee();
        a.addAttendee(new1);
        a.addAttendee(new2);
        a.addAttendee(new3);

        e.userCheckIn(new1);
        e.userCheckIn(new2);
        e.userCheckIn(new3);

        //Test checked in count
        assertEquals(3, a.CheckedInCount(e));

        //Check out and test again
        e.userCheckIn(new1);
        assertEquals(2, a.CheckedInCount(e));
    }




}
