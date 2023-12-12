import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Process {
    String processName;
    int arrivalTime;
    int burstTime;
    int priorityNumber;
    int waitingTime;
    int turnaroundTime;

    public Process(String processName, int arrivalTime, int burstTime, int priorityNumber) {
        this.processName = processName;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priorityNumber = priorityNumber;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriorityNumber() {
        return priorityNumber;
    }

    @Override
    public String toString() {
        return "Process Name: " + processName;
    }
}

class NonPreemptiveSJF {

    private static List<Process> readyQueue;
    private static List<Process> processes;
    private static List<Process> executedProcesses;
    private static int currentTime;
    private static int totalTurnaround = 0;
    private static int totalWaiting = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();

        List<Process> inputProcesses = new ArrayList<>();
        for (int i = 1; i <= numProcesses; i++) {
            System.out.println("Enter details for Process " + i + ":");
            System.out.print("Process Name: ");
            String processName = scanner.next();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Priority Number: ");
            int priorityNumber = scanner.nextInt();

            inputProcesses.add(new Process(processName, arrivalTime, burstTime, priorityNumber));
        }

        System.out.print("Enter context switch time: ");
        int contextSwitchTime = scanner.nextInt();

        scheduleProcesses(inputProcesses, contextSwitchTime);

        scanner.close();
    }

    public static void scheduleProcesses(List<Process> inputProcesses, int contextSwitchTime) {
        readyQueue = new ArrayList<>();
        processes = new ArrayList<>(inputProcesses);
        executedProcesses = new ArrayList<>();
        currentTime = processes.get(0).getArrivalTime();

        System.out.printf("%-15s%-15s%-15s%-15s%-15s%-15s%n", "Process Name", "Arrival Time", "Burst Time", "Priority", "Turnaround Time    ", "Waiting Time");

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            addAndSortProcesses();
            executeAndRemoveProcesses(contextSwitchTime);
        }

        System.out.println("\nExecution Order:");
        for (Process executedProcess : executedProcesses) {
            System.out.println(executedProcess.processName);
        }

        System.out.println("\nAverage Waiting Time for Processes: " + (float) totalWaiting / inputProcesses.size());
        System.out.println("Average Turnaround Time for Processes: " + (float) totalTurnaround / inputProcesses.size());
    }

    private static void addAndSortProcesses() {
        int i = 0;
        while (i < processes.size() && processes.get(i).getArrivalTime() <= currentTime) {
            readyQueue.add(processes.get(i));
            processes.remove(i);
        }
        Collections.sort(readyQueue, Comparator.comparingInt(Process::getPriorityNumber).thenComparingInt(Process::getBurstTime));
    }

    private static void executeAndRemoveProcesses(int contextSwitchTime) {
        if (!readyQueue.isEmpty()) {
            final Process currentProcess = readyQueue.get(0);

            currentTime += currentProcess.getBurstTime();
            currentTime += contextSwitchTime;

            printDetails(currentProcess);

            executedProcesses.add(currentProcess);

            readyQueue.remove(0);
        }
    }
    private static void printDetails(Process currentProcess) {
        int turnaroundTime = currentTime - currentProcess.getArrivalTime();
        int waitingTime = turnaroundTime - currentProcess.getBurstTime();

        System.out.printf("%-15s%-15d%-15d%-15d%-15d\t\t%-15d%n",
                currentProcess.processName,
                currentProcess.getArrivalTime(),
                currentProcess.getBurstTime(),
                currentProcess.getPriorityNumber(),
                turnaroundTime,
                waitingTime);

        totalWaiting += waitingTime;
        totalTurnaround += turnaroundTime;
    }
}
