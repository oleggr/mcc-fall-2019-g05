from firebase_interaction import ref


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

    

    return project_key.key
