assignment 3 1893758 

Paxos Voting Protocol Implementation
=====================================

Description
------------
This project implements a simulation of the Paxos consensus algorithm, where a set of council members (M1-M9) participate in reaching consensus for a given proposal. The system includes:

*Council Members: Each acts as an acceptor and can propose candidates.
*Learner: A centralized component that gathers votes and determines consensus.
*Test Scenarios: Several scenarios are implemented to test the robustness of the Paxos protocol, such as simultaneous proposals, immediate responses, and varying delays.

--A Makefile is provided to simplify the process of building, cleaning, and running the program, as well as executing specific test cases.--

Makefile Instructions
----------------------
The provided Makefile allows you to easily manage the program. Below is an explanation of each target and how to use them:

1. clean
Removes the compiled files and cleans the build directory.

make clean


2. build
Compiles the source code into the bin directory.

make build


3. run-main
Runs the main Paxos program where council members interact, and one proposer initiates the consensus process.

make run-main


4. Test Scenarios
Run specific test scenarios to verify the Paxos protocol in different conditions:

* Simultaneous Proposals: Tests the scenario where multiple proposers act simultaneously.

make test-simultaneous


* Immediate Responses: Tests the case where all council members respond immediately.

make test-immediate


* Varying Profiles: Tests scenarios with council members having varying response times.

make test-varying


Test Results
-------------
All the test cases have been executed successfully, demonstrating that the Paxos implementation is:

* Fault Tolerant: Handles network delays, dropped messages, and partial system failures without compromising consensus.
* Resilient: Ensures consensus is achieved even in scenarios with simultaneous proposals or varying response times.
The implementation works seamlessly under all tested conditions and showcases the robustness of the Paxos consensus algorithm.

Note
-----
Ensure you have Java 11 or higher installed.
The program logs all events to the console for easy debugging and verification.
Use the appropriate test target to explore how the Paxos algorithm handles different scenarios.
