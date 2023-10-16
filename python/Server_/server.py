import socket
import json

# Define the host and port
HOST = '127.0.0.1'  # Standard loopback interface address (localhost)
PORT = 65433        # Port to listenon-privileged ports are > 1023)

def save_as_json(text, filename='output.json'):
    with open(filename, 'w') as f:
        json.dump(text, f)

# Create a socket object
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    # Bind the socket to the host and port
    s.bind((HOST, PORT))
    # Listen for incoming connections
    s.listen()
    print('Server is listening on', HOST, 'port', PORT)
    while True:
        client, address = s.accept()
        received_data = b''
        while True:
            data, address = client.recvfrom(1024)
            if not data:
                break
            received_data += data

        with open('received_song.wav', 'wb') as file:
            file.write(received_data)
            print("File has been received and written successfully.")
