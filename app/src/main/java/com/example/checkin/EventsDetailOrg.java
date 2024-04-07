/**
 * Fragment for displaying event details and managing event actions for organizers.
 * Allows organizers to view and edit event details, manage attendees, and interact with event features.
 */
//https://www.youtube.com/watch?v=mbQd6frpC3g
//https://developer.android.com/develop/sensors-and-location/location/retrieve-current
//https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
//https://medium.com/@grudransh1/best-way-to-get-users-location-in-android-app-using-location-listener-from-java-in-android-studio-77882f8b87fd
//https://www.geeksforgeeks.org/how-to-get-user-location-in-android/

package com.example.checkin;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

// Shows event information for an organizer
public class EventsDetailOrg extends Fragment {
    Button attendeelistbutton;
    Button qrcodebutton;
    private String userId;
    Event myevent;
    EditText eventnametxt;
    EditText eventdetails;
    Button backbutton;

    Button posterbutton;
    Button editPoster;

    Button detailscodebutton;
    EditText eventDate;
    EditText eventTime;
    EditText eventlocation;
    EditText attendeeCap;

    Button savebutton;
    private Button checkInLocation;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 2000, false);
                            ImageEncoder imgEncoder = new ImageEncoder();
                            myevent.setPoster(imgEncoder.BitmapToBase64(bitmap));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                         Bundle savedInstanceState) {

//<<<<<<< HEAD

//
//        if (myevent.getPoster().equals("")){
//            //no poster for this event
//            //posterbutton.setError(String.format("%s has no poster.", myevent.getEventname()));
//            posterbutton.setText("No Poster Available");
//        }
//
//
//
//        // move back to pevious fragment when clicked
//        backbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().getSupportFragmentManager().popBackStack();
//            }
//        });
//
//        // move to fragment where qr code is displayed
//        qrcodebutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ShareCode code_frag = new ShareCode();
//                Bundle args = new Bundle();
//                args.putSerializable("event", myevent);
//                code_frag.setArguments(args);
//                getParentFragmentManager().setFragmentResult("event",args);
//
//                getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout, code_frag).addToBackStack(null).commit();
//=======
                    View view = inflater.inflate(R.layout.fragment_events_detail_org, container, false);
                    // Inflate the layout for this fragment
                    checkInLocation = view.findViewById(R.id.checkin_location_btn);
                    attendeelistbutton = (Button) view.findViewById(R.id.attendeeslistbtn);
                    qrcodebutton = (Button) view.findViewById(R.id.codebtn);
                    backbutton = view.findViewById(R.id.backbtn);
                    eventnametxt = view.findViewById(R.id.eventname_text);
                    eventdetails = view.findViewById(R.id.eventdetails_txt);
                    detailscodebutton = view.findViewById(R.id.detailscode);
                    posterbutton = view.findViewById(R.id.posterbtn);
                    eventDate = view.findViewById(R.id.Eteventdate);
                    eventTime = view.findViewById(R.id.Eteventtime);
                    eventlocation = view.findViewById(R.id.Eteventlocation);
                    savebutton = view.findViewById(R.id.savebtn);
                    editPoster = view.findViewById(R.id.editposterbtn);
                    attendeeCap = view.findViewById(R.id.EditAttendeeCap);

                    // get event object from previous fragment
                    Bundle bundle = this.getArguments();
                    if (bundle != null) {
                        myevent = (Event) bundle.getSerializable("event");
                    }

                    String time = "Time: " + myevent.getEventTime();
                    String date = "Date: "+ myevent.getEventDate();
                    String location = "Location: " +myevent.getLocation();

                    eventnametxt.setText(myevent.getEventname());
                    eventdetails.setText(myevent.getEventDetails());
                    eventDate.setText(date);
                    eventTime.setText(time);
                    eventlocation.setText(location);
                    attendeeCap.setText(myevent.getAttendeeCap());

                    // Display user check-in locations if available
                    Map<String, String> checkInLocations = myevent.getUserCheckInLocation();
                    if (checkInLocations != null) {
                        String locationString = "";
                        for (Map.Entry<String, String> entry : checkInLocations.entrySet()) {
                            locationString += entry.getKey() + ": " + entry.getValue() + "\n";
                        }
                        checkInLocation.setText(locationString);
                    } else {
                        checkInLocation.setText("No check-in locations available");
                    }

                    checkInLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle check-in location button click
                        }
                    });

                    if (myevent.getPoster().equals("")) {
                        //no poster for this event
                        //posterbutton.setError(String.format("%s has no poster.", myevent.getEventname()));
                        posterbutton.setText("No Poster Available");
                    }
                    editPoster.setOnClickListener(v -> mGetContent.launch("image/*"));
