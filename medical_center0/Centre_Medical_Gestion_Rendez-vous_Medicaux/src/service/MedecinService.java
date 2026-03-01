package service;

import dao.MedecinDao;
import model.Medecin;
import java.util.List;

public class MedecinService {
    private final MedecinDao dao = new MedecinDao();

    public List<Medecin> getAllMedecins() throws Exception { return dao.findAll(); }
    public Medecin       getMedecin(int id) throws Exception { return dao.findById(id); }
    public void          createMedecin(Medecin m) throws Exception { dao.save(m); }
    public void          updateMedecin(Medecin m) throws Exception { dao.update(m); }
    public void          deleteMedecin(int id)    throws Exception { dao.delete(id); }
}