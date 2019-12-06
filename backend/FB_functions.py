from main import ref


def create_project(
            name='default_project_name',
            is_shared=True,
            key_word_1=True,
            key_word_2=True,
            key_word_3=True,
            author_id='default_author_id',
            deadline='01/01/1970',
            description='default_description'
    ):

    project_ref = ref.child('projects')

    project_key = project_ref.push({
                'name': name,
                'is_shared': is_shared,
                'key_word_1': key_word_1,
                'key_word_2': key_word_2,
                'key_word_3': key_word_3,
                'author_id': author_id,
                'deadline': deadline,
                'description': description
    })

    return project_key.key


def delete_project(project_id):

    project_ref = ref.child('projects')
    project_ref.child(project_id).delete()

    return 'INFO: project {} deleted'.format(project_id)


def add_members_to_project(users_id, project_id):

    member_ref = ref.child('members')

    try:
        for user_id in users_id:
            member_ref.push({
                    'user_id': user_id,
                    'project_id': project_id,
                    'role_id': 'standart user'
            })
        return 'INFO: Members added.'

    except:
        return 'ERROR: Members were not added.'


def add_task_to_project(project_id, creater_id, description, status, taskname):
    '''
    Create task the task attributes and assign it to project by project ID.
    Returns the task ID after creation.
    '''

    tasks_to_project_ref = ref.child('tasks')

    id_ref  = tasks_to_project_ref.push({
            'taskname': taskname,
            'project_id': project_id,
            'creater_id': creater_id,
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

def get_members_of_project(project_id):

    members = []
    members_ref = ref.child('members')
    members_dict = members_ref.get()

    for member_record_id in members_dict:
        member_record = members_dict[member_record_id]
        if member_record['project_id'] == project_id:
            members.append(member_record['user_id'])

    return members


def get_tasks_of_project(project_id):

    tasks = []
    tasks_ref = ref.child('tasks')
    tasks_dict = tasks_ref.get()

    for tasks_record_id in tasks_dict:
        task_record = tasks_dict[tasks_record_id]
        if task_record['project_id'] == project_id:
            tasks.append(tasks_record_id)

    return tasks
