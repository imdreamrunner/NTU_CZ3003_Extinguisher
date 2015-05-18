import ConfigParser
from flask import json
import os
import requests
import time


config = ConfigParser.ConfigParser()
config.read(os.path.dirname(os.path.realpath(__file__)) + '/../../server.cfg')

cps_address = config.get('CPS', 'address')


def post_incident(incident):
    post_package = {
        "incident": incident,
        "operator": {
            "username": "operator5",
            "password": "1234"
        }
    }
    r = requests.post(cps_address + "incident", data=json.dumps(post_package))
    print "here"
    print json.dumps(post_package)
    print r.text
    return r.text


def prepare_incident_data(region, level):
    incident = {
        'initialId': None,
        'parent': None,
        'type': 'haze',
        'location': [
            {
                'type': 'region',
                'regions': [region]

            }
        ],
        'level': int(level),
        'startTime': int(time.time() * 1000),
        'completeTime': None,
        'remark': "A level " + str(level) + " haze report",
        'isValid': True,
        'reporter': {
            'name': "NEA.gov.sg"
        }
    }
    post_incident(incident)


if __name__ == "__main__":
    print "hello world"
    prepare_incident_data(2, 1)