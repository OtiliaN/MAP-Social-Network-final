package org.example.lab6javafx.utils.paging;


//inglobeaza informatii despre paginare
public class Pageable {
    private int pageSize;
    private int pageNumber;

    public Pageable(int pageNumber, int pageSize) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

}
