package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Treasurer;

public class NavFragment_Treasurer_Financial_RecyclerModel {
    private int id;
    private String month;
    private String year;
    private String semester;

    private boolean isBKU, isEval;

    public NavFragment_Treasurer_Financial_RecyclerModel(int id, String fromMonthToMonth, String year, String semester) {
        this.id = id;
        this.month = fromMonthToMonth;
        this.year = year;
        this.semester = semester;

        isBKU = false;
        isEval = true;
    }

    public NavFragment_Treasurer_Financial_RecyclerModel(int id, String month, String year) {
        this.id = id;
        this.month = month;
        this.year = year;

        isBKU = true;
        isEval = false;
    }

    public int getId() {
        return id;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getSemester() {
        if (semester.contentEquals("I")||semester.contentEquals("II"))
            return "Semester " + semester;
        else return semester;
    }

    public boolean isBKU() {
        return isBKU;
    }

    public boolean isEval() {
        return isEval;
    }
}
