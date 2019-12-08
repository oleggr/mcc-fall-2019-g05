import random
import string


def randomString(stringLength=10):
    '''
    Generate a random string of fixed length.
    '''
    letters = string.ascii_letters + string.digits
    return ''.join(random.choice(letters) for i in range(stringLength))


# email server
MAIL_SERVER = 'smtp.googlemail.com'
MAIL_PORT = 465
MAIL_USE_TLS = False
MAIL_USE_SSL = True
MAIL_USERNAME ='mcc-fall-2019-g05'#os.environ.get('MAIL_USERNAME')
MAIL_PASSWORD = 'Q!W@E#r4t5y6'#os.environ.get('MAIL_PASSWORD')

# administrator list
ADMINS = ['mcc.fall.2019.g05@gmail.com']
