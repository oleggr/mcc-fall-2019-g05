import random
import string


def randomString(stringLength=10):
    '''
    Generate a random string of fixed length.
    '''
    letters = string.ascii_letters + string.digits
    return ''.join(random.choice(letters) for i in range(stringLength))
