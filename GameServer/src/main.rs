use crate::web_server::my_web_server;

pub mod pente;
pub mod web_server;

fn main() {
    println!("Hello, world!");

    // let controller = pente::pente_controller::PenteController::new();

    let my_tcp_web_server = my_web_server::MyTcpWebServer::new("127.0.0.1", 8000);
    my_tcp_web_server.run()
}

