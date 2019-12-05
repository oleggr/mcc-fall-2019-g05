import firebase_admin
from firebase_admin import credentials
from firebase_admin import storage
from firebase_admin import db
from google.cloud import storage

import flask
from flask import request

import os


# Check if db activating first time

if not len(firebase_admin._apps):
    # Fetch the service account key JSON file contents
    cred = credentials.Certificate('cred.json')
    # Initialize the app with a service account, granting admin privileges
    firebase_admin.initialize_app(cred, {
        'databaseURL': 'https://mcc-fall-2019-g5-258415.firebaseio.com/'
    })

    # Credentials for google cloud storage
    os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "cred.json"

ref = db.reference('/')

import firebase_interaction as bfi
import FB_functions


app = flask.Flask(__name__)


@app.route('/')
def default_route():
    return 'Hello world!'


@app.route('/first_set_data', methods=['GET'])
def first_set_data():
    bfi.table_fill()
    return 'INFO::Table filling is done'


@app.route('/update_data')
def update_data():
    pass


@app.route('/upload_image/<filename>', methods=['GET', 'POST'])
def upload_file(filename):
    bfi.file_upload('attachments/', filename)
    return 'INFO::Image uploaded'


@app.route('/download_image/<path>/<filename>', methods=['GET', 'POST'])
def download_image(path, filename):
    bfi.file_download(path + '/', filename)
    return 'INFO::Image downloaded'


@app.route('/user_authentication')
def user_authentication():
    return "This is user_authentication method. returns fails or not"


@app.route('/get_profile_settings')
def get_profile_settings():
    return "This is get_profile_settings method. returns profile info"


@app.route('/set_profile_settings')
def set_profile_settings():
    return "This is set_profile_settings method. returns fails or not"


@app.route('/post_test', methods=['POST'])
def post_test():
    data = request.args
    return data['pizda']


@app.route('/create_project', methods=['POST'])
def create_project():

    data = request.args

    return FB_functions.create_project(
            data['name'],
            data['is_shared'],
            data['key_word_1'],
            data['key_word_2'],
            data['key_word_3'],
            data['author_id'],
            data['deadline'],
            data['description']
    )


@app.route('/delete_project', methods=['POST'])
def delete_project():

    data = request.args

    # TODO: Add user check (if user admin or not)
    # if data.TOKEN is valid (check with firebase)
    if True:
        return FB_functions.delete_project(data['project_id'])

    return "ERROR: Wrong project id."


@app.route('/add_members_to_project', methods=['POST'])
def add_members_to_project():

    data = request.args
    users_id = data.getlist('user_id')
    project_id = data['project_id']

    return FB_functions.add_members_to_project(users_id, project_id)


@app.route('/get_member_to_project')
def get_member_to_project():
    return "This is get_member_to_project method. returns a list of members"


@app.route('/add_task_to_project', methods=['POST'])
def set_task_to_project():
    data=request.args
    task_id = FB_functions.add_task_to_project(data["project_id"], data["creater_id"], data["description"], data["status"], data["taskname"])
    return str(task_id)

@app.route('/update_task_status', methods=['POST'])
def update_task_status():
    data=request.args
    FB_functions.update_task(data["task_id"], data["new_task_status"])
    return "OK"

@app.route('/assign_task_to_users', methods=['POST'])
def assign_task_to_users():
    data=request.args
    FB_functions.assign_task_to_users(data["task_id"], data["user_ids"])
    return "OK"

@app.route('/get_task_to_project')
def get_task_to_project():
    return "This is get_task_to_project method. returns list of projects"


@app.route('/convert_image_to_task')
def convert_image_to_task():
    return "This is convert_image_to_task method. returns fails or not"


@app.route('/add_attachments_to_project')
def add_attachments_to_project():
    return "This is add_attachments_to_project method. returns fails or not"


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
