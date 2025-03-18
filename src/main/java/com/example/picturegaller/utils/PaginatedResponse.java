package com.example.picturegaller.utils;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class PaginatedResponse {
    private boolean last;
    private int pageNumber;
    private List<HashMap<String, String>> data;

}
