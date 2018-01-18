package com.rebuilding.day6;

public class EntityNotFoundException extends RuntimeException {
    private Long id;

    public EntityNotFoundException(Long id) {
        super(String.format("ID: %d找不到實體", id));
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
