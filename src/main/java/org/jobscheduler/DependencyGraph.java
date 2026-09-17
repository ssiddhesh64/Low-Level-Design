package org.jobscheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyGraph {

    private final Map<String, ScheduledJob> jobs = new HashMap<>();
    Map<String, List<ScheduledJob>> unresolvedDeps = new HashMap<>();

    public void addJob(ScheduledJob job) {
        jobs.put(job.getJob().getJobId(), job);
    }

    public ScheduledJob getJob(String jobId) {
        if(jobs.containsKey(jobId)) {
            return jobs.get(jobId);
        }
        return null;
    }

    public Collection<ScheduledJob> getAllJobs() {
        return jobs.values();
    }
}
