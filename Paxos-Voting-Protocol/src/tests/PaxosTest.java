package tests;

import paxos.learner.Learner;
import paxos.utils.CouncilMember;

import java.net.ServerSocket;
import java.io.IOException;
import java.util.*;

public class PaxosTest {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No test case specified. Please provide one of the following test cases as an argument:");
            System.out.println("- testSimultaneousProposals");
            System.out.println("- testImmediateResponses");
            System.out.println("- testVaryingProfiles");
        } else {
            switch (args[0]) {
                case "testSimultaneousProposals":
                    System.out.println("Running test case: Simultaneous Proposals...");
                    testSimultaneousProposals();
                    break;

                case "testImmediateResponses":
                    System.out.println("Running test case: Immediate Responses...");
                    testImmediateResponses();
                    break;

                case "testVaryingProfiles":
                    System.out.println("Running test case: Varying Profiles...");
                    testVaryingProfiles();
                    break;

                default:
                    System.out.println("Invalid test case: " + args[0]);
                    System.out.println("Please provide one of the following test cases as an argument:");
                    System.out.println("- testSimultaneousProposals");
                    System.out.println("- testImmediateResponses");
                    System.out.println("- testVaryingProfiles");
                    break;
            }
        }
    }


    private static void startLearner() {
        new Thread(() -> {
            Learner learner = new Learner(9000, 9); // Initialize Learner with port 9000 and 9 council members
            learner.start(); // Start the learner's listening loop
        }).start();
    }

    /**
     * Test Case: Simultaneous Proposals
     * Simulates two council members proposing simultaneously.
     */
    private static void testSimultaneousProposals() {
        System.out.println("Testing simultaneous proposals...");

        clearPorts(); // Clear ports before starting the test

        startLearner(); // Start the learner
        List<CouncilMember> councilMembers = createCouncilMembers(); // Initialize council members

        // Start acceptor roles for all council members
        for (CouncilMember member : councilMembers) {
            new Thread(member::acceptorRole).start();
        }

        // Simulate simultaneous proposals
        new Thread(() -> councilMembers.get(0).proposerRole("M1")).start(); // M1 proposes
        new Thread(() -> councilMembers.get(1).proposerRole("M2")).start(); // M2 proposes

        // Wait for proposals to complete
        sleep(5000);

        System.out.println("Simultaneous proposals test completed.");
    }

    /**
     * Test Case: Immediate Responses
     * Tests how the system behaves when all council members respond immediately.
     */
    private static void testImmediateResponses() {
        System.out.println("Testing immediate responses...");

        clearPorts(); // Clear ports before starting the test

        startLearner(); // Start the learner
        List<CouncilMember> councilMembers = createCouncilMembers(); // Initialize council members

        // Start acceptor roles for all council members
        for (CouncilMember member : councilMembers) {
            new Thread(member::acceptorRole).start();
        }

        // Simulate a single proposal
        new Thread(() -> councilMembers.get(2).proposerRole("M1")).start(); // M3 proposes

        // Wait for the test to complete
        sleep(5000);

        System.out.println("Immediate responses test completed.");
    }

    /**
     * Test Case: Varying Profiles
     * Tests the system with council members having different response profiles.
     */
    private static void testVaryingProfiles() {
        System.out.println("Testing varying response profiles...");

        clearPorts(); // Clear ports before starting the test

        startLearner(); // Start the learner
        List<CouncilMember> councilMembers = createCouncilMembersWithProfiles(); // Initialize council members with varying profiles

        // Start council members
        for (CouncilMember member : councilMembers) {
            new Thread(member::start).start();
        }

        // Simulate a single proposal
        new Thread(() -> councilMembers.get(0).proposerRole("M1")).start();

        // Wait for consensus
        sleep(5000);

        System.out.println("Varying profiles test completed.");
    }

    /**
     * Helper Method: Creates 9 Council Members
     */
    private static List<CouncilMember> createCouncilMembers() {
        List<Integer> memberPorts = Arrays.asList(8000, 8001, 8002, 8003, 8004, 8005, 8006, 8007, 8008);
        List<CouncilMember> councilMembers = new ArrayList<>();

        for (int i = 0; i < memberPorts.size(); i++) {
            councilMembers.add(new CouncilMember("M" + (i + 1), memberPorts.get(i), memberPorts, 9000));
        }
        return councilMembers;
    }

    /**
     * Helper Method: Creates Council Members with Random Response Profiles
     */
    private static List<CouncilMember> createCouncilMembersWithProfiles() {
        List<Integer> memberPorts = Arrays.asList(8000, 8001, 8002, 8003, 8004, 8005, 8006, 8007, 8008);
        List<CouncilMember> councilMembers = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < memberPorts.size(); i++) {
            CouncilMember member = new CouncilMember("M" + (i + 1), memberPorts.get(i), memberPorts, 9000);
            member.setResponseProfile(random.nextInt(4)); // Random profile: 0 = Immediate, 1 = Small Delay, etc.
            councilMembers.add(member);
        }
        return councilMembers;
    }

    /**
     * Helper Method: Sleeps for the specified time
     */
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears all ports to prevent conflicts before running a test.
     */
    private static void clearPorts() {
        List<Integer> portsToClear = Arrays.asList(8000, 8001, 8002, 8003, 8004, 8005, 8006, 8007, 8008, 9000);
        for (int port : portsToClear) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Cleared port: " + port);
            } catch (IOException e) {
                System.err.println("Failed to clear port: " + port + " - " + e.getMessage());
            }
        }
    }
}
