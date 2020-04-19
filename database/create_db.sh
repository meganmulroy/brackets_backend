#!/bin/bash
BASEDIR=$(dirname $0)
DATABASE=tournamenthostingdb
psql -U meganmulroy -f "$BASEDIR/dropdb.sql" &&
createdb -U meganmulroy $DATABASE &&
psql -U meganmulroy -d $DATABASE -f "$BASEDIR/schema.sql" &&
psql -U meganmulroy -d $DATABASE -f "$BASEDIR/data.sql"
