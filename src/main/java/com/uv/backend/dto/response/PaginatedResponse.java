package com.uv.backend.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> data;
    private long total;
    private int page;
    private int limit;
    private boolean hasNext;
    private boolean hasPrevious;

    public PaginatedResponse(Page<T> page) {
        this.data = page.getContent();
        this.total = page.getTotalElements();
        this.page = page.getNumber();
        this.limit = page.getSize();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }

    public PaginatedResponse(List<T> data, long total, int page, int limit, boolean hasNext, boolean hasPrevious) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.limit = limit;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    // Getters and Setters
    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }

    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
}