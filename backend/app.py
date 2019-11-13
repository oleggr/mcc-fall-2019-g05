import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

import flask


app = flask.Flask(__name__)


# Fetch the service account key JSON file contents
cred = credentials.Certificate('cred.json')
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://mcc-fall-2019-g5-258415.firebaseio.com/'
})


ref = db.reference('/')


@app.route('/set_data', methods=['GET'])
def set_data():
    ref.set({
        'boxes': 
            {
                'box001': {
                    'color': 'red',
                    'width': 1,
                    'height': 3,
                    'length': 2
                },
                'box002': {
                    'color': 'green',
                    'width': 1,
                    'height': 2,
                    'length': 3
                },
                'box003': {
                    'color': 'yellow',
                    'width': 3,
                    'height': 2,
                    'length': 1
                }
            }
        })

@app.route('/update_data', methods=['GET'])
def update_data():
    ref = db.reference('boxes')
    box_ref = ref.child('box001')
    box_ref.update({
        'color': 'test aaAA$$$$'
    })
    # pizda


if __name__ == "__main__":
    app.run(debug = True, port = 8080)