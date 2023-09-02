# MM BUS 项目

## 背景

本项目基于 [Transitnet Bus](https://github.com/TotemSmartBus/transitnet)，由于存在多模态的数据存储需求，因此需要将数据导入到
[ArangoDB](https://www.arangodb.com) 中进行存储，并结合其他多模态的数据综合进行查询。

## NYC 数据导入

位于 `/src/py/nyc` 目录下，先运行 `spider_for_query.py` 将指定关键词的查询结果保存。

再运行 `downloader_for_query` 将刚爬取的列表中的数据集导入到datasets目录下。