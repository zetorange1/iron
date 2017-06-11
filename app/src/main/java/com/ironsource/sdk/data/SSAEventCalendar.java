/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.sdk.data;

import com.ironsource.sdk.data.SSAObj;

public class SSAEventCalendar
extends SSAObj {
    private String ID = "id";
    private String DESCRIPTION = "description";
    private String START = "start";
    private String END = "end";
    private String STATUS = "status";
    private String RECURRENCE = "recurrence";
    private String REMINDER = "reminder";
    private String FREQUENCY = "frequency";
    private String INTERVAL = "interval";
    private String EXPIRES = "expires";
    private String EXCEPTIONDATES = "exceptionDates";
    private String DAYS_IN_WEEK = "daysInWeek";
    private String DAYS_IN_MONTH = "daysInMonth";
    private String DAYS_IN_YEAR = "daysInYear";
    private String WEEKS_IN_MONTH = "weeksInMonth";
    private String MONTHS_IN_YEAR = "monthsInYear";
    private String DAILY = "daily";
    private String WEEKLY = "weekly";
    private String MONTHLY = "monthly";
    private String YEARLY = "yearly";
    private String mDescription;
    private String mStart;
    private String mEnd;

    public SSAEventCalendar(String value) {
        super(value);
        if (this.containsKey(this.DESCRIPTION)) {
            this.setDescription(this.getString(this.DESCRIPTION));
        }
        if (this.containsKey(this.START)) {
            this.setStart(this.getString(this.START));
        }
        if (this.containsKey(this.END)) {
            this.setEnd(this.getString(this.END));
        }
    }

    public String getDescription() {
        return this.mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getStart() {
        return this.mStart;
    }

    public void setStart(String Start) {
        this.mStart = Start;
    }

    public String getEnd() {
        return this.mEnd;
    }

    public void setEnd(String end) {
        this.mEnd = end;
    }
}

