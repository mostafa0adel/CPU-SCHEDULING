import java.awt.geom.QuadCurve2D;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static java.lang.Math.ceil;

class Process {
    String processName;
    Color processColor;
    int arrivalTime;
    int burstTime;
    int priorityNumber;
    int waitingTime;
    int turnaroundTime;
    int quantumTime;
    int startTime;
    int endTime;
    int AgFactor;

    public Process(String processName, Color processColor, int arrivalTime, int burstTime, int priorityNumber, int quantumTime) {
        this.processName = processName;
        this.processColor = processColor;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priorityNumber = priorityNumber;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.quantumTime = quantumTime;
        this.AgFactor = calculateAgFactor();
    }



    public String getProcessName() {
        return processName;
    }
    public Color getProcessColor() {
        return processColor;
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

    private int calculateAgFactor() {
        Random rand = new Random();
        int rf = rand.nextInt(21); // Random function between 0 and 20

        if (rf < 10)
            return rf + arrivalTime + burstTime;
        else if (rf > 10)
            return 10 + arrivalTime + burstTime;
        else
            return priorityNumber + arrivalTime + burstTime;
    }
}



class RunningProcess {
    String processName;
    Color processColor;
    int UsedTime;


    public RunningProcess(String processName, Color processColor, int UsedTime) {
        this.processName = processName;
        this.processColor = processColor;
        this.UsedTime = UsedTime;

    }

    public String getprocessName() {
        return processName;
    }

    public Color getprocessColor() {
        return processColor;
    }

    public int getUsedTime() {
        return UsedTime;
    }

}

class Burst {
    String name;
    int startTime;
    int endTime;
    Color color;

    Burst(String name, int startTime, int endTime , Color color) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
    }
}
class NonPreemptiveSJF {

    private static List<Process> readyQueue;
    private static List<Process> processes;
    private static List<Process> executedProcesses;
    private static int currentTime;
    private static int totalTurnaround = 0;
    private static int totalWaiting = 0;
    private static List<RunningProcess> ForGUI;


    public static void scheduleProcesses(List<Process> inputProcesses, int contextSwitchTime) {
        readyQueue = new ArrayList<>();
        ForGUI = new ArrayList<>();
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
        Collections.sort(readyQueue, Comparator.comparingInt(Process::getBurstTime).thenComparingInt(Process::getArrivalTime));
    }

