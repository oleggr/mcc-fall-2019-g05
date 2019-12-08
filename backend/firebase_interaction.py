from main import ref, storage
from dev_functions import randomString

from datetime import datetime
from PIL import Image


def image_upload(path_to_file='attachments/', filename='default_name'):
    # Enable Storage
    client = storage.Client()

    # Reference an existing bucket.
    bucket = client.get_bucket('mcc-fall-2019-g5-258415.appspot.com')

    im = Image.open('img/{}'.format(filename))
    low_q = Image.new('RGB', im.size, (255,255,255))
    low_q.paste(im, (0,0), im)
    low_q.save('img/low_quality.png', quality=40)

    mid_q = Image.new('RGB', im.size, (255,255,255))
    mid_q.paste(im, (0,0), im)
    mid_q.save('img/middle_quality.png', quality=70)

    tmpBlob = bucket.blob(path_to_file + filename + '/best_quality')
    tmpBlob.upload_from_filename(filename='img/{}'.format(filename))

    tmpBlob = bucket.blob(path_to_file + filename + '/middle_quality')
    tmpBlob.upload_from_filename(filename='img/middle_quality.png')

    tmpBlob = bucket.blob(path_to_file + filename + '/low_quality')
    tmpBlob.upload_from_filename(filename='img/low_quality.png')


def image_download(path_to_file='attachments/', filename='default_name', quality='best_quality'):
    # Enable Storage
    client = storage.Client()

    # Reference an existing bucket.
    bucket = client.get_bucket('mcc-fall-2019-g5-258415.appspot.com')

    if quality == 'middle_quality':
        tmpBlob = bucket.blob(path_to_file + filename + 'middle_quality')

    elif quality == 'low_quality':
        tmpBlob = bucket.blob(path_to_file + filename + 'low_quality')

    else:
        tmpBlob = bucket.blob(path_to_file + filename + 'best_quality')

    tmpBlob.download_to_filename(filename)


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


def add_users():

    users_ref = ref.child('users')

    for i in range(0, 3):

        name = 'name' + str(i + 1)

       # if not object_exists('users', name):
        email = name + '@mail.ru'
        image_url = 'http://photo-link.ru/' + name
        registration_token = ''

        users_ref.push().set({
                    'name': name,
                    'email': email,
                    'image_url': image_url,
                    'registration_token': registration_token
        })


def add_projects():

    project_ref = ref.child('projects')

    for i in range(0, 3):

        title = randomString()
       # if not object_exists('projects', title):
        is_shared = True
        key_words = [
                {'key_word_1': True},
                {'key_word_2': True},
                {'key_word_3': True}
                ]
        creator_id = 'u001'
        deadline = '01/01/2020'
        description = 'some default description'
        image_url = 'link_to_icon.com/project_id/icon'
        last_modified = 'date'
        is_media_available = True


        project_ref.push().set({
                'title': title,
                'is_shared': is_shared,
                'key_words': key_words,
                'creator_id': creator_id,
                'deadline': deadline,
                'description': description,
                'image_url': image_url,
                'last_modified': last_modified,
                'is_media_available': is_media_available
        })


def add_members():

    members_ref = ref.child('members')

    for i in range(0, 3):
        user_id = 'user_id' + str(i)
        name = 'default_name'
        project_id = 'prjct_id'
        role_id = 'role_id'
        image_url = 'member_image_url.com'

        members_ref.push().set({
                    'user_id': user_id,
                    'name': name,
                    'project_id': project_id,
                    'role_id': role_id,
                    'image_url': image_url
        })


def add_roles():

    roles_ref = ref.child('roles')

    for i in range(0, 3):
        rolename = 'role_name' + str(i)
        level = 'role_level'

        roles_ref.push().set({
                    'rolename': rolename,
                    'level': level
        })


def add_favorite_project():

    favorites_ref = ref.child('favorite_project')

    for i in range(0, 3):
        user_id = 'user' + str(i)
        project_id = 'lol'

        favorites_ref.push().set({
                    'user_id': user_id,
                    'project_id': project_id
        })


def add_tasks():

    tasks_ref = ref.child('tasks')

    for i in range(0, 3):

        now = datetime.now()
        dt_string = now.strftime("%d/%m/%Y %H:%M:%S")

        taskname = 'taskname' + str(i)
        project_id = 'prjct_id'
        creator_id = 'creator_id'
        description = 'description' + str(i)
        status = 'status' + str(i)
        createdAt = dt_string
        deadline = '01/01/2020'

        tasks_ref.push().set({
                    'project_id': project_id,
                    'creator_id': creator_id,
                    'assignee_id': creator_id,
                    'description': description,
                    'status': status,
                    'createdAt': createdAt,
                    'deadline': deadline
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
