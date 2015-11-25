package com.judopay.model;

import java.util.List;

public class Receipts {

    private final int resultCount;
    private final int pageSize;
    private final int offset;
    private List<Receipt> results;

    public Receipts(int resultCount, int pageSize, int offset, List<Receipt> results) {
        this.resultCount = resultCount;
        this.pageSize = pageSize;
        this.offset = offset;
        this.results = results;
    }

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

    @Override
    public String toString() {
        return "Receipts{" +
                "results=" + results +
                ", offset=" + offset +
                ", pageSize=" + pageSize +
                ", resultCount=" + resultCount +
                '}';
    }
}