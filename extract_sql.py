import re
import os

log_file = "/home/vedant-2506/.gemini/antigravity/brain/670c8787-9b15-4c6f-a516-1b731607af75/.system_generated/logs/overview.txt"
with open(log_file, "r", encoding="utf-8") as f:
    content = f.read()

# Find the start of the SQL statement
start_idx = content.find("USE FarmFeed;")
if start_idx == -1:
    print("Could not find SQL in log")
    exit(1)

# Extract from start_idx onwards
sql_part = content[start_idx:]

# Find the end of the insert statement (end of the VALUES list)
# Usually ends with a semicolon. We'll find the first semicolon after VALUES.
values_idx = sql_part.find("VALUES")
end_idx = sql_part.find(";", values_idx)

if end_idx == -1:
    print("Could not find end of SQL")
    # If it's truncated, maybe it doesn't end with semicolon.
    # Just take everything up to the end of the user's turn
    end_idx = len(sql_part)

sql_statement = sql_part[:end_idx+1]

# Replace mysql_import_key with id
sql_statement = sql_statement.replace("mysql_import_key", "id")

# Replace bighaat_ with pr
sql_statement = sql_statement.replace("'bighaat_", "'pr")

# Save to file
with open("/home/vedant-2506/Desktop/FarmFeed/insert_data.sql", "w", encoding="utf-8") as out:
    out.write(sql_statement)
    
print("Successfully extracted and modified SQL statement.")
