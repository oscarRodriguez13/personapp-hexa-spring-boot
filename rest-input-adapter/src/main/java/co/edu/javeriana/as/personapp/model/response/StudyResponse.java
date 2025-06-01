package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.StudyRequest;

public class StudyResponse extends StudyRequest {

    private String status;

    public StudyResponse(Integer personId, Integer professionId, String graduationDate, String universityName, String database, String status) {
        super(personId, professionId, graduationDate, universityName, database);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}