# -*- coding: utf-8 -*-
"""
Main configuration file.
"""
import os

_BASEDIR = os.path.abspath(os.path.dirname(__file__))
_USE_MYSQL = True

# Database config
DATABASE_NAME = os.environ.get('GREEN_DB_NAME', 'greendb')
USER = os.environ.get('DPI_DB_USER', 'root')
PASS = os.environ.get('DPI_DB_PASS', 'toor')
HOST = os.environ.get('DPI_DB_HOST', 'localhost')

if _USE_MYSQL:
    SQLALCHEMY_DATABASE_URI = 'mysql+pymysql://' + USER + ':' +  PASS + \
      '@' + HOST +'/' + DATABASE_NAME + '?charset=utf8'
else:
    SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(_BASEDIR,
                                                          DATABASE_NAME)
