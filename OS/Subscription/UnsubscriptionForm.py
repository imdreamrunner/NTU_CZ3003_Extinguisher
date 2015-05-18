from flask.ext.wtf import Form
from wtforms import TextField
from wtforms.validators import Required


class UnsubscriptionForm(Form):
    handphone = TextField('handphone', validators=[Required()])
    email = TextField('email', validators=[Required()])