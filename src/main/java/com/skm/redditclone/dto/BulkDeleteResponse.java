package com.skm.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkDeleteResponse {
    private int deleted;
    private List<Long> NotFound;

    public BulkDeleteResponse(int deleted) {
        this.deleted = deleted;
        this.NotFound = null;
    }
}
