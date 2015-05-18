__author__ = 'AmmiNi'

from Subscription import db


class Subscription(db.Model):
    __tablename__ = "subscription"

    id = db.Column(db.Integer, primary_key=True)
    handphone = db.Column(db.String(20))
    email = db.Column(db.String(50))
    region = db.Column(db.String(10))
    handphone_verified = db.Column(db.SmallInteger)
    email_verified = db.Column(db.SmallInteger)
    longtitude = db.Column(db.Float)
    latitude = db.Column(db.Float)
    create_time = db.Column(db.DateTime)
    email_hash = db.Column(db.String(100))

    def __repr__(self):
        return '<subscription %r>' % self.id
