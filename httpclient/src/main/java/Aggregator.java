import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Aggregator {

    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }

    /* send task to list of workers */
    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {
        validateTaskCount(workersAddresses, tasks);

        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < workersAddresses.size(); i++) {
            byte[] payload = tasks.get(i).getBytes(StandardCharsets.UTF_8);
            futures.add(webClient.sendTask(workersAddresses.get(i), payload));
        }

        return collectResults(futures);
    }

    /* send task to list of workers with custom headers */
    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks, String headers) {
        validateTaskCount(workersAddresses, tasks);

        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < workersAddresses.size(); i++) {
            byte[] payload = tasks.get(i).getBytes(StandardCharsets.UTF_8);
            futures.add(webClient.sendTask(workersAddresses.get(i), payload, headers));
        }

        return collectResults(futures);
    }

    private void validateTaskCount(List<String> workersAddresses, List<String> tasks) {
        if (workersAddresses.size() != tasks.size()) {
            throw new IllegalArgumentException("Each worker must have exactly one task.");
        }
    }

    private List<String> collectResults(List<CompletableFuture<String>> futures) {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
