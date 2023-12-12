


import java.util.*;

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

    // Getters and toString method remain the same
}

class NonPreemptiveSJF {
    private List<Process> readyQueue;
    private List<Process> processes;
    private List<Process> executedProcesses;
    private int currentTime;
    private int totalTurnaround = 0;
    private int totalWaiting = 0;

    public void scheduleProcesses(List<Process> inputProcesses, int contextSwitchTime) {
        readyQueue = new ArrayList<>();
        processes = new ArrayList<>(inputProcesses);
        executedProcesses = new ArrayList<>();
        currentTime = processes.get(0).arrivalTime;

        System.out.printf("%-15s%-15s%-15s%-15s%-15s%-15s%n", "Process Name", "Arrival Time", "Burst Time", "Priority", "Turnaround Time", "Waiting Time");

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

    private void addAndSortProcesses() {
        int i = 0;
        while (i < processes.size() && processes.get(i).arrivalTime <= currentTime) {
            readyQueue.add(processes.get(i));
            processes.remove(i);
        }
        Collections.sort(readyQueue, Comparator.comparingInt(p -> p.burstTime));
    }

    private void executeAndRemoveProcesses(int contextSwitchTime) {
        if (!readyQueue.isEmpty()) {
            final Process currentProcess = readyQueue.get(0);

            currentTime += currentProcess.burstTime;
            currentTime += contextSwitchTime;

            printDetails(currentProcess);

            executedProcesses.add(currentProcess);

            readyQueue.remove(0);
        }
    }

    private void printDetails(Process currentProcess) {
        int turnaroundTime = currentTime - currentProcess.arrivalTime;
        int waitingTime = turnaroundTime - currentProcess.burstTime;

        System.out.printf("%-15s%-15d%-15d%-15d%-15d\t\t%-15d%n",
                currentProcess.processName,
                currentProcess.arrivalTime,
                currentProcess.burstTime,
                currentProcess.priorityNumber,
                turnaroundTime,
                waitingTime);

        totalWaiting += waitingTime;
        totalTurnaround += turnaroundTime;
    }
}

class PriorityScheduler {
    public void scheduleProcesses(Vector<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;

        Vector<Process> completedProcesses = new Vector<>();

        while (!processes.isEmpty()) {
            int highestPriority = Integer.MAX_VALUE;
            Process selectedProcess = null;

            for (Process process : processes) {
                if (process.arrivalTime <= currentTime && process.priorityNumber < highestPriority) {
                    highestPriority = process.priorityNumber;
                    selectedProcess = process;
                }
            }

            if (selectedProcess == null) {
                currentTime++;
            } else {
                processes.remove(selectedProcess);
                System.out.println(selectedProcess.processName);

                int waitingTime = currentTime - selectedProcess.arrivalTime;
                selectedProcess.waitingTime = waitingTime;

                int turnaroundTime = waitingTime + selectedProcess.burstTime;
                selectedProcess.turnaroundTime = turnaroundTime;

                completedProcesses.add(selectedProcess);

                currentTime += selectedProcess.burstTime;
            }
        }

        System.out.println("\nWaiting Time for each process:");
        for (Process process : completedProcesses) {
            System.out.println(process.processName + ": " + process.waitingTime);
        }

        System.out.println("\nTurnaround Time for each process:");
        for (Process process : completedProcesses) {
            System.out.println(process.processName + ": " + process.turnaroundTime);
        }

        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        for (Process process : completedProcesses) {
            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;
        }

        double avgWaitingTime = (double) totalWaitingTime / completedProcesses.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();

        System.out.println("\nAverage Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
    }
}
class Scheduler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose the scheduling algorithm:");
        System.out.println("1. Non-Preemptive Shortest- Job First (SJF)");
        System.out.println("2. Shortest- Remaining Time First (SRTF) Scheduling");
        System.out.println("3.Non-preemptive Priority Scheduling");
        System.out.println("4.AG Scheduling");
        System.out.print("Enter your choice (1 or 4): ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                runSJF(scanner);
                break;
            case 2:
                break;
            case 3:
                runPriorityScheduling(scanner);
                break;
            case 4:
                break;
            default:
                System.out.println("Invalid choice! Please enter 1 or 4.");
        }
    }

    public static void runSJF(Scanner scanner) {
        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();

        List<Process> processes = new ArrayList<>();
        List<Process> completedProcesses = new ArrayList<>();

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("\nEnter details for Process " + (i + 1) + ":");
            System.out.print("Process Name: ");
            String name = scanner.next();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Priority Number: ");
            int priority = scanner.nextInt();

            processes.add(new Process(name, arrivalTime, burstTime, priority));
        }
        NonPreemptiveSJF sjfScheduler = new NonPreemptiveSJF();
        System.out.print("Enter context switch time: ");
        int contextSwitchTime = scanner.nextInt();
        sjfScheduler.scheduleProcesses(processes, contextSwitchTime);
    }

    public static void runPriorityScheduling(Scanner scanner) {
        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();

        Vector<Process> processes = new Vector<>();
        Vector<Process> completedProcesses = new Vector<>();

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("\nEnter details for Process " + (i + 1) + ":");
            System.out.print("Process Name: ");
            String name = scanner.next();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Priority Number: ");
            int priority = scanner.nextInt();

            processes.add(new Process(name, arrivalTime, burstTime, priority));
        }
        PriorityScheduler priorityScheduler = new PriorityScheduler();
        priorityScheduler.scheduleProcesses(processes);
    }
}
