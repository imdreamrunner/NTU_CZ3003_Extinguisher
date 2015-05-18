from twilio.rest import TwilioRestClient
import threading
from Subscription import Subscription
import latlng

HEADER = '\033[95m'
OKBLUE = '\033[94m'
OKGREEN = '\033[92m'
WARNING = '\033[93m'
FAIL = '\033[91m'
ENDC = '\033[0m'


def the_print(info):
    print OKBLUE + "[SMS]" + ENDC, info


class SMSSender(threading.Thread):
    def __init__(self, msg, gps):
        threading.Thread.__init__(self)
        self.account_sid = '__REMOVED__'
        self.auth_token = '__REMOVED__'
        self.client = TwilioRestClient(self.account_sid, self.auth_token)
        self.msg = msg
        self.gps = gps

    def send_message(self, user, message):
        """ send one SMS to user, with given message

        Differentiate this method with run() which sends SMS to all users.
        """
        try:
            self.client.messages.create(body=message, to=user, from_='+19794315676')
        except Exception:
            print "send SMS to subscriber failed."

    def run(self):
        """Trigger thread run and send message (given during construction) to all subscribed users
        """
        the_print("thread starts. sender ready.")
        subscriptions = Subscription.query.all()
        for s in subscriptions:
            if s.handphone_verified == 1:
                the_print("prepare sms to " + s.handphone)
                if self.gps is None:
                    self.send_message(s.handphone, self.msg)
                    continue
                distance = latlng.distance(self.gps['lat'], self.gps['lng'], s.latitude, s.longtitude)
                the_print("distance: " + str(distance) + "km")
                if distance <= 10:
                    the_print("within distance, sent sms to " + s.handphone)
                    self.send_message(s.handphone, self.msg)
                else:
                    the_print("too far, do not sent to" + s.handphone)
        the_print("all SMS sent. thread finished.")