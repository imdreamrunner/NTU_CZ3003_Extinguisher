import ConfigParser
import os
import datetime
from flask import json
import requests
import time
from EmailSender import EmailSender
import ReportGenerator


def main():
    print "Report server starts."
    email_client = EmailSender('incidentsinsg@gmail.com', 'incidents', "test")
    title = 'Crisis Report on ' + str(datetime.datetime.now())
    file_name = ReportGenerator.generate_report()
    msg = "Dear officer, <br />"
    msg += "This is the incident report for the past 30 minutes. <br />"
    msg += "Regards, <br />"
    msg += "Crisis Management System Singapore"
    email_client.send_email_to("zhou0199@e.ntu.edu.sg", title, msg, file_name)

if __name__ == '__main__':
    main()