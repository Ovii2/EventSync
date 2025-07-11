package org.example.backend.dto.page;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponseDTO<T> {
    private List<T> content;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean last;
    private Boolean first;
}
