package com.judopay.payment;

import java.util.List;

public class Receipts {

    private int resultCount;
    private int pageSize;
    private int offset;
    private List<Receipt> results;

    public int getResultCount() {
        return resultCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getOffset() {
        return offset;
    }

    public List<Receipt> getResults() {
        return results;
    }

}