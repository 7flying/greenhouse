# -*-  coding: utf-8 -*-
"""
Greenhouse's databse.
"""
from sqlalchemy import Column, String, Integer, Long, Double, Boolean, \
     DateTime, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship

Base = declarative_base()


# -- Instrument & inst. types --

class InstrumentType(Base):
    __tablename__ = 'instrument_type'
    id = Column(Integer, primary_key=True)
    name = Column(String(30), nullable=False)
    type = Column(String(30))

    __mapper_args__ = {
        'polymorphic_identity': 'instrument_type',
        'polymorphic_on': type
    }

    def __init__(self, name):
        self.name = name

class ActuatorType(InstrumentType):
    __tablename__ = 'actuator_type'
    actuator_id = Column(Integer, ForeignKey('actuator.id'))
    actuator = relationship('Actuator', back_populates='actuator_type',
                            uselist=False)

    __mapper_args__ = { 'polymorphic_identity': 'actuator_type'}

    def __init__(self, name):
        self.InstrumentType.__init__(self, name)

class SensorType(InstrumentType):
    __tablename__ = 'sensor_type'
    sensor_id = Column(Integer, ForeignKey('sensor.id'))
    sensor = relationship('Sensor', back_populates='sensor_type',
                          uselist=False)

    __mapper_args__ = { 'polymorphic_identity': 'sensor_type'}

    def __init__(self, name):
        self.InstrumentType.__init__(self, name)


# -- Instruments --

class Instrument(Base):
    __tablename__ = 'instrument'
    id = Column(Integer, primary_key=True)
    name = Column(String(30), nullable=False)
    pin_id = Column(String(10), nullable=False)
    type = Column(String(30))

    __mapper_args__ = {
        'polymorphic_identity': 'instrument',
        'polymorphic_on': type
    }

    def __init__(self, name, pin_id):
        self.name = name
        self.pin_id = pin_id
    
class Sensor(Instrument):
    __tablename__ = 'sensor'
    refresh_rate = Column(Long, nullable=False)
    power_saving_mode = Column(Boolean, default=False)
    ensure_refresh = Column(Boolean)
    last_refresh = Column(DateTime, nullable=True)
    sensor_type_id = Column(Integer, ForeignKey('sensor_type.id'))
    sensor_type = relationship('SensorType', backref=backref('sensor_type',
                                                             uselist=False))

    __mapper_args__ = { 'polymorphic_identity': 'sensor'}

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
    actuator_type_id = Column(Integer, ForeignKey('actuator_type.id'))
    actuator_type = relationship('ActuatorType', backref=backref('actuator_type',
                                                                 uselist=False))
    control_sensor_id = Column(Integer, ForeignKey('sensor.id'))
    control_sensor = relationship('sensor', backref=backref('sensor',
                                                            uselist=False))

    __mapper_args__ = { 'polymorphic_identity': 'actuator'}

    def __init__(self, name, pin_id, compare_value):
        self.compare_value = compare_value
