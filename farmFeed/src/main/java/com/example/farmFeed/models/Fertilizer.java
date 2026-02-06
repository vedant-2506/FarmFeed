package com.example.farmFeed.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fertilizer {
    private Integer fertilizerId;
    private String name;
    private byte[] image;
    private Integer price;
    private String description;
    private String feedback;
    private Integer ratingReview;
    private Integer stock;
}

