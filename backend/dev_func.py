import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import flask
import random
import string

# Fetch the service account key JSON file contents
cred = credentials.Certificate('cred.json')
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://mcc-fall-2019-g5-258415.firebaseio.com/'
})


ref = db.reference('/')

def add_users():
	
	users_ref = ref.child('users')

    for i in range(0, 3):
        name = 'name' + str(i + 1)
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
        is_shared = True
        key_word_1 = True
        key_word_2 = True
        key_word_3 = True
        author_id = 'u001'
        deadline = '01/01/2020'
        description = 'some default description'
        name = randomString()

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

    project_ref = ref.child('members')

    for i in range(0, 3):
        member_id = 'name' + str(i + 1)
        project_id = name + '@mail.ru'
        password = '1234'
        photo = 'http://photo-link.ru/' + name

        users_ref.push().set({
                    'name': name,
                    'email': email,
                    'password': password,
                    'photo': photo
        })