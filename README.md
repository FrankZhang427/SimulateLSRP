# Simulate Link State Routing Protocol

The goal of this project is to develop a pure user-space program which simulates the major functionalities of a routing device running a simplified Link State Routing protocol.
To simulate the real-world network environment, you have to start multiple instances of the program, each of which connecting with (some of) others via socket.
Each program instance represents a router or host in the simulated network space.
Correspondingly, the links connecting the routers/hosts and the IP addresses identifying the routers/hosts are simulate by the in-memory data structures.
By defining the format of the messages transmitting between the program instances,
as well as the parser and the handlers of these messages, you simulate the routing protocol with the userspace processes.
