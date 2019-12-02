import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

import random
import string


def object_exists(object_type='default_object', object_name="dafault_name"):

    object_ref = ref.child(object_type)
    all_objects = object_ref.get()

    for object_id in all_objects:
        if object_name == all_objects[object_id]['name']: 
            # user exists
            return True

    # user not found
    return False


def add_users():

    users_ref = ref.child('users')

    for i in range(0, 3):

        name = 'name' + str(i + 1)

        if not object_exists('users', name):
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

def table_fill():
    add_users()
    add_projects()
    add_members()
    
    print('INFO::Table filling is done')
