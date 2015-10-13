package com.judopay.payment;

import rx.Observer;

public class PaymentPresenter {

    private PaymentView view;
    private PaymentResponse data;
    private boolean loadInProgress;

    private PaymentService paymentService;

    public PaymentPresenter() {
        this.paymentService = new PaymentService();
    }

    public void performPayment(Transaction transaction) {
        view.showLoading();

        loadInProgress = true;

        paymentService.payment(transaction)
                .subscribe(new Observer<PaymentResponse>() {
                    @Override
                    public void onCompleted() {
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(PaymentResponse paymentResponse) {
                        if (view == null) {
                            PaymentPresenter.this.data = paymentResponse;
                        } else {
                            view.setViewModel(paymentResponse);
                        }
                    }
                });
    }

    public void bindView(PaymentView view) {
        this.view = view;

        if(loadInProgress) {
            view.showLoading();
        }

        if (data != null) {
            view.setViewModel(data);
        }
    }

    public void unbindView() {
        this.view = null;
    }

}