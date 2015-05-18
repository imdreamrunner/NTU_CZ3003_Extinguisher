__author__ = 'AmmiNi'

import unittest
import SMSSender
import EmailSender


class NotificationTest(unittest.TestCase):
    def test_one_sms(self):
        sms_client = SMSSender.SMSSender("test sms", '')
        raised = False
        try:
            sms_client.send_message("+6594681497", "Test message")
        except:
            raised = True

        self.assertNotEqual(raised, True)

    def test_one_email(self):
        email_client = EmailSender.EmailSender('incidentsinsg@gmail.com', 'incidents', "test")
        raised = False
        try:
            email_client.send_email_to('lisi0010@e.ntu.edu.sg', 'Test email', 'test email')
        except:
            raised = True

        self.assertNotEqual(raised, True)

    def test_all_sms(self):
        sms_client = SMSSender.SMSSender("test sms", '')
        raised = False
        try:
            sms_client.start()
        except:
            raised = True

        self.assertNotEqual(raised, True)

    def test_all_email(self):
        email_client = EmailSender.EmailSender('incidentsinsg@gmail.com', 'incidents', "test")
        raised = False
        try:
            email_client.start()
        except:
            raised = True

        self.assertNotEqual(raised, True)

if __name__ == '__main__':
    unittest.main()