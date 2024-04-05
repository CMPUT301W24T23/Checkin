package com.example.checkin;

import android.os.IBinder;
import android.view.WindowManager;

import androidx.test.espresso.Root;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

//https://stackoverflow.com/questions/29896223/android-espresso-how-to-check-that-toast-message-is-not-shown
public class ToastMatcher extends TypeSafeMatcher<Root> {


    @Override
    protected boolean matchesSafely(Root item) {
        int type = item.getWindowLayoutParams().get().type;
        if ((type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)) {
            IBinder windowToken = item.getDecorView().getWindowToken();
            IBinder appToken = item.getDecorView().getApplicationWindowToken();
            if (windowToken == appToken) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is toast");

    }
}
