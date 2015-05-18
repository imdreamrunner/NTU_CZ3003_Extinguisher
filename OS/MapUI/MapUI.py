import os
from flask import Flask, render_template, jsonify, request
import json
import requests
import time
import ConfigParser
import IncidentCache


config = ConfigParser.ConfigParser()
config.read(os.path.dirname(os.path.realpath(__file__)) + '/../../server.cfg')

cps_address = config.get('CPS', 'address')
subscribe_address = config.get('OS', 'subscribe')

app = Flask(__name__)
app.debug = True


def the_print(info):
    print "[LOADER]", info



@app.route('/')
def index():
    return render_template("index.html", subscribe_address=subscribe_address)


@app.route('/incident')
def get_incident():
    from_value = int(request.args['from'])
    to_value = None
    to_str = request.args['to']
    if len(to_str) > 0:
        to_value = int(to_str)
    # print from_value, to_value
    incidents = IncidentCache.get(from_value, to_value)
    if incidents is None:
        incidents = load_incident_from_cps(from_value, to_value)
        IncidentCache.insert(incidents, from_value, to_value)
    return jsonify(incidents)


def load_incident_from_cps(from_value, to_value):
    the_print("try to load incident from cps.")
    type_list = ["fire", "haze", "dengue", "illness", "rescue", "gas"]
    query = {
        "type": type_list,
        "completeTime": {
            "after": from_value,
            "allowIncomplete": True
        },
        "isLatest": True
    }
    if to_value is not None:
        query = {
            "type": type_list,
            "startTime": {
                "before": to_value
            },
            "completeTime": {
                "after": from_value,
                "allowIncomplete": False
            },
            "isLatest": True
        }
    post_pcakage = {
        "query": query,
        "operator": {
            "username": "operator5",
            "password": "1234"
        }
    }
    the_print(json.dumps(post_pcakage))
    r = requests.post(cps_address + "request", data=json.dumps(post_pcakage))
    # print r.text
    incidents = json.loads(r.text)
    the_print("loaded incidents from cps.")
    return incidents


@app.route('/update', methods=['POST'])
def update_incident():
    data_str = request.stream.read()
    data = json.loads(data_str)
    print data_str
    print data
    ret = {
        "success": True
    }
    return jsonify(ret)


@app.route('/weather')
def get_weather():
    the_print("try to load weather.")
    import WeatherParser
    weather_list = WeatherParser.get_weather()
    # print weather_list
    the_print("weather loaded.")
    return json.dumps(weather_list)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=16000)
