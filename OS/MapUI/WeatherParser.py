__author__ = 'Li Siyao'


import xml.etree.ElementTree as ET
import urllib2


def parse(url):
    html_content = urllib2.urlopen(url).read()
    tree = ET.fromstring(html_content)
    temperature = tree[0][12][5].attrib['temp']
    weather = tree[0][12][5].attrib['text']
    lat = float(tree[0][12][1].text)
    lng = float(tree[0][12][2].text)
    return {
        "temperature": temperature,
        "weather": weather,
        "lat": lat,
        "lng": lng
    }


urls = [
    'http://weather.yahooapis.com/forecastrss?w=24703041&u=c',
    'http://weather.yahooapis.com/forecastrss?w=24703042&u=c',
    'http://weather.yahooapis.com/forecastrss?w=24703043&u=c',
    'http://weather.yahooapis.com/forecastrss?w=24703044&u=c',
    'http://weather.yahooapis.com/forecastrss?w=24703045&u=c'
]


def get_weather():
    weather_list = []
    for url in urls:
        weather_list.append(parse(url))
    return weather_list


if __name__ == "__main__":
    print get_weather()

