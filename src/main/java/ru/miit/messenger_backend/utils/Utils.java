package ru.miit.messenger_backend.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class Utils {
    public static <T> Page<T> paginate(List<T> list, Pageable pageable) {
        int from = (int) pageable.getOffset();
        int to = Math.min((from + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(from, to), pageable, list.size());
    }

    public static <T> Page<T> paginatePaginated(List<T> list, Pageable pageable, int size) {
        return new PageImpl<>(list, pageable, size);
    }
}
