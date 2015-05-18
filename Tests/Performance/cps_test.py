import ConfigParser
import datetime
import time
import os
from flask import json
import requests


config = ConfigParser.ConfigParser()
config.read(os.path.dirname(os.path.realpath(__file__)) + '/server.cfg')

cps_address = config.get('CPS', 'address')


def test_report_incident():
    start_time = datetime.datetime.now()
    print "start report test: " + str(start_time)
    post_package = """
    {
        "incident": {
            "completeTime": null,
            "initialId": null,
            "isValid": true,
            "level": 1,
            "location": [{
                "location": "Nanyang Technological University (NTU) - Administration Building, 50 Nanyang Avenue, Singapore 639798",
                "type": "string"
            }, {
                "lat": 1.34447,
                "lng": 103.681,
                "type": "gps"
            }],
            "parent": null,
            "remark": "test",
            "reporter": {
                "name": "test",
                "phone": "88888888"
            },
            "startTime": 1397752574928,
            "type": "fire"
        },
        "operator": {
            "password": "1234",
            "username": "operator5"
        }
    }"""
    try:
        requests.post(cps_address + "request", data=json.dumps(post_package))
    except Exception as ex:
        print "Exception appear"
        print ex.message
        print "test fails"
        return
    end_time = datetime.datetime.now()
    print "get report data: " + str(start_time)
    duration = end_time - start_time
    print "get data in " + str(duration.total_seconds()) + "s"
    if duration.total_seconds() <= 5.0:
        print "report test is passed"
    else:
        print "test fails"


def test_query_incident():
    start_time = datetime.datetime.now()
    print "start query test: " + str(start_time)
    query_object = {
        "type": ["fire"],
        "completeTime": {
            "after": int(time.time() * 1000),
            "allowIncomplete": True
        },
        "isLatest": True
    }
    post_package = {
        "query": query_object,
        "operator": {
            "username": "operator5",
            "password": "1234"
        }
    }
    try:
        requests.post(cps_address + "request", data=json.dumps(post_package))
    except Exception as ex:
        print "Exception appear"
        print ex.message
        print "test fails"
        return
    end_time = datetime.datetime.now()
    print "get query data: " + str(start_time)
    duration = end_time - start_time
    print "get data in " + str(duration.total_seconds()) + "s"
    if duration.total_seconds() <= 5.0:
        print "query test is passed"
    else:
        print "test fails"


def main():
    test_report_incident()
    test_query_incident()


if __name__ == '__main__':
    main()