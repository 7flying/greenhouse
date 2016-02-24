# -*-  coding: utf-8 -*-
"""
Greenhouse's databse.
"""
from sqlalchemy import Column, String, Integer, Float, Boolean, DateTime, \
     ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship, backref

Base = declarative_base()


# -- Instrument & inst. types --

class InstrumentType(Base):
    __tablename__ = 'instrument_type'
    id = Column(Integer, primary_key=True)
    name = Column(String(30), nullable=False)
    identifier = Column(String(1), unique=True)
    unit = Column(String(15))
    type = Column(String(30))

    __mapper_args__ = {
        'polymorphic_identity': 'instrument_type',
        'polymorphic_on': type
    }


class ActuatorType(InstrumentType):
    __tablename__ = 'actuator_type'
    id = Column(Integer, ForeignKey('instrument_type.id'), primary_key=True)
    actuators = relationship('Actuator', back_populates='actuator_type')

    __mapper_args__ = { 'polymorphic_identity': 'actuator_type',}


class SensorType(InstrumentType):
    __tablename__ = 'sensor_type'
    id = Column(Integer, ForeignKey('instrument_type.id'), primary_key=True)
    sensors = relationship('Sensor', back_populates='sensor_type')

    __mapper_args__ = { 'polymorphic_identity': 'sensor_type',}


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


class Sensor(Instrument):
    __tablename__ = 'sensor'
    id = Column(Integer, ForeignKey('instrument.id'), primary_key=True)
    refresh_rate = Column(Integer, nullable=False)
    power_saving_mode = Column(Boolean, default=False)
    ensure_refresh = Column(Boolean)
    last_refresh = Column(DateTime, nullable=True)
    sensor_type_id = Column(Integer, ForeignKey('sensor_type.id'))
    sensor_type = relationship('SensorType', back_populates='sensors')

    __mapper_args__ = { 'polymorphic_identity': 'sensor',}


class Actuator(Instrument):
    __tablename__ = 'actuator'
    id = Column(Integer, ForeignKey('instrument.id'), primary_key=True)
    compare_value = Column(Float, nullable=False)
    compare_type = None #TODO
    actuator_type_id = Column(Integer, ForeignKey('actuator_type.id'))
    actuator_type = relationship('ActuatorType', back_populates='actuators')
    control_sensor_id = Column(Integer, ForeignKey('sensor.id'))
    control_sensor = relationship('Sensor', backref=backref('sensor',
                                                            uselist=False),
                                  foreign_keys=[control_sensor_id])

    __mapper_args__ = { 'polymorphic_identity': 'actuator',}
