package com.judopay;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.judopay.model.CardType;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

public class StartDateAndIssueNumberValidation {

    private int startDateError;
    private boolean showStartDateError;
    private boolean startDateEntryComplete;

    private boolean issueNumberValid;
    private boolean showIssueNumberAndStartDate;

    public StartDateAndIssueNumberValidation(PaymentForm paymentForm, int cardType) {
        this.startDateEntryComplete = paymentForm.getStartDate().length() == 5;
        this.showStartDateError = !isStartDateValid(paymentForm.getStartDate()) && startDateEntryComplete;

        if (showStartDateError) {
            startDateError = R.string.check_start_date;
        }

        boolean maestroCardType = cardType == CardType.MAESTRO;

        this.issueNumberValid = isIssueNumberValid(paymentForm.getIssueNumber());
        this.showIssueNumberAndStartDate = paymentForm.isMaestroSupported() && maestroCardType;
    }

    private boolean isIssueNumberValid(String issueNumber) {
        try {
            int issueNo = Integer.parseInt(issueNumber);
            return issueNo > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isStartDateValid(String startDate) {
        DateTime midnightToday = new DateTime().withTimeAtStartOfDay();

        if (!startDate.matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
            return false;
        }

        int month = Integer.parseInt(startDate.substring(0, 2));
        int year = 2000 + Integer.parseInt(startDate.substring(3, 5));

        LocalDate startDateLocalDate = getLocalDate(month, year);
        LocalDate minStartDate = getLocalDate(month, midnightToday.getYear() - 10);

        return startDateLocalDate.isBefore(midnightToday.toLocalDate()) && startDateLocalDate.isAfter(minStartDate);
    }

    @NonNull
    private LocalDate getLocalDate(int month, int year) {
        return new YearMonth(year, month).toLocalDate(1);
    }

    public boolean isShowStartDateError() {
        return showStartDateError;
    }

    public boolean isStartDateEntryComplete() {
        return startDateEntryComplete;
    }

    public boolean isShowIssueNumberAndStartDate() {
        return showIssueNumberAndStartDate;
    }

    @StringRes
    public int getStartDateError() {
        return startDateError;
    }

    public boolean isIssueNumberValid() {
        return issueNumberValid;
    }


}