    private static void executeAndRemoveProcesses(int contextSwitchTime) {
        if (!readyQueue.isEmpty()) {
            final Process currentProcess = readyQueue.get(0);

            currentTime += currentProcess.getBurstTime();
            currentTime += contextSwitchTime;

            int Bedtime = currentProcess.getBurstTime()+contextSwitchTime;

            ForGUI.add(new RunningProcess(currentProcess.getProcessName() , currentProcess.getProcessColor() , Bedtime));


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

    public static List<RunningProcess> GetForGUI(){
        return ForGUI;
    }

}

class PriorityScheduler {

    private static List<RunningProcess> ForGUI;

    public void scheduleProcesses(Vector<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;

        Vector<Process> completedProcesses = new Vector<>();
        ForGUI = new ArrayList<>();

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

                ForGUI.add(new RunningProcess("" , Color.WHITE , waitingTime));

                int turnaroundTime = waitingTime + selectedProcess.burstTime;
                selectedProcess.turnaroundTime = turnaroundTime;

                ForGUI.add(new RunningProcess(selectedProcess.getProcessName() , selectedProcess.getProcessColor() , turnaroundTime));

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

    public static List<RunningProcess> GetForGUI(){
        return ForGUI;
    }
}


class SRTFScheduling {

    static int[] waitTime;
    static int[] turnaroundTime;

    private static List<RunningProcess> ForGUI;
    public void start(Process[] processes) {
        waitTime = new int[processes.length];
        turnaroundTime = new int[processes.length];
        List<Burst> bursts = calculateTimes(processes);
        printResults(bursts, processes);
        ForGUI = new ArrayList<>();

        for (Burst burst : bursts) {
            ForGUI.add(new RunningProcess(burst.name , burst.color, burst.endTime - burst.startTime));
        }

        double totalWaitTime = 0;
        double totalTurnaroundTime = 0;

        for (int i = 0; i < processes.length; i++) {
            totalWaitTime += waitTime[i];
            totalTurnaroundTime += turnaroundTime[i];
        }

        double averageWaitTime = totalWaitTime / processes.length;
        double averageTurnaroundTime = totalTurnaroundTime / processes.length;

        System.out.println("\nAverage Waiting Time: " + averageWaitTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);

    }

    public static List<Burst> calculateTimes(Process[] processes) {
        int n = processes.length;
        // Remaining burst time for each process
        int[] remainingTime = new int[n];
        int[] waitingTime = new int[n];
        for (int i = 0; i < n; i++) {
            remainingTime[i] = processes[i].burstTime;
            waitingTime[i] = 0;
        }

        int currentTime = 0;
        int completed = 0;

        List<Burst> bursts = new ArrayList<>();
        Burst currentBurst = null;

        while (completed < n) {
            int shortest = -1;
            int minTime = Integer.MAX_VALUE;

            // Find the process with the shortest remaining time
            for (int i = 0; i < n; i++) {
                if (processes[i].arrivalTime <= currentTime && remainingTime[i] < minTime && remainingTime[i] > 0) {
                    minTime = remainingTime[i];
                    shortest = i;
                }
            }

            // If no process is available at the current time, increment time
            if (shortest == -1) {
                currentTime++;
            } else {
                // Aging mechanism: Increase waiting time for other processes
                for (int i = 0; i < n; i++) {
                    if (i != shortest && processes[i].arrivalTime <= currentTime) {
                        waitingTime[i]++;
                    }
                }

                // Execute the shortest remaining time process
                remainingTime[shortest]--;
                int startTime = currentTime;
                currentTime++;

                // Check if the process is completed
                if (remainingTime[shortest] == 0) {
                    completed++;
                    turnaroundTime[shortest] = currentTime - processes[shortest].arrivalTime;
                    waitTime[shortest] = turnaroundTime[shortest] - processes[shortest].burstTime;
                }

                // If the current burst is for the same process, update its end time
                if (currentBurst != null && currentBurst.name == processes[shortest].processName) {
                    currentBurst.endTime = currentTime;
                } else {
                    // Otherwise, add a new burst entry
                    currentBurst = new Burst(processes[shortest].processName, startTime, currentTime, processes[shortest].getProcessColor());
                    bursts.add(currentBurst);
                }
            }
        }

        return bursts;

    }

    public static void printResults(List<Burst> bursts, Process[] processes) {

        System.out.println("\nExecution Order:");
        for (Burst burst : bursts) {
            System.out.println("Process " + burst.name + " executed from " + burst.startTime + " to " + burst.endTime);
        }

        System.out.println("\nProcess\tWaiting Time\tTurnaround Time");
        for (int i = 0; i < processes.length; i++) {
            System.out.println(processes[i].processName + "\t\t" + waitTime[i] + "\t\t\t\t" + turnaroundTime[i]);
        }

    }

    public static List<RunningProcess> GetForGUI(){
        return ForGUI;
    }


}






class AG {
    ArrayList<Process> processes = new ArrayList<>();
    ArrayList<Process> processesCopy = new ArrayList<>();
    //queue arranges the process according to Ag Factor
    PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingInt((Process o) -> o.AgFactor).thenComparingInt(o -> o.arrivalTime));
    //queue to store the arrived process
    Queue<Process> arrivedQueue = new LinkedList<>();
    ArrayList<Process> executedProcesses = new ArrayList<>();
    ArrayList<Process> die_list = new ArrayList<>();
    ArrayList<ArrayList<Integer>> quantumUpdateHistory = new ArrayList<ArrayList<Integer>>();
    double avgTurnAroundTime, avgWaitingTime;
    private static List<RunningProcess> ForGUI;

    public AG(ArrayList<Process> processes) {
        this.processes = processes;
        //deep copy of the processes
        for (Process p : processes) {
            processesCopy.add(new Process(p.processName, p.getProcessColor(),p.arrivalTime, p.burstTime, p.priorityNumber, p.quantumTime));

        }
    }



    public void UpdateQuantum() {
        ArrayList<Integer> quantumUpdate = new ArrayList<Integer>();
        for (Process p : processes) {
            quantumUpdate.add(p.quantumTime);
        }
        quantumUpdateHistory.add(quantumUpdate);
    }

    void start() {
        int burst_Time = 0;
        Process currentProcess = null;
        ForGUI = new ArrayList<>();
        UpdateQuantum();
        for (int time = 0, pNum = 0; !arrivedQueue.isEmpty() || pNum < processes.size() || currentProcess != null; time++) {
            //check if the process is arrived or not
            while (pNum < processes.size() && processes.get(pNum).arrivalTime == time) {
                arrivedQueue.add(processes.get(pNum));
                queue.add(processes.get(pNum));
                pNum++;
            }
            //if there is any process in the queue
            if (!arrivedQueue.isEmpty()) {
                //if the process is not started yet
                if (currentProcess == null) {
                    currentProcess = arrivedQueue.poll();
                    queue.remove(currentProcess);
                    currentProcess.startTime = time;
                    burst_Time = currentProcess.burstTime;

                }

                //if there is a process in the queue that can replace the current process after its Non-preemptive time
                if (ceil(0.5 * (currentProcess.quantumTime)) <= (burst_Time - currentProcess.burstTime)
                        && !queue.isEmpty() && queue.peek().AgFactor < currentProcess.AgFactor ) {
                    currentProcess.quantumTime += (currentProcess.quantumTime - (burst_Time - currentProcess.burstTime));
                    UpdateQuantum();
                    arrivedQueue.add(currentProcess);
                    queue.add(currentProcess);
                    currentProcess.endTime = time;
                    executedProcesses.add(currentProcess);

                    int executionTime = currentProcess.endTime - currentProcess.startTime;
                    ForGUI.add(new RunningProcess(currentProcess.getProcessName() , currentProcess.getProcessColor() , executionTime));

                    currentProcess = queue.poll();
                    arrivedQueue.remove(currentProcess);
                    assert currentProcess != null;
                    currentProcess.startTime = time;
                    burst_Time = currentProcess.burstTime;
                }

            }
            // get here if there is no process in the queue
            if (currentProcess != null) {
                currentProcess.burstTime--;
                if (currentProcess.burstTime == 0) {
                    currentProcess.endTime = time + 1;
                    currentProcess.quantumTime = 0;
                    UpdateQuantum();
                    currentProcess.turnaroundTime = currentProcess.endTime - currentProcess.arrivalTime;
                    avgTurnAroundTime += currentProcess.turnaroundTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - processesCopy.get(processes.indexOf(currentProcess)).burstTime;
                    avgWaitingTime += currentProcess.waitingTime;
                    executedProcesses.add(currentProcess);

                    int executionTime = currentProcess.endTime - currentProcess.startTime;
                    ForGUI.add(new RunningProcess(currentProcess.getProcessName() , currentProcess.getProcessColor() , executionTime));

                    die_list.add(currentProcess);
                    currentProcess = null;
                }

            }
            //else if its finished its quantum time and still have burst time
            if (currentProcess != null && currentProcess.quantumTime == burst_Time - currentProcess.burstTime) {
                currentProcess.endTime = time + 1;
                executedProcesses.add(currentProcess);

                int executionTime = currentProcess.getArrivalTime() - currentProcess.startTime;
                ForGUI.add(new RunningProcess(currentProcess.getProcessName() , currentProcess.getProcessColor() , executionTime));



                double sum = 0.0;
                int n = 0;
                for (Process p : arrivedQueue) {
                    sum += p.quantumTime;
                    if (p.quantumTime != 0)
                        n++;

                }
                currentProcess.quantumTime += (int) ceil((sum / n) * 0.1);
                UpdateQuantum();
                queue.add(currentProcess);
                arrivedQueue.add(currentProcess);
                currentProcess = null;

            }
        }
        printResult();
    }

    void printResult() {
        System.out.println("Quantum Update: ");
        for (int i = 0; i < quantumUpdateHistory.size(); i++) {
            System.out.print("( ");
            for (int j = 0; j < quantumUpdateHistory.get(i).size(); j++) {
                if (j < quantumUpdateHistory.get(i).size() - 1)
                    System.out.print(quantumUpdateHistory.get(i).get(j) + ", ");
                else
                    System.out.print(quantumUpdateHistory.get(i).get(j));


            }
            System.out.println(" )");

        }
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("Execution Order");
        for (Process p : executedProcesses) {
            System.out.println(p.processName);
        }
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("Process Name\tArrivalTime\tBurstTime\tPriorityNumber\tTurnaroundTime\tWaitingTime");
        for (Process p : die_list) {
            System.out.println(p.processName + "\t\t\t\t" + p.arrivalTime + "\t\t\t\t" + processesCopy.get(processes.indexOf(p)).burstTime + "\t\t\t\t" +
                    p.priorityNumber + "\t\t\t\t" + p.turnaroundTime + "\t\t\t\t" + p.waitingTime);
        }
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("AverageTurnAroundTime\tAverageWaitingTime");
        avgWaitingTime /= processes.size();
        avgTurnAroundTime /= processes.size();
        System.out.println(avgTurnAroundTime + "\t\t\t\t\t\t" + avgWaitingTime);
        System.out.println("---------------------------------------------------------------------------------------");

    }

    public static List<RunningProcess> GetForGUI(){
        return ForGUI;
    }


}






class GUI extends JPanel {
    private final List<RunningProcess> runningProcesses;

    public GUI(List<RunningProcess> processes) {
        this.runningProcesses = processes;
    }

    @Override

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int xPos = 50;

        for (RunningProcess process : runningProcesses) {
            g.setColor(process.getprocessColor());
            g.fillRect(xPos, 50, process.getUsedTime() * 50, 30);
            g.setColor(Color.BLACK);
            // Centering the text within the rectangle
            int textWidth = g.getFontMetrics().stringWidth(process.getprocessName());
            int textXPos = xPos + (process.getUsedTime() * 50 ) / 2;
            g.drawString(process.getprocessName(), textXPos, 70); // Adjust the y-position as needed
            xPos += process.getUsedTime() * 50; // Adding padding between rectangles
        }
    }


    
    public void displayGUI() {
        JFrame frame = new JFrame("Scheduling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setSize(16000, 4800);
        frame.setVisible(true);
    }
}


 class CpuScheduling {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();
        System.out.print("Enter Round Robin Time Quantum: ");
        int quantumTime = scanner.nextInt();
        System.out.print("Enter context switch time: ");
        int contextSwitchTime = scanner.nextInt();
        ArrayList<Process> processes = new ArrayList<>();
        Vector<Process> processes1 = new Vector<>();
        Process[] processes2 = new Process[numProcesses];
        for (int i = 1; i <= numProcesses; i++) {
            System.out.println("Enter details for Process " + i + ":");
            System.out.print("Process Name: ");
            String processName = scanner.next();
            System.out.print("Choose Color number (1.RED, 2.BLUE, 3.GREEN, 4.YELLOW, 5.ORANGE): ");
            int colorNumber = scanner.nextInt();
            Color color;
            switch (colorNumber) {
                case 1:
                    color = Color.RED;
                    break;
                case 2:
                    color = Color.BLUE;
                    break;
                case 3:
                    color = Color.GREEN;
                    break;
                case 4:
                    color = Color.YELLOW;
                    break;
                case 5:
                    color = Color.ORANGE;
                    break;
                default:
                    color = Color.WHITE;
                    break;
            }
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Priority Number: ");
            int priorityNumber = scanner.nextInt();
            processes.add(new Process(processName, color, arrivalTime, burstTime, priorityNumber, quantumTime));
            processes1.add(new Process(processName, color, arrivalTime, burstTime, priorityNumber, quantumTime));
            processes2[i-1] = new Process(processName, color, arrivalTime, burstTime, priorityNumber, quantumTime);
        }
        System.out.println("Choose the scheduling algorithm:");
        System.out.println("1. Non-Preemptive Shortest- Job First (SJF)");
        System.out.println("2. Shortest- Remaining Time First (SRTF) Scheduling");
        System.out.println("3.Non-preemptive Priority Scheduling");
        System.out.println("4.AG Scheduling");
        System.out.print("Enter your choice (1 or 4): ");
        int choice = scanner.nextInt();


        List <RunningProcess> forGui;
        GUI gui;
        switch (choice) {
            case 1:
                NonPreemptiveSJF.scheduleProcesses(processes, contextSwitchTime);

                forGui = NonPreemptiveSJF.GetForGUI();
                gui = new GUI(forGui);
                SwingUtilities.invokeLater(gui::displayGUI);
                break;
            case 2:
                SRTFScheduling srtfScheduling = new SRTFScheduling();
                srtfScheduling.start(processes2);

                forGui = srtfScheduling.GetForGUI();
                gui = new GUI(forGui);
                SwingUtilities.invokeLater(gui::displayGUI);
                break;
            case 3:
                PriorityScheduler priorityScheduler = new PriorityScheduler();
                priorityScheduler.scheduleProcesses(processes1);

                forGui = PriorityScheduler.GetForGUI();
                gui = new GUI(forGui);
                SwingUtilities.invokeLater(gui::displayGUI);

                break;
            case 4:
                AG ag = new AG(processes);
                ag.start();

                forGui = AG.GetForGUI();
                gui = new GUI(forGui);
                SwingUtilities.invokeLater(gui::displayGUI);
                break;

            default:
                System.out.println("Invalid choice!");
        }

    }
}


