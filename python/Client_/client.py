import socket
import time

# Define the host and port
HOST = '127.0.0.1'  # The server's hostname or IP address
PORT = 65433        # The port used by the server

# Create a socket object
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    # Connect to the server
    s.connect((HOST, PORT))
    # Send the WAV file to the server
    with open('source0.wav', 'rb') as f:
        s.sendall(f.read())
    time.sleep(2)
    

print('Received JSON file from the server.')
