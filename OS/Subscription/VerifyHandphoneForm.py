from flask.ext.wtf import Form
from wtforms import TextField
from wtforms.validators import Required


class VerifyHandphoneForm(Form):
    handphone_hash = TextField('Enter verification code here', validators=[Required()])