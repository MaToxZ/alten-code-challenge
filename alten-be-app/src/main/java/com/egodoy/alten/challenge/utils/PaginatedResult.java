package com.egodoy.alten.challenge.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Generic class that wraps paginated result
 */
public class PaginatedResult<T> {
    private List<T> data;
    private Integer pageSize;
    private Integer pageNumber;
    private Long totalRecords;
}
