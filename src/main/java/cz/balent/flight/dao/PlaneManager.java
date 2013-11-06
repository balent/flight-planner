/**
 * Copyright 2013 Robert Balent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.balent.flight.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

import cz.balent.flight.model.Plane;
import cz.balent.flight.model.User;
/**
 * Handles Plane entity CRUD operation above database layer.
 * 
 * @author Robert Balent
 *
 */
public class PlaneManager implements Serializable {
	private static final long serialVersionUID = -1575945255065427008L;

	@PersistenceContext
	EntityManager entityManager;

	@Inject
	UserTransaction utx;

	public void savePlane(Plane plane) {
		if (getUserPlane(plane.getUser(), plane.getName()) == null) {
			createPlane(plane);
		} else {
			updatePlane(plane);
		}
	}

	public void createPlane(Plane plane) {
		try {
			utx.begin();
			entityManager.persist(plane);
			entityManager.flush();
			utx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public List<Plane> getUserPlanes(User user) {
		List<Plane> planes = null;
		try {
			utx.begin();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Plane> cq = cb.createQuery(Plane.class);
			Root<Plane> userRoot = cq.from(Plane.class);
			cq.where(cb.equal(userRoot.get("user"), user));
			TypedQuery<Plane> q = entityManager.createQuery(cq);
			planes = q.getResultList();

			utx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return planes;
	}

	public Plane getUserPlane(User user, String name) {
		Plane plane = null;
		try {
			utx.begin();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Plane> cq = cb.createQuery(Plane.class);
			Root<Plane> userRoot = cq.from(Plane.class);
			cq.where(cb.and(cb.equal(userRoot.get("user"), user), cb.equal(userRoot.get("name"), name)));
			TypedQuery<Plane> q = entityManager.createQuery(cq);
			plane = q.getSingleResult();

			utx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return plane;
	}

	public boolean updatePlane(Plane plane) {
		try {
			utx.begin();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Plane> cq = cb.createQuery(Plane.class);
			Root<Plane> userRoot = cq.from(Plane.class);
			cq.where(cb.and(cb.equal(userRoot.get("user"), plane.getUser()), cb.equal(userRoot.get("name"), plane.getName())));
			TypedQuery<Plane> q = entityManager.createQuery(cq);
			Plane persistedPlane = q.getSingleResult();
			persistedPlane.setSpeed(plane.getSpeed());
			persistedPlane.setTankVolume(plane.getTankVolume());
			persistedPlane.setConsumption(plane.getConsumption());
			entityManager.flush();

			utx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public void deleteUserPlane(User user, String name) {
		Plane plane = null;
		try {
			utx.begin();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Plane> cq = cb.createQuery(Plane.class);
			Root<Plane> userRoot = cq.from(Plane.class);
			cq.where(cb.and(cb.equal(userRoot.get("user"), user), cb.equal(userRoot.get("name"), name)));
			TypedQuery<Plane> q = entityManager.createQuery(cq);
			plane = q.getSingleResult();

			entityManager.remove(plane);
			utx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
