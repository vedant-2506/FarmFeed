package com.example.farmFeed.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class ProductSeedDataRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ProductSeedDataRunner.class);

    private final JdbcTemplate jdbcTemplate;
    private final Object seedLock = new Object();
    private volatile boolean seedCompleted = false;

    @Value("${farmfeed.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${farmfeed.seed.product-count:1000}")
    private int productCount;

    public ProductSeedDataRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        seedIfNeeded();
    }

    public void seedIfNeeded() {
        if (seedCompleted) {
            return;
        }

        synchronized (seedLock) {
            if (seedCompleted) {
                return;
            }

            if (!seedEnabled) {
                logger.info("Product seeding disabled");
                seedCompleted = true;
                return;
            }

            if (!tableExists("products")) {
                logger.warn("Table 'products' not found. Skipping seed");
                seedCompleted = true;
                return;
            }

            Long existing = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Long.class);
            long existingCount = existing == null ? 0L : existing;

            int safeCount = Math.max(100, productCount);
            if (existingCount >= safeCount) {
                logger.info("Products already available: {}. Target {} satisfied.", existingCount, safeCount);
                seedCompleted = true;
                return;
            }

            int toInsert = (int) (safeCount - existingCount);
            logger.info("Products available: {}. Seeding {} more to reach {}.", existingCount, toInsert, safeCount);

            Random random = new Random(42);
            String[] categories = {"Seed", "Crop Production", "Crop Nutrition", "Organic", "Chemical"};
            String[] subcategories = {"Seeds", "Plant Nutrition", "Biostimulants", "Bio Control", "Micronutrients"};

            String sql = """
                INSERT INTO products (
                    name, category, subcategory, price, description, image_url, stock_quantity,
                    rating, total_reviews, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            List<Object[]> batchArgs = new ArrayList<>(toInsert);
            Timestamp now = Timestamp.from(Instant.now());

            for (int i = 1; i <= toInsert; i++) {
                int sequence = (int) existingCount + i;
                String category = categories[i % categories.length];
                String subcategory = subcategories[i % subcategories.length];
                String productName = String.format("FarmFeed Product %d", sequence);
                String description = String.format(
                        "High quality %s input for improved crop performance. Batch %04d for soil and yield optimization.",
                        category,
                        sequence
                );
                double price = 150 + (random.nextInt(1850));
                int stock = 10 + random.nextInt(490);
                double rating = 3.5 + (random.nextDouble() * 1.5);
                int totalReviews = 1 + random.nextInt(250);

                String imageUrl = null;

                batchArgs.add(new Object[]{
                        productName,
                        category,
                        subcategory,
                        Math.round(price * 100.0) / 100.0,
                        description,
                        imageUrl,
                        stock,
                        Math.round(rating * 100.0) / 100.0,
                        totalReviews,
                        now,
                        now
                });
            }

            jdbcTemplate.batchUpdate(sql, batchArgs);
            logger.info("Seeded {} products successfully", toInsert);
            seedCompleted = true;
        }
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }
}
