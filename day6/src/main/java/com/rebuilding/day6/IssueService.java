package com.rebuilding.day6;

public class IssueService {

    public Issue queryById(Long id) throws EntityNotFoundException {
        throw new EntityNotFoundException(id);
    }
}
