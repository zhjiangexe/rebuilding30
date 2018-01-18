package com.rebuilding.day17.service;

import com.rebuilding.day17.entity.Issue;

import java.util.Map;

public interface IssueService {

    Issue findOrSave(Map<String, String> map);

    Issue findById(Long id);
}
