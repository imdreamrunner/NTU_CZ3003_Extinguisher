import ConfigParser
from datetime import datetime
import time
import os
from flask import json
import requests
from weasyprint import HTML

file_path = os.path.dirname(os.path.realpath(__file__)) + "/"

REPORT_DURATION = 1.8e+6  # 30 minutes

config = ConfigParser.ConfigParser()
config.read(os.path.dirname(os.path.realpath(__file__)) + '/../../server.cfg')

cps_address = config.get('CPS', 'address')


def get_incidents():
    query_object = {
        "completeTime": {
            "after": int(time.time() * 1000 - REPORT_DURATION),
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
    r = requests.post(cps_address + "request", data=json.dumps(post_package))
    incidents = json.loads(r.text)
    # print "receive incidents:", incidents
    return incidents


def get_content():
    incidents = get_incidents()['data']
    type_map = {}
    for incident in incidents:
        type_name = incident['type']
        if type_name not in type_map.keys():
            type_map[type_name] = []
        type_map[type_name].append(incident)
    output_html = "<h1>Incidents Statistics</h1>"
    for type_name, incident_list in type_map.items():
        output_html += "<div><h3>"
        output_html += type_name
        output_html += "</h3><br/> Total: " + str(len(incident_list))
        output_html += "<br/>"
        i = 0
        for incident in incident_list:
            i += 1
            output_html += "> " + str(i)
            location = ""
            for item in incident['location']:
                if item["type"] == "string":
                    location = item["location"]
            output_html += "<br />"
            output_html += " >>> Location: " + location
            output_html += "<br />"
            output_html += " >>> Remark: " + incident['remark']
            output_html += "<br />"
        output_html += "</div>"
    return output_html


def generate_file():
    f = open('report.html', 'w')
    h = open('header.html', 'r')
    for line in h:
        f.write(line)
    f.write(get_content())
    h = open('footer.html', 'r')
    for line in h:
        f.write(line)
    f.close()


def generate_report():
    generate_file()
    file_name = "report_" + str(datetime.now()) + ".pdf"
    HTML("file://" + file_path + "report.html").write_pdf(file_path + file_name)
    return file_name


if __name__ == "__main__":
    report = generate_report()
    print "Report generated at", report