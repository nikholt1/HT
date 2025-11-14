
<h1 align="center">
  <br>
  <a href=""><img src="https://raw.githubusercontent.com/nikholt1/HTasset/refs/heads/main/loginImage/logo.png" alt="Markdownify" width="200"></a>
  <br>
  Local Streaming made easy
  <br>
</h1>


[![OS](https://img.shields.io/badge/OS-linux%2C%20windows%2C%20macOS-0078D4)](https://docs.abblix.com/docs/technical-requirements)
[![getting started](https://img.shields.io/badge/getting_started-guide-1D76DB)](#getting-started)


**Read about the full development process here:
[Refract article](https://niklasholtlau.web.app/R.html)**

* ##### Download the Installer: [Here](https://example.com)
* [Go to Recent updates](#Recent-updates)

## Table of contents
* [Getting started](#Getting-started)
* [Future implementations](#future-implementations)
* [About](#about)
* [Why](#why)
* [Maintenance and Contribution](#maintenance-and-contributions)
* [SWOT and Risk Analysis](https://github.com/nikholt1/HT/blob/master/analysis/HT%20SWOT%20analysis%20and%20Risk%20analysis.pdf)
* [Feasibility Study](https://github.com/nikholt1/HT/blob/master/analysis/HT%20Feasibility%20Study.pdf)
* [Go to Retrieve lost username and password guide](#Retreive-username-and-password-guide)
* [Go to Database Setup](#database-setup)


  
<!--What the project does
    Why the project is useful
    How users can get started with the project
    Where users can get help with your project
    Who maintains and contributes to the project--> 

## About
This project is a locally run web-application, allowing users to view and stream videos from a locally stored
host computer. The System is a desktop application that through Springboot framework serves a web-application on the users Wi-Fi, allowing
anyone on the same Wi-Fi to access the specified folders with the content and stream videos from said web-application.
The system installs a default folder structure and executable application, that together with a SQLite database lets the user register profiles and stream the content from them.
The system also allows the user to add new folders or modify the existing default folders for categories of content.


## Why
Given the rising annoyance of streaming services' rising prices together with more ads implementations and
since most of my projects are not made for public display, i decided to make this locally run streaming service.

## Maintenance and contributions
### Github versions and Automatic update
The prod system automatically searches for the latest version on Github versions, and automatically updates itself after comparing its own version to the newest version.
The version control is maintained by me and contributions that are accepted will be implemented in newer versions of the system once i roll out a new version of the system to Github version. 



## Getting started

## Installation
I have designed an installer script that can be downloaded, which through automation creates the necessary folder-structure
and installs the executable and assets in the default folders, together with the default SQLite database.

<!-- fix -->
* Download the installer
 * Download the installer: [Here](https://example.com)
 * Select the preferred _directory_ where you want the program to be installed
 * Wait for the installer to set up the program
 * Press finish

Now the Refract should automatically launch, if not, search for it and launch it.

## Modification of the system
From here the user has the option to specify a certain directory if they please, or they can populate the
default folders with the videos they want. To add a image to a specific video, the user can place the video with the name (video.mp4) and in the same folder
place an image with the exact same name as the video (video.jpg). From there the system will automatically update the backend to serve the video
and place the cover of the video with the dedicated image to the frontend.

**To specify new path**
* Log in to the program
* Choose any profile
* Navigate to the **"settings"** endpoint
* Scroll down, and find **"path"**
* Change the path to the desired new path

Now the system should automatically fetch the content from the new specified path

**To add new content to the category folders**
* Find the specified path in the OS's folder manager
* Add a subfolder with a specific name or navigate into a existing folder
* If a new folder has been created, you can choose to save a image with the exact name as the parent folder.
* Place any video in the folder
* Place a image with the exact name of the video

Now the system should automatically serve the new category folder with its corresponding image, and video with its corresponding image.


## Retreive username and password guide
The default Username and Password for the login, is username: Admin and password: admin. After initial login the user has the option and is encouraged to
change the username and password to what they desire. This is locally stored on their host computer, which means that the system does not send any data to anywhere, which means that if the username and password is lost,
the user would have to follow the "retrieve username and password" guide, to fetch the lost username and password directly from the SQLite database.

**retrieve username and password guide**
* In the OS folder manager, navigate to the directory where the system is installed
* In the system folder, navigate to the **"Data"** folder
* In the datafolder look for the **"hometheaterdb.db"** SQLite database
* If the database is present, we now know that the username and password should be there


## Future Implementations
* Resume and see watched videos
  * I am currently working on the last table of the SQLite database, "unfinished_videos", The user should be able to resume a unfinished video, or the system should be able to recognize which movies have been watched.

* Profile picture selection
  * The user should be able to, when creating a new profile, see the images that they can choose from
  * The user should be able to change profile picture and username for the profiles when they want to

## notes
- Implemented user_profile_picture_path varchar(255) into database for profile picture handling per user.
08/10/2025

<!-- Database Setup section END -->


## Recent updates
* [14/11/2025](https://example.com)sS
