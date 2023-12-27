

use std::{
    io::{prelude::*, BufReader},
    net::{TcpListener, TcpStream},
};

pub struct MyTcpWebServer {
    ip: String,
    port: u16
}

impl MyTcpWebServer { 
    pub fn new(ip: &str, port: u16) -> Self {
        MyTcpWebServer { 
            ip: ip.to_string(), 
            port: port 
        }
    } 

    pub fn run(self) {
        println!("Starting MyTcpWebServer...");
        let mut address = self.ip;
        let stringified_port: String = self.port.to_string();
        address.push(':');
        address.push_str(stringified_port.as_str());
        println!("Running at address: {address}");

        let listener: TcpListener = TcpListener::bind(address).unwrap();

        for stream in listener.incoming() {
            let stream = stream.unwrap();

            println!("Connection Established");
        }
    }

    fn handle_connection(mut stream: TcpStream) {
        let buff_reader = BufReader::new(&mut stream);
        let http_request: Vec<_> = buff_reader
            .lines()
            .map(|result| result.unwrap())
            .take_while(|line| !line.is_empty())
            .collect();

        println!("Request: {:#?}", http_request);
    }
}