package service;

import dao.PatientDao;
import model.Patient;
import java.util.List;

public class PatientService {
    private final PatientDao dao = new PatientDao();

    public List<Patient> getAllPatients() throws Exception { return dao.findAll(); }
    public Patient       getPatient(int id) throws Exception { return dao.findById(id); }
    public void          createPatient(Patient p) throws Exception { dao.save(p); }
    public void          updatePatient(Patient p) throws Exception { dao.update(p); }
    public void          deletePatient(int id)    throws Exception { dao.delete(id); }
}