//>>>>>>> main


                    savebutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String eventName = eventnametxt.getText().toString().trim();
                            String eventDateStr = eventDate.getText().toString().trim();
                            String eventTimeStr = eventTime.getText().toString().trim();
                            String eventDetailsStr = eventdetails.getText().toString().trim();
                            String eventlocationStr = eventlocation.getText().toString().trim();
                            String AttendeeCapStr = attendeeCap.getText().toString().trim();

                            myevent.setEventDate(eventDateStr);
                            myevent.setEventTime(eventTimeStr);
                            myevent.setEventDetails(eventDetailsStr);
                            myevent.setLocation(eventlocationStr);
                            myevent.setEventname(eventName);
                            myevent.setAttendeeCap(AttendeeCapStr);

//<<<<<<< HEAD
//                Bundle args = new Bundle();
//                args.putSerializable("event", myevent);
//                args.putSerializable("frameLayout", frameLayout);
//                list_frag.setArguments(args);
//                getParentFragmentManager().setFragmentResult("event",args);
//                getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout, list_frag).addToBackStack(null).commit();
//=======
                            Database db = new Database();
                            db.updateEvent(myevent);
                            Toast.makeText(getContext(), "Details Saved!", Toast.LENGTH_LONG).show();
                        }
                    });

                    detailscodebutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PromotionQrShare sharefrag = new PromotionQrShare();
                            Bundle args = new Bundle();
                            // Frame layout on which to display the other sub informational fragment.
                            int frameLayout = (int) bundle.getSerializable("frameLayout");
                            args.putSerializable("frameLayout", frameLayout);
                            args.putSerializable("event", myevent);
                            sharefrag.setArguments(args);
                            getParentFragmentManager().setFragmentResult("event", args);

                            getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout, sharefrag).addToBackStack(null).commit();

                        }
                    });


                    // move back to pevious fragment when clicked
                    backbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });

                    // move to fragment where qr code is displayed
                    qrcodebutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ShareCode code_frag = new ShareCode();
                            Bundle args = new Bundle();
                            args.putSerializable("event", myevent);
                            // Frame layout on which to display the other sub informational fragment.
                            int frameLayout = (int) bundle.getSerializable("frameLayout");
                            args.putSerializable("frameLayout", frameLayout);
                            code_frag.setArguments(args);
                            getParentFragmentManager().setFragmentResult("event", args);

                            getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout, code_frag).addToBackStack(null).commit();


                        }
                    });

                    // move to fragment that shows attendees list options
                    attendeelistbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // add attendees list fragment
                            AttendeesOptions list_frag = new AttendeesOptions();

                            Bundle args = new Bundle();
                            args.putSerializable("event", myevent);
                            // Frame layout on which to display the other sub informational fragment.
                            int frameLayout = (int) bundle.getSerializable("frameLayout");
                            args.putSerializable("frameLayout", frameLayout);
                            list_frag.setArguments(args);
                            getParentFragmentManager().setFragmentResult("event", args);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout, list_frag).addToBackStack(null).commit();

                        }
                    });

                    //move to poster fragment
                    posterbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (myevent.getPoster().equals("")) {
                                //no poster for this event
                                //posterbutton.setError(String.format("%s has no poster.", myevent.getEventname()));
                                return;
                            }

                            //Create fragment
                            EventPosterFrag posterShareFrag = new EventPosterFrag();

                            UserImage poster = new UserImage();
                            poster.setImageB64(myevent.getPoster());
                            poster.setID(myevent.getEventId());
                            // Frame layout on which to display the other sub informational fragment.
                            int frameLayout = (int) bundle.getSerializable("frameLayout");

                            Bundle args = new Bundle();
                            args.putSerializable("Poster", poster);


                            posterShareFrag.setArguments(args);
                            //ShareCode code_frag = new ShareCode();
                            //Bundle args = new Bundle();
                            //args.putSerializable("event", myevent);
                            //code_frag.setArguments(args);
                            getParentFragmentManager().setFragmentResult("Poster", args);

                            getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout, posterShareFrag).addToBackStack(null).commit();


                            //getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });


                    return view;
////>>>>>>> main
////
////
                }
////
////<<<<<<< HEAD
//                //Create fragment
//                EventPosterFrag posterShareFrag = new EventPosterFrag();
//
//                UserImage poster = new UserImage();
//                poster.setImageB64(myevent.getPoster());
//                poster.setID(myevent.getEventId());
//
//                Bundle args = new Bundle();
//                args.putSerializable("Poster", poster);
//
//                posterShareFrag.setArguments(args);
//                //ShareCode code_frag = new ShareCode();
//                //Bundle args = new Bundle();
//                //args.putSerializable("event", myevent);
//                //code_frag.setArguments(args);
//                getParentFragmentManager().setFragmentResult("Poster",args);
//
//                getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout, posterShareFrag).addToBackStack(null).commit();
//
//
//
//                //getActivity().getSupportFragmentManager().popBackStack();
//            }
//        });
//
//        eventnametxt.setText(myevent.getEventname());
//        eventdetails.setText(myevent.getEventDetails());
//
//        return view;
//
//
//    }
//}
//=======
}
//>>>>>>> main
