package vn.edu.hust.soict.soe.assetmanagement.common;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic paginated list response wrapper.
 * Used by all list endpoints that return paginated data.
 *
 * Usage in a service:
 *   Page<Asset> page = assetRepository.findAll(pageable);
 *   return PageResponse.of(page.map(assetMapper::toDto));
 */
@Getter
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;

    private PageResponse(Page<T> page) {
        this.content       = page.getContent();
        this.page          = page.getNumber();
        this.size          = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages    = page.getTotalPages();
        this.last          = page.isLast();
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page);
    }
}