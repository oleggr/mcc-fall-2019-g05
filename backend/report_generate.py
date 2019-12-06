from main import ref

from FPDF import FPDF


get_member_info()


def get_data(project_id):

    data = dict()

    project_ref = ref.child('projects')
    users_ref = ref.child('users')
    members_ref = ref.child('members')
    roles_ref = ref.child('roles')
    tasks_ref = ref.child('tasks')
    tasks_to_user_ref = ref.child('task_to_user')
    attachments_ref = ref.child('attachments')

    # Get info about project itself
    project = project_ref.get(project_id)
    project['project_id'] = project_id

    # Get base info about members of project
    # (User's names, roles in project)
    users = []
    members_list = get_members_of_project(project_id)

    for member in members_list:
        user = users_ref.get(member['user_id'])
        user['user_id'] = member['user_id']

        user_role = roles_ref.get(member['role_id'])
        user['role_name'] = user_role['name']
        user['role_level'] = user_role['level']

        users.append(user)


    # Get tasks of this project with assigned users
    pass

    # Get list of attachments
    pass

    data['project_info'] = project    

    return data


def generate_pdf(data):
    pdf = FPDF()
    pdf.add_page()
    pdf.set_font("Arial", size=12)
    pdf.cell(200, 10, txt="Welcome to Python!", ln=1, align="C")
    pdf.output("simple_demo.pdf")


def generate_project_report(project_id):

    data = get_data(project_id)
    report_name = generate_pdf(data)

    return report_name
