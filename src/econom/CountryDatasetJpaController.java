/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package econom;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import econom.entities.Country;
import econom.entities.CountryData;
import econom.entities.CountryDataset;
import econom.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author kmt
 */
public class CountryDatasetJpaController implements Serializable {

    public CountryDatasetJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CountryDataset countryDataset) {
        if (countryDataset.getCountryDataList() == null) {
            countryDataset.setCountryDataList(new ArrayList<CountryData>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country countryCode = countryDataset.getCountryCode();
            if (countryCode != null) {
                countryCode = em.getReference(countryCode.getClass(), countryCode.getIsoCode());
                countryDataset.setCountryCode(countryCode);
            }
            List<CountryData> attachedCountryDataList = new ArrayList<CountryData>();
            for (CountryData countryDataListCountryDataToAttach : countryDataset.getCountryDataList()) {
                countryDataListCountryDataToAttach = em.getReference(countryDataListCountryDataToAttach.getClass(), countryDataListCountryDataToAttach.getId());
                attachedCountryDataList.add(countryDataListCountryDataToAttach);
            }
            countryDataset.setCountryDataList(attachedCountryDataList);
            em.persist(countryDataset);
            if (countryCode != null) {
                countryCode.getCountryDatasetList().add(countryDataset);
                countryCode = em.merge(countryCode);
            }
            for (CountryData countryDataListCountryData : countryDataset.getCountryDataList()) {
                CountryDataset oldDatasetOfCountryDataListCountryData = countryDataListCountryData.getDataset();
                countryDataListCountryData.setDataset(countryDataset);
                countryDataListCountryData = em.merge(countryDataListCountryData);
                if (oldDatasetOfCountryDataListCountryData != null) {
                    oldDatasetOfCountryDataListCountryData.getCountryDataList().remove(countryDataListCountryData);
                    oldDatasetOfCountryDataListCountryData = em.merge(oldDatasetOfCountryDataListCountryData);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CountryDataset countryDataset) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CountryDataset persistentCountryDataset = em.find(CountryDataset.class, countryDataset.getDatasetId());
            Country countryCodeOld = persistentCountryDataset.getCountryCode();
            Country countryCodeNew = countryDataset.getCountryCode();
            List<CountryData> countryDataListOld = persistentCountryDataset.getCountryDataList();
            List<CountryData> countryDataListNew = countryDataset.getCountryDataList();
            if (countryCodeNew != null) {
                countryCodeNew = em.getReference(countryCodeNew.getClass(), countryCodeNew.getIsoCode());
                countryDataset.setCountryCode(countryCodeNew);
            }
            List<CountryData> attachedCountryDataListNew = new ArrayList<CountryData>();
            for (CountryData countryDataListNewCountryDataToAttach : countryDataListNew) {
                countryDataListNewCountryDataToAttach = em.getReference(countryDataListNewCountryDataToAttach.getClass(), countryDataListNewCountryDataToAttach.getId());
                attachedCountryDataListNew.add(countryDataListNewCountryDataToAttach);
            }
            countryDataListNew = attachedCountryDataListNew;
            countryDataset.setCountryDataList(countryDataListNew);
            countryDataset = em.merge(countryDataset);
            if (countryCodeOld != null && !countryCodeOld.equals(countryCodeNew)) {
                countryCodeOld.getCountryDatasetList().remove(countryDataset);
                countryCodeOld = em.merge(countryCodeOld);
            }
            if (countryCodeNew != null && !countryCodeNew.equals(countryCodeOld)) {
                countryCodeNew.getCountryDatasetList().add(countryDataset);
                countryCodeNew = em.merge(countryCodeNew);
            }
            for (CountryData countryDataListOldCountryData : countryDataListOld) {
                if (!countryDataListNew.contains(countryDataListOldCountryData)) {
                    countryDataListOldCountryData.setDataset(null);
                    countryDataListOldCountryData = em.merge(countryDataListOldCountryData);
                }
            }
            for (CountryData countryDataListNewCountryData : countryDataListNew) {
                if (!countryDataListOld.contains(countryDataListNewCountryData)) {
                    CountryDataset oldDatasetOfCountryDataListNewCountryData = countryDataListNewCountryData.getDataset();
                    countryDataListNewCountryData.setDataset(countryDataset);
                    countryDataListNewCountryData = em.merge(countryDataListNewCountryData);
                    if (oldDatasetOfCountryDataListNewCountryData != null && !oldDatasetOfCountryDataListNewCountryData.equals(countryDataset)) {
                        oldDatasetOfCountryDataListNewCountryData.getCountryDataList().remove(countryDataListNewCountryData);
                        oldDatasetOfCountryDataListNewCountryData = em.merge(oldDatasetOfCountryDataListNewCountryData);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = countryDataset.getDatasetId();
                if (findCountryDataset(id) == null) {
                    throw new NonexistentEntityException("The countryDataset with id " + id + " no longer exists.");
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
            CountryDataset countryDataset;
            try {
                countryDataset = em.getReference(CountryDataset.class, id);
                countryDataset.getDatasetId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The countryDataset with id " + id + " no longer exists.", enfe);
            }
            Country countryCode = countryDataset.getCountryCode();
            if (countryCode != null) {
                countryCode.getCountryDatasetList().remove(countryDataset);
                countryCode = em.merge(countryCode);
            }
            List<CountryData> countryDataList = countryDataset.getCountryDataList();
            for (CountryData countryDataListCountryData : countryDataList) {
                countryDataListCountryData.setDataset(null);
                countryDataListCountryData = em.merge(countryDataListCountryData);
            }
            em.remove(countryDataset);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CountryDataset> findCountryDatasetEntities() {
        return findCountryDatasetEntities(true, -1, -1);
    }

    public List<CountryDataset> findCountryDatasetEntities(int maxResults, int firstResult) {
        return findCountryDatasetEntities(false, maxResults, firstResult);
    }

    private List<CountryDataset> findCountryDatasetEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CountryDataset.class));
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

    public CountryDataset findCountryDataset(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CountryDataset.class, id);
        } finally {
            em.close();
        }
    }

    public int getCountryDatasetCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CountryDataset> rt = cq.from(CountryDataset.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
