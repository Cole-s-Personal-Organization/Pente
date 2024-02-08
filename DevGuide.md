# Starter Development Documentation
This documentation is intended to get people started and firmiliar with how the project is set up and how to develop it. 

## Initial Setup
To get your environment setup:
1. Download Java - here is a [link](https://www.oracle.com/java/technologies/downloads/) to the download page, **We will be using JDK version 17.0.7**
1. Download Maven - here is a [link](https://maven.apache.org/download.cgi) to the download page, **We will be using Apache Maven 3.8.5**
1. Clone Repository - Follow [link](https://www.simplilearn.com/tutorials/git-tutorial/git-installation-on-windows) to downlod git if you dont have it(I assume you do and are on windows), navigate to a folder where you want to store your project and run:

        git clone *github_repository_url*

1. Setup IDE - I think I made you download vscode so you should already be good on this step but in case you don't, download it, its cool -> [link](https://code.visualstudio.com/download) 
1. Navigate to GameServer folder

        cd *Your folder location*


At this point you should have everything you need for development.

## Git Workflow
Everytime you want to work on this project you should follow these steps to setup your environment.
1. Navigate to GameServer folder.

        cd *Your folder location*

1. Check branch.
    - To set up a branch **when first starting out** run the following commands. These will create your branch, and move you to it respectivley. 

            git branch *your_branch_name*

            git checkout *your_branch_name*
    
    - **Everytime you develop**, you can check your status to ensure you're on your own branch:

            git status

1. Pull the most recent changes.
    In order to update your version of the code with the most recent changes you must run:

            git checkout main

            git pull

            git checkout *your branch*

            git merge main
    
    This will allow you to pull all changes done by others and merge it with your own code.

1. Start Coding o7
1. Once you want to save and push your changes to github run:
        
        git push

    **NOTE:** Send me a message when you're done and I'll get your code pulled into main. 
