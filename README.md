# MM BUS Project

## Background

This project based on [Transitnet Bus](https://github.com/TotemSmartBus/transitnet). As the requirement for multi-model
data storage, We import the data into [ArangoDB](https://www.arangodb.com) for a better storage and mix up with some
other multi model data for query.

## Import NYC Datasets

Codes at `/src/py/nyc` derectory, run `spider_for_query.py` first to save result list for specified query keyword.

Run `downloader_for_query` then to import dataset gained above to datasets directory.