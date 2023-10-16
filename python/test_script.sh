python3 ./Server_/server.py &
python3 ./Client_/client.py
# Kill the server script after the client script finishes
kill $(pgrep -f "python3 tcp_server.py")
