package com.example.kitchen.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class OrderDTO {
    @NotNull
    private String id;

    @NotNull
    private String name;

    @Min(1)
    @Max(60)
    private int prepTime;
}
