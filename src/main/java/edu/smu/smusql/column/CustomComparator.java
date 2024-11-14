package edu.smu.smusql.column;

import java.util.Comparator;

public class CustomComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        try {
            double d1 = Double.parseDouble(o1);
            double d2 = Double.parseDouble(o2);
            return Double.compare(d1, d2);
        } catch (NumberFormatException e) {
            return o1.compareTo(o2);
        }
    }
}
