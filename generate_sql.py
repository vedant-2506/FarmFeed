import pandas as pd
import math

df = pd.read_excel('/home/vedant-2506/Desktop/DB/bighaat_product.xlsx')

sql_statements = []
sql_statements.append("USE FarmFeed;\n")
sql_statements.append("DROP TABLE IF EXISTS Product;\n")
sql_statements.append("""
CREATE TABLE Product (
    id VARCHAR(255) PRIMARY KEY,
    product_name VARCHAR(255),
    image_link VARCHAR(2000),
    primary_category VARCHAR(255),
    subcategory VARCHAR(255),
    price_inr DOUBLE,
    rating DOUBLE DEFAULT 0,
    description_clean TEXT,
    detailed_description_10_sentences TEXT,
    manufacturer VARCHAR(255),
    vendor_id BIGINT,
    stock INT DEFAULT 100,
    total_reviews INT DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME
);
""")
sql_statements.append("DELETE FROM Product;\n")

for index, row in df.iterrows():
    # Construct id
    sr_no = row.get('Sr.No.', index + 1)
    if pd.isna(sr_no): sr_no = index + 1
    
    product_id = f"pr{int(sr_no)}"
    
    def escape_str(val):
        if pd.isna(val):
            return "NULL"
        val_str = str(val)
        val_str = val_str.replace("'", "''").replace("\\", "\\\\")
        return f"'{val_str}'"
        
    def escape_num(val):
        if pd.isna(val):
            return "NULL"
        return str(val)

    name = escape_str(row.get('product_name'))
    image = escape_str(row.get('image_link'))
    cat = escape_str(row.get('primary_category'))
    price = escape_num(row.get('price_inr'))
    rating = escape_num(row.get('rating'))
    desc = escape_str(row.get('description_clean'))
    detail = escape_str(row.get('detailed_description_10_sentences'))
    
    # Optional fields not in excel: subcategory, manufacturer
    subcat = "NULL"
    vendor_id = "1"
    stock = "100"
    
    sql = f"INSERT INTO Product (id, product_name, image_link, primary_category, subcategory, price_inr, rating, description_clean, detailed_description_10_sentences, vendor_id, stock) VALUES ('{product_id}', {name}, {image}, {cat}, {subcat}, {price}, {rating}, {desc}, {detail}, {vendor_id}, {stock});"
    sql_statements.append(sql + "\n")

with open('/home/vedant-2506/Desktop/FarmFeed/full_insert.sql', 'w', encoding='utf-8') as f:
    f.writelines(sql_statements)

print("SQL generated successfully.")
