package com.rebuilding.day19.service;

import com.rebuilding.day19.entity.Issue;

import java.util.Map;

public interface IssueService {

    Issue findOrSave(Map<String, String> map);

    Issue findById(Long id);
}
