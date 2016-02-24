# -*- coding: utf-8 -*-
"""
Db creation script.
"""

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from app import models, modelsconst
import config

def create():
    """Creates the engine"""
    engine = create_engine(config.SQLALCHEMY_DATABASE_URI)
    Session = sessionmaker(bind=engine)
    session = Session()
    models.Base.metadata.create_all(bind=engine)

    # Add constants
    for sensor_t in modelsconst.SENSOR_TYPES:
        tmp = models.SensorType(name=sensor_t)
        session.add(tmp)
        session.commit()

if __name__ == '__main__':
    create()
