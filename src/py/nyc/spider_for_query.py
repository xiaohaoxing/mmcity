import json

import requests
import os
from bs4 import BeautifulSoup


def spider_page(url):
    params = url.split('?')[1]
    file_name = params + '.txt'
    if os.path.exists(file_name):
        with open(file_name) as f:
            return f.read()
    resp = requests.get(url)
    txt = resp.text
    with open(file_name, 'w') as f:
        f.write(txt)
        f.close()


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
        items = exact_records(spider_page(pre_url + page_id))
        result_list.extend(items)
        if len(items) == 0:
            has_next = False
    return result_list


if __name__ == '__main__':
    list = get_list('location')
    with open('datasets', 'w') as f:
        f.write(json.dumps(list))
