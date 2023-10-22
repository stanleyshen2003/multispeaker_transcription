import socket
from process_verification_new import Voice_process_agent

class connect_agent():
    def __init__(self):
    # Define the host and port
        self.HOST = input("your host ip: ")
        self.PORT = int(input("the port you want to choose"))
        self.api_key = input("your openai api key (optional): ")

    def connect(self):
        # Create a socket object
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            # Bind the socket to the host and port
            s.bind((self.HOST, self.PORT))
            # Listen for incoming connections
            s.listen()
            agent = Voice_process_agent(need_load=True)
            print('Server is listening on', self.HOST, 'port', self.PORT)
            while True:
                client, address = s.accept()
                print(address)
                received_data = b''
                while True:
                    data, address = client.recvfrom(2048)
                    if not data:
                        break
                    received_data += data                
                client, address = s.accept()
                msg_json = agent.process(received_data)
                print(msg_json)
                msg_json += '\n'
                client.sendall(msg_json.encode())
                
if __name__ == "__main__":
    agent = connect_agent()
    agent.connect()
