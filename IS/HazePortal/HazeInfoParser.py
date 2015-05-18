import urllib2
from HTMLParser import HTMLParser


class HazeInfoParser(HTMLParser):
    default_url = "http://app2.nea.gov.sg/anti-pollution-radiation-protection/air-pollution-control/psi/psi-readings-over-the-last-24-hours"

    def __init__(self, url=default_url):
        HTMLParser.__init__(self)
        self.url = url
        self.north_data = 0
        self.south_data = 0
        self.east_data = 0
        self.west_data = 0
        self.central_data = 0
        self.tr_count = 0
        html_content = urllib2.urlopen(url).read()
        self.feed(html_content)

    def handle_starttag(self, tag, attrs):
        if tag == "tr":
            self.tr_count += 1

    def handle_data(self, data):
        if self.tr_count == 2 or self.tr_count == 9:
            if data.isdigit():
                self.north_data = int(data)
        elif self.tr_count == 3 or self.tr_count == 10:
            if data.isdigit():
                self.south_data = int(data)
        elif self.tr_count == 4 or self.tr_count == 11:
            if data.isdigit():
                self.east_data = int(data)
        elif self.tr_count == 5 or self.tr_count == 12:
            if data.isdigit():
                self.west_data = int(data)
        elif self.tr_count == 6 or self.tr_count == 13:
            if data.isdigit():
                self.central_data = int(data)

if __name__ == '__main__':
    # parser = HazeInfoParser(url=r'file:///D|\EdveNTUre & Data\Year 3 sem 2\CZ3003\CZ3003_Extinguisher\IS\HazePortal\PSI_Readings.htm')
    parser = HazeInfoParser()
    print parser.url
    print parser.north_data
    print parser.south_data
    print parser.east_data
    print parser.west_data
    print parser.central_data
