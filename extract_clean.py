import os

log_file = "/home/vedant-2506/.gemini/antigravity/brain/670c8787-9b15-4c6f-a516-1b731607af75/.system_generated/logs/overview.txt"
with open(log_file, "r", encoding="utf-8") as f:
    content = f.read()

start_idx = content.find("USE FarmFeed;")
if start_idx != -1:
    end_idx = content.find("<truncated", start_idx)
    if end_idx == -1:
        end_idx = len(content)
        
    sql = content[start_idx:end_idx]
    
    # We must unescape \\n and \\" because it was inside JSON
    sql = sql.replace("\\n", "\n").replace('\\"', '"').replace("\\'", "'")
    
    # User's replacement requests
    sql = sql.replace("mysql_import_key", "id")
    sql = sql.replace("'bighaat_", "'pr")
    
    # Since it was truncated, it likely ends in the middle of a value list.
    # We will find the last occurrence of '),' and replace it with ');'
    last_tuple = sql.rfind("),")
    if last_tuple != -1:
        sql = sql[:last_tuple] + ");\n"
        
    with open("/home/vedant-2506/Desktop/FarmFeed/clean_insert.sql", "w", encoding="utf-8") as out:
        out.write(sql)
    print("Done")
else:
    print("Not found")
