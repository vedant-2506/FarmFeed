#!/usr/bin/env python3
import sys
from pathlib import Path

root = Path('farmFeed/src/main/resources/static')
if not root.exists():
    print('Static directory not found:', root)
    sys.exit(1)

files = list(root.rglob('*'))
changed = 0
for p in files:
    if p.is_file():
        try:
            text = p.read_text(encoding='utf-8')
        except UnicodeDecodeError:
            # skip binary or non-text files
            continue
        if '<<<<<<<' not in text:
            continue
        lines = text.splitlines()
        out = []
        i = 0
        L = len(lines)
        while i < L:
            line = lines[i]
            if line.startswith('<<<<<<<'):
                # skip first block until =======
                i += 1
                while i < L and not lines[i].startswith('======='):
                    i += 1
                # skip the ======= line
                i += 1
                # collect second block until >>>>>>>
                second_block = []
                while i < L and not lines[i].startswith('>>>>>>>'):
                    second_block.append(lines[i])
                    i += 1
                # append second block
                out.extend(second_block)
                # skip the >>>>>>> line
                i += 1
            else:
                out.append(line)
                i += 1
        p.write_text('\n'.join(out) + '\n', encoding='utf-8')
        print('Fixed', p)
        changed += 1
print('Done. Files changed:', changed)
