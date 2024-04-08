package com.example.checkin;

import com.example.checkin.Organizer;

import java.util.ArrayList;

/**
Represents the list of Organizers.
 */
public class OrganizerList {
    //has list of Organizers
    private ArrayList<Organizer> Organizers = new ArrayList<>();

    public OrganizerList(){}

    /**
     * Find if Organizer exists in list
     * @param toFind
     * a valid Organizer object to be found
     * @return
     * boolean
     */
    public boolean contains (Organizer toFind){
        if (Organizers.isEmpty()){
            return false;
        }
        for (Organizer o: Organizers){
            if (o == toFind){
                return true;
            }
        }
        return false;
    }

    /**
     * Adds an Organizer to the list
     * @param o
     * a valid Organizer object
     */
    public void addOrganizer(Organizer o){
        Organizers.add(o);
    }

    /**
     * Removes an organizer from the list
     * @param o
     * a valid Organizer object
     */
    public void removeOrganizer(Organizer o){
        Organizers.remove(o);
    }

    public ArrayList<Organizer> getOrganizers() {
        return Organizers;
    }

    public Organizer getOrganizerPosition(int position){
        Organizer org = Organizers.get(position);
        return org;
    }
}
