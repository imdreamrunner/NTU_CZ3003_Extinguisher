from flask import Flask, request, jsonify
import datetime
import json


app = Flask(__name__)


@app.route('/update', methods=['POST'])
def update_incident():
    data_str = request.stream.read()
    decoded = json.loads(data_str)

    incident_list = parse_incident(decoded)

    for incident in incident_list:
        short_msg = parse_short_status(incident)

        # Facebook update status
        from FacebookMessenger import FacebookMessenger
        facebook_client = FacebookMessenger()
        facebook_client.post_message(short_msg)

        # Twitter update status
        from TwitterMessenger import TwitterMessenger
        twitter_client = TwitterMessenger()
        twitter_client.tweet(short_msg)

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
          + ". Location: " + incident['string_location']\
          + ". " + incident['remark']
    return msg


if __name__ == "__main__":
    app.debug = True
    app.run(host='0.0.0.0', port=16300)