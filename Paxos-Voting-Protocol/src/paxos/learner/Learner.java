package paxos.learner;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Learner {
    private final int port;
    private final Map<String, Integer> votes; // Tracks votes for each proposal (candidate-proposalId)
    private final Set<String> consensusReached; // Tracks proposals with consensus
    private final int majorityThreshold;

    public Learner(int port, int numMembers) {
        this.port = port;
        this.votes = new ConcurrentHashMap<>();
        this.consensusReached = ConcurrentHashMap.newKeySet(); // Thread-safe set
        this.majorityThreshold = (numMembers / 2) + 1; // Majority rule
    }

    public void start() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Learner] Listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> handleNotification(socket));
            }
        } catch (IOException e) {
            System.err.println("[Learner] Failed: " + e.getMessage());
        }
    }

    private void handleNotification(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message = in.readLine();
            System.out.println("[Learner] Received: " + message);

            if (message.startsWith("ACCEPT")) {
                String[] parts = message.split(" ");
                String candidate = parts[1];
                String proposalId = parts[2];
                String proposalKey = candidate + "-" + proposalId;

                // Update vote count for this proposal
                votes.put(proposalKey, votes.getOrDefault(proposalKey, 0) + 1);
                System.out.println("[Learner] Proposal votes updated: " + votes);

                // Synchronize to ensure thread safety
                synchronized (consensusReached) {
                    if (!consensusReached.contains(proposalKey) &&
                            votes.get(proposalKey) >= majorityThreshold) {

                        System.out.println("[Learner] Proposal " + proposalKey + " votes: " + votes.get(proposalKey) + " (Threshold: " + majorityThreshold + ")");
                        System.out.println("[Learner] Consensus reached! President: " + candidate + " (Proposal ID: " + proposalId + ")");
                        consensusReached.add(proposalKey); // Mark as consensus reached
                        System.out.println("[Learner] Consensus reached for proposals: " + consensusReached);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Learner] Error handling notification: " + e.getMessage());
        }
    }





    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Learner <port> <numMembers>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        int numMembers = Integer.parseInt(args[1]);

        Learner learner = new Learner(port, numMembers);
        learner.start();
    }
}
