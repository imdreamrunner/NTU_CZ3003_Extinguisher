__author__ = 'AmmiNi'

import unittest
import HazeInfoParser


class HazeInfoParserTest(unittest.TestCase):
    def test_parser(self):
        """Test may fail due to change in haze reading
        """
        parser = HazeInfoParser.HazeInfoParser()
        self.assertEqual(parser.north_data, 62)
        self.assertEqual(parser.south_data, 61)
        self.assertEqual(parser.east_data, 59)
        self.assertEqual(parser.west_data, 61)
        self.assertEqual(parser.central_data, 60)

if __name__ == '__main__':
    unittest.main()