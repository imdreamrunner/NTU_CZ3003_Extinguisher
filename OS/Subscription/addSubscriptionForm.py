from flask.ext.wtf import Form
from wtforms import TextField, SelectField, validators


class AddSubscriptionForm(Form):
    handphone = TextField('handphone',
                          validators=[validators.Regexp(r'^\d+$'), validators.Length(min=8, max=8)])
    email = TextField('email', validators=[validators.Regexp(r'^[\w.@+-]+$'), validators.Length(min=4, max=50)])

    # area = SelectField('Area', choices=[
    #    ('', 'Select Region'), ('east', 'East'), ('west', 'West'), ('south', "South"), ('north', "North"),
    #    ('central', "Central"), ('all', "All")])
    lat = TextField('lat')
    lng = TextField('lng')