__author__ = 'AmmiNi'

import unittest
import WeatherParser


class WeatherParserTest(unittest.TestCase):


    def test_weather_parser(self):
        """Test may fail due to change in weather information
        """
        self.assertEqual(WeatherParser.get_weather().__repr__(),
                         "[{'lat': 1.34, 'lng': 103.99, 'weather': 'Mostly Cloudy', 'temperature': '29'}, {'lat': 1.37, 'lng': 103.91, 'weather': 'Partly Cloudy', 'temperature': '27'}, {'lat': 1.37, 'lng': 103.69, 'weather': 'Mostly Cloudy', 'temperature': '26'}, {'lat': 1.34, 'lng': 103.84, 'weather': 'Partly Cloudy', 'temperature': '27'}, {'lat': 1.39, 'lng': 103.81, 'weather': 'Mostly Cloudy', 'temperature': '26'}]")