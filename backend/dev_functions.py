import random
import string
import smtplib, ssl
import traceback
from email.message import EmailMessage


def randomString(stringLength=10):
    '''
    Generate a random string of fixed length.
    '''
    letters = string.ascii_letters + string.digits
    return ''.join(random.choice(letters) for i in range(stringLength))


def send_mail(connected_users, object_name, object_id):

    for user in connected_users:

        mail_receiver = user['email']

        # email server
        mail_server = 'smtp.yandex.ru'
        mail_port = 465
        mail_sender ='mcc.fall.2019.g05@yandex.ru'
        mail_password = 'Q!W@E#r4t5y6'

        # body = '\nHello!\nYour {} by id {} will expires in 1 day.'.format(object_name, object_id)

        msg = EmailMessage()

        msg['Subject'] = 'Your activity is expiring'
        msg['From'] = mail_sender
        msg['To'] = mail_receiver
        msg.preamble = 'Mail notification\n'

        msg.set_content("""\
Hello!

Your {} by id {} will expires in 1 day
""".format(object_name, object_id))

        # message  ='Sent from: {}\nSent to:{}\nSubject:{}\n Body:{}'.format(mail_sender, mail_receiver, subject, body)

        try:
            # Create a secure SSL context
            context = ssl.create_default_context()

            with smtplib.SMTP_SSL(mail_server, mail_port, context=context) as server:
                server.login(mail_sender, mail_password)
                # server.sendmail(mail_sender, mail_receiver, message)
                server.send_message(msg)

        except Exception:
            print('ERROR: Somathind wrong happened.\nException: {}\n{}'.format(Exception, traceback.print_exc()))
