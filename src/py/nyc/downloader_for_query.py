import json
from spider_for_query import spider_page
from bs4 import BeautifulSoup
import os
import requests

file_name = 'datasets.json'

base_dir = os.path.join(os.path.abspath(os.path.dirname(__file__)), 'datasets')


def read_json():
    with open(file_name, 'r') as f:
        txt = f.read()
        list = json.loads(txt)
        return list


def directory(item, meta, download_urls):
    if meta is None or meta is {}:
        return
    id = meta['view']['id']
    dir_name = item['title'].replace(' ', '_')
    full_path = os.path.join(base_dir, dir_name)
    if not os.path.exists(full_path):
        os.mkdir(full_path)
    # meta json file
    with open(os.path.join(full_path, 'meta-' + id + '.json'), 'w') as f:
        f.write(json.dumps(meta))
    # dataset file
    for appendix in download_urls:
        resp = requests.get(download_urls[appendix])
        file_name = id + '.' + appendix
        with open(os.path.join(full_path, file_name), 'w') as f:
            f.write(resp.text)
        print('Saved %s dataset %s' % (item['title'], file_name))


def get_meta(item):
    url = item['link']
    resp = requests.get(url)
    if resp.status_code != 200:
        print('Error visiting %s' % url)
        return {}, {}
    content = resp.text
    html = BeautifulSoup(content, 'html.parser')
    scripts = html.findAll('script')
    element = None
    for script in scripts:
        if 'initialState' in script.text:
            element = script
    if element is None:
        print('cannot find data row in url%s' % url)
        return {}, {}
    text = element.text
    if not text.strip().startswith('var initialState'):
        print('This js is wrong: %s' % text)
        return {}, {}
    text = text.split('=', 1)[1]
    text = text.replace(';', '')
    json_str = text
    obj = json.loads(json_str)
    urls = {}
    json_url = obj['view']['resourceUrl']
    csv_url = obj['view']['csvResourceUrl']
    geojson_url = obj['view']['geoJsonResourceUrl']
    if json_url is not None:
        urls['json'] = json_url
    if csv_url is not None:
        urls['csv'] = csv_url
    if geojson_url is not None:
        urls['geo.json'] = geojson_url
    return obj, urls


if __name__ == '__main__':
    if not os.path.exists(base_dir):
        os.mkdir(base_dir)
    print('Saving datasets to %s' % base_dir)
    dataset_list = read_json()
    errs = []
    for dataset in dataset_list:
        try:
            meta, urls = get_meta(dataset)
            directory(dataset, meta, urls)
        except Exception:
            print('Error on dataset %s' % dataset['title'])
            errs.append(dataset)
    print('Saved %d datasets' % len(dataset_list))
    if len(errs) > 0:
        with open('error.json', 'w') as f:
            f.write(json.dumps(errs))
