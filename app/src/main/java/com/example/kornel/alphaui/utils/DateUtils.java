package com.example.kornel.alphaui.utils;

public class DateUtils {
    
    public static String convertPmTime(String pmHour) {
        switch (pmHour) {
            case "01":
                return "13";
            case "02":
                return "14";
            case "03":
                return "15";
            case "04":
                return "16";
            case "05":
                return "17";
            case "06":
                return "18";
            case "07":
                return "19";
            case "08":
                return "20";
            case "09":
                return "21";
            case "10":
                return "22";
            case "11":
                return "23";
            case "12":
                return "24";
        }
        return "ERROR";
    }
    
    public static String convertDayName(String dayName) {
        switch (dayName) {
            case "Mon":
                return "Monday";
            case "Tue":
                return "Tuesday";
            case "Wed":
                return "Wednesday";
            case "Thu":
                return "Thursday";
            case "Fri":
                return "Friday";
            case "Sat":
                return "Saturday";
            case "Sun":
                return "Sunday";
        }
        return "ERROR";
    }
    
    public static String convertMonthToFullName(String month) {
        switch (month) {
            case "Jan":
                return "January";
            case "Feb":
                return "February";
            case "Mar":
                return "March";
            case "Apr":
                return "April";
            case "May":
                return "May";
            case "Jun":
                return "June";
            case "Jul":
                return "July";
            case "Aug":
                return "August";
            case "Sept":
                return "September";
            case "Oct":
                return "October";
            case "Nov":
                return "November";
            case "Dec":
                return "December";
        }
        return "ERROR";
    }

    public static String convertMonthToNumber(String month) {
        switch (month) {
            case "Jan":
                return "01";
            case "Feb":
                return "02";
            case "Mar":
                return "03";
            case "Apr":
                return "04";
            case "May":
                return "05";
            case "Jun":
                return "06";
            case "Jul":
                return "07";
            case "Aug":
                return "08";
            case "Sept":
                return "09";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
        }
        return "ERROR";
    }
}
