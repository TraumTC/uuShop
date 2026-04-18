package com.tc.vo;

import lombok.Data;

import java.util.List;

@Data
public class StackerLineInnerVO {
    private String name;
    private String type;
    private String stack;
    private List<Integer> data;
}
