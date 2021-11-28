package br.cams7.tests.springwebfluxessentials.utils;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

public final class Pagination {

  private Pagination() {}

  public static Sort getSort(String field, String direction) {
    var sort = Sort.by(!ObjectUtils.isEmpty(field) ? field : "id");
    if (SortDirection.DESCENDING.equals(SortDirection.getDirection(direction)))
      sort = sort.descending();
    else sort = sort.ascending();

    return sort;
  }

  @AllArgsConstructor
  private enum SortDirection {
    ASCENDING("asc"),
    DESCENDING("desc");

    private final String direction;

    public static SortDirection getDirection(String direction) {
      for (SortDirection sortDirection : values())
        if (sortDirection.direction.equals(direction)) return sortDirection;
      return ASCENDING;
    }
  }
}
