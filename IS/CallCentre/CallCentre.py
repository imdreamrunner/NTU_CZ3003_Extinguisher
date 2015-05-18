import os
import time
from flask import Flask, render_template, request, json, jsonify
import requests
import ConfigParser

config = ConfigParser.ConfigParser()
config.read(os.path.dirname(os.path.realpath(__file__)) + '/../../server.cfg')

cps_address = config.get('CPS', 'address')

app = Flask(__name__)
app.debug = True


@app.route('/')
def hello_world():
    return render_template('report.html')


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


@app.route('/submit', methods=['POST'])
def submit_incident():
    try:
        incident_type = request.form['type']
        level = request.form['level']
        location_string = request.form['location']
        lat = request.form['lat']
        lng = request.form['lng']
        remark = request.form['remark']
        reporter_name = request.form['reporter-name']
        reporter_phone = request.form['reporter-phone']
        incident = {
            'initialId': None,
            'parent': None,
            'type': incident_type,
            'location': [
                {
                    'type': 'string',
                    'location': location_string
                },
                {
                    'type': 'gps',
                    'lat': float(lat),
                    'lng': float(lng)
                }
            ],
            'level': int(level),
            'startTime': int(time.time() * 1000),
            'completeTime': None,
            'remark': remark,
            'isValid': True,
            'reporter': {
                'name': reporter_name,
                'phone': reporter_phone
            }
        }
        ret_data = post_incident(incident)
        return ret_data
    except ValueError:
        return json.jsonify(error=1, message="Value Error")


@app.route('/test_post', methods=['POST'])
def test_post():
    return "ok"


# Same code are used in MapUI.


@app.route('/incident')
def get_incident():
    rd = request.args['rd']
    query_object = {
        "type": [rd],
        "completeTime": {
            "after": int(time.time() * 1000),
            "allowIncomplete": True
        },
        "isLatest": True
    }
    incidents = load_incident_from_cps(query_object)
    return jsonify(incidents)


@app.route('/incidentByInitialId', methods=['POST'])
def get_incident_by_initial_id():
    initial_id = request.form["initialId"]
    query_object = {
        "initialId": [initial_id],
        "isLatest": True
    }
    incidents = load_incident_from_cps(query_object)
    return jsonify(incidents)


def load_incident_by_initial_id(initial_id):
    query_object = {
        "initialId": [initial_id],
        "isLatest": True
    }
    incidents = load_incident_from_cps(query_object)['data']
    print "load one ", incidents
    return incidents[0]


def load_incident_from_cps(query_object):
    post_pcakage = {
        "query": query_object,
        "operator": {
            "username": "operator5",
            "password": "1234"
        }
    }
    print json.dumps(post_pcakage)
    r = requests.post(cps_address + "request", data=json.dumps(post_pcakage))
    print "receive data: " + r.text
    incidents = json.loads(r.text)
    return incidents


@app.route('/completeIncident', methods=['POST'])
def complete_incident():
    initial_id = request.form['initialId']
    incident = load_incident_by_initial_id(initial_id)
    del incident['_id']
    del incident['isLatest']
    if 'completeTime' in request.form:
        incident['completeTime'] = int(request.form['completeTime'])
    print incident
    ret_date = post_incident(incident)
    return ret_date


@app.route('/updateIncident', methods=['POST'])
def update_incident():
    initial_id = request.form['initialId']
    incident = load_incident_by_initial_id(initial_id)
    del incident['_id']
    del incident['isLatest']
    incident['remark'] = request.form['remark']
    incident['type'] = request.form['type']
    incident['level'] = int(request.form['level'])
    location = [{
                    "type": "string",
                    "location": request.form['location']
                },
                {
                        "type": "gps",
                        "lat": float(request.form['lat']),
                        "lng": float(request.form['lng'])
                }]
    incident['location'] = location
    incident['reporter'] = {
        "name": "Related Department"
    }
    ret_date = post_incident(incident)
    return ret_date


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=15000)
