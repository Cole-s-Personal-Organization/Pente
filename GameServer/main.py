"""Main driver for the Catan game server."""

from Pente import PenteGameModel
from PenteController import PenteController
from GameLobby import Lobby


from flask import Flask, render_template
from flask_socketio import SocketIO

app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app)

socketio.on_namespace(Lobby())

lobbies = []



if __name__=="__main__":
    new_catan_lobby = Lobby()
    lobbies.append(new_catan_lobby)
    model = CatanGameModel()
    controller = CatanController(model=model, lobby=new_catan_lobby)
    
    controller.run()