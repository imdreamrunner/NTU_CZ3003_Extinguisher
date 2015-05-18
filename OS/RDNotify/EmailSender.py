import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
import threading
from RDNotify import rd_email

HEADER = '\033[95m'
OKBLUE = '\033[94m'
OKGREEN = '\033[92m'
WARNING = '\033[93m'
FAIL = '\033[91m'
ENDC = '\033[0m'


def the_print(info):
    print OKGREEN + "[EMAIL]" + ENDC, info


class EmailSender (threading.Thread):
    def __init__(self, username, password, message='', gps = None):
        threading.Thread.__init__(self)
        self.sender = username
        self.message = message
        self.gps = gps
        try:
            self.session = smtplib.SMTP('smtp.gmail.com:587')
            self.session.ehlo()
            self.session.starttls()
            self.session.login(username, password)
        except smtplib.SMTPException:
            print "Unable to connect to SMTP server"

    def send_email_to(self, receiver, subject, body):
        """ send one email to receiver, with given subject line and body

        Body will be sent in format of HTML.
        Differentiate this method with run() which sends email to all users.
        """
        msg = MIMEMultipart('alternative')
        msg['Subject'] = subject
        msg['From'] = self.sender
        msg['To'] = receiver

        html_body = MIMEText(body, 'html')
        msg.attach(html_body)

        try:
            self.session.sendmail(self.sender, receiver, msg.as_string())
        except smtplib.SMTPException:
            print "Error: unable to send email"

    def run(self):
        """Trigger thread run and send message (given during construction) to all subscribed users
        """
        the_print("thread starts. email sender ready.")
        self.send_email_to("zhou0199@e.ntu.edu.sg", "SG Incident Alert", self.message)
        the_print("finished sending email.")
if __name__ == '__main__':
    email_client = EmailSender(rd_email, '__REMOVED__', "test")
    email_client.start()
