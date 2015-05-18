from flask import Flask, render_template, redirect, request, jsonify, flash
from flask.ext.sqlalchemy import SQLAlchemy
import datetime
import json
import hashlib
import ConfigParser
import os

app = Flask(__name__)
app.config.from_object('config')
db = SQLAlchemy(app)


class Subscription(db.Model):
    __tablename__ = "subscription"

    id = db.Column(db.Integer, primary_key=True)
    handphone = db.Column(db.String(20))
    email = db.Column(db.String(50))
    region = db.Column(db.String(10))
    handphone_verified = db.Column(db.SmallInteger)
    email_verified = db.Column(db.SmallInteger)
    longtitude = db.Column(db.Float)
    latitude = db.Column(db.Float)
    create_time = db.Column(db.DateTime)
    email_hash = db.Column(db.String(100))
    handphone_hash = db.Column(db.String(10))

    def __repr__(self):
        return '<subscription %r>' % self.id


# add subscription page
from addSubscriptionForm import AddSubscriptionForm


@app.route('/addsubscription', methods=['GET', 'POST'])
def add_subscription():
    form = AddSubscriptionForm()
    if form.validate_on_submit():

        existing_subscription = Subscription.query.filter_by(handphone="+65" + form.handphone.data,
                                                             email=form.email.data).first()
        # if the subscription already exist
        if existing_subscription:
            if existing_subscription.handphone_verified != 1 or existing_subscription.email_verified != 1:
                # if not verified, re\send verification
                flash("""
                      You have subscribed to our services but have not verified your email or handphone.
                      We have resent the email to your email address and resend SMS to your handphone for verification
                      """, category='info')
                send_verification_email(existing_subscription)
            else:
                flash("You have already subscribed our services.", category='info')
        else:
            new_subscription = Subscription(
                handphone="+65" + form.handphone.data,
                email=form.email.data,
                region=0,
                handphone_verified=0,
                email_verified=0,
                longtitude=form.lng.data,
                latitude=form.lat.data,
                create_time=datetime.datetime.utcnow(),
                email_hash=hashlib.sha224(form.email.data).hexdigest(),
                handphone_hash=hashlib.sha224(form.handphone.data).hexdigest()[0:6]
            )
            db.session.add(new_subscription)
            db.session.commit()
            send_verification_email(new_subscription)
            send_verification_message(new_subscription)
            flash("Subscription successful. We have sent email to your email address for verification",
                  category='success')
        return redirect('/addsubscription')
    return render_template('addSubscription.html',
                           form=form)


from UnsubscriptionForm import UnsubscriptionForm


@app.route('/unsubscribe', methods=['GET', 'POST'])
def unsubscribe():
    form = UnsubscriptionForm()
    if form.validate_on_submit():
        existing_subscription = Subscription.query.filter_by(handphone="+65" + form.handphone.data,
                                                             email=form.email.data).first()
        # if the subscription already exist, 
        if existing_subscription:
            existing_subscription.handphone_verified = 0
            existing_subscription.email_verified = 0
            db.session.commit()
            flash("You have successfully unsubscribed our services.", category='success')
        else:
            flash("Your credentials does not exist.", category='error')
        return redirect('/unsubscribe')
    return render_template('unsubscribe.html',
                           form=form)


@app.route('/update', methods=['POST'])
def update_incident():
    data_str = request.stream.read()
    decoded = json.loads(data_str)

    incident_list = parse_incident(decoded)

    for incident in incident_list:
        short_msg = parse_short_status(incident)
        long_msg = parse_long_status(incident)

        # Email and SMS update to individual subscriber
        from SMSSender import SMSSender
        sms_client = SMSSender(short_msg, incident['gps_location'])
        sms_client.start()
        #print "SMS content: ", short_msg

        from EmailSender import EmailSender
        email_client = EmailSender('incidentsinsg@gmail.com', 'incidents', long_msg, incident['gps_location'])
        email_client.start()

        #print "Sent email", long_msg

    ret = {
        "success": True
    }
    return jsonify(ret)

from VerifyHandphoneForm import VerifyHandphoneForm


@app.route('/verify', methods=['GET', 'POST'])
def verify_email():
    existing_subscription = Subscription.query.filter_by(email_hash=request.args.get('hash')).first()
    if existing_subscription:
        existing_subscription.email_verified = 1
        db.session.commit()
        flash("You have successfully verified your email: " + existing_subscription.email, category='success')
    else:
        flash("The hash code you have entered does not match any record.", category='error')

    form = VerifyHandphoneForm()
    if form.validate_on_submit():
        existing_subscription_sms = Subscription.query.filter_by(handphone_hash=form.handphone_hash.data).first()
        # if the subscription already exist
        if existing_subscription_sms:
            existing_subscription_sms.handphone_verified = 1
            db.session.commit()
            flash("You have successfully verified your handphone.", category='success')
        else:
            flash("Your verification code does not exist.", category='error')
    return render_template('verify.html',
                           form=form)


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


def send_verification_email(subscription):
    from EmailSender import EmailSender

    config = ConfigParser.ConfigParser()
    config.read(os.path.dirname(os.path.realpath(__file__)) + '/../../server.cfg')
    local_address = config.get('OS', 'subscribe')

    emailclient = EmailSender('incidentsinsg@gmail.com', 'incidents')
    emailclient.send_email_to(subscription.email, "Verification of Subscription", "Please click the link below to verify your registration."
                                                                                  + local_address + "verify?hash=" + subscription.email_hash)


def send_verification_message(subscription):
    from SMSSender import SMSSender

    smsclient = SMSSender("", "")
    smsclient.send_message(subscription.handphone, "Your verification code is " + subscription.handphone_hash)

if __name__ == "__main__":
    app.debug = True
    app.run(host='0.0.0.0', port=16200)