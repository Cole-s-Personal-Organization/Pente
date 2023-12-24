# Dan Development Documentation
This documentation will be focused around providing a straightforward insight into how to interact and develop this project.

## Github Workflow
### Initial Setup
To get your environment setup:
1. Download Python - here is a [link](https://www.python.org/downloads/) to the download page
1. Clone Repository - Follow [link](https://www.simplilearn.com/tutorials/git-tutorial/git-installation-on-windows) to downlod git if you dont have it(I assume you do and are on windows), navigate to a folder where you want to store your project and run:

    git clone *github_repository_url*

1. Setup IDE - I think I made you download vscode so you should already be good on this step but in case you don't, download it, its cool -> [link](https://code.visualstudio.com/download) 
1. Navigate to GameServer folder
1. Setup venv - to manage dependancies in the project we will be using pythons built in venv(virtual environment), run:

        python3 -m venv venv 

    Then, depending on your operating system run:
    - Linux: 
        
            source venv/bin/activate
    
    - Windows: 
        
            venv\Scripts\activate
    
    Finally, download all required libraries by:

            pip install -r requirements.txt



At this point you should have everything you need for development.

### Development Workflow
Everytime you want to work on this project you should follow these steps to setup your enviornment.
1. Navigate to GameServer folder
1. Ensure you're on your own branch
    - For now just branch, switch to it, and develop there. I'll help you with the merging when you're done. You should definitly learn about the branch, work, merge workflow at some point. Commands to run initially(not everytime):

            git branch *your_branch_name*

            git checkout *your_branch_name*
    
    - Everytime you develop, you can check your status to ensure you're on your own branch:

            git status

1. Activate enviornment - depending on your operating system run:
    - Linux: 
        
            source venv/bin/activate
    
    - Windows: 
        
            venv\Scripts\activate




## Your Starting Point (12/23)
The code I have written so far is very much still in a skeleton stage so reading too much into the stuff I have currently written may confuse you just as much as it has me confused. **The main thing to know** is that I will be following a MVC(Model View Controller) design pattern. 

[Link](https://www.geeksforgeeks.org/mvc-design-pattern/) to an article about it.

Your mission, should you choose to accept it, is to implement the model portion of this game. This will mean that you will need to make a model class which 
1. has state which represents the board
1. make member set functions which can take in data and alter the game state/board 
1. make member get functions which can be called to get relevent information about the board, the last play, etc

You should not have to worry about the players themselves, if you need to identify the pieces then use some identifier which can be passed in through a set function. It is not up to the model to know who the players are, only how the pieces interact with each other and what that means to the overall game. Hopefully that example gives you an idea of how to setup the model.

**NOTE:** We will not be able to acutally run the code and play with it through the cli until I get it setup. This means you cannot write code, run it, and see how it behaves until that happens. If you don't want to write the code somewhat aimlessly, a great way to test functionality would be through **unit testing**.

[Python has a library called pytest](https://docs.pytest.org/en/7.4.x/) which can help write tests for evaluating responses from functions which would be awesome to have for our model. **It will save you a lot of time** if you write these for your functions as you build them.