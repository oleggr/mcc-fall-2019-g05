from main import ref
from dev_functions import randomString

from datetime import datetime


def create_project(
            title='default_project_title',
            is_shared=True,
            key_words = [
                {'key_word_1': True},
                {'key_word_2': True},
                {'key_word_3': True}],
            creator_id='default_author_id',
            deadline='01/01/1970',
            description='default_description',
            image_url = 'link_to_icon.com/project_id/icon',
            last_modified = 'date',
            is_media_available = True
    ):

    project_ref = ref.child('projects')

    project_key = project_ref.push({
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

    return project_key.key


def create_user(uid, name, email, image_url=""):

    users_ref = ref.child('users')
    user_ref = users_ref.child(uid).set({
        "email" : email,
        "image_url" : image_url,
        "name" : name
    })

    return True


def delete_project(project_id):

    #delete project
    project_ref = ref.child('projects')
    project_ref.child(project_id).delete()

    #delete members
    members_ref = ref.child('members')
    members_dict = ref.child('members').get()

    for member_id in members_dict:
        if(members_dict[member_id]["project_id"] == project_id):
            members_ref.child(member_id).delete()

    #delete tasks
    tasks_ref = ref.child('tasks')
    tasks_dict = ref.child('tasks').get()

    list_of_tasks = []

    for tasks_id in tasks_dict:
        if(tasks_dict[tasks_id]["project_id"] == project_id):
            tasks_ref.child(tasks_id).delete()
            list_of_tasks.append(tasks_id)

    #delete task_to_user
    task_to_user_ref = ref.child('task_to_user')
    task_to_user_dict = ref.child('task_to_user').get()

    for task_to_user_id in task_to_user_dict:
        if(task_to_user_dict[task_to_user_id]["task_id"] in list_of_tasks):
            task_to_user_ref.child(task_to_user_id).delete()

    return 'INFO: project {} deleted'.format(project_id)


def delete_members(project_id):

    #delete tasks
    tasks_ref = ref.child('tasks')
    tasks_dict = ref.child('tasks').get()

    list_of_tasks = []

    for tasks_id in tasks_dict:
        if(tasks_dict[tasks_id]["project_id"] == project_id):
            #tasks_ref.child(tasks_id).delete()
            list_of_tasks.append(tasks_id)
    # print(list_of_tasks)

    #delete task_to_user
    task_to_user_ref = ref.child('task_to_user')
    task_to_user_dict = ref.child('task_to_user').get()

    for task_to_user_id in task_to_user_dict:
        if(task_to_user_dict[task_to_user_id]["task_id"] in list_of_tasks):
            #task_to_user_ref.child(task_to_user_id).delete()
            print(task_to_user_id)

    return True


def add_members_to_project(users_id, project_id):

    member_ref = ref.child('members')
    users_ref = ref.child('users')

    try:

        for user_id in users_id:
            user = users_ref.child(user_id)

            name = user['name']
            image_url = user['image_url']

            member_ref.push({
                    'user_id': user_id,
                    'name': name,
                    'project_id': project_id,
                    'role_id': '',
                    'image_url': image_url
            })

        return 'INFO: Members added.'

    except:
        return 'ERROR: Members were not added.'


def update_project(project_id, param_name, param_value):

    try:
        project_ref = ref.child('projects')
        project = project_ref.child(project_id)

        fields = project.get()

        project.update({param_name: param_value})

        return True

    except:
        return False


def add_attachment(
            project_id,
            name='default_attachment_name',
            attachment_url = '/attachments',
            attachment_type='image'):

    attachments_ref = ref.child('attachments')

    try:

        now = datetime.now()
        dt_string = now.strftime("%d/%m/%Y %H:%M:%S")

        attachments_ref.push({
                    'name': name,
                    'project_id': project_id,
                    'attachment_type': attachment_type,
                    'attachment_url': attachment_url,
                    'creation_time': dt_string
        })

        update_project(project_id, 'is_media_available', True)

        return 'INFO: Attachment added.'

    except:
        return 'ERROR: Attachment was not added.'


def add_task(project_id, creator_id, assignee_id, description, deadline, createdAt, status):
    '''
    Create task the task attributes and assign it to project by project ID.
    Returns the task ID after creation.
    '''

    tasks_to_project_ref = ref.child('tasks')

    id_ref  = tasks_to_project_ref.push({
            'project_id': project_id,
            'creator_id': creator_id,
            'assignee_id': assignee_id,
            'description': description,
            'deadline': deadline,
            'createdAt': createdAt,
            'status': status
    })

    task_id = id_ref.key
    return  task_id


def update_task(task_id, new_task_status):
    '''
    Updates the task status by given task ID.
    '''

    tasks_ref = ref.child('tasks')

    path = task_id + '/status'

    tasks_ref.update({
        path : new_task_status
    })


def assign_task_to_users(task_id, *user_ids):
    '''
    Assign a task to a user(s) by given project ID and task ID.
    Create a row in table task_to_user with task_id and user_id.
    '''

    task_to_user_ref = ref.child('task_to_user')

    for user_id in user_ids:
        task_to_user_ref.push({
            'task_id' : task_id,
            'user_id' : user_id
        })


def get_list_of_projects_implementation(user_id):

    projects_ref = ref.child('projects/')
    projects_list = []
    projects = projects_ref.get()
    for project_id in projects:
        if(projects[project_id]["creator_id"] == user_id):
            projects_list.append(project_id)

    members_ref = ref.child('members/')
    members = members_ref.get()
    for member in members:
        if(members[member]["user_id"] == user_id and not(members[member]["project_id"] in projects_list)):
            projects_list.append(members[member]["project_id"])

    response_list = []
    for project in projects_list:
        certain_project_item = {}
        certain_project_item.update({ "id" : project})
        certain_project = projects_ref.child(project).get()
        certain_project_item.update({ "title" : certain_project["title"]})
        certain_project_item.update({ "deadline" : certain_project["deadline"]})
        certain_project_item.update({ "imageUrl" : certain_project["image_url"]})
        certain_project_item.update({ "lastModified" : certain_project["last_modified"]})
        certain_project_item.update({ "isFavorite" : favorite_project_exists(user_id, project)})
        certain_project_item.update({ "isMediaAvailable" : certain_project["is_media_available"]})
        certain_project_item.update({ "isShared" : certain_project["is_media_available"]})
        certain_project_item.update({ "keywords" : certain_project["key_words"]})
        certain_project_item.update({ "isOwner" : True if certain_project["creator_id"] == user_id else False })
        members_list = []
        for member in members:
            if(members[member]["user_id"] != user_id):
                members_list.append(members[member]["user_id"])
        members_data_list = []
        users_ref = ref.child('users/').get()
        for m in members_list:
            user = users_ref[m]
            members_data_list.append({"id" : m, "imageUrl" : user["image_url"]})
        certain_project_item.update({ "members" : members_data_list})

        response_list.append(certain_project_item)
    return response_list


def search_for_project_implementation(project_id):

    project = ref.child('projects/' + project_id).get()
    response_list = []
    response_list.append({project_id : project})

    return response_list


def get_members_of_project(project_id):

    members = []
    members_ref = ref.child('members')
    members_dict = members_ref.get()

    for member_record_id in members_dict:

        member_record = members_dict[member_record_id]

        if member_record['project_id'] == project_id:
            member_record['id'] = member_record_id
            members.append(member_record)

    return members


def get_tasks_of_project(project_id):

    tasks = []
    tasks_ref = ref.child('tasks')
    tasks_dict = tasks_ref.get()

    for task_record_id in tasks_dict:

        task_record = tasks_dict[task_record_id]

        if task_record['project_id'] == project_id:
            task_record['id'] = task_record_id
            tasks.append(task_record)

    return tasks


def get_users_by_id(users_id):

    res = []
    users_ref = ref.child('users')
    users = users_ref.get()

    for user_id in users:
        user = users[user_id]
        if user_id in users_id:
            res.append(user)

    return res


def get_users_on_task(task_id):

    users_on_task = []
    tasks_to_user_ref = ref.child('task_to_user')
    records_dict = tasks_to_user_ref.get()

    for record_id in records_dict:
        record = records_dict[record_id]
        if record['task_id'] == task_id:
            users_on_task.append(record['user_id'])

    return users_on_task


def get_attachments_of_project(project_id):

    attachments = []
    attachments_ref = ref.child('attachments')
    attachments_dict = attachments_ref.get()

    for attachments_record_id in attachments_dict:
        attachments_record = attachments_dict[attachments_record_id]
        if attachments_record['project_id'] == project_id:
            attachments_record['id'] = attachments_record_id
            attachments.append(attachments_record)

    return attachments


def verify_user(uid):

    users_ref = ref.child('users').get()

    for user in users_ref:
        if (user==uid):
            return True

    return False


def does_user_in_project(user_id, project_id):

    members_ref = ref.child("members").get()

    for member_id in members_ref:

        member = members_ref[member_id]

        if(member["project_id"] == project_id and member["user_id"] == user_id):
            return True

    return False


def does_user_admin_of_project(user_id, project_id):

    members_ref = ref.child("members").get()

    for member_id in members_ref:

        member = members_ref[member_id]

        if(member["project_id"] == project_id and member["user_id"] == user_id):

            member_role = member["role_id"]
            roles_ref = ref.child("roles").get()

            for role in roles_ref:
                if (role == member_role) and (roles_ref[role]["level"] == "admin"):
                    return True

    projects_ref = ref.child("projects")
    project = projects_ref.child(project_id).get()
    if(project["creator_id"] == user_id):
        return True

    return False


def user_is_unique(username):

    users = ref.child('users').get()

    for user_id in users:
        user = users[user_id]
        if user['name'] == username:
            return False

    return True


def registration_token_is_unique(registration_token):

    users = ref.child('users').get()

    for user_id in users:
        user = users[user_id]
        if user['registration_token'] == registration_token:
            return False

    return True


def email_is_unique(email):

    users = ref.child('users').get()

    for user_id in users:
        user = users[user_id]
        if user['email'] == email:
            return False

    return True


def unique_names(username):

    username_options = []

    while len(username_options) != 3:

        tmp = username
        tmp += randomString(3)

        if user_is_unique(tmp):
            username_options.append(tmp)

    return username_options


def return_all_users():
    users = ref.child('users').get()
    response_list = []
    for user_id in users:
        temp_dict = users[user_id]
        temp_dict.update({"id" : user_id})
        temp_dict.update({"imageUrl" : temp_dict["image_url"]})
        response_list.append(temp_dict)
    return response_list


def return_certain_user(user_id):
    user = ref.child('users/' + user_id).get()
    return {user_id : user}


def update_user(user_id, data):

    users = ref.child('users')
    user = users.child(user_id).get()
    old_data = user

    if (not(data)):
        return "ERROR: Empty dictionary"

    if ("name" in data):

        if(not(user_is_unique(data["name"]))):
            return "ERROR: Name is not unique"

        if(data["name"] == ""):
            return "ERROR: Name is empty"

        path_user_name = user_id + "/name"
        users.update({
            path_user_name : data["name"]
        })

    if ("email" in data):

        if(not(email_is_unique(data["email"]))):
            return "ERROR: Email is not unique"

        if(data["email"] == ""):
            return "ERROR: Email is empty"

        path_user_email = user_id + "/email"
        users.update({
            path_user_email : data["email"]
        })

    if ("image_url" in data):
        path_user_image_url = user_id + "/image_url"
        users.update({
            path_user_image_url : data["image_url"]
        })

    if ("registration_token" in data):
        if(not(registration_token_is_unique(data["registration_token"]))):
            return "ERROR: Registration token is not unique"

        path_user_registration_token = user_id + "/registration_token"
        users.update({
            path_user_registration_token : data["registration_token"]
        })
    return "ok"


def favorite_project_exists(user_id, project_id):

    favorites_ref = ref.child('favorite_project').get()

    for favorite_id in favorites_ref:
        if (favorites_ref[favorite_id]["user_id"] == user_id and favorites_ref[favorite_id]["project_id"] == project_id):
            return True

    return False


def make_favorite(user_id, project_id):
    if(favorite_project_exists(user_id, project_id)):
        return "ERROR: The project is already favorite"

    favorites_ref = ref.child('favorite_project')
    favorites_ref.push().set({
                'user_id': user_id,
                'project_id': project_id
                })
    return "OK"


def make_unfavorite(user_id, project_id):
    if(not(favorite_project_exists(user_id, project_id))):
        return "ERROR: The project is not favorite"

    favorites_ref = ref.child('favorite_project').get()
    for favorite_id in favorites_ref:
        if (favorites_ref[favorite_id]["user_id"] == user_id and favorites_ref[favorite_id]["project_id"] == project_id):
            ref.child('favorite_project').child(favorite_id).delete()

    return "OK"
