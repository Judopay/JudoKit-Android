package com.judopay;

import android.support.annotation.StringRes;

import com.judopay.model.CardDate;
import com.judopay.model.CardNetwork;

@Deprecated
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

        boolean maestroCardType = cardType == CardNetwork.MAESTRO;

        this.issueNumberValid = isIssueNumberValid(paymentForm.getIssueNumber());
        this.showIssueNumberAndStartDate = paymentForm.isMaestroSupported() && maestroCardType && !paymentForm.isTokenCard();
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
        CardDate cardDate = new CardDate(startDate);
        return cardDate.isBeforeToday() && cardDate.isInsideAllowedDateRange();
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