from main import ref, storage
from dev_functions import randomString

import random
import string


from PIL import Image


def randomString(stringLength=10):
    '''
    Generate a random string of fixed length.
    '''
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(stringLength))


def image_resize(path='img/', filename='def_name'):
    '''
    Image from client resizing.
    Return array of names of image files.
    '''

    file_format = filename.split('.')[-1]

    new_filename = randomString(10)

    low_quality_name = new_filename + '.low.' + file_format
    mid_quality_name = new_filename + '.mid.' + file_format
    def_quality_name = new_filename + '.def.' + file_format

    im = Image.open(path + filename)
    im.putalpha(255)
    img = Image.new('RGB', im.size, (255,255,255))
    img.paste(im, (0,0), im)
    img.save(path + low_quality_name, quality=1)
    img.save(path + mid_quality_name, quality=40)
    img.save(path + def_quality_name, quality=95)

    images_names = [new_filename + '.' + file_format, low_quality_name, mid_quality_name, def_quality_name]

    return images_names


def image_upload(source_dir='img/', dest_fb_dir='attachments/', filenames=['default_name']):
    '''
    Upload resized images to server.
    '''

    try:   
        # Enable Storage
        client = storage.Client()

        # Reference an existing bucket.
        bucket = client.get_bucket('mcc-fall-2019-g5-258415.appspot.com')

        for filename in filenames:
            tmpBlob = bucket.blob(dest_fb_dir + filename)
            tmpBlob.upload_from_filename(filename=source_dir + filename)

        return True

    except:
        return False


def image_download(path_to_file='attachments/', filename='default_name'):
    # Enable Storage
    client = storage.Client()

    # Reference an existing bucket.
    bucket = client.get_bucket('mcc-fall-2019-g5-258415.appspot.com')

    tmpBlob = bucket.blob(path_to_file + filename)
    tmpBlob.download_to_filename('tmp/' + filename)


def image_download_res(path_to_file='attachments/', filename='default_name', quality='best_quality'):
    # Enable Storage
    client = storage.Client()

    # Reference an existing bucket.
    bucket = client.get_bucket('mcc-fall-2019-g5-258415.appspot.com')

    if quality == 'middle_quality':
        tmpBlob = bucket.blob(path_to_file + filename + 'middle_quality')

    elif quality == 'low_quality':
        tmpBlob = bucket.blob(path_to_file + filename + 'low_quality')

    else:
        tmpBlob = bucket.blob(path_to_file + filename + 'best_quality')
 
    tmpBlob.download_to_filename(filename)


def file_upload(path_to_file='attachments/', filename='default_name'):
    # Enable Storage
    client = storage.Client()

    # Reference an existing bucket.
    bucket = client.get_bucket('mcc-fall-2019-g5-258415.appspot.com')

    # Upload a local file to a new file to be created in your bucket.
    tmpBlob = bucket.blob(path_to_file + filename)
    tmpBlob.upload_from_filename(filename='img/{}'.format(filename))


def file_download(path_to_file='attachments/', filename='default_name'):
    # Enable Storage
    client = storage.Client()

    # Reference an existing bucket.
    bucket = client.get_bucket('mcc-fall-2019-g5-258415.appspot.com')

    # Download a file from your bucket.
    tmpBlob = bucket.blob(path_to_file + filename)
    tmpBlob.download_to_filename(filename)
