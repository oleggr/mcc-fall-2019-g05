FROM debian

WORKDIR /usr/app
COPY / .

#python installation
RUN apt-get update && apt-get upgrade && apt-get dist-upgrade && apt-get autoremove
RUN apt-get install -y python3 python3-pip

#libraries
RUN pip3 install firebase_admin google flask datetime
RUN python3 -m pip install Pillow
RUN python3 -m pip install apscheduler

CMD ["python3","main.py"]
#launch like "docker run -p 80:8080 <container_id>"
