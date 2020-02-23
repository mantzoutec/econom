/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package econom;

import econom.entities.Country;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import econom.entities.CountryDataset;
import econom.exceptions.NonexistentEntityException;
import econom.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author kmt
 */
public class CountryJpaController implements Serializable {

    public CountryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Country country) throws PreexistingEntityException, Exception {
        if (country.getCountryDatasetList() == null) {
            country.setCountryDatasetList(new ArrayList<CountryDataset>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CountryDataset> attachedCountryDatasetList = new ArrayList<CountryDataset>();
            for (CountryDataset countryDatasetListCountryDatasetToAttach : country.getCountryDatasetList()) {
                countryDatasetListCountryDatasetToAttach = em.getReference(countryDatasetListCountryDatasetToAttach.getClass(), countryDatasetListCountryDatasetToAttach.getDatasetId());
                attachedCountryDatasetList.add(countryDatasetListCountryDatasetToAttach);
            }
            country.setCountryDatasetList(attachedCountryDatasetList);
            em.persist(country);
            for (CountryDataset countryDatasetListCountryDataset : country.getCountryDatasetList()) {
                Country oldCountryCodeOfCountryDatasetListCountryDataset = countryDatasetListCountryDataset.getCountryCode();
                countryDatasetListCountryDataset.setCountryCode(country);
                countryDatasetListCountryDataset = em.merge(countryDatasetListCountryDataset);
                if (oldCountryCodeOfCountryDatasetListCountryDataset != null) {
                    oldCountryCodeOfCountryDatasetListCountryDataset.getCountryDatasetList().remove(countryDatasetListCountryDataset);
                    oldCountryCodeOfCountryDatasetListCountryDataset = em.merge(oldCountryCodeOfCountryDatasetListCountryDataset);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCountry(country.getIsoCode()) != null) {
                throw new PreexistingEntityException("Country " + country + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Country country) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country persistentCountry = em.find(Country.class, country.getIsoCode());
            List<CountryDataset> countryDatasetListOld = persistentCountry.getCountryDatasetList();
            List<CountryDataset> countryDatasetListNew = country.getCountryDatasetList();
            List<CountryDataset> attachedCountryDatasetListNew = new ArrayList<CountryDataset>();
            for (CountryDataset countryDatasetListNewCountryDatasetToAttach : countryDatasetListNew) {
                countryDatasetListNewCountryDatasetToAttach = em.getReference(countryDatasetListNewCountryDatasetToAttach.getClass(), countryDatasetListNewCountryDatasetToAttach.getDatasetId());
                attachedCountryDatasetListNew.add(countryDatasetListNewCountryDatasetToAttach);
            }
            countryDatasetListNew = attachedCountryDatasetListNew;
            country.setCountryDatasetList(countryDatasetListNew);
            country = em.merge(country);
            for (CountryDataset countryDatasetListOldCountryDataset : countryDatasetListOld) {
                if (!countryDatasetListNew.contains(countryDatasetListOldCountryDataset)) {
                    countryDatasetListOldCountryDataset.setCountryCode(null);
                    countryDatasetListOldCountryDataset = em.merge(countryDatasetListOldCountryDataset);
                }
            }
            for (CountryDataset countryDatasetListNewCountryDataset : countryDatasetListNew) {
                if (!countryDatasetListOld.contains(countryDatasetListNewCountryDataset)) {
                    Country oldCountryCodeOfCountryDatasetListNewCountryDataset = countryDatasetListNewCountryDataset.getCountryCode();
                    countryDatasetListNewCountryDataset.setCountryCode(country);
                    countryDatasetListNewCountryDataset = em.merge(countryDatasetListNewCountryDataset);
                    if (oldCountryCodeOfCountryDatasetListNewCountryDataset != null && !oldCountryCodeOfCountryDatasetListNewCountryDataset.equals(country)) {
                        oldCountryCodeOfCountryDatasetListNewCountryDataset.getCountryDatasetList().remove(countryDatasetListNewCountryDataset);
                        oldCountryCodeOfCountryDatasetListNewCountryDataset = em.merge(oldCountryCodeOfCountryDatasetListNewCountryDataset);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = country.getIsoCode();
                if (findCountry(id) == null) {
                    throw new NonexistentEntityException("The country with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country country;
            try {
                country = em.getReference(Country.class, id);
                country.getIsoCode();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The country with id " + id + " no longer exists.", enfe);
            }
            List<CountryDataset> countryDatasetList = country.getCountryDatasetList();
            for (CountryDataset countryDatasetListCountryDataset : countryDatasetList) {
                countryDatasetListCountryDataset.setCountryCode(null);
                countryDatasetListCountryDataset = em.merge(countryDatasetListCountryDataset);
            }
            em.remove(country);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Country> findCountryEntities() {
        return findCountryEntities(true, -1, -1);
    }

    public List<Country> findCountryEntities(int maxResults, int firstResult) {
        return findCountryEntities(false, maxResults, firstResult);
    }

    private List<Country> findCountryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Country.class));
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

    public Country findCountry(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Country.class, id);
        } finally {
            em.close();
        }
    }

    public int getCountryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Country> rt = cq.from(Country.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
