import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

import flask


# Fetch the service account key JSON file contents
cred = credentials.Certificate('cred.json')
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://mcc-fall-2019-g5-258415.firebaseio.com/'
})


ref = db.reference('/')

if __name__=="__main__":
    table_fill()