package com.epam.ilyankov.helpdesk.converter.api;

import java.util.List;
import java.util.stream.Collectors;

public interface Converter<E, D> {

    D toDto(E e);

    E fromDto(D d);

    default List<D> toDto(List<E> e) {
        return e.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<E> fromDto(List<D> d) {
        return d.stream().map(this::fromDto).collect(Collectors.toList());
    }
}
