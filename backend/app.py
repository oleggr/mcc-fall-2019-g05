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
    return "cya bloat'"

@app.route('/update_data', methods=['GET'])
def update_data():
    ref = db.reference('boxes')
    box_ref = ref.child('box001')
    box_ref.update({
        'color': 'test aaAA$$$$'
    })
    # pizda

@app.route('/user_authentication')
def user_authentication():
    return "This is user_authentication method. returns fails or not"

@app.route('/get_profile_settings')
def get_profile_settings():
    return "This is get_profile_settings method. returns profile info"

@app.route('/set_profile_settings')
def set_profile_settings():
    return "This is set_profile_settings method. returns fails or not"

@app.route('/create_project')
def create_project():
    return "This is create_project method. returns fails or not"

@app.route('/set_member_to_project')
def set_member_to_project():
    return "This is set_member_to_project method. returns fails or not"

@app.route('/get_member_to_project')
def get_member_to_project():
    return "This is get_member_to_project method. returns a list of members"

@app.route('/set_task_to_project')
def set_task_to_project():
    return "This is set_task_to_project method. returns fails or not"

@app.route('/get_task_to_project')
def get_task_to_project():
    return "This is get_task_to_project method. returns list of projects"

@app.route('/convert_image_to_task')
def convert_image_to_task():
    return "This is convert_image_to_task method. returns fails or not"

@app.route('/add_attachments_to_project')
def add_attachments_to_project():
    return "This is add_attachments_to_project method. returns fails or not"

@app.route('/delete_project')
def delete_project():
    return "This is delete_project method. returns fails or not"

@app.route('/show_project_content')
def show_project_content():
    return "This is show_project_content method. returns project contennt"

@app.route('/generate_project_report')
def generate_project_report():
    return "This is generate_project_report method. returns fails or not or may be returns a report"

@app.route('/get_list_of_projects')
def get_list_of_projects():
    return "This is get_list_of_projects method. returns list of created projects"

@app.route('/search_for_project')
def search_for_project():
    return "This is search_for_project method. returns null or a project"

@app.route('/get_image_resolution')
def get_image_resolution():
    return "This is get_image_resolution method. returns a new image"

@app.route('/send_notification')
def send_notification():
    return "This is send_notification method. sends a notification"



if __name__ == "__main__":
    app.run(debug = True, port = 8080)
