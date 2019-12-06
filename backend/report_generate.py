from main import ref
from FB_functions import get_members_of_project, get_tasks_of_project, get_users_on_task, get_attachments_of_project

from fpdf import FPDF


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

        user_role = roles_ref.get(member['role_id'])
        user['role_name'] = user_role['name']
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
    data['tasks_info'] = tasks  
    data['attachments_info'] = attachments  

    return data


def generate_pdf(data):
    # pdf = FPDF()
    # pdf.add_page()
    # pdf.set_font("Arial", size=12)
    # pdf.cell(200, 10, txt="Welcome to Python!", ln=1, align="C")
    # pdf.output("simple_demo.pdf")

    print(data)


def generate_project_report(project_id):

    data = get_data(project_id)
    report_name = generate_pdf(data)

    return report_name
