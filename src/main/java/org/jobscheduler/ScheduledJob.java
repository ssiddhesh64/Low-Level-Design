package org.jobscheduler;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ScheduledJob implements Comparable<ScheduledJob> {

    private final Job job;
    private final Instant executeAt;
    private JobStatus jobStatus;

    Set<String> dependsOn;
    Set<String> dependents;

    private int dependencies;

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public void addDependent(String jobId) {
        dependents.add(jobId);
    }

    public void addParent(String jobId) {
        dependsOn.add(jobId);
    }

    public void setDependsOn(Set<String> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public int getDependencies() {
        return dependsOn.size();
    }

    public void setDependents(Set<String> dependents) {
        this.dependents = dependents;
    }



    public ScheduledJob(Job job, Instant executeAt) {
        this.job = Objects.requireNonNull(job, "Job must not be null");
        this.jobStatus = JobStatus.SCHEDULED;
        this.dependsOn = new HashSet<>();
        this.dependents = new HashSet<>();
        this.executeAt = Objects.requireNonNull(
                executeAt,
                "Execution time must not be null"
        );
        this.dependencies = 0;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public boolean run() {
        job.run();
        return true;
    }

    public Job getJob() {
        return job;
    }

    public Instant getExecuteAt() {
        return executeAt;
    }

    @Override
    public int compareTo(ScheduledJob other) {
        return executeAt.compareTo(other.executeAt);
    }

    public Set<String> getDependsOn() {
        return dependsOn;
    }

    public Set<String> getDependents() {
        return dependents;
    }
}