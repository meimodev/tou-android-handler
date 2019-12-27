package com.meimodev.sitouhandler.Models;

import java.util.ArrayList;

public class BasicMember_Model {
    private String TAG = "BasicMember_Model";

    //    System attr
    private int ID;

    //    Personal attr
    private String firstName;
    private ArrayList<String> middleNames;
    private String lastName;
    private ArrayList<String> degreeNames;
    private String DOB;
    private String imageURL;

    //    Church attr
    private int churchId;
    private String churchName;
    private String column;
    private ArrayList<String> assignedPositions;
    private String baptizeLetterEntry, sidiLetterEntry, marriageLetterEntry;


//    public BasicMember_Model(String firstName, String lastName, boolean unregisteredMember,
//                             @Nullable ArrayList<String> middleNames,
//                             @Nullable ArrayList<String> degreeNames) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//
//        this.unregisteredMember = unregisteredMember;
//
//        if (middleNames != null && degreeNames != null) {
//            this.middleNames = new ArrayList<>(middleNames);
//            this.degreeNames = new ArrayList<>(degreeNames);
//        }
//    }

    public BasicMember_Model(int ID, String firstName, String lastName, String DOB
    ) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;

        middleNames = new ArrayList<>();
        degreeNames = new ArrayList<>();

        assignedPositions = new ArrayList<>();
    }

    public BasicMember_Model(int ID, String firstName, String lastName, String DOB, ArrayList<String> assignedPositions
    ) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;

        middleNames = new ArrayList<>();
        degreeNames = new ArrayList<>();

        this.assignedPositions = assignedPositions;
    }

    public BasicMember_Model(int ID, String firstName, String lastName, String DOB,
                             int churchId, String churchName, String coloumn,
                             String baptizeLetterEntry, String sidiLetterEntry,
                             String marriageLetterEntry
    ) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;
        this.churchId = churchId;
        this.churchName = churchName;
        this.column = coloumn;
        this.baptizeLetterEntry = baptizeLetterEntry;
        this.sidiLetterEntry = sidiLetterEntry;
        this.marriageLetterEntry = marriageLetterEntry;

        middleNames = new ArrayList<>();
        degreeNames = new ArrayList<>();

        assignedPositions = new ArrayList<>();
    }


    public void addMiddleNames(String middleName) {
        middleNames.add(middleName);
    }

    public void addDegreeNames(String degreeName) {
        degreeNames.add(degreeName);
    }

    public String getFullName() {
        String m = "";
        for (String mName : middleNames) {
            m = mName.concat(" ");
        }
        String d = "";
        for (String dName : degreeNames) {
            d = " ".concat(dName);
        }
        return firstName.concat(" ").concat(m).concat(lastName).concat(d);

    }

    public int getID() {
        return ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDOB() {
        return DOB;
    }

    public ArrayList<String> getAssignedPositions() {
        return assignedPositions;
    }

    public void setAssignedPositions(ArrayList<String> assignedPositions) {
        this.assignedPositions = assignedPositions;
    }

    public int getChurchId() {
        return churchId;
    }

    public String getChurchName() {
        return churchName;
    }

    public String getColumn() {
        return column;
    }

    public String getBaptizeLetterEntry() {
        return baptizeLetterEntry;
    }

    public String getSidiLetterEntry() {
        return sidiLetterEntry;
    }

    public String getMarriageLetterEntry() {
        return marriageLetterEntry;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
