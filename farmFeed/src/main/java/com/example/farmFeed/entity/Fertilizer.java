package com.example.farmFeed.entity;

/**
 * ============================================================
 * Fertilizer — Plain Java class (not a JPA entity)
 * ============================================================
 *
 * NOTE: This class is NOT annotated with @Entity because fertilizer
 * data is fetched using raw JDBC queries in FertilizerRepository
 * (via JdbcTemplate). The result is mapped to Map<String, Object>
 * directly, not to this class. This class is kept as a reference
 * model but is not used in the data layer currently.
 */
public class Fertilizer {

    private Integer fertilizerId;
    private String  name;
    private byte[]  image;
    private Integer price;
    private String  description;
    private String  feedback;
    private Integer ratingReview;
    private Integer stock;

    public Fertilizer() {}

    public Fertilizer(Integer fertilizerId, String name, byte[] image, Integer price,
                      String description, String feedback, Integer ratingReview, Integer stock) {
        this.fertilizerId = fertilizerId;
        this.name         = name;
        this.image        = image;
        this.price        = price;
        this.description  = description;
        this.feedback     = feedback;
        this.ratingReview = ratingReview;
        this.stock        = stock;
    }

    public Integer getFertilizerId()                       { return fertilizerId; }
    public void setFertilizerId(Integer fertilizerId)      { this.fertilizerId = fertilizerId; }

    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; }

    public byte[] getImage()               { return image; }
    public void setImage(byte[] image)     { this.image = image; }

    public Integer getPrice()              { return price; }
    public void setPrice(Integer price)    { this.price = price; }

    public String getDescription()                     { return description; }
    public void setDescription(String description)     { this.description = description; }

    public String getFeedback()                    { return feedback; }
    public void setFeedback(String feedback)       { this.feedback = feedback; }

    public Integer getRatingReview()                       { return ratingReview; }
    public void setRatingReview(Integer ratingReview)      { this.ratingReview = ratingReview; }

    public Integer getStock()              { return stock; }
    public void setStock(Integer stock)    { this.stock = stock; }

    @Override
    public String toString() {
        return "Fertilizer{id=" + fertilizerId + ", name='" + name + "', price=" + price
                + ", stock=" + stock + "}";
    }
}