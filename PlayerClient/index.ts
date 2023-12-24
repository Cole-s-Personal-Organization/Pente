const figlet = require("figlet");
const inquirer = require('inquirer');


interface ClientDisplay {
    display: () => void
}

class TitleDisplay implements ClientDisplay {
    display() {
        console.log(figlet.textSync("Pente"));
    };
}

class GameSearchDisplay implements ClientDisplay {
    display() {
        console.log(figlet.textSync("Pente"));
    };
}

class LobbyDisplay implements ClientDisplay {
    display() {
        console.log(figlet.textSync("Pente"));
    };
}

class PenteBoardDisplay implements ClientDisplay {
    display() {

    }
}

class TurnCounterDisplay implements ClientDisplay {
    display() {
        
    }
}

class PenteInGameDisplay implements ClientDisplay {
    display() {
        console.log(figlet.textSync("Pente"));
    };
}


const ui = new inquirer.ui.BottomBar();
let activeScreen: ClientDisplay = new TitleDisplay()
console.clear()
activeScreen.display()

const changeActiveDisplay = (display: ClientDisplay) => {
    activeScreen = display
    console.clear()
    activeScreen.display()
}

// inquirer.prompt(["Press enter to proceed!"])

// console.pipe(ui.log);

// // Or simply write output
ui.log.write('something just happened.');
ui.log.write('Almost over, standby!');

// // During processing, update the bottom bar content to display a loader
// // or output a progress bar, etc
ui.updateBottomBar('new bottom bar content');



interface Answers {
    name: string;
    // Add more properties based on your questions
  }




// Simulating an asynchronous operation
const simulateAsyncOperation = () => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(0);
        }, 3000);
    });
};

const questions = [
    {
        type: 'input',
        name: 'name',
        message: 'What is your name?',
    },
    ];

inquirer.prompt(questions).then(async (answers: Answers) => {
    // Start the Bottom Bar
    ui.updateBottomBar('Processing...');

    // Simulate an asynchronous operation
    await simulateAsyncOperation();

    console.clear()
    // Stop the Bottom Bar
    ui.updateBottomBar('Finished processing.');

    // Access the user's input
    console.log('Hello, ' + answers.name + '!');
});