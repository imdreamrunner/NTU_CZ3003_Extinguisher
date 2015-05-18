import ConfigParser
from flask import Flask, request, jsonify
import datetime
import json
import os

config = ConfigParser.ConfigParser()
config.read(os.path.dirname(os.path.realpath(__file__)) + '/../../server.cfg')

rd_email = config.get('RD', 'email')
rd_phone = config.get('RD', 'phone')


app = Flask(__name__)

@app.route('/update', methods=['POST'])
def update_incident():
    data_str = request.stream.read()
    decoded = json.loads(data_str)

    incident_list = parse_incident(decoded)

    for incident in incident_list:
        short_msg = parse_short_status(incident)
        long_msg = parse_long_status(incident)

        from SMSSender import SMSSender
        sms_client = SMSSender(short_msg, incident['gps_location'])
        sms_client.start()

        from EmailSender import EmailSender
        email_client = EmailSender('incidentsinsg@gmail.com', '__REMOVED__', long_msg, incident['gps_location'])
        email_client.start()

        # print "Sent email", long_msg

    ret = {
        "success": True
    }
    return jsonify(ret)


"""
data format

[{
     "tags": ["fire", "level1"],
     "incident": {
         "startTime": 1396509341400,
         "isLatest": true,
         "level": 1,
         "timeStamp": 1396509354240,
         "remark": "lala",
         "location": [{
                          "location": "Changi Airport Terminal 2, Airport Boulevard, Singapore 819643",
                          "type": "string"
                      }, {
                          "lat": 1.35372,
                          "lng": 103.989,
                          "type": "gps"
                      }],
         "parent": null,
         "type": "fire",
         "isValid": true,
         "completeTime": null,
         "operator": "operator5",
         "initialId": "533d0aaa84ae5f59ea022715",
         "_id": "533d0aaa84ae5f59ea022715"
     }
 }]
"""


def parse_incident(decoded):
    incident_list = []
    for incident in decoded:
        notification_type = "new"
        if incident['incident']['_id'] != incident['incident']['initialId']:
            notification_type = "update"
        if incident['incident']['completeTime'] is not None:
            notification_type = "complete"
        start_time = datetime.datetime.fromtimestamp(incident['incident']['startTime'] / 1e3)
        start_time_str = start_time.strftime('%H:%M %m/%d/%Y')
        remark = incident['incident']['remark']
        type = incident['incident']['type']
        level = incident['incident']['level']
        string_location = "Singapore"
        gps_location = None
        for location in incident['incident']['location']:
            if location['type'] == 'string':
                string_location = location['location']
            if location['type'] == 'gps':
                gps_location = {
                    "lat": location['lat'],
                    "lng": location['lng']
                }
        incident_list.append({
            "notification_type": notification_type,
            "type": type,
            "time": start_time_str,
            "remark": remark,
            "level": level,
            "string_location": string_location,
            "gps_location": gps_location
        })
    return incident_list


def parse_long_status(incident):
    msg = """\
<html>
  <head></head>
  <body>"""
    if incident['notification_type'] == "new":
        msg += "<h3>Incident Alert</h3>"
    elif incident['notification_type'] == "update":
        msg += "<h3>Incident Update</h3>"
    elif incident['notification_type'] == "complete":
        msg += "<h3>Incident Alert Lifted</h3>"
    msg += """\
    <div>
"""
    msg += "<p> Time: " + incident['time'] + "</p>"
    msg += "<p> Type: " + incident['type'] + "</p>"
    msg += "<p> Level: " + str(incident['level']) + "</p>"
    msg += "<p> Location: " + incident['string_location'] + "</p>"
    msg += "<p> Remark: " + incident['remark'] + "</p>"
    msg += """
    </div>
  </body>
</html>
"""
    return msg


def parse_short_status(incident):
    title = ""
    if incident['notification_type'] == 'new':
        title = 'Incident Alert'
    elif incident['notification_type'] == 'update':
        title = 'Incident Update'
    elif incident['notification_type'] == 'complete':
        title = 'Incident Alert Lifted'
    msg = title + ": " + incident['type']\
          + " level " + str(incident['level'])\
          + " reported at " + incident['time']\
          + ". Location: " + incident['string_location']\
          + ". " + incident['remark']
    return msg


if __name__ == "__main__":
    app.debug = True
    app.run(host='0.0.0.0', port=16100)