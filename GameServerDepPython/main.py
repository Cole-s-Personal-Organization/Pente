"""Main driver for the Catan game server."""

from GameServer.src.PenteModel import PenteGameModel
from GameServer.src.PenteController import PenteController
from GameServer.src.GameLobby import Lobby, GamePool


from flask import Flask, render_template, request
from flask_socketio import SocketIO

app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app)

# collection of all availible games
gamePool = GamePool(socketio)



# endpoints for non socket interactions 
@app.route("/lobby", methods=["GET", "POST"])
def lobby_endpoints():
    if request.method == "GET":
        return
    elif request.method == "POST":
        request.json



# main boot script for flask server
if __name__=="__main__":
    socketio.run(app)


    # new_catan_lobby = Lobby()
    # lobbies.append(new_catan_lobby)
    # model = CatanGameModel()
    # controller = CatanController(model=model, lobby=new_catan_lobby)
    
    # controller.run()