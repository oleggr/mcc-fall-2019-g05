from basic_firebase_interaction import ref


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

    keys_before = project_ref.get().keys()

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

    keys_after = project_ref.get().keys()

    project_key = [x for x in keys_after if x not in keys_before]

    return project_key[0]