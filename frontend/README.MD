# Task management android application
This app can help you plan both personal and collaboration work. The application allows users to correctly distribute tasks and control their deadlines thanks to notifications. Use it to increase productivity and save time.

----------------------- GETTING STARTED----------------
You get started by cloning the project to your local machine:
https://version.aalto.fi/gitlab/CS-E4100/mcc-fall-2019-g05
and run it in android studio.

------------------PROGRAMMING LANGUAGE AND DATABASE----
The application is implemented using Kotlin as the Programming Language and Firebase as the Realtime Database and Storage

---------------------- USED LIBRARIES-------------------
FirebaseAuth
FirebaseDatabase
FirebaseInstanceId
FirebaseMessaging
FirebaseStorage
FirebaseVisionText
FirebaseVisionImage
Picasso 
Toast
TimePickerDialog
DatePickerDialog
SimpleDateFormat
DrawerBuilder
IDrawerItem

---------------------- IMPORTANT FEATURES----------------
This application implements adaptive user interfaces that can be opened both on mobile phones and tablets.

---------------------- REALIZED ACTIVITY----------------
LoginActivity
Login functions were realized in this activity. Function doLogin also checks the correct of inputting data.

SignUpActivity
This activity allows new users to register in the app, fulling username, password and image. 

ProfileSettingsActivity
This activity realizes the change profile image or edit user password.

CreateAProjectActivity
This activity realizes the opportunity to create projects. CreateAProjectActivity includes choosePhotoFromGallary function for uploading images from gallery, updateDateInView function for using date-time format, createProject for fulling project data.

AddUserActivity
This activity realizes the option to add members through a search based on the display name to group projects. 

AddTasksToAProjectActivity
Thanks to this activity users provided with an option to extract the text from an image (uploaded from the phone’s gallery) and create a task. Text recognition supported by using the OCR detection feature in Cloud Vision API.

ProjectContentActivity
This activity creates a view to display all the content of the project. The view consists of three tabs: Tasks, Pictures and Files. For all the tabs, the content is populated in lists and sorted by creation date.

ListOfCreatedProjectsActivity
This activity includes functions for view displays the projects list according to filters: date, favorite projects and upcoming deadline.

------------------ FUNCTIONALITY OF THE APP ----------------
Task management application provides the ability to create personal and group projects. Users can create individual tasks in a personal project and group project for collaborative work between several users.

Users can add several tasks in a project. Task creation includes three attributes: a description, a status, and a deadline. A task can have three possible statuses: pending, on-going and completed.

The application supports uploading files and associating them to a project. Supported file types are text, documents (.pdf), images (.jpg) and audio (.mp3). Images can be uploaded in two ways: from the camera or by choosing an image from the phone’s gallery. 

There are tasks view, pictures view and files view to display all its content in each project. For all the tabs, the content is populated in lists and sorted by creation date. 

Users involved in a group project can generate a report of the project.

Each listed project shows basic information about its content: the last modification date, a media icon when the project contains media and the profile image of the project members.

Projects filtered by date are sorted by modification date, favorite projects are sorted alphabetically by project name, projects with upcoming deadline are those to be completed in a week sorted by earliest deadline.

Users can search for projects by two criterias: keywords and project name.

A settings screen allows users to specify different resolutions for images. It allows to specify the image quality when uploading or downloading pictures. The image quality can be low (640×480 px),high (1280×960 px) and full (original size) resolution.

The application can notify users when they are added to a project, assigned to a task or when a task (project) deadline is approaching. Notifications can be received even if the application is in the background.