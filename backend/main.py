import firebase_interaction as fi
import flask


app = flask.Flask(__name__)


@app.route('/')
def default_route():
    return 'Hello world!'


@app.route('/first_set_data')
def first_set_data():
    ftf.table_fill()
    return 'INFO::Table filling is done'


@app.route('/update_data')
def update_data():
    pass


@app.route('/upload_image/<filename>')
def upload_file(filename):
    fi.file_upload('attachments/', filename)
    return 'INFO::Image uploaded'


@app.route('/download_image/<path>/<filename>')
def download_image(path, filename):
    fi.file_download(path + '/', filename)
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


@app.route('/create_project')
def create_project():
    return "This is create_project method. returns fails or not"


@app.route('/assign_member_to_project')
def set_member_to_project():
    return "This is set_member_to_project method. returns fails or not"


@app.route('/get_member_to_project')
def get_member_to_project():
    return "This is get_member_to_project method. returns a list of members"


@app.route('/add_task_to_project')
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
