package com.judopay.payment.form;

import android.support.annotation.StringRes;

import com.judopay.R;
import com.judopay.customer.CardType;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

public class StartDateAndIssueNumberValidation {

    private int startDateError;
    private boolean showStartDateError;
    private boolean startDateEntryComplete;

    private boolean issueNumberValid;
    private boolean issueNumberAndStartDateRequired;

    public StartDateAndIssueNumberValidation(PaymentForm paymentForm,
                                             boolean cardNumberValid, boolean cvvValid,
                                             boolean expiryDateValid) {

        this.startDateEntryComplete = paymentForm.getStartDate().length() == 5;
        this.showStartDateError = !isStartDateValid(paymentForm.getStartDate()) && startDateEntryComplete;

        if (!showStartDateError) {
            startDateError = R.string.error_check_date;
        }

        boolean maestroCardType = CardType.matchCardNumber(paymentForm.getCardNumber()) == CardType.MAESTRO;

        this.issueNumberValid = isIssueNumberValid(paymentForm.getIssueNumber());
        this.issueNumberAndStartDateRequired = paymentForm.isMaestroSupported()
                && maestroCardType
                && cardNumberValid
                && cvvValid
                && expiryDateValid;
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
        if (!startDate.matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
            return false;
        }

        DateTime midnightToday = new DateTime().withTimeAtStartOfDay();

        int year = 2000 + Integer.parseInt(startDate.substring(3, 5));
        int month = Integer.parseInt(startDate.substring(0, 2));

        LocalDate monthAndYear = new YearMonth(year, month).toLocalDate(1);

        return monthAndYear.isBefore(midnightToday.toLocalDate());
    }

    public boolean isShowStartDateError() {
        return showStartDateError;
    }

    public boolean isStartDateEntryComplete() {
        return startDateEntryComplete;
    }

    public boolean issueNumberValid() {
        return issueNumberValid;
    }

    public boolean isIssueNumberAndStartDateRequired() {
        return issueNumberAndStartDateRequired;
    }

    @StringRes
    public int getStartDateError() {
        return startDateError;
    }

    public boolean isIssueNumberValid() {
        return issueNumberValid;
    }


}
