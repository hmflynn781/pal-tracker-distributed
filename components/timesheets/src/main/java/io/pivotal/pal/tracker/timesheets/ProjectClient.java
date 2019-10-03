package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestOperations restOperations;
    private final String endpoint;
    private Map<Long, ProjectInfo> fetchedProjects = new ConcurrentHashMap();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo pInfo = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        fetchedProjects.put(projectId, pInfo);
        return pInfo;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting project with id {} from cache", projectId);
        return fetchedProjects.get(projectId);
    }
}
