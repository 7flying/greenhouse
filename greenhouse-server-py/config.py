# -*- coding: utf-8 -*-
"""
Main configuration file.
"""
import os

_BASEDIR = os.path.abspath(os.path.dirname(__file__))

# Database config
DATABASE_NAME = os.environ.get('GREEN_DB_NAME', 'apptestmysql')

SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(basedir, DATABASE_NAME)
