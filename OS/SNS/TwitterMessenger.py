__author__ = 'AmmiNi'

import twitter


class TwitterMessenger:
    def __init__(self):
        self.api = twitter.Api(consumer_key='__REMOVED__',
                               consumer_secret='__REMOVED__',
                               access_token_key='__REMOVED__',
                               access_token_secret='__REMOVED__')

    def tweet(self, msg):
        ###
        try:
            self.api.PostUpdate(msg[0:100])
            print "Twitter success"
        except Exception as e:
            print "Twitter messenger failed."
            print e.message


if __name__ == '__main__':
    twitter_client = TwitterMessenger()
    #statuses = twitter_client.api.GetUserTimeline()
    #for s in statuses:
    #    print twitter_client.api.DestroyStatus(s.id)
    twitter_client.tweet("test message2")