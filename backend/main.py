import firebase_admin
from firebase_admin import credentials
from firebase_admin import storage
from firebase_admin import db
from firebase_admin import auth
from google.cloud import storage

import flask
from flask import request
from flask import send_file

import json
import os
import json


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
import report_generate


app = flask.Flask(__name__)


@app.route('/')
def default_route():
    return 'Hello world!'


@app.route('/first_set_data', methods=['GET'])
def first_set_data():
    bfi.table_fill()
    return 'INFO::Table filling is done.'


@app.route('/update_data')
def update_data():
    pass


# TODO: Fix this function to post according to new requirements
@app.route('/upload_image/<filename>', methods=['GET', 'POST'])
def upload_file(filename):
    bfi.file_upload('attachments/', filename)
    return 'INFO::Image uploaded'


# TODO: Fix this function to post according to new requirements
@app.route('/download_image/<path>/<filename>', methods=['GET', 'POST'])
def download_image(path, filename):
    bfi.file_download(path + '/', filename)
    return 'INFO::Image downloaded'


@app.route('/user_authentication', methods=['POST'])
def user_authentication():
    '''
    This is user_authentication method. 
    Returns fails or not
    '''

    try:

        data = request.args
        id_token = data['id_token']

        decoded_token = auth.verify_id_token(id_token)
        uid = decoded_token['uid']

        return decoded_token#'INFO: Authenfication successful.'
    
    except:
        return 'ERROR: Authenfication failed.'


#?? what about settings of profile and what profile is
@app.route('/users/<user_id>/get_profile_settings')
def get_profile_settings():
    return "This is get_profile_settings method. returns profile info"


@app.route('/set_profile_settings')
def set_profile_settings():
    return "This is set_profile_settings method. returns fails or not"


@app.route('/create_project', methods=['POST'])
def create_project():

    data = request.args
    project_id = FB_functions.create_project(
            data['title'],
            data['is_shared'],
            data['key_words'],
            data['creator_id'],
            data['deadline'],
            data['description'],
            data['image_url'],
            data['last_modified'],
            data['is_media_available']
    )

    return project_id


@app.route('/project/<project_id>/delete', methods=['DELETE'])
def delete_project(project_id):

    # TODO: Add user check (if user admin or not)
    # if data.TOKEN is valid (check with firebase)
    if True:
        return FB_functions.delete_project(project_id)

    return "ERROR: Wrong project id."


@app.route('/project/<project_id>/add_members', methods=['POST'])
def add_members_to_project(project_id):

    data = request.args
    users_id = data.getlist('user_id')

    return FB_functions.add_members_to_project(users_id, project_id)


@app.route('/project/<project_id>/members')
def get_members_of_project(project_id):
    '''
    Returns dict of members which consist of user_id, project_id and role_id
    '''

    members = FB_functions.get_members_of_project(project_id)
    # String is not correct way. TODO: Fix it
    return json.dumps(members)


@app.route('/project/<project_id>/add_task', methods=['POST'])
def set_task_to_project(project_id):

    data=request.args
    task_id = FB_functions.add_task_to_project(
            project_id, 
            data["creater_id"], 
            data["description"], 
            data["status"], 
            data["taskname"]
    )

    return json.dumps(task_id)


@app.route('/task/<task_id>/status_update', methods=['PUT'])
def update_task_status(task_id):
    data=request.args
    FB_functions.update_task(task_id, data["task_status"])
    return "OK"


@app.route('/task/<task_id>/assign_to_user', methods=['POST'])
def assign_task_to_users(task_id):
    data=request.args
    FB_functions.assign_task_to_users(task_id, data["user_ids"])
    return "OK"


@app.route('/project/<project_id>/tasks')
def get_tasks_of_project(project_id):
    '''
    Get_tasks_of_project method.
    Returns list of tasks.
    '''

    tasks = FB_functions.get_tasks_of_project(project_id)
    # String is not correct way. TODO: Fix it

    return json.dumps(tasks)


@app.route('/convert_image_to_task')
def convert_image_to_task():
    return "This is convert_image_to_task method. returns fails or not"


@app.route('/project/<project_id>/add_attachments', methods=['POST'])
def add_attachments_to_project(project_id):
    return "This is add_attachments_to_project method. returns fails or not"


@app.route('/project/<project_id>/generate_report') 
def generate_project_report(project_id):
    '''
    This is generate_project_report method. 
    Returns fails or not or may be returns a report
    '''

    report_name = report_generate.generate_project_report(project_id)

    # print(report_name)
    # if not report_name:
    #     return 'ERROR: Project not exist'

    return report_name
    # return send_file('/pdf/{}'.format(report_name))


# Get all projects as json
@app.route('/get_projects', methods=['GET'])
def get_list_of_projects():
    return json.dumps(FB_functions.get_list_of_projects_implementation(request.args["user_id"]))


# Get single project as json
@app.route('/project/<project_id>/search', methods=['GET'])
def search_for_project(project_id):
    return json.dumps(FB_functions.search_for_project_implementation(project_id))


@app.route('/get_image_resolution')
def get_image_resolution():
    return "This is get_image_resolution method. returns a new image"


@app.route('/send_notification')
def send_notification():
    return "This is send_notification method. sends a notification"


if __name__ == "__main__":

    app.run(debug = True, port = 8080)
