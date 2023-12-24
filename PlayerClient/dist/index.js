"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
// const { Command } = require("commander");
const figlet = require("figlet");
const inquirer = require('inquirer');
// const program = new Command();
console.clear();
console.log(figlet.textSync("Pente"));
const ui = new inquirer.ui.BottomBar();
// inquirer.prompt(["Press enter to proceed!"])
// console.pipe(ui.log);
// // Or simply write output
ui.log.write('something just happened.');
ui.log.write('Almost over, standby!');
// // During processing, update the bottom bar content to display a loader
// // or output a progress bar, etc
ui.updateBottomBar('new bottom bar content');
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
inquirer.prompt(questions).then((answers) => __awaiter(void 0, void 0, void 0, function* () {
    // Start the Bottom Bar
    ui.updateBottomBar('Processing...');
    // Simulate an asynchronous operation
    yield simulateAsyncOperation();
    console.clear();
    // Stop the Bottom Bar
    ui.updateBottomBar('Finished processing.');
    // Access the user's input
    console.log('Hello, ' + answers.name + '!');
}));
//# sourceMappingURL=index.js.map