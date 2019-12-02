import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

import random
import string


# Fetch the service account key JSON file contents
cred = credentials.Certificate('cred.json')
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://mcc-fall-2019-g5-258415.firebaseio.com/'
})


ref = db.reference('/')

# Checking if object already exist required for
# not rewriting object every time when we need to
# run this functions

def object_exists(object_type='default_object', object_value="dafault_name",  object_parameter='name'):

    object_ref = ref.child(object_type)
    all_objects = object_ref.get()

    for object_id in all_objects:
        if object_value == all_objects[object_id][object_parameter]: 
            # user exists
            return True

    # user not found
    return False


def randomString(stringLength=10):
    """Generate a random string of fixed length """
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(stringLength))


def add_users():

    users_ref = ref.child('users')

    for i in range(0, 3):

        name = 'name' + str(i + 1)

       # if not object_exists('users', name):
        email = name + '@mail.ru'
        password = '1234'
        photo = 'http://photo-link.ru/' + name

        users_ref.push().set({
                    'name': name,
                    'email': email,
                    'password': password,
                    'photo': photo
        })


def add_projects():

    project_ref = ref.child('projects')

    for i in range(0, 3):

        name = randomString()
       # if not object_exists('projects', name):
        is_shared = True
        key_word_1 = True
        key_word_2 = True
        key_word_3 = True
        author_id = 'u001'
        deadline = '01/01/2020'
        description = 'some default description'
        

        project_ref.push().set({
                    'name': name,
                    'is_shared': is_shared,
                    'key_word_1': key_word_1,
                    'key_word_2': key_word_2,
                    'key_word_3': key_word_3,
                    'author_id': author_id,
                    'deadline': deadline,
                    'description': description
        })


def add_members():

    members_ref = ref.child('members')

    for i in range(0, 3):
        user_id = 'user_id' + str(i)
        project_id = 'prjct_id'
        role_id = 'role_id'

        members_ref.push().set({
                    'user_id': user_id,
                    'project_id': project_id,
                    'role_id': role_id
        })


def add_roles():
    
    roles_ref = ref.child('roles')

    for i in range(0, 3):
        name = 'role_name' + str(i)
        level = 'role_level'

        roles_ref.push().set({
                    'name': name,
                    'level': level
        })


def add_tasks():

    tasks_ref = ref.child('tasks')

    for i in range(0, 3):
        
        taskname = 'taskname' + str(i)
        project_id = 'prjct_id'
        creater_id = 'creater_id'
        description = 'description' + str(i)
        status = 'status' + str(i)

        tasks_ref.push().set({
                    'taskname': taskname,
                    'project_id': project_id,
                    'creater_id': creater_id,
                    'description': description,
                    'status': status
        })


def add_task_to_user_link():

    tasks_to_user_ref = ref.child('task_to_user')

    for i in range(0, 3):
        
        task_id = 'taskid' + str(i)
        user_id = 'user_id'

        tasks_to_user_ref.push().set({
                    'task_id': task_id,
                    'user_id': user_id
        })


def add_attachments():

    attachments_ref = ref.child('attachments')

    for i in range(0, 3):
        
        name = 'attachment_name' + str(i)
        project_id = 'prjct_id'
        attachment_type = 'attachment_type'
        attachment_url = 'attachment_url'
        creation_time = 'creation_time */*/* **:**:**'

        attachments_ref.push().set({
                    'name': name,
                    'project_id': project_id,
                    'attachment_type': attachment_type,
                    'attachment_url': attachment_url,
                    'creation_time': creation_time
        })


def table_fill():
    add_users()
    add_projects()
    add_members()
    add_roles()
    add_tasks()
    add_task_to_user_link()
    add_attachments()
