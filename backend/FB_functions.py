from main import ref


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

def create_user(uid, name, email, image_url):
    users_ref = ref.child('users')
    user_ref = users_ref.child(uid).set({
        "email" : email,
        "image_url" : image_url,
        "name" : name
    })

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
    print(list_of_tasks)

    #delete task_to_user
    task_to_user_ref = ref.child('task_to_user')
    task_to_user_dict = ref.child('task_to_user').get()
    for task_to_user_id in task_to_user_dict:
        if(task_to_user_dict[task_to_user_id]["task_id"] in list_of_tasks):
            #task_to_user_ref.child(task_to_user_id).delete()
            print(task_to_user_id)
    return "ok"

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
                    'role_id': 'standart user',
                    'image_url': image_url
            })

        return 'INFO: Members added.'

    except:
        return 'ERROR: Members were not added.'


def add_task_to_project(project_id, creator_id, description, status, taskname):
    '''
    Create task the task attributes and assign it to project by project ID.
    Returns the task ID after creation.
    '''

    tasks_to_project_ref = ref.child('tasks')

    id_ref  = tasks_to_project_ref.push({
            'taskname': taskname,
            'project_id': project_id,
            'creator_id': creator_id,
            'description': description,
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

    task_to_user_ref = ref.child('task_to_user').get()#get list of users and add_projects
    user_tasks = []
    response_list = []

    for task_to_user in task_to_user_ref:
        if(task_to_user_ref[task_to_user]["user_id"] == user_id):
            user_tasks.append(task_to_user_ref[task_to_user]["task_id"]) #get a list of users tasks

    if(len(user_tasks) > 0):
        tasks_ref = ref.child('tasks').get()

        for i in tasks_ref:
            if i in user_tasks:
                path_str = "tasks/" + i
                response_list.append({i : ref.child(path_str).get()})

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
            member_record['member_id'] = member_record_id
            members.append(member_record)

    return members


def get_tasks_of_project(project_id):

    tasks = []
    tasks_ref = ref.child('tasks')
    tasks_dict = tasks_ref.get()

    for task_record_id in tasks_dict:
        task_record = tasks_dict[task_record_id]
        if task_record['project_id'] == project_id:
            task_record['task_id'] = task_record_id
            tasks.append(task_record)

    return tasks


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
            attachments_record['attachments_id'] = attachments_record_id
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
                if (role == member_role):
                    if(roles_ref[role]["level"] == "admin"):
                        return True
    return False
