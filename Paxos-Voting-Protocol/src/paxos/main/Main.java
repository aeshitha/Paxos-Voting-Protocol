package paxos.main;

import paxos.learner.Learner;
import paxos.utils.CouncilMember;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        int learnerPort = 9000;
        List<Integer> memberPorts = Arrays.asList(8000, 8001, 8002, 8003, 8004, 8005, 8006, 8007, 8008);
        List<CouncilMember> councilMembers = new ArrayList<>();
        Map<CouncilMember, String> memberNames = new HashMap<>(); // Map to track member names

        // Start the Learner in a separate thread
        new Thread(() -> {
            Learner learner = new Learner(learnerPort, memberPorts.size()); // Learner port and number of members
            learner.start();
        }).start();

        // Create council members
        for (int i = 0; i < memberPorts.size(); i++) {
            String name = "M" + (i + 1);
            CouncilMember member = new CouncilMember(name, memberPorts.get(i), memberPorts, learnerPort);
            councilMembers.add(member);
            memberNames.put(member, name); // Store the name
        }

        // Start all council members
        for (CouncilMember member : councilMembers) {
            new Thread(member::start).start();
        }

        // Simulate a single proposal
        try {
            // Allow council members and learner to initialize
            Thread.sleep(2000);

            // Select a random proposer
            CouncilMember proposer = councilMembers.get(new Random().nextInt(councilMembers.size()));
            String proposerName = memberNames.get(proposer); // Retrieve name
            System.out.println(proposerName + " is the proposer.");

            // Start the proposer role with a candidate name
            proposer.proposerRole("Candidate1");

            // Wait for consensus to be reached
            Thread.sleep(5000); // Adjust this if needed for larger setups

            System.out.println("Consensus process completed. Program ending.");
        } catch (InterruptedException e) {
            System.err.println("Error during execution: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
