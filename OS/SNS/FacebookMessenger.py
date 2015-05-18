import facebook


class FacebookMessenger:
    def __init__(self):
        self.graph = facebook.GraphAPI(
            "__REMOVED__")

    def post_message(self, msg):
        try:
            self.graph.put_object("__REMOVED__", "feed", message=msg)
            print "Facebook success"
        except Exception as e:
            print "Facebook update failed"
            print e.message
