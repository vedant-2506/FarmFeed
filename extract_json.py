import json

log_file = "/home/vedant-2506/.gemini/antigravity/brain/670c8787-9b15-4c6f-a516-1b731607af75/.system_generated/logs/overview.txt"

with open(log_file, "r", encoding="utf-8") as f:
    lines = f.readlines()

for line in lines:
    if "USE FarmFeed;" in line:
        try:
            data = json.loads(line)
            # Find the string containing the SQL
            # It could be deeply nested. Just search through the dict
            content_str = str(data)
            
            # Since the text itself is truncated, we just regex for INSERT
            import re
            match = re.search(r'INSERT INTO Product.*?VALUES.*?\)', content_str, re.DOTALL | re.IGNORECASE)
            
            if match:
                print("Found match")
            
            # Let's just do a simpler string replacement on the line directly:
            # We know the prompt text in JSON has \n instead of actual newlines.
            pass
        except:
            pass

# Simpler approach:
import ast
import re

for line in lines:
    if "USE FarmFeed;" in line:
        # try to extract the user message content
        match = re.search(r'"parts":\[\{"text":"(.*?)"\}\]', line)
        if match:
            text = match.group(1)
            # unescape
            text = text.replace('\\n', '\n').replace('\\t', '\t').replace('\\"', '"').replace("\\'", "'")
            
            start_idx = text.find("USE FarmFeed;")
            sql = text[start_idx:]
            
            sql = sql.replace("mysql_import_key", "id")
            sql = sql.replace("'bighaat_", "'pr")
            
            with open("/home/vedant-2506/Desktop/FarmFeed/clean_insert.sql", "w", encoding="utf-8") as out:
                out.write(sql)
            print("Extracted properly")
            break
