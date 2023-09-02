import requests
import json
import os
import re
from bs4 import BeautifulSoup

page_html = 'page.txt'


def spider_page(url):
    if os.path.exists(page_html):
        with open(page_html) as f:
            return f.read()
    resp = requests.get(url)
    txt = resp.text
    with open(page_html, 'w') as f:
        f.write(txt)
        f.close()


def exact_records(txt):
    html = BeautifulSoup(txt, 'html.parser')
    list_element = html.find(attrs={'class': 'browse2-results'})
    items = []
    for item in list_element.contents:
        if item.name == 'div':
            items.append(item)
    return items


# TODO
if __name__ == '__main__':
    url = "https://data.cityofnewyork.us/browse?q=&sortBy=last_modified&page=2"
    content = spider_page(url)
    items = exact_records(content)
    print(str(len(items)))
