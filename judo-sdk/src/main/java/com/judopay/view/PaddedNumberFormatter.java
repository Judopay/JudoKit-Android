package com.judopay.view;

public class PaddedNumberFormatter {

    public static String format(String number, String format) {
        StringBuilder formatBuilder = new StringBuilder();

        int numPosition = 0;
        for (int i = 0; i < format.length(); i++) {
            if (Character.isDigit(format.charAt(i))) {
                formatBuilder.append(number.charAt(numPosition));
                numPosition++;
            } else {
                formatBuilder.append(format.charAt(i));
            }

            if(numPosition == number.length()) {
                break;
            }
        }
        return formatBuilder.toString();
    }

}
