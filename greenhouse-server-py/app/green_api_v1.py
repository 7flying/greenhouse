# -*- coding: utf-8 -*-
"""
green_api v1: to manage sensors, actuators 
"""
from flask import jsonify, request, Blueprint

green_api = Blueprint('green_api', __name__, url_prefix='/green/v1')

@green_api.route('/greenhouse/<greenhouse_id>',
                 methods=['GET', 'POST', 'DELETE'])
@green_api.route('/greenhouse/', defaults={'greenhouse_id': None},
                 methods=['GET', 'POST'])
def greenhouse(greenhouse_id):
    pass

@green_api.route('/instrument/<greenhouse_id>', methods=['POST', 'DELETE'])
def sensor(greenhouse_id):
    pass

@green_api.route('/instruments/<greenhouse_id>')
def sensors(greenhouse_id):
    pass

@green_api.route('/alert/<greenhouse_id>', methods=['POST', 'DELETE'])
def alert(greenhouse_id):
    pass

@green_api.route('/alerts/<greenhouse_id>')
def alerts(greenhouse_id):
    pass
