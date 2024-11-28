package paxos.utils;

import java.io.*;
import java.net.*;
import java.util.*;

public class CouncilMember {
    private final String memberId;
    private final int port;
    private final List<Integer> memberPorts;
    private final int learnerPort;
    private boolean hasProposed = false;

    public CouncilMember(String memberId, int port, List<Integer> memberPorts, int learnerPort) {
        this.memberId = memberId;
        this.port = port;
        this.memberPorts = memberPorts;
        this.learnerPort = learnerPort;
    }

    public void setResponseProfile(int profile) {
        // Optional: Configure response profiles
    }

    public void start() {
        // Start Acceptor (listener)
        new Thread(this::acceptorRole).start();
    }

    public void acceptorRole() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[" + memberId + "] Acceptor is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String message = in.readLine();
                System.out.println("[" + memberId + "] Received: " + message);

                if (message.startsWith("PROPOSE")) {
                    String[] parts = message.split(" ");
                    String candidate = parts[1];
                    int proposalId = Integer.parseInt(parts[2]);

                    // Send ACCEPT response to proposer
                    out.println("ACCEPT " + candidate + " " + proposalId + " FROM " + memberId);

                    // Notify the learner
                    try (Socket learnerSocket = new Socket("localhost", learnerPort);
                         PrintWriter learnerOut = new PrintWriter(learnerSocket.getOutputStream(), true)) {
                        learnerOut.println("ACCEPT " + candidate + " " + proposalId + " FROM " + memberId);
                        System.out.println("[" + memberId + "] Sent ACCEPT to Learner: " + candidate + " " + proposalId);
                    } catch (IOException e) {
                        System.err.println("[" + memberId + "] Failed to notify Learner: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[" + memberId + "] Acceptor failed: " + e.getMessage());
        }
    }


    public void proposerRole(String candidate) {
        if (hasProposed) {
            System.out.println("[" + memberId + "] Already proposed. Skipping proposal.");
            return; // Prevent multiple proposals from the same member
        }

        try {
            hasProposed = true; // Mark as proposed
            int proposalId = new Random().nextInt(1000); // Generate unique Proposal ID
            System.out.println("[" + memberId + "] Proposing candidate: " + candidate + " with Proposal ID: " + proposalId);

            for (int memberPort : memberPorts) {
                try (Socket socket = new Socket("localhost", memberPort);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    out.println("PROPOSE " + candidate + " " + proposalId + " FROM " + memberId);
                    System.out.println("[" + memberId + "] Proposal sent to port " + memberPort);
                } catch (IOException e) {
                    System.err.println("[" + memberId + "] Failed to connect to port " + memberPort);
                }
            }

            System.out.println("[" + memberId + "] Proposal for " + candidate + " with Proposal ID: " + proposalId + " completed.");
        } catch (Exception e) {
            System.err.println("[" + memberId + "] Proposer encountered an error: " + e.getMessage());
        }
    }

    public Object getMemberId() {
        return this.memberId;
    }

}
