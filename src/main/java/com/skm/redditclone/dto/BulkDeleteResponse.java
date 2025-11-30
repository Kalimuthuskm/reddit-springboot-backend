package com.skm.redditclone.dto;


import java.util.List;

public record BulkDeleteResponse (
     int deleted,
     List<Long> NotFound
){
}
