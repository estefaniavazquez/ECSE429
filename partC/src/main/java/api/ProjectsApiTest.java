package api;

import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.junit.*;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import general.Api;
import models.Project;
import static general.CommonConstants.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectsApiTest extends Api {
    /* Helpers */

    // List of all projects to be used in tests
    private List<String> testProjectsStrings = new ArrayList<>();

    private void populateTestProjects() {
        for (int i = 0; i<MAX_NUM_OBJECTS_FOR_PERFORMANCE_TESTING; i++) {
            String title = generateRandomString(1,50,false);
            String description = generateRandomString(0,200,true);
            Boolean completed = generateRandomBoolean();
            Boolean active = generateRandomBoolean();
            Project project = new Project(title, completed, active, description);
            Map<String, Object> payloadMap = project.toPayloadMap();
            String jsonBody = toJson(payloadMap);
            testProjectsStrings.add(jsonBody);
        }
    }

    // Send a project creation request 
    private void createProject(String projectJsonBody) throws Exception {
        HttpURLConnection connection = request(PROJECTS_ENDPOINT, POST_METHOD, projectJsonBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);
        assertEquals(201, responseCode);
        assertEquals("Created", responseMessage);

        Project createdProject = new ObjectMapper().readValue(responseBody, Project.class);
        // Verify that the created project matches the sent data
        assertTrue(toJson(createdProject.toPayloadMap()).contains(projectJsonBody));

        connection.getInputStream().close();

        latestCreatedProjectId = Integer.parseInt(createdProject.getId());
    }

    // Send a project change request
    private void changeProject(String id, String projectJsonBody) throws Exception {
        HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, PUT_METHOD, id, projectJsonBody);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);
        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);   

        Project updatedProject = new ObjectMapper().readValue(responseBody, Project.class);
        // Verify that the updated project matches the sent data
        assertTrue(toJson(updatedProject.toPayloadMap()).contains(projectJsonBody));

        connection.getInputStream().close();
    }

    // Send a project deletion request
    private void deleteProject(String id) throws Exception {
        HttpURLConnection connection = requestWithId(PROJECTS_ENDPOINT, DELETE_METHOD, id, null);
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = readResponse(connection);
        assertEquals(200, responseCode);
        assertEquals("OK", responseMessage);
        assertEquals("", responseBody);

        connection.getInputStream().close();
    }

    /* Tests */
    @Test
    public void testPostProjectsJson() throws Exception {
        System.out.println("\n----------------------Creating projects performance tests");

        populateTestProjects();
        Map<Integer, List<String>> projectsMetrics = new HashMap<>();
    
        for (int numProjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {
            System.out.println("\n############# Testing with " + numProjects + " projects");
            
            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numProjects; i++) {
                    try{
                        createProject(testProjectsStrings.get(i));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create project " + (i + 1) + " of " + numProjects, e);
                    }
                }
            });
            projectsMetrics.put(numProjects, metrics);
            // Sleep for a short duration to allow server to stabilize
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        String filePath = Paths.get(System.getProperty("user.dir"), "results", "createProjects.csv").toString();
        savePerformanceMetricsToCSV(filePath, projectsMetrics);

        System.out.println("\nSaved creating projects performance tests----------------------\n");

    }

    // Test PUT /projects/:id
    @Test
    public void testPutProjectsIdJson() throws Exception {
        System.out.println("\n----------------------Updating projects performance tests");
        populateTestProjects();
        Map<Integer, List<String>> projectsMetrics = new HashMap<>();
        for (int numProjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {
            System.out.println("\n############# Testing with " + numProjects + " projects to update");
            
            // First, create the projects to be updated
            for (int i = 0; i < numProjects; i++) {
                createProject(testProjectsStrings.get(i));
            }
            populateTestProjects(); // Refresh test projects for update data

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numProjects; i++) {
                    String currentId = String.valueOf(latestCreatedProjectId - numProjects + 1 + i);
                    try{
                        changeProject(currentId, testProjectsStrings.get(i));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to update project " + (i + 1) + " of " + numProjects, e);
                    }
                }
            });
            projectsMetrics.put(numProjects, metrics);
            // Sleep for a short duration to allow server to stabilize
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        String filePath = Paths.get(System.getProperty("user.dir"), "results", "updateProjects.csv").toString();
        savePerformanceMetricsToCSV(filePath, projectsMetrics);
        System.out.println("\nSaved updating projects performance tests----------------------\n");
    }

    // Test DELETE /projects/:id
    @Test
    public void testDeleteProjectsId() throws Exception {
        System.out.println("\n----------------------Deleting projects performance tests");
        populateTestProjects();

        Map<Integer, List<String>> projectsMetrics = new HashMap<>();
        for (int numProjects : NUM_OBJECTS_FOR_PERFORMANCE_TESTING) {
            System.out.println("\n############# Testing with " + numProjects + " projects to delete");
            
            // First, create the projects to be deleted
            for (int i = 0; i < numProjects; i++) {
                createProject(testProjectsStrings.get(i));
            }

            List<String> metrics = measurePerformanceMetrics(() -> {
                for (int i = 0; i < numProjects; i++) {
                    String currentId = String.valueOf(latestCreatedProjectId - numProjects + 1 + i);
                    try{
                        deleteProject(currentId);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to delete project " + (i + 1) + " of " + numProjects, e);
                    }
                }
            });
            projectsMetrics.put(numProjects, metrics);
            // Sleep for a short duration to allow server to stabilize
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        String filePath = Paths.get(System.getProperty("user.dir"), "results", "deleteProjects.csv").toString();
        savePerformanceMetricsToCSV(filePath, projectsMetrics);
        System.out.println("\nSaved deleting projects performance tests----------------------\n");
    }
}