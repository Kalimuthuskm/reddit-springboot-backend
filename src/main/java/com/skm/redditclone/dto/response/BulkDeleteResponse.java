package com.skm.redditclone.dto.response;

import java.util.List;

public record BulkDeleteResponse(
        int deleted,
        List<Long> NotFound) {}
