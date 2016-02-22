# -*-  coding: utf-8 -*-
"""
Greenhouse's databse.
"""
from sqlalchemy import Column, String, Integer, Long, Double, Boolean, \
     DateTime
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()


# -- Instrument & inst. types --

class InstrumentType(object):
    id = Column(Integer, primary_key=True)
    name = Column(String(30), nullable=False)

    def __init__(self, name):
        self.name = name

class ActuatorType(InstrumentType):
    __tablename__ = 'actuator_type'

    def __init__(self, name):
        self.InstrumentType.__init__(self, name)

class SensorType(InstrumentType):
    __tablename__ = 'sensor_type'

    def __init__(self, name):
        self.InstrumentType.__init__(self, name)


# -- Instruments --

class Instrument(object):
    id = Column(Integer, primary_key=True)
    name = Column(String(30), nullable=False)
    pin_id = Column(String(10), nullable=False)

    def __init__(self, name, pin_id):
        self.name = name
        self.pin_id = pin_id
    
class Sensor(Instrument):
    __tablename__ = 'sensor'
    refresh_rate = Column(Long, nullable=False)
    power_saving_mode = Column(Boolean, default=False)
    ensure_refresh = Column(Boolean)
    last_refresh = Column(DateTime, nullable=True)
    sensor_type_id = None #TODO

    def __init__(self, name, pin_id, refresh_rate, power_saving_mode=False,
                 ensure_refresh=True, last_refresh=None):
        self.Instrument.__init__(self, name, pin_id)
        self.refresh_rate = refresh_rate
        self.power_saving_mode = power_saving_mode
        self.ensure_refresh = ensure_refresh
        self.last_refresh = last_refresh
        
class Actuator(Instrument):
    __tablename__ = 'actuator'
    compare_value = Column(Double, nullable=False)
    compare_type = None #TODO
    actuator_type_id = None #TODO
    control_sensor_id = None #TODO

    def __init__(self, name, pin_id, compare_value):
        self.compare_value = compare_value
