__author__ = 'AmmiNi'

import unittest
import TwitterMessenger
import FacebookMessenger


class TestSNS(unittest.TestCase):

    def test_twitter(self):
        twitter_client = TwitterMessenger.TwitterMessenger()
        raised = False
        try:
            twitter_client.tweet("test message2")
        except:
            raised = True
        self.assertEqual(raised, False)
        self.assertRaises(Exception, twitter_client.tweet("test message2"))

    def test_facebook(self):
        facebook_client = FacebookMessenger.FacebookMessenger()
        raised = False
        try:
            facebook_client.post_message("test message2")
        except:
            raised = True
        self.assertEqual(raised, False)


if __name__ == '__main__':
    unittest.main()