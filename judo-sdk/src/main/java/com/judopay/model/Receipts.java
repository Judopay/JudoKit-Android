package com.judopay.model;

import java.util.List;

public class Receipts {

    private final Integer resultCount;
    private final Integer pageSize;
    private final Integer offset;
    private final List<Receipt> results;

    public Receipts(Integer resultCount, Integer pageSize, Integer offset, List<Receipt> results) {
        this.resultCount = resultCount;
        this.pageSize = pageSize;
        this.offset = offset;
        this.results = results;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getOffset() {
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