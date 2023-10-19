import socket
import json
import process

# Define the host and port
HOST = '172.16.168.1'  # Standard loopback interface address (localhost)
PORT = 8082        # Port to listenon-privileged ports are > 1023)


# Create a socket object
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    # Bind the socket to the host and port
    s.bind((HOST, PORT))
    # Listen for incoming connections
    s.listen()
    agent = Voice_process_agent(need_load=True)
    print('Server is listening on', HOST, 'port', PORT)
    while True:
        client, address = s.accept()
        received_data = b''
        while True:
            data, address = client.recvfrom(1024)
            if not data:
                break
            received_data += data
        print(received_data)
        agent.process('Server_/received_song.bin')
        with open('received_song.wav', 'wb') as file:
            file.write(received_data)
            print("File has been received and written successfully.")
        with open('received_song.bin', 'wb') as file:
            file.write(received_data)
            print("File has been received and written successfully.")
