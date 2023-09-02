import json

import requests
import os
from bs4 import BeautifulSoup


# target query keyword
query = 'location'


def spider_page(url, cache=True):
    name = url.split('/')[-1]
    splits = name.split('?')
    if len(splits) < 2:
        params = name
    else:
        params = splits[1]
    file_name = params + '.txt'
    if cache and os.path.exists(file_name):
        with open(file_name) as f:
            return f.read()
    resp = requests.get(url)
    txt = resp.text
    if cache:
        with open(file_name, 'w') as f:
            f.write(txt)
            f.close()
    return txt


def exact_records(txt):
    html = BeautifulSoup(txt, 'html.parser')
    titles = html.findAll(attrs={'class': 'browse2-result-name-link'})
    items = []
    for item in titles:
        items.append({'title': item.string, 'link': item.attrs['href']})
    return items


def get_list(query_str):
    pre_url = 'https://data.cityofnewyork.us/browse?q=' + query_str + '&sortBy=relevance&page='
    result_list = []
    has_next = True
    page_id = 1
    while has_next:
        items = exact_records(spider_page(pre_url + str(page_id)))
        print('Spider page %d, %d items' % (page_id, len(items)))
        result_list.extend(items)
        page_id += 1
        if len(items) == 0:
            has_next = False
    return result_list


if __name__ == '__main__':
    list = get_list(query)
    with open('datasets.json', 'w') as f:
        f.write(json.dumps(list))
        f.close()
    print('Save the datasets as datasets.json success!')
