/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JPAControllers;

import econom.entities.CountryData;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import econom.entities.CountryDataset;
import econom.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author kmt
 */
public class CountryDataJpaController implements Serializable {

    public CountryDataJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CountryData countryData) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CountryDataset dataset = countryData.getDataset();
            if (dataset != null) {
                dataset = em.getReference(dataset.getClass(), dataset.getDatasetId());
                countryData.setDataset(dataset);
            }
            em.persist(countryData);
            if (dataset != null) {
                dataset.getCountryDataList().add(countryData);
                dataset = em.merge(dataset);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CountryData countryData) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CountryData persistentCountryData = em.find(CountryData.class, countryData.getId());
            CountryDataset datasetOld = persistentCountryData.getDataset();
            CountryDataset datasetNew = countryData.getDataset();
            if (datasetNew != null) {
                datasetNew = em.getReference(datasetNew.getClass(), datasetNew.getDatasetId());
                countryData.setDataset(datasetNew);
            }
            countryData = em.merge(countryData);
            if (datasetOld != null && !datasetOld.equals(datasetNew)) {
                datasetOld.getCountryDataList().remove(countryData);
                datasetOld = em.merge(datasetOld);
            }
            if (datasetNew != null && !datasetNew.equals(datasetOld)) {
                datasetNew.getCountryDataList().add(countryData);
                datasetNew = em.merge(datasetNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = countryData.getId();
                if (findCountryData(id) == null) {
                    throw new NonexistentEntityException("The countryData with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CountryData countryData;
            try {
                countryData = em.getReference(CountryData.class, id);
                countryData.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The countryData with id " + id + " no longer exists.", enfe);
            }
            CountryDataset dataset = countryData.getDataset();
            if (dataset != null) {
                dataset.getCountryDataList().remove(countryData);
                dataset = em.merge(dataset);
            }
            em.remove(countryData);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CountryData> findCountryDataEntities() {
        return findCountryDataEntities(true, -1, -1);
    }

    public List<CountryData> findCountryDataEntities(int maxResults, int firstResult) {
        return findCountryDataEntities(false, maxResults, firstResult);
    }

    private List<CountryData> findCountryDataEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CountryData.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public CountryData findCountryData(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CountryData.class, id);
        } finally {
            em.close();
        }
    }

    public int getCountryDataCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CountryData> rt = cq.from(CountryData.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
