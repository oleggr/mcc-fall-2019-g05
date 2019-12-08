from main import ref
from FB_functions import get_members_of_project, get_tasks_of_project, get_users_on_task, get_attachments_of_project

from datetime import datetime
from jinja2 import Environment, FileSystemLoader
from weasyprint import HTML


def get_data(project_id):

    data = dict()

    project_ref = ref.child('projects')
    users_ref = ref.child('users')
    roles_ref = ref.child('roles')


    # Get info about project itself
    project = project_ref.get()
    project = project[project_id]
    project['project_id'] = project_id
    

    # Get base info about members of project
    # (User's names, roles in project)
    users = []
    members_list = get_members_of_project(project_id)

    for member in members_list:
        user = users_ref.get()
        user = user[member['user_id']]

        user['user_id'] = member['user_id']

        user_role = roles_ref.get()
        user_role = user_role[member['role_id']]

        user['role_name'] = user_role['rolename']
        user['role_level'] = user_role['level']

        users.append(user)


    # Get tasks of this project with assigned users
    tasks = get_tasks_of_project(project_id)
    result_tasks = []

    for task in tasks:

        users_on_task = get_users_on_task(task['task_id'])
        task['members'] = []

        for user in users:
            if user['user_id'] in users_on_task:
                task['members'].append({
                        'name': user['name'], 
                        'user_id': user['user_id']})
                result_tasks.append(task)

    tasks = result_tasks


    # Get list of attachments
    attachments = get_attachments_of_project(project_id)

    data['project_info'] = project  
    data['users_info'] = users  
    data['tasks_info'] = result_tasks  
    data['attachments_info'] = attachments  

    return data


def generate_pdf(data):

    project = data['project_info'] 
    users = data['users_info']  
    tasks = data['tasks_info']  
    attachments = data['attachments_info']

    # Filename consist of date to make it unique
    now = datetime.now()
    dt_string = now.strftime("%d-%m-%Y_%H-%M-%S")

    env = Environment(loader=FileSystemLoader('.'))
    template = env.get_template("report_template.html")
    template_vars = {
                    "title" : 'Report of project {}'.format(project['project_id']),
                    "project_name" : project['title'],
                    "datetime" : dt_string,
                    "project_info" : project,
                    "key_word_1": key_word_1,
                    "key_word_2": key_word_2,
                    "key_word_3": key_word_3,
                    "users" : users,
                    "tasks" : tasks,
                    "attachments" : attachments
                    }

    html_out = template.render(template_vars)

    html_file = open(dt_string + '.html', 'w')
    html_file.write(html_out)
    html_file.close()

    HTML(string=html_out).write_pdf(dt_string + '.pdf')

    return dt_string


def generate_project_report(project_id):

    data = get_data(project_id)
    report_name = generate_pdf(data)

    return report_name